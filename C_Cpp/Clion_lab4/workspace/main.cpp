// kill me maybe


#include <array>
#include <concepts>
#include <cstddef>
#include <iostream>
#include <stdexcept>
#include <utility>

template<typename T, size_t depth>
class NDArray;

template<typename T, size_t depth>
class AbstractView;

template<typename T, size_t depth>
using NDArrayView = AbstractView<T, depth>;

template<typename T, size_t depth>
using NDArrayConstView = AbstractView<const T, depth>;

template<typename T, size_t depth>
class AbstractView {
public:
    using size_type = std::size_t;
    using value_type = T;
    using reference = value_type &;
    using pointer = T *;
    using const_pointer = const T *;
    using difference_type = std::ptrdiff_t;

    using ThisView = AbstractView<T, depth>;
    using NextView = NDArrayView<T, depth - 1>;
    using NextConstView = NDArrayConstView<T, depth - 1>;

    using iterator_category = std::contiguous_iterator_tag;

    AbstractView(pointer data = nullptr, size_type *dimensions = nullptr,
                    size_type *accum_dim = nullptr) : m_data(data), m_dimensions(dimensions), m_accum_dim(accum_dim) {
    }

    AbstractView(const AbstractView &other) : AbstractView(other.m_data, other.m_dimensions, other.m_accum_dim) {
    }

    AbstractView(AbstractView &&other) noexcept {
        move(other);
    }

    AbstractView(NDArray<T, depth> &impr) : AbstractView<T, depth>(
        impr.data(), impr.dimensions(), impr.accum_dim()) {
        m_total_size = impr.total_count();
    }

    AbstractView(AbstractView<T, depth - 1> &start, AbstractView<T, depth - 1> &end) {
        size_type wide_dim = (end.data() - start.data()) / start.accum_dim()[0];
        m_data = start.data();
        m_dimensions = start.dimensions() - 1;
        m_accum_dim = start.accum_dim() - 1;
        m_total_size = wide_dim * start.total_count();
    }

    ~AbstractView() = default;

    AbstractView &operator=(const AbstractView &other) {
        if (this != other) {
            AbstractView tmp(other);
            tmp.swap(*this);
        }
        return *this;
    }

    AbstractView &operator=(AbstractView &&other) noexcept {
        swap(other);
        return *this;
    }

    reference operator*() const
        requires(depth == 0) {
        return *m_data;
    }

    ThisView operator*() const
        requires(depth > 0) {
        return ThisView(m_data, m_dimensions, m_accum_dim);
    }

    pointer operator->() const { return m_data; }

    // walking
    AbstractView &operator++() {
        m_data += m_accum_dim[0];
        return *this;
    }

    AbstractView operator++(int) {
        AbstractView tmp(*this);
        ++(*this);
        return tmp;
    }

    AbstractView &operator--() {
        m_data -= m_accum_dim[0];
        return *this;
    }

    AbstractView operator--(int) {
        AbstractView tmp(*this);
        --(*this);
        return tmp;
    }

    AbstractView operator+(difference_type tar) const {
        return This(m_data + calc_offset(tar), m_dimensions, m_accum_dim);
    }

    AbstractView operator-(difference_type tar) const {
        return This(m_data - calc_offset(tar), m_dimensions, m_accum_dim);
    }

    difference_type operator-(const ThisView &other) const { return (m_data - other.m_data) / m_dimensions[0]; }

    AbstractView &operator+=(difference_type delt) {
        m_data += calc_offset(delt);
        return *this;
    }

    AbstractView &operator-=(difference_type delt) {
        m_data -= calc_offset(delt);
        return *this;
    }

    void move(AbstractView &other) {
        m_data = std::exchange(other.m_data, nullptr);
        m_dimensions = std::exchange(other.m_dimensions, nullptr);
        m_accum_dim = std::exchange(other.m_accum_dim, nullptr);
        m_total_size = other.m_total_size;
        other.m_total_size = 0;
    }

    size_type count() const { return m_dimensions[1]; }
    size_type total_count() const {
        if (depth == 0) {
            return 1;
        }
        return m_dimensions[1] * m_accum_dim[1];
    }
    size_type dim() const { return depth; }

    T &operator[](size_type tar) noexcept
        requires(depth == 1) {
        return m_data[tar];
    }

    const T &operator[](size_type tar) const noexcept
        requires(depth == 1) {
        return m_data[tar];
    }

    NextView operator[](const size_type tar) noexcept
        requires(depth > 1) {
        size_type offset = calc_offset(tar);
        return NextView(m_data + offset, m_dimensions + 1, m_accum_dim + 1);
    }

