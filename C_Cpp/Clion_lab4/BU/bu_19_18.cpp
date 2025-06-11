// i am in process

// сейчас надо придумать как сделать begin, чтобы он падал по размерностям, но
// при этом не поехала логика с dimensions и accum. Так же формула для end - dim[0] * accum[0]

// как создавать View (это же можно делать и из Array). Если честно, мне не нравится эта зтея,
// хочу логично брать верх массивов.

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

    using view = NDArrayView<T, depth>;
    using const_view = NDArrayConstView<T, depth>;

    using ThisView = AbstractView<T, depth, type>;
    using NextView = AbstractView<T, depth - 1, type>;

    void swap(AbstractNDArray &other) {
        std::swap(m_data, other.m_data);
        std::swap(m_dimensions, other.m_dimensions);
        std::swap(m_accum_dim, other.m_accum_dim);
        std::swap(m_total_size, other.m_total_size);
    }

    void move(AbstractNDArray &other) {
        m_data = std::exchange(other.m_data, nullptr);
        m_dimensions = std::exchange(other.m_dimensions, nullptr);
        m_accum_dim = std::exchange(other.m_accum_dim, nullptr);
        m_total_size = other.m_total_size;
    }

    NextView begin() noexcept requires(depth > 0) {
        return NextView(m_data, m_dimensions + 1, m_accum_dim + 1);
    }
    T* begin() noexcept requires(depth == 0) {
        return m_data;
    }
    NextView end() noexcept requires(depth > 0) {
        return NextView(m_data + m_dimensions[1] * m_accum_dim[1], m_dimensions + 1, m_accum_dim + 1);
    }
    T* end() noexcept requires(depth == 0) {
        return m_data + m_dimensions[0] * m_accum_dim[0];
    }

    const_view cbegin() const noexcept { return begin(); }
    const_view cend() const noexcept { return end(); }

    T& operator[](difference_type tar) const requires (depth == 1) {
        return m_data[tar];
    }

    NextView operator[](difference_type tar) const requires (depth > 1) {
        size_type offset = calc_offset(tar);
        return Next(m_data + offset, m_dimensions + 1, m_accum_dim + 1);
    }

    size_type calc_offset(size_t index) const {
        return index * m_accum_dim[0];
    }

    bool empty() {
        return begin() == end();
    }

    bool operator==(const AbstractNDArray& other) const {
        return m_data == other.m_data && m_dimensions == other.m_dimensions;
    }

    bool operator!=(const AbstractNDArray& other) const {
        return !(*this == other);
    }

protected:
    T* m_data;
    size_type* m_dimensions;
    size_t m_total_size;
    size_type* m_accum_dim;

    AbstractNDArray(T* data = nullptr, size_t* dimensions = nullptr, size_t* accum_dim = nullptr) :
    m_data(data), m_dimensions(dimensions), m_accum_dim(accum_dim) {}
};

// template <typename T, size_t depth>
// class NDArrayView;

// template <typename T, size_t depth>
// class NDArrayConstView;

template <typename T, size_t depth, bool type>
class AbstractView : public AbstractNDArray<std::conditional_t<type, const T, T>, depth, type> {
public:
    using iterator_category = std::random_access_iterator_tag;

    using abstract = AbstractNDArray<std::conditional_t<type, const T, T>, depth, type>;
    using abstract::m_data;
    using abstract::m_dimensions;
    using abstract::m_accum_dim;
    using abstract::m_total_size;
    using typename abstract::size_type;
    using typename abstract::value_type;
    using typename abstract::reference;
    using typename abstract::pointer;
    using typename abstract::difference_type;

    // using This = AbstractView<T, depth, type>;
    // using Next = AbstractView<T, depth - 1, type>;
    using typename abstract::ThisView;
    using typename abstract::NextView;
    using abstract::begin;
    using abstract::end;

    AbstractView(pointer data, size_type* dimensions, size_type* accum_dim) :
    abstract(data, dimensions, accum_dim) {}

    AbstractView(const AbstractView& other)
    : AbstractView(other.m_data, other.m_dimensions, other.m_accum_dim) {}

    AbstractView(AbstractView&& other) {
        abstract::move(other);
    }

    AbstractView(NDArray<T, depth>& impr)
    : AbstractView<T, depth, type>(impr.m_data, impr.m_dimensions, impr.m_accum_dim) {}

    ~AbstractView() = default;

    AbstractView& operator=(const AbstractView& other) {
        if (this != other) {
            AbstractView tmp(other);
            tmp.swap(*this);
        }
        return *this;
    }

    AbstractView& operator=(AbstractView&& other) noexcept {
        abstract::swap(other);
        return *this;
    }

