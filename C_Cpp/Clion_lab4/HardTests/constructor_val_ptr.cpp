// kill me maybe

// 3. аргументы конструкторов (костя)

#include <algorithm>
#include <array>
#include <iostream>
#include <utility>
#include <pstl/algorithm_impl.h>


template <typename T, size_t depth>
class NDArray;

template <typename T, size_t depth, bool type>
class AbstractView;

template <typename T, size_t depth>
using NDArrayView = AbstractView<T, depth, false>;

template <typename T, size_t depth>
using NDArrayConstView = AbstractView<T, depth, true>;

template <typename T, size_t depth, bool type>
class AbstractNDArray {
public:
    using size_type = std::size_t;
    using value_type = std::conditional_t<type, const T, T>;
    using reference = std::conditional_t<type, const T&, T&>;
    using pointer = std::conditional_t<type, const T*, T*>;
    using difference_type = std::ptrdiff_t;

    using ThisView = AbstractView<T, depth, type>;
    using NextView = NDArrayView<T, depth - 1>;
    using NextConstView = NDArrayConstView<T, depth - 1>;

    void move(AbstractNDArray &other) {
        m_data = std::exchange(other.m_data, nullptr);
        m_dimensions = std::exchange(other.m_dimensions, nullptr);
        m_accum_dim = std::exchange(other.m_accum_dim, nullptr);
        m_total_size = other.m_total_size;
        other.m_total_size = 0;
    }

    size_type count() const {return m_dimensions[1];}
    size_type total_count() const {return m_dimensions[1] * m_accum_dim[1];}
    size_type dim() const {return depth;}

    T& operator[](difference_type tar) noexcept requires (depth == 1) {
        return m_data[tar];
    }

    const T& operator[](difference_type tar) const noexcept requires (depth == 1) {
        return m_data[tar];
    }

    NextView operator[](const difference_type tar) noexcept requires (depth > 1) {
        size_type offset = calc_offset(tar);
        return NextView(m_data + offset, m_dimensions + 1, m_accum_dim + 1);
    }

    NextConstView operator[](const difference_type tar) const noexcept requires (depth > 1) {
        size_type offset = calc_offset(tar);
        return NextConstView(m_data + offset, m_dimensions + 1, m_accum_dim + 1);
    }

    T& at(const std::initializer_list<size_t> indexes) const {
        size_t offset = 0;
        size_t i = 1;
        for (auto cur : indexes) {
            offset += cur * m_accum_dim[i];
            i++;
        }
        return m_data[offset];
    }

    bool is_equal(const AbstractNDArray<T, depth, type>& rhs) const {
        if (m_data == rhs.m_data
            && m_dimensions == rhs.m_dimensions
            && m_accum_dim == rhs.m_accum_dim
            && m_dimensions[1] == rhs.m_dimensions[1]
            && m_accum_dim[1] == rhs.m_accum_dim[1]) {
            return true;
        }
        return false;
    }

    NextView begin() noexcept requires(depth > 1) {
        return NextView(m_data, m_dimensions + 1, m_accum_dim + 1);
    }

    T* begin() noexcept requires(depth == 1) {
        return m_data;
    }

    NextConstView begin() const noexcept requires(depth > 1) {
        return NextConstView(m_data, m_dimensions + 1, m_accum_dim + 1);
    }

    const T* begin() const noexcept requires(depth == 1) {
        return m_data;
    }

    NextView end() noexcept requires(depth > 1) {
        return NextView(m_data + m_dimensions[1] * m_accum_dim[1], m_dimensions + 1, m_accum_dim + 1);
    }
    NextConstView end() const noexcept requires(depth > 1) {
        return NextConstView(m_data + m_dimensions[1] * m_accum_dim[1], m_dimensions + 1, m_accum_dim + 1);
    }
    T* end() noexcept requires(depth == 1) {
        return m_data + m_dimensions[1] * m_accum_dim[1];
    }
    const T* end() const noexcept requires(depth == 1) {
        return m_data + m_dimensions[1] * m_accum_dim[1];
    }