    NextConstView operator[](const size_type tar) const noexcept
        requires(depth > 1) {
        size_type offset = calc_offset(tar);
        return NextConstView(m_data + offset, m_dimensions + 1, m_accum_dim + 1);
    }

    T& at(const std::initializer_list<size_type> &indexes) {
        if (indexes.size() != depth) {
            throw std::out_of_range("Error at: necessary amount of args = depth");
        }
        size_type offset = 0;
        size_t i = 1;
        for (const size_type idx : indexes) {
            if (idx >= m_dimensions[i]) {
                throw std::out_of_range("Error at: index out of range");
            }
            offset += idx * m_accum_dim[i];
            i++;
        }
        return m_data[offset];
    }

    bool is_equal(const AbstractView<T, depth> &rhs) const noexcept
    requires std::equality_comparable<T> {
        if (m_total_size != rhs.m_total_size) {
            return false;
        }
        for (size_t i = 0; i < depth; i++) {
            if (m_dimensions[i] != rhs.m_dimensions[i]) {
                return false;
            }
        }
        for (size_t i = 0; i < m_dimensions[1] * m_accum_dim[1]; i++) {
            if (m_data[i] != rhs.m_data[i]) {
                return false;
            }
        }

        return true;
    }

    NextView begin() noexcept
        requires(depth > 1) {
        return NextView(m_data, cnt_dim_next(), cnt_acc_next());
    }

    T *begin() noexcept
        requires(depth == 1) {
        return m_data;
    }

    NextConstView begin() const noexcept
        requires(depth > 1) {
        return NextConstView(m_data, cnt_dim_next(), cnt_acc_next());
    }

    const T *begin() const noexcept
        requires(depth == 1) {
        return m_data;
    }

    NextView end() noexcept
        requires(depth > 1) {
        return NextView(cnt_data_offset(), cnt_dim_next(), cnt_acc_next());
    }

    NextConstView end() const noexcept
        requires(depth > 1) {
        return NextConstView(cnt_data_offset(), cnt_dim_next(), cnt_acc_next());
    }

    T *end() noexcept
        requires(depth == 1) {
        return cnt_data_offset();
    }

    const T *end() const noexcept
        requires(depth == 1) {
        return cnt_data_offset();
    }

    NextConstView cbegin() const noexcept
        requires(depth > 1) {
        return begin();
    }

    const T *cbegin() const noexcept
        requires(depth == 1) {
        return begin();
    }

    NextConstView cend() const noexcept
        requires(depth > 1) {
        return end();
    }

    const T *cend() const noexcept
        requires(depth == 1) {
        return end();
    }

    template <typename SizeList>
    ThisView reshape(const SizeList sizes) {
        size_t i = 1;
        for (T size : sizes) {
            m_dimensions[i] = size;
            i++;
        }
        size_type accum = 1;
        for (size_t i = depth; i > 0; --i) {
            m_accum_dim[i] = accum;
            accum *= m_dimensions[i];
        }
        return ThisView(m_data, m_dimensions, m_accum_dim);
    }

    void swap(AbstractView &other) {
        for (size_t i = 0; i < depth + 1; i++) {
            m_dimensions[i] = other.m_dimensions[i];
            m_accum_dim[i] = other.m_accum_dim[i];
        }
        std::swap(m_data, other.m_data);
        std::swap(m_total_size, other.m_total_size);
    }

    bool empty() noexcept { return begin() == end(); }

    // comparing
    std::strong_ordering operator<=>(const AbstractView &rhs) const noexcept {
        return m_data <=> rhs.m_data;
    }

    bool operator==(const AbstractView &rhs) const noexcept {
        if (m_data == rhs.m_data && m_dimensions == rhs.m_dimensions && m_accum_dim == rhs.m_accum_dim &&
            m_dimensions[1] == rhs.m_dimensions[1] && m_accum_dim[1] == rhs.m_accum_dim[1]) {
            return true;
            }
        return false;
    }

    // for tests
    pointer data() noexcept { return m_data; }
    const_pointer data() const noexcept { return m_data; }
    size_type* dimensions() const noexcept { return m_dimensions; }
    size_type* accum_dim() const noexcept { return m_accum_dim; }

protected:
    pointer m_data;
    size_type *m_dimensions;
    size_type m_total_size;
    size_type *m_accum_dim;

    void fill_accum_dim() {
        m_accum_dim[depth] = 1;
        if (depth >= 2) {
            for (size_type i = depth; i > 0; i--) {
                m_accum_dim[i - 1] = m_dimensions[i] * m_accum_dim[i];
            }
        }
    }