    reference operator*() const requires(depth == 0) {
        return *m_data;
    }

    ThisView operator*() const requires(depth > 0) {
        // return NextView{ this->m_data,
                         // this->m_dimensions + 1,
                         // this->m_accum_dim  + 1 };
        return ThisView(m_data, m_dimensions, m_accum_dim);
    }

    // T& operator[](difference_type tar) const requires (depth == 1) {
    //     return m_data[tar];
    // }
    // NextView operator[](difference_type tar) const requires (depth > 1) {
    //     size_type offset = calc_offset(tar);
    //     return Next(m_data + offset, m_dimensions + 1, m_accum_dim + 1);
    // }

    pointer operator->() const { return m_data;}

    // walking
    AbstractView& operator++() { m_data += m_accum_dim[0]; return *this; }
    AbstractView operator++(int) { AbstractView tmp(*this); ++(*this); return tmp; }
    AbstractView& operator--() { m_data -= m_accum_dim[0]; return *this; }
    AbstractView operator--(int) { AbstractView tmp(*this); --(*this); return tmp; }
    AbstractView operator+(difference_type tar) const { return This(m_data + abstract::calc_offset(tar), m_dimensions, m_accum_dim); }
    AbstractView operator-(difference_type tar) const { return This(m_data - abstract::calc_offset(tar), m_dimensions, m_accum_dim); }
    difference_type operator-(const ThisView& other) const { return (m_data - other.m_data) / m_dimensions[0]; }
    AbstractView& operator+=(difference_type delt) { m_data += abstract::calc_offset(delt); return *this; }
    AbstractView& operator-=(difference_type delt) { m_data -= abstract::calc_offset(delt); return *this; }

    // comparing
    // bool operator==(const ThisView& rhs) const { return m_data != rhs.m_data; }
    // bool operator!=(const ThisView& rhs) const { return !(this == rhs); }
    bool operator< (const ThisView& rhs) const { return m_data < rhs.m_data; }
    bool operator> (const ThisView& rhs) const { return m_data > rhs.m_data; }
    bool operator<=(const ThisView& rhs) const { return m_data <= rhs.m_data; }
    bool operator>=(const ThisView& rhs) const { return m_data >= rhs.m_data; }

private:
    // size_type calc_offset(size_t index) const {
    //     return index * m_accum_dim[0];
    // }
};


template <typename T, size_t depth>
class NDArray : public AbstractNDArray<T, depth, false> {
public:
    using abstract = AbstractNDArray<T, depth, false>;
    using abstract::m_data;
    using abstract::m_dimensions;
    using abstract::m_total_size;
    using abstract::m_accum_dim;
    using typename abstract::size_type;
    using typename abstract::value_type;
    using typename abstract::reference;
    using typename abstract::pointer;
    using typename abstract::difference_type;

    using typename abstract::view;
    using typename abstract::const_view;
    using typename abstract::ThisView;
    using typename abstract::NextView;

    using iterator = NDArrayView<T, depth>;
    using const_iterator = NDArrayConstView<T, depth>;

// Constructors:
    NDArray(const T* impr_data, const size_t* impr_dimensions, const size_t impr_total_size) {
        m_total_size = impr_total_size;
        if (impr_data) {
            m_dimensions = new size_t[depth];
            for (size_t i = 0; i < depth; i++) {
                m_dimensions[i] = impr_dimensions[i];
            }
            fill_accum_dim();
            m_data = new T[m_total_size];
            for (size_t i = 0; i < m_total_size; i++) {
                m_data[i] = impr_data[i];
            }
        }
    }

    NDArray(const NDArray& other) :
    NDArray(other.m_data, other.m_dimensions, other.m_total_size) {}

    NDArray(NDArray&& other) noexcept {
        abstract::move(other);
    }

    NDArray() {}

    template <size_t N, typename U>
    struct mode_init {
        using type = std::initializer_list<typename mode_init<N - 1, U>::type>;
    };

    template <typename U>
    struct mode_init<1, U> {
        using type = std::initializer_list<U>;
    };

    NDArray(typename mode_init<depth, T>::type init) {
        m_dimensions = new size_t[depth + 1];
        m_total_size = 1;
        for (size_t i = 0; i < depth + 1; i++) {
            m_dimensions[i] = 0;
        }
        count_dims<depth>(init);
        fill_accum_dim();
        for (size_t i = 1; i < depth + 1; i++) {
            m_total_size *= m_dimensions[i];
        }
        m_data = new T[m_total_size];
        T* it = m_data;
        fill_data<depth>(init, it);
        // DEBUG
        std::cout << "init_constructor: ";
        for (size_t i = 0; i < m_total_size; i++) {
            std::cout << m_data[i] << " ";
        }
        std::cout << "\n";
    }