    NextConstView cbegin() const noexcept requires(depth > 1) {
        return NextConstView(m_data, m_dimensions + 1, m_accum_dim + 1);
    }
    const T* cbegin() const noexcept requires(depth == 1) {
        return m_data;
    }

    NextConstView cend() const noexcept requires(depth > 1) {
        return NextConstView(m_data + m_dimensions[1] * m_accum_dim[1], m_dimensions + 1, m_accum_dim + 1);
    }
    const T* cend() const noexcept requires(depth == 1) {
        return m_data + m_dimensions[1] * m_accum_dim[1];
    }


    ThisView reshape(const std::array<size_type, depth> sizes) {
        for (size_t i = 0; i < depth; i++) {
            m_dimensions[i] = sizes[i];
        }
        fill_accum_dim();
    }

    void swap(AbstractNDArray &other) {
        std::swap(m_data, other.m_data);
        std::swap(m_dimensions, other.m_dimensions);
        std::swap(m_accum_dim, other.m_accum_dim);
        std::swap(m_total_size, other.m_total_size);
    }

    bool empty() {
        return begin() == end();
    }

    bool operator==(const AbstractNDArray& other) const {
        return is_equal(other);
    }

    bool operator!=(const AbstractNDArray& other) const {
        return !(*this == other);
    }

    // for tests

    pointer data() noexcept {return m_data;}
    const value_type* data() const noexcept {return m_data;}

protected:
    pointer m_data;
    size_type* m_dimensions;
    size_type m_total_size;
    size_type* m_accum_dim;

    AbstractNDArray(pointer data = nullptr, size_type* dimensions = nullptr, size_type* accum_dim = nullptr) :
    m_data(data), m_dimensions(dimensions), m_accum_dim(accum_dim) {}

    void fill_accum_dim() {
        m_accum_dim = new size_type[depth + 1];
        m_accum_dim[depth] = 1;
        if (depth >= 2) {
            for (size_type i = depth; i > 0; i--) {
                m_accum_dim[i - 1] = m_dimensions[i] * m_accum_dim[i];
            }
        }
    }

    size_type calc_offset(const size_type index) const {
        return index * m_accum_dim[1];
    }
};

template <typename T, size_t depth, bool type>
class AbstractView : public AbstractNDArray<std::conditional_t<type, const T, T>, depth, type> {
public:
    using abt = AbstractNDArray<std::conditional_t<type, const T, T>, depth, type>;
    using typename abt::size_type;
    using typename abt::value_type;
    using typename abt::reference;
    using typename abt::pointer;
    using typename abt::difference_type;
    using typename abt::ThisView;
    // using typename abt::NextView;
    using abt::m_data;
    using abt::m_dimensions;
    using abt::m_accum_dim;
    using abt::m_total_size;
    using abt::begin;
    using abt::end;

    using iterator_category = std::random_access_iterator_tag;

    AbstractView(pointer data, size_type* dimensions, size_type* accum_dim)
    : abt(data, dimensions, accum_dim) {}

    AbstractView(const AbstractView& other)
    : AbstractView(other.m_data, other.m_dimensions, other.m_accum_dim) {}

    AbstractView(AbstractView&& other) noexcept { abt::move(other); }

    AbstractView(NDArray<T, depth>& impr)
    : AbstractView<T, depth, type>(impr.m_data, impr.m_dimensions, impr.m_accum_dim) {}

    AbstractView(AbstractView<T, depth - 1, type>& start, AbstractView<T, depth - 1, type>& end) {
        size_type wide_dim = (end.m_data - start.m_data) / start.m_accum_dim[0];
        m_data = start.m_data;
        m_dimensions = start.m_dimensions - 1;
        m_accum_dim = start.m_accum_dim - 1;
        m_total_size = wide_dim * start.m_total_size;
    }