    inline size_type calc_offset(const size_type index) const noexcept { return index * m_accum_dim[1]; }

private:
    pointer cnt_data_offset() const noexcept {
        return m_data + m_dimensions[1] * m_accum_dim[1];
    }
    size_type *cnt_dim_next() const noexcept {
        return m_dimensions + 1;
    }
    size_type *cnt_acc_next() const noexcept {
        return m_accum_dim + 1;
    }
};

template<typename T, size_t depth>
class NDArray : public AbstractView<T, depth> {
public:
    using abstract = AbstractView<T, depth>;
    using typename abstract::difference_type;
    using typename abstract::pointer;
    using typename abstract::reference;
    using typename abstract::size_type;
    using typename abstract::value_type;
    using abstract::total_count;

    using iterator = NDArrayView<T, depth>;
    using const_iterator = NDArrayConstView<T, depth>;

    template<size_t N, typename U>
    struct mode_init {
        using type = std::initializer_list<typename mode_init<N - 1, U>::type>;
    };

    template<typename U>
    struct mode_init<0, U> {
        using type = U;
    };

    template<typename U>
    struct mode_init<1, U> {
        using type = std::initializer_list<U>;
    };

    NDArray(const NDArray &other) {
        m_total_size = other.m_total_size;

        m_store_dims = std::move(other.m_store_dims);
        m_dimensions = m_store_dims.data();
        m_store_accum = std::move(other.m_store_accum);
        m_accum_dim = m_store_accum.data();

        create_data_by_ptr(other.m_data);
    }

    NDArray(NDArray &&other) noexcept {
        m_store_dims = std::move(other.m_store_dims);
        m_store_accum = std::move(other.m_store_accum);
        abstract::move(other);
    }

    NDArray() {
        m_data = nullptr;
        m_store_dims = {0, 0};
        m_dimensions = m_store_dims.data();
        m_store_accum = {0, 0};
        m_accum_dim = m_store_accum.data();
        m_total_size = 0;
    }

    NDArray(const T &scalar)
        requires(depth == 0) {
        m_total_size = 1;
        m_store_dims = std::array<size_type, 1>{0};
        m_dimensions = m_store_dims.data();
        m_store_accum = std::array<size_type, 1>{0};
        m_accum_dim = m_store_accum.data();
        m_data = static_cast<T *>(operator new(sizeof(T)));
        new(m_data) T(scalar);
    }

    NDArray(typename mode_init<depth, T>::type init)
    requires(depth > 0) {
        alloc_support_arrays();

        m_total_size = 1;
        for (size_t i = 0; i < depth + 1; i++) {
            m_dimensions[i] = 0;
        }
        count_dims<depth>(init);

        abstract::fill_accum_dim();
        for (size_t i = 1; i < depth + 1; i++) {
            m_total_size *= m_dimensions[i];
        }

        m_data = static_cast<T *>(operator new(sizeof(T) * m_total_size));
        T *it = m_data;
        fill_data<depth>(init, it);
    }

    template<typename SizeList>
    NDArray(const SizeList &list, T* ptr) {
        stock_init(list);
        create_data_by_ptr(ptr);
    }

    template<typename SizeList>
    NDArray(const SizeList &list, const T& value = NULL) {
        stock_init(list);

        m_data = static_cast<T *>(operator new(sizeof(T) * m_total_size));
        size_t i = 0;
        try {
            for (; i < m_total_size; i++) {
                if (value) {
                    new(m_data + i) T(value);
                } else {
                    new(m_data + i) T();
                }
            }
        } catch (...) {
            clear(m_data + i - 1);
        }
    }

    NDArray(AbstractView<T, depth> view) {
        m_total_size = view.total_count();

        alloc_support_arrays();

        for (size_t i = 0; i < depth + 1; i++) {
            m_dimensions[i] = view.dimensions()[i];
        }
        abstract::fill_accum_dim();

        std::cout << "before cr_data\n";

        create_data_by_ptr(view.data());
    }

    NDArray(AbstractView<T, depth - 1> &start, AbstractView<T, depth - 1> &end) {
        size_type wide_dim = (end.data() - start.data()) / start.accum_dim()[0];
        m_total_size = wide_dim * start.total_count();

        alloc_support_arrays();

        m_dimensions[0] = wide_dim;
        for (size_t i = 0; i < depth; i++) {
            m_dimensions[i + 1] = start.dimensions()[i];
        }
        abstract::fill_accum_dim();

        create_data_by_ptr(start.data());
    }

