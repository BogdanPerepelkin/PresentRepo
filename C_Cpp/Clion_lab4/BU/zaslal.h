// kill me maybe

#ifndef NDARRAY_H
#define NDARRAY_H

#include <execution>

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

    void move(AbstractNDArray &other) noexcept {
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

    T& at(const difference_type i) noexcept requires (depth == 1) {
        if (i < 0 && i > m_dimensions[1]) {
            throw std::out_of_range("Error at: index out of range");
        }
        return m_data[i];
    }

    const T& at(const difference_type i) const noexcept requires (depth == 1) {
        if (i < 0 && i > m_dimensions[1]) {
            throw std::out_of_range("Error at: index out of range");
        }
        return m_data[i];
    }

    NextView at(const difference_type i) noexcept requires (depth > 1) {
        if (i < 0 && i > m_dimensions[1]) {
            throw std::out_of_range("Error at: index out of range");
        }
        return NextView(m_data + calc_offset(i), m_dimensions + 1, m_accum_dim + 1);
    }

    NextConstView at(const difference_type i) const noexcept requires (depth > 1) {
        if (i < 0 && i > m_dimensions[1]) {
            throw std::out_of_range("Error at: index out of range");
        }
        return NextConstView(m_data + calc_offset(i), m_dimensions, m_accum_dim + 1);
    }

    bool is_equal(const AbstractNDArray<T, depth, type>& rhs) const {
        for (size_t i = 0; i < depth + 1; i++) {
            if (m_dimensions[i] != rhs.m_dimensions[i]) { return false; }
        }
        const size_t size = m_dimensions[1] * m_accum_dim[1];
        for (size_t i = 0; i < size; i++) {
            if (m_data[i] != rhs.m_data[i]) { return false; }
        }
        return true;
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

    template <typename ... Args>
    ThisView reshape(Args... args) {
        size_type size = sizeof...(args);
        size_type sizes[size] = {args...};
        for (size_t i = 1; i < depth + 1; i++) {
            m_dimensions[i] = sizes[i - 1];
        }
        fill_accum_dim();
        return ThisView(m_data, m_dimensions, m_accum_dim);
    }

    void swap(AbstractNDArray &other) noexcept {
        std::swap(m_data, other.m_data);
        std::swap(m_dimensions, other.m_dimensions);
        std::swap(m_accum_dim, other.m_accum_dim);
        std::swap(m_total_size, other.m_total_size);
    }

    bool empty() {
        return begin() == end();
    }

    bool operator==(const AbstractNDArray& rhs) const {
        if (m_data == rhs.m_data
            && m_dimensions == rhs.m_dimensions
            && m_accum_dim == rhs.m_accum_dim
            && m_dimensions[1] == rhs.m_dimensions[1]
            && m_accum_dim[1] == rhs.m_accum_dim[1]) {
            return true;
            }
        return false;
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

    size_type calc_offset(const size_type index) const {
        return index * m_accum_dim[1];
    }

private:
    void fill_accum_dim() {
        m_accum_dim = new size_type[depth + 1];
        m_accum_dim[depth] = 1;
        if (depth >= 2) {
            for (size_type i = depth; i > 0; i--) {
                m_accum_dim[i - 1] = m_dimensions[i] * m_accum_dim[i];
            }
        }
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
    using abt::m_data;
    using abt::m_dimensions;
    using abt::m_accum_dim;
    using abt::m_total_size;
    using abt::begin;
    using abt::end;

    using iterator_category = std::random_access_iterator_tag;

    AbstractView(pointer data, size_type* dimensions, size_type* accum_dim) noexcept
    : abt(data, dimensions, accum_dim) {}

    AbstractView(const AbstractView& other) noexcept
    : AbstractView(other.m_data, other.m_dimensions, other.m_accum_dim) {}

    AbstractView(AbstractView&& other) noexcept { abt::move(other); }

    AbstractView(NDArray<T, depth>& impr) noexcept
    : AbstractView<T, depth, type>(impr.m_data, impr.m_dimensions, impr.m_accum_dim) {}

    AbstractView(AbstractView<T, depth - 1, type>& start, AbstractView<T, depth - 1, type>& end) noexcept {
        size_type wide_dim = (end.m_data - start.m_data) / start.m_accum_dim[0];
        m_data = start.m_data;
        m_dimensions = start.m_dimensions - 1;
        m_accum_dim = start.m_accum_dim - 1;
        m_total_size = wide_dim * start.m_total_size;
    }

    ~AbstractView() noexcept = default;

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
        std::unique_ptr<size_t[]> tmp_dims(new size_t[depth + 1]);
        std::unique_ptr<size_t[]> tmp_accum(new size_t[depth + 1]);
        for (size_t i = 0; i < depth + 1; ++i) {
            tmp_dims[i] = other.m_dimensions[i];
            tmp_accum[i] = other.m_accum_dim[i];
        }
        std::unique_ptr<T[], void(*)(void*)> tmp_data(
            static_cast<T*>(::operator new(sizeof(T) * m_total_size)),
            &operator delete
        );
        size_t i = 0;
        try {
            for (; i < m_total_size; ++i) {
                new (static_cast<void*>(tmp_data.get() + i)) T(other.m_data[i]);
            }
        } catch (...) {
            for (size_t j = 0; j < i; ++j) {
                tmp_data[j].~T();
            }
            throw;
        }
        m_dimensions = tmp_dims.release();
        m_accum_dim = tmp_accum.release();
        m_data = tmp_data.release();
    }

    NDArray(NDArray&& other) noexcept {
        abstract::move(other);
    }

    NDArray() {
        std::unique_ptr<size_type[]> tmp_dims(new size_type[2]{0, 0});
        std::unique_ptr<size_type[]> tmp_accum(new size_type[2]{0, 0});
        m_total_size = 0;
        m_data = nullptr;
        m_dimensions = tmp_dims.release();
        m_accum_dim = tmp_accum.release();
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
        std::unique_ptr<size_type[]> tmp_dims(new size_type[depth + 1]{});
        m_total_size = 1;
        count_dims<depth>(init, tmp_dims.get());

        std::unique_ptr<size_type[]> tmp_accum(new size_type[depth + 1]);
        tmp_accum[depth] = 1;
        for (size_t i = depth; i > 0; i--) {
            tmp_accum[i - 1] = tmp_dims[i] * tmp_accum[i];
        }

        for (size_t i = 1; i < depth + 1; i++) {
            m_total_size *= tmp_dims[i];
        }

        std::unique_ptr<T[], void(*)(void*)> tmp_data(
            static_cast<T*>(operator new(sizeof(T) * m_total_size)),
            &operator delete);

        T* it = tmp_data.get();
        fill_data<depth>(init, it, tmp_accum.get());

        m_data = tmp_data.release();
        m_dimensions = tmp_dims.release();
        m_accum_dim = tmp_accum.release();
    }

    NDArray(const T& scalar) requires (depth == 0) {
        m_total_size = 1;
        std::unique_ptr<size_type[]> tmp_dims(new size_type[1]{1});
        std::unique_ptr<size_type[]> tmp_accum(new size_type[1]{1});
        std::unique_ptr<T[], void(*)(void*)> tmp_data(
            static_cast<T*>(operator new(sizeof(T))),
            &operator delete);
        new (static_cast<void*>(tmp_data.get())) T(scalar);
        m_data = tmp_data.release();
        m_dimensions = tmp_dims.release();
        m_accum_dim = tmp_accum.release();
    }

    template <typename... dims_type>
    NDArray(dims_type... dims) {
        auto tuple = std::make_tuple(dims...);
        std::unique_ptr<size_type[]> tmp_dims(new size_type[depth + 1]);
        tmp_dims[0] = 0;
        m_total_size = 1;
        fill_dimensions(tuple, std::make_index_sequence<depth>{}, tmp_dims.get());

        std::unique_ptr<size_type[]> tmp_accum;
        tmp_accum.reset(new size_type[depth + 1]);
        tmp_accum[depth] = 1;
        for (size_type i = depth; i > 0; i--) {
            tmp_accum[i - 1] = tmp_dims[i] * tmp_accum[i];
        }

        std::unique_ptr<T[], void(*)(void*)> tmp_data(
            static_cast<T*>(::operator new(sizeof(T) * m_total_size)),
            &::operator delete);

        if constexpr (sizeof...(dims_type) == depth + 1) {
            using last_type = typename std::tuple_element_t<depth, decltype(tuple)>;
            if constexpr (std::is_convertible_v<last_type, T>) {
                T val = static_cast<T>(std::get<depth>(tuple));
                for (size_t i = 0; i < m_total_size; i++)
                    new (static_cast<void*>(tmp_data.get() + i)) T(val);
            } else {
                T* ptr = static_cast<T*>(std::get<depth>(tuple));
                for (size_t i = 0; i < m_total_size; i++)
                    new (static_cast<void*>(tmp_data.get() + i)) T(ptr[i]);
            }
        } else {
            for (size_t i = 0; i < m_total_size; i++)
                new (static_cast<void*>(tmp_data.get() + i)) T();
        }

        m_dimensions = tmp_dims.release();
        m_accum_dim = tmp_accum.release();
        m_data = tmp_data.release();
    }

    ~NDArray() noexcept {
        clear();
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
    template <typename tuple, std::size_t... i>
    void fill_dimensions(const tuple& in_tup, std::index_sequence<i...>, size_type* tmp_dims) {
        ((
            tmp_dims[i + 1] = static_cast<size_type>(std::get<i>(in_tup)),
            m_total_size *= tmp_dims[i + 1]
        ), ...);
    }

    template <size_type cur_depth>
    void count_dims(const typename mode_init<cur_depth, T>::type& init, size_type* tmp_dims) {
        const size_type level = depth - cur_depth + 1;
        tmp_dims[level] = std::max(tmp_dims[level], init.size());
        if constexpr (cur_depth > 1) {
            for (const auto& cur_init : init) {
                count_dims<cur_depth - 1>(cur_init, tmp_dims);
            }
        }
    }

    template <size_type cur_depth>
    void fill_data(typename mode_init<cur_depth, T>::type& init, T*& it, size_type* tmp_accum) {
        if constexpr (cur_depth > 1) {
            for (auto cur_init : init) {
                T* old = it;
                const size_type level = depth - cur_depth + 1;
                fill_data<cur_depth - 1>(cur_init, it, tmp_accum);

                const size_type shortage = tmp_accum[level] - (it - old);
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

    void clear() {
        for (size_t i = 0; i < m_total_size; ++i) {
            m_data[i].~T();
        }
        operator delete(static_cast<void*>(m_data));
        delete[] m_dimensions;
        delete[] m_accum_dim;
    }
};

#endif /* NDARRAY_H */