    ~AbstractView() = default;

    AbstractView& operator=(const AbstractView& other) {
        if (this != other) {
            AbstractView tmp(other);
            tmp.swap(*this);
        }
        return *this;
    }

    AbstractView& operator=(AbstractView&& other) noexcept {
        abt::swap(other);
        return *this;
    }

    reference operator*() const requires(depth == 0) {
        return *m_data;
    }

    ThisView operator*() const requires(depth > 0) {
        return ThisView(m_data, m_dimensions, m_accum_dim);
    }

    pointer operator->() const { return m_data;}

    // walking
    AbstractView& operator++() { m_data += m_accum_dim[0]; return *this; }
    AbstractView operator++(int) { AbstractView tmp(*this); ++(*this); return tmp; }
    AbstractView& operator--() { m_data -= m_accum_dim[0]; return *this; }
    AbstractView operator--(int) { AbstractView tmp(*this); --(*this); return tmp; }
    AbstractView operator+(difference_type tar) const { return This(m_data + abt::calc_offset(tar),
        m_dimensions, m_accum_dim); }
    AbstractView operator-(difference_type tar) const { return This(m_data - abt::calc_offset(tar),
        m_dimensions, m_accum_dim); }
    difference_type operator-(const ThisView& other) const { return (m_data - other.m_data) / m_dimensions[0]; }
    AbstractView& operator+=(difference_type delt) { m_data += abt::calc_offset(delt); return *this; }
    AbstractView& operator-=(difference_type delt) { m_data -= abt::calc_offset(delt); return *this; }

    // comparing
    bool operator< (const ThisView& rhs) const { return m_data < rhs.m_data; }
    bool operator> (const ThisView& rhs) const { return m_data > rhs.m_data; }
    bool operator<=(const ThisView& rhs) const { return m_data <= rhs.m_data; }
    bool operator>=(const ThisView& rhs) const { return m_data >= rhs.m_data; }

};

template <typename T, size_t depth>
class NDArray : public AbstractNDArray<T, depth, false> {
public:
    using abstract = AbstractNDArray<T, depth, false>;
    using typename abstract::size_type;
    using typename abstract::value_type;
    using typename abstract::reference;
    using typename abstract::pointer;
    using typename abstract::difference_type;
    using abstract::m_data;
    using abstract::m_dimensions;
    using abstract::m_total_size;
    using abstract::m_accum_dim;

    using iterator = NDArrayView<T, depth>;
    using const_iterator = NDArrayConstView<T, depth>;

    NDArray(const NDArray& other) {
        m_total_size = other.m_total_size;
        m_dimensions = new size_t[depth + 1];
        m_accum_dim = new size_t[depth + 1];
        for (size_t i = 0; i < depth + 1; ++i) {
            m_dimensions[i] = other.m_dimensions[i];
            m_accum_dim[i] = other.m_accum_dim[i];
        }

        m_data = static_cast<T*>(operator new(sizeof(T) * m_total_size));
        size_t i = 0;
        try {
            for (; i < m_total_size; ++i) {
                new (static_cast<void*>(m_data + i)) T(other.m_data[i]);
            }
        } catch (...) {
            for (size_t j = 0; j < i; j++) {
                m_data[j].~T();
            }
            operator delete(static_cast<void*>(m_data));
            delete[] m_dimensions;
            delete[] m_accum_dim;
            throw;
        }
    }

    NDArray(NDArray&& other) noexcept {
        abstract::move(other);
    }

    NDArray() {
        m_data = nullptr;
        m_dimensions = new size_t[2]{0, 0};
        m_accum_dim = new size_t[2]{0, 0};
        m_total_size = 0;
    }

    template <size_t N, typename U>
    struct mode_init {
        using type = std::initializer_list<typename mode_init<N - 1, U>::type>;
    };

    template <typename U>
    struct mode_init<0, U> {
        using type = U;
    };