    ~NDArray() {
        clear();
    }

    NDArray &operator=(const NDArray &other) {
        if (this != other) {
            NDArray tmp(other);
            tmp.swap(*this);
        }
        return *this;
    }

    NDArray &operator=(NDArray &&other) noexcept {
        if (*this != other) {
            clear();
            abstract::move(other);
        }
        return *this;
    }

private:
    using abstract::m_accum_dim;
    using abstract::m_data;
    using abstract::m_dimensions;
    using abstract::m_total_size;

    std::array<size_type, depth + 1> m_store_dims;
    std::array<size_type, depth + 1> m_store_accum;

    template<size_type cur_depth>
    void count_dims(typename mode_init<cur_depth, T>::type &init) noexcept {
        const size_type level = depth - cur_depth + 1;
        m_dimensions[level] = std::max(m_dimensions[level], init.size());
        if constexpr (cur_depth > 1) {
            for (typename mode_init<cur_depth - 1, T>::type i : init) {
                count_dims<cur_depth - 1>(i);
            }
        }
    }

    template<size_type cur_depth>
    void fill_data(typename mode_init<cur_depth, T>::type &init, T *&it) {
        if constexpr (cur_depth > 1) {
            for (typename mode_init<cur_depth - 1, T>::type cur_init: init) {
                T *old = it;
                const size_type level = depth - cur_depth + 1;
                fill_data<cur_depth - 1>(cur_init, it);

                const size_type shortage = m_accum_dim[level] - (it - old);
                for (size_type i = 0; i < shortage; i++) {
                    new(it) T();
                    ++it;
                }
            }
        } else {
            for (const T &cur: init) {
                try {
                    new(it) T(cur);
                    it++;
                } catch (...) {
                    clear(it - 1);
                    throw;
                }
            }
        }
    }

    template <typename SizeList>
    void stock_init(SizeList& list) {
        alloc_support_arrays();

        m_total_size = 1;
        m_dimensions[0] = 0;
        size_t i = 1;
        for (T elem : list) {
            m_dimensions[i] = elem;
            m_total_size *= m_dimensions[i];
            i++;
        }

        abstract::fill_accum_dim();
    }

    void alloc_support_arrays() {
        m_store_dims = std::array<size_t, depth + 1>();
        m_dimensions = m_store_dims.data();
        m_store_accum = std::array<size_t, depth + 1>();
        m_accum_dim = m_store_accum.data();
    }

    void create_data_by_ptr(T* ptr) {
        std::cout << "m_total_size: " << m_total_size << std::endl;
        m_data = static_cast<T *>(operator new(sizeof(T) * m_total_size));
        size_t i = 0;
        try {
            for (; i < m_total_size; i++) {
                new(m_data + i) T(ptr[i]);
            }
        } catch (...) {
            if (i == 0) {
                operator delete(m_data);
                m_data = nullptr;
            } else {
                clear(m_data + i - 1);
            }
            throw;
        }
    }

    void clear(T* end = nullptr) noexcept {
        if (!this->m_data) return;
        if (!end) {
            end = m_data + total_count();
        }
        while (end != this->m_data) {
            (--end)->~T();
        }
        operator delete(m_data);
        this->m_data = nullptr;
    }
};




















struct ThrowOnCopy
{
    inline static int copies = 0;
    inline static int max_copies = 2;
    int id;

    explicit ThrowOnCopy(int id = 0) : id(id) {}

    ThrowOnCopy(const ThrowOnCopy& other) : id(other.id)
    {
        if (++copies > max_copies)
            throw std::runtime_error("copy failed");
    }

    ThrowOnCopy(ThrowOnCopy&&) noexcept = default;
    ThrowOnCopy& operator=(const ThrowOnCopy&) = default;
    ThrowOnCopy& operator=(ThrowOnCopy&&) noexcept = default;

    operator int() const { return id; }

    static void reset() { copies = 0; }

    static void set(int value) { max_copies = value; }

    bool operator==(const ThrowOnCopy& other) const { return id == other.id; }
};

