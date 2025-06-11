// kill me maybe

// PLAN:


#include <array>
#include <iostream>
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

    using iterator_category = std::random_access_iterator_tag;

    AbstractView(pointer data = nullptr, size_type *dimensions = nullptr,
                    size_type *accum_dim = nullptr) : m_data(data), m_dimensions(dimensions), m_accum_dim(accum_dim) {
    }
    // AbstractView(pointer data, size_type *dimensions, size_type *accum_dim) : abt(data, dimensions, accum_dim) {
    // }

    AbstractView(const AbstractView &other) : AbstractView(other.m_data, other.m_dimensions, other.m_accum_dim) {
    }

    AbstractView(AbstractView &&other) noexcept { move(other); }

    AbstractView(NDArray<T, depth> &impr) : AbstractView<T, depth>(
        impr.m_data, impr.m_dimensions, impr.m_accum_dim) {
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
    size_type total_count() const { return m_dimensions[1] * m_accum_dim[1]; }
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

    T &at(const std::initializer_list<size_t> indexes) const {
        size_t offset = 0;
        size_t i = 1;
        for (auto cur: indexes) {
            offset += cur * m_accum_dim[i];
            i++;
        }
        return m_data[offset];
    }

    bool is_equal(const AbstractView<T, depth> &rhs) const noexcept {
        if (m_data == rhs.m_data && m_dimensions == rhs.m_dimensions && m_accum_dim == rhs.m_accum_dim &&
            m_dimensions[1] == rhs.m_dimensions[1] && m_accum_dim[1] == rhs.m_accum_dim[1]) {
            return true;
        }
        return false;
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
        for (auto size : sizes) {
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
        std::swap(m_data, other.m_data);
        std::swap(m_dimensions, other.m_dimensions);
        std::swap(m_accum_dim, other.m_accum_dim);
        std::swap(m_total_size, other.m_total_size);
    }

    bool empty() noexcept { return begin() == end(); }

    // comparing
    std::strong_ordering operator<=>(const AbstractView &rhs) const noexcept {
        return m_data <=> rhs.m_data;
    }

    bool operator==(const AbstractView &rhs) const noexcept {
        return is_equal(rhs);
    }

    // for tests
    pointer data() noexcept { return m_data; }
    const_pointer data() const noexcept { return m_data; }
    size_type* dimensions() const noexcept { return m_dimensions; }
    size_type total_size() const noexcept { return m_total_size; }
    size_type* accum_dim() const noexcept { return m_accum_dim; }

protected:
    pointer m_data;
    size_type *m_dimensions;
    size_type m_total_size;
    size_type *m_accum_dim;

    // AbstractView(pointer data = nullptr, size_type *dimensions = nullptr,
                    // size_type *accum_dim = nullptr) : m_data(data), m_dimensions(dimensions), m_accum_dim(accum_dim) {
    // }

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
    using abstract::m_accum_dim;
    using abstract::m_data;
    using abstract::m_dimensions;
    using abstract::m_total_size;
    using typename abstract::difference_type;
    using typename abstract::pointer;
    using typename abstract::reference;
    using typename abstract::size_type;
    using typename abstract::value_type;

    using iterator = NDArrayView<T, depth>;
    using const_iterator = NDArrayConstView<T, depth>;

    NDArray(const NDArray &other) {
        m_total_size = other.m_total_size;

        m_store_dims = other.m_store_dims;
        m_dimensions = m_store_dims.data();

        m_store_accum = other.m_store_accum;
        m_accum_dim = m_store_accum.data();

        m_data = static_cast<T *>(operator new(sizeof(T) * m_total_size));
        size_t i = 0;
        try {
            for (; i < m_total_size; ++i) {
                new(static_cast<void *>(m_data + i)) T(other.m_data[i]);
            }
        } catch (...) {
            for (size_t j = i; j > 0; j--) {
                m_data[j - 1].~T();
            }
            operator delete(static_cast<void *>(m_data));
            throw;
        }
    }

    NDArray(NDArray &&other) noexcept { abstract::move(other); }

    NDArray() {
        m_data = nullptr;

        m_store_dims = {0, 0};
        m_dimensions = m_store_dims.data();

        m_store_accum = {0, 0};
        m_accum_dim = m_store_accum.data();

        m_total_size = 0;
    }

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

    NDArray(typename mode_init<depth, T>::type init)
        requires(depth > 0) {
        m_store_dims = std::array<size_t, depth + 1>();
        m_dimensions = m_store_dims.data();
        m_total_size = 1;
        for (size_t i = 0; i < depth + 1; i++) {
            m_dimensions[i] = 0;
        }
        count_dims<depth>(init);

        m_store_accum = std::array<size_t, depth + 1>();
        m_accum_dim = m_store_accum.data();
        abstract::fill_accum_dim();
        for (size_t i = 1; i < depth + 1; i++) {
            m_total_size *= m_dimensions[i];
        }

        m_data = static_cast<T *>(operator new(sizeof(T) * m_total_size));
        T *it = m_data;
        fill_data<depth>(init, it);
    }

    NDArray(const T &scalar)
        requires(depth == 0) {
        m_total_size = 1;

        m_store_dims = std::array<size_type, 1>{1};
        m_dimensions = m_store_dims.data();

        m_store_accum = std::array<size_type, 1>{1};
        m_accum_dim = m_store_accum.data();

        m_data = static_cast<T *>(operator new(sizeof(T)));
        new(static_cast<void *>(m_data)) T(scalar);
    }

    template<typename SizeList>
    NDArray(const SizeList &list, const T &value = NULL) {
        m_store_dims = std::array<size_t, depth + 1>();
        m_dimensions = m_store_dims.data();

        m_total_size = 1;
        m_dimensions[0] = 0;

        size_t i = 1;
        for (auto elem : list) {
            m_dimensions[i] = elem;
            m_total_size *= m_dimensions[i];
            i++;
        }

        m_store_accum = std::array<size_type, depth + 1>();
        m_accum_dim = m_store_accum.data();
        abstract::fill_accum_dim();

        m_data = static_cast<T *>(operator new(sizeof(T) * m_total_size));
        if (value) {
            for (size_t i = 0; i < m_total_size; i++) {
                new(static_cast<void *>(m_data + i)) T(value);
            }
        } else {
            for (size_t i = 0; i < m_total_size; i++) {
                new(static_cast<void *>(m_data + i)) T();
            }
        }
    }

    ~NDArray() { clear(); }

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
            abstract::swap(other);
            other.m_data = nullptr;
            other.m_dimensions = nullptr;
            other.m_accum_dim = nullptr;
            other.m_total_size = 0;
        }
        return *this;
    }