    template <typename U>
    struct mode_init<1, U> {
        using type = std::initializer_list<U>;
    };

    NDArray(typename mode_init<depth, T>::type init) requires (depth > 0) {
        m_dimensions = new size_type[depth + 1];
        m_total_size = 1;
        for (size_t i = 0; i < depth + 1; i++) {
            m_dimensions[i] = 0;
        }
        count_dims<depth>(init);
        abstract::fill_accum_dim();
        for (size_t i = 1; i < depth + 1; i++) {
            m_total_size *= m_dimensions[i];
        }
        m_data = static_cast<T*>(operator new(sizeof(T) * m_total_size));
        T* it = m_data;
        fill_data<depth>(init, it);
    }

    NDArray(const T& scalar) requires (depth == 0) {
        m_total_size = 1;
        m_dimensions = new size_type[1]{1};
        m_accum_dim = new size_type[1]{1};
        m_data = static_cast<T*>(operator new(sizeof(T)));
        new (static_cast<void*>(m_data)) T(scalar);
    }

    // NDArray(const std::array<size_type, depth>& st_dims, T number = NULL) {
    //     stock_init(st_dims);
    //     m_data = static_cast<T*>(::operator new(sizeof(T) * m_total_size));
    //     for (size_t i = 0; i < m_total_size; i++) {
    //         m_data[i] = number;
    //     }
    // }
    //
    // NDArray(const std::array<size_type, depth>& st_dims, T* ptr) {
    //     stock_init(st_dims);
    //     m_data = static_cast<T*>(::operator new(sizeof(T) * m_total_size));
    //     for (size_t i = 0; i < m_total_size; i++) {
    //         m_data[i] = ptr[i];
    //     }
    // }

    template <typename... dims_type>
    NDArray(dims_type... dims) {
        auto tuple = std::make_tuple(dims...);
        m_dimensions = new size_type[depth + 1];
        m_total_size = 1;
        m_dimensions[0] = 0;

        fill_dimensions(tuple, std::make_index_sequence<depth>{});

        abstract::fill_accum_dim();
        m_data = static_cast<T*>(operator new(sizeof(T) * m_total_size));
        if constexpr (sizeof...(dims_type) == depth + 1) {
            using last_type = typename std::tuple_element_t<depth, decltype(tuple)>;
            if constexpr (std::is_convertible_v<last_type, T>) {
                // fill var
                T last = static_cast<T>(std::get<depth>(tuple));
                for (size_t i = 0; i < m_total_size; i++) {
                    new (static_cast<void*>(m_data + i)) T(last);
                }
            } else {
                // fill by *
                T* ptr = static_cast<T*>(std::get<depth>(tuple));
                for (size_t i = 0; i < m_total_size; i++) {
                    new (static_cast<void*>(m_data + i)) T(ptr[i]);
                }
            }
        } else {
            // fill 0
            for (size_t i = 0; i < m_total_size; i++) {
                new (static_cast<void*>(m_data + i)) T();
            }
        }
    }

    ~NDArray() {
        for (size_t i = 0; i < m_total_size; ++i) {
            m_data[i].~T();
        }
        operator delete(static_cast<void*>(m_data));
        delete[] m_dimensions;
        delete[] m_accum_dim;
    }

    NDArray& operator=(const NDArray& other) {
        if (this != other) {
            NDArray tmp(other);
            tmp.swap(*this);
        }
        return *this;
    }

    NDArray& operator=(NDArray&& other) noexcept {
        if (*this != other) {
            abstract::swap(other);
            other.m_data = nullptr;
            other.m_dimensions = nullptr;
            other.m_accum_dim = nullptr;
            other.m_total_size = 0;
        }
        return *this;
    }

private:
    template <typename tuple, std::size_t... i>
    void fill_dimensions(const tuple& in_tup, std::index_sequence<i...>) {
        ((
            m_dimensions[i + 1] = static_cast<size_type>(std::get<i>(in_tup)),
            m_total_size *= m_dimensions[i + 1]
            ), ...);
    }