int main() {
    std::cout << "1D\n";
    NDArray<int, 1> arr1D{1, 2, 3};
    for (auto i : arr1D) {
        std::cout << i << " ";
    }
    std::cout << "\n\n";

    std::cout << "2D\n";
    NDArray<int, 2> arr2D{{1, 2}, {3, 4}};


    for (auto i : arr2D) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << std::endl;
    for (size_t i = 0; i < 2; i++) {
        for (size_t j = 0; j < 2; j++) {
            std::cout << arr2D[i][j] << " ";
        }
    }
    std::cout << "\n\n";

    std::cout << "stas\n";
    NDArray<int, 3> stas{{{1, 3, 4},{5, 6, 7}},{{9},{13, 14, 15}},{{17, 18, 19, 20},{21, 22, 23, 24}}};
    for (auto i : stas) {
        for (auto j : i) {
            for (auto k : j) {
                std::cout << k << " ";
            }
        }
    }
    std::cout << "\n\n";

    std::cout << "4D\n";
    NDArray<int, 4> arr4D{{{{{1}, {}}, {}}, {}}, {}};
    for (auto i : arr4D) {
        for (auto j : i) {
            for (auto k : j) {
                for (auto l : k) {
                    std::cout << l << " ";
                }
            }
        }
    }
    std::cout << "\n\n";

    std::cout << "[] test\n";
    NDArray<int, 2> arr_sq2D{{{1, 2}, {3, 4}}};
    for (size_t i = 0; i < 2; i++) {
        for (size_t j = 0; j < 2; j++) {
            std::cout << arr_sq2D[i][j] << " ";
        }
    }
    std::cout << "\ncout: " << arr_sq2D.count() << "total: " << arr_sq2D.total_count() << " dim: " << arr_sq2D.dim() << "\n";
    std::cout << "\n\n";

    std::cout << "View test\n";
    NDArray<int, 1> arr_for_view{1, 2};
    NDArrayView<int, 1> view(arr_for_view);
    for (auto i : view) {
        std::cout << i << " ";
    }
    std::cout << "\ncout: " << view.count() << " total: " << view.total_count() << " dim: " << view.dim() << "\n";
    std::cout << "\n\n";

    std::cout << "copy assigment test\n";
    ThrowOnCopy::reset();
    NDArray< ThrowOnCopy, 1 > good{ ThrowOnCopy(4), ThrowOnCopy(2) };
    // NDArray< ThrowOnCopy, 1 > doomed(good);
    std::cout << "\n\n";

    std::cout << "2 view -> 1 big view\n";
    NDArray<int, 2> big{{1,2},{3,4}};
    auto small_1 = big.begin();
    auto small_2 = big.end();
    auto rest_big = AbstractView<int, 2>(small_1, small_2);
    for (auto i : rest_big) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n\n";

    std::cout << "constructor for dims\n";
    std::array d0 = {2, 3};
    NDArray<int, 2> dims_only(d0);
    std::array d1 = {2, 3};
    NDArray<int, 2> dims_val(d1, 7);
    NDArray<int, 2> dims_ptr(d1, dims_val.data());
    for (auto i : dims_only) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n";
    for (auto i : dims_val) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n";
    for (auto i : dims_ptr) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n\n";

    std::cout << "reshape test\n";

    NDArray<int, 2> arr_reshape(d0, 5);
    std::cout << "old size = " << arr_reshape.total_count() << "\n";
    std::array<int, 2> for_rsp = {3, 2};
    auto new_arr = arr_reshape.reshape(for_rsp);
    std::cout << "new size = " << new_arr.total_count() << "\n";
    for (auto i : new_arr) {
        std::cout << " aga ";
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n\n";

    std::cout << "copy test\n";
    NDArray<int,2> original{{1,2},{3,4}};
    NDArray<int,2> copied(original);
    NDArray<int,2> moved(std::move(original));
    std::cout << "\n\n";

    std::cout << "at test\n";
    NDArray<int, 2> for_at = {{1,2},{3,4}};
    std::cout << for_at.at({0, 1}) << "\n";
    std::cout << "\n\n";

    std::cout << "equal test\n";
    NDArray<int, 2> equal_1 = {{1, 3}, {5}};
    NDArray<int, 2> equal_2 = {{}, {1}};
    std::cout << equal_1.is_equal(equal_2);
    std::cout << "\n\n";

    std::cout << "NDArray(View)\n";
    NDArray<int, 2> nda1 = {{5, 6}, {7, 8}};
    NDArrayView<int, 2> view1 = NDArrayView<int, 2>(nda1);
    NDArray<int, 2> nda2 = NDArray<int, 2>(view1);
    for (auto i : nda2) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n\n";

    std::cout << "NDArray(view1, view2)\n";
    NDArrayView<int, 2> wide_view = NDArrayView<int, 2>(nda1);
    NDArrayView<int, 1> start_view = wide_view.begin();
    NDArrayView<int, 1> end_view = wide_view.end();
    NDArray<int, 2> wide_nda = NDArray<int, 2>(start_view, end_view);
    for (auto i : wide_nda) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n\n";


    return 0;
}