private:
    std::array<size_type, depth + 1> m_store_dims;
    std::array<size_type, depth + 1> m_store_accum;

    template<typename tuple, std::size_t... i>
    void fill_dimensions(const tuple &in_tup, std::index_sequence<i...>) {
        ((m_dimensions[i + 1] = static_cast<size_type>(std::get<i>(in_tup)), m_total_size *= m_dimensions[i + 1]), ...);
    }

    template<size_type cur_depth>
    void count_dims(typename mode_init<cur_depth, T>::type &init) {
        const size_type level = depth - cur_depth + 1;
        m_dimensions[level] = std::max(m_dimensions[level], init.size());
        if constexpr (cur_depth > 1) {
            for (auto cur_init: init) {
                count_dims<cur_depth - 1>(cur_init);
            }
        }
    }

    template<size_type cur_depth>
    void fill_data(typename mode_init<cur_depth, T>::type &init, T *&it) {
        if constexpr (cur_depth > 1) {
            for (auto cur_init: init) {
                T *old = it;
                const size_type level = depth - cur_depth + 1;
                fill_data<cur_depth - 1>(cur_init, it);

                const size_type shortage = m_accum_dim[level] - (it - old);
                for (size_type i = 0; i < shortage; i++) {
                    new(static_cast<void *>(it)) T();
                    ++it;
                }
            }
        } else {
            for (const T &cur: init) {
                try {
                    new(static_cast<void *>(it)) T(cur);
                    it++;
                } catch (...) {
                    for (T *p = m_data; p < it; p++)
                        p->~T();
                    throw;
                }
            }
        }
    }

    void clear() {
        for (size_t i = 0; i < m_total_size; ++i) {
            m_data[i].~T();
        }
        operator delete(static_cast<void *>(m_data));
    }
};