    void stock_init(const std::array<size_type, depth>& st_dims) {
        m_dimensions = new size_type[depth + 1];
        m_total_size = 1;
        size_type i = 1;
        for (auto cur : st_dims) {
            m_dimensions[i] = cur;
            i++;
        }
        abstract::fill_accum_dim();
        for (size_t i = 1; i < depth + 1; i++) {
            m_total_size *= m_dimensions[i];
        }
    }

    template <size_type cur_depth>
    void count_dims(typename mode_init<cur_depth, T>::type& init) {
        const size_type level = depth - cur_depth + 1;
        m_dimensions[level] = std::max(m_dimensions[level], init.size());
        if constexpr (cur_depth > 1) {
            for (auto cur_init : init) {
                count_dims<cur_depth - 1>(cur_init);
            }
        }
    }

    template <size_type cur_depth>
    void fill_data(typename mode_init<cur_depth, T>::type& init, T*& it) {
        if constexpr (cur_depth > 1) {
            for (auto cur_init : init) {
                T* old = it;
                const size_type level = depth - cur_depth + 1;
                fill_data<cur_depth - 1>(cur_init, it);

                const size_type shortage = m_accum_dim[level] - (it - old);
                for (size_type i = 0; i < shortage; i++) {
                    new (static_cast<void*>(it)) T();
                    ++it;
                }
            }
        } else {
            for (const T& cur : init) {
                try {
                    new (static_cast<void*>(it)) T(cur);
                    it++;
                } catch (...) {
                    for (T* p = m_data; p < it; p++)
                        p->~T();
                    throw;
                }
            }
        }
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
    std::cout << "at: "  << arr_sq2D.at({1,1});
    std::cout << "\n\n";

    std::cout << "View test\n";
    NDArray<int, 1> arr_for_view{1, 2};
    NDArrayView<int, 1> view(arr_for_view);
    for (auto i : view) {
        std::cout << i << " ";
    }
    std::cout << "\ncout: " << view.count() << " total: " << view.total_count() << " dim: " << view.dim() << "\n";
    std::cout << "at: "  << view.at({1}) << "\n";
    std::cout << "\n\n";


    NDArray< int, 1 > empty1d{};

    NDArray< int, 2 > src{ { 1, 2 }, { 3, 4 } };
    int* oldPtr = src.data();

    NDArray< int, 2 > dst{ { 1, 1 }, { 1, 1 } };
    std::cout << "just before move\n";
    dst.move(src);

    dst.data()[0];
    dst.data()[3];
    src.data();

    std::cout << "copy assigment test\n";
    ThrowOnCopy::reset();
    NDArray< ThrowOnCopy, 1 > good{ ThrowOnCopy(4), ThrowOnCopy(2) };
    std::cout << "\n\n";

    std::cout << "casual view\n";
    NDArray<int, 1> src1{1, 2};
    // auto view_1 = AbstractView<int, 1, false>(src1);
    auto view_1 = NDArrayView<int, 1>(src1);
    for (auto i : view_1) {
        std::cout << i << " ";
    }
    std::cout << "\n";
    NDArray<int, 2> src2{{1,2},{3,4}};
    auto view_2 = AbstractView<int, 2, false>(src2);
    for (auto i : view_2) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n\n";

    std::cout << "2 view -> 1 big view\n";
    NDArray<int, 2> big{{1,2},{3,4}};
    auto small_1 = big.begin();
    auto small_2 = big.end();
    auto rest_big = AbstractView<int, 2, false>(small_1, small_2);
    for (auto i : rest_big) {
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n\n";

    std::cout << "constructor for dims\n";
    NDArray<int, 2> dims_only(2, 3);
    NDArray<int, 2> dims_val(2, 3, 7);
    NDArray<int, 2> dims_ptr(2, 3, dims_val.m_data);
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


    return 0;
}