    NDArray(const std::array<size_t, depth>& st_dims, T number = NULL) {
        stock_init(st_dims);

        m_data = std::allocator<T>{}.allocate(m_total_size);
        for (size_t i = 0; i < m_total_size; i++) {
            m_data[i] = number;
        }

        // DEBUG
        for (size_t i = 0; i < m_total_size; i++) {
            std::cout << m_data[i] << " ";
        }
        std::cout << "\n";
        std::cout << "\n";
    }

    NDArray(const std::array<size_t, depth>& st_dims, T* ptr) {
        stock_init(st_dims);

        m_data = std::allocator<T>{}.allocate(m_total_size);
        for (size_t i = 0; i < m_total_size; i++) {
            m_data[i] = ptr[i];
        }
    }

    ~NDArray() {
        std::allocator<T>{}.deallocate(m_data, m_total_size);
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
        abstract::swap(other);
        return *this;
    }

    size_type count() const {return m_dimensions[0];}
    size_type total_count() const {return m_total_size;}
    size_type dim() const {return depth;}

    T& at(const std::initializer_list<size_t> indexes) const {
        size_t offset = 0;
        size_t i = 0;
        for (auto cur : indexes) {
            offset += cur * m_accum_dim[i];
            i++;
        }
        return m_data[offset];
    }

    bool is_equal(const NDArray& rhs) const {
        if (!(std::equal(m_data, m_data + m_total_size, rhs.m_data)
            && std::equal(m_dimensions, m_dimensions + depth, rhs.m_dimensions))) {
            return false;
        }
        return true;
    }

    NextView begin() noexcept requires(depth > 1) {
        return ThisView(m_data, m_dimensions, m_accum_dim).begin();
    }
    NextView end() noexcept requires(depth > 1) {
        return ThisView(m_data, m_dimensions, m_accum_dim).end();
    }
    pointer begin() noexcept requires(depth == 1) {
        return m_data;
    }
    pointer end() noexcept requires(depth == 1) {
        return m_data + m_dimensions[1];
    }
    // const_view begin() const noexcept { return NDArrayConstView<T, depth>(m_data, m_dimensions, m_accum_dim); }
    // const_view end() const noexcept { return NDArrayConstView<T, depth>(m_data + m_total_size, m_dimensions, m_accum_dim); }
    // const_view cbegin() const noexcept { return begin(); }
    // const_view cend() const noexcept { return end(); }

    view reshape(const std::array<size_type, depth> sizes) {
        for (size_t i = 0; i < depth; i++) {
            m_dimensions[i] = sizes[i];
        }
        fill_accum_dim();
    }

private:
    void stock_init(const std::array<size_t, depth>& st_dims) {
        m_dimensions = new size_t[depth + 1];
        m_total_size = 1;
        size_t i = 1;
        for (auto cur : st_dims) {
            m_dimensions[i] = cur;
            i++;
        }
        fill_accum_dim();
        for (size_t i = 1; i < depth + 1; i++) {
            m_total_size *= m_dimensions[i];
        }
    }

    void fill_accum_dim() {
        m_accum_dim = new size_t[depth + 1];
        m_accum_dim[depth] = 1;
        if (depth >= 2) {
            for (size_t i = depth; i > 0; i--) {
                m_accum_dim[i - 1] = m_dimensions[i] * m_accum_dim[i];
            }
        }
    }

    template <size_t cur_depth>
    void count_dims(typename mode_init<cur_depth, T>::type& init) {
        const size_t level = depth - cur_depth + 1;
        m_dimensions[level] = std::max(m_dimensions[level], init.size());
        if constexpr (cur_depth > 1) {
            for (auto cur_init : init) {
                count_dims<cur_depth - 1>(cur_init);
            }
        }
    }

    template <size_t cur_depth>
    void fill_data(typename mode_init<cur_depth, T>::type& init, T*& it) {
        if constexpr (cur_depth > 1) {
            for (auto cur_init : init) {
                T* old = it;
                const size_t level = depth - cur_depth + 1;
                fill_data<cur_depth - 1>(cur_init, it);

                const size_t shortage = m_accum_dim[level] - (it - old);
                for (size_t i = 0; i < shortage; i++) {
                    *it = NULL;
                    ++it;
                }
            }
        } else {
            for (T cur : init) {
                *it = cur;
                ++it;
            }
        }
    }
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
    NDArray<int, 4> arr4D{{{{1}}}};
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

    return 0;
}


