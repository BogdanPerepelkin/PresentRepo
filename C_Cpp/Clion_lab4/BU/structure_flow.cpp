// i am in process

// вынес правило 5-ти
// попытался вынести using'и
// Надо:
//      реализовать все с доки
//      продолжить после блока про begin-end

#include <algorithm>
#include <array>
#include <iostream>
#include <limits>
#include <utility>
#include <pstl/algorithm_impl.h>


template <typename T, size_t depth, bool type>
class AbstractNDArray {
public:
    using size_type = std::size_t;
    using value_type = std::conditional_t<type, const T, T>;
    using reference = std::conditional_t<type, const T&, T&>;
    using pointer = std::conditional_t<type, const T*, T*>;
    using difference_type = std::conditional_t<type, const std::ptrdiff_t, std::ptrdiff_t>;

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

protected:
    AbstractNDArray(T* data = nullptr, size_t* dimensions = nullptr, size_t* accum_dim = nullptr) :
    m_data(data), m_dimensions(dimensions), m_accum_dim(accum_dim) {}

    T* m_data;
    size_type* m_dimensions;
    size_t m_total_size;
    size_type* m_accum_dim;
};

// template <typename T, size_t depth>
// class NDArrayView;

// template <typename T, size_t depth>
// class NDArrayConstView;

template <typename T, size_t depth, bool type>
class AbstractView : AbstractNDArray<std::conditional_t<type, const T, T>, depth, type> {
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

    using This = AbstractView<T, depth, type>;
    using Next = AbstractView<T, depth - 1, type>;

    AbstractView(pointer data, size_type* dimensions, size_type* accum_dim) :
    abstract(data, dimensions, accum_dim) {}

    AbstractView(const AbstractView& other)
    : AbstractView(other.m_data, other.m_dimensions, other.m_accum_dim) {}

    AbstractView(AbstractView&& other) {
        abstract::move(other);
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
        abstract::swap(other);
        return *this;
    }

    // fallin
    reference operator*() const {
        return *m_data;
    }
    T& operator[](difference_type tar) const requires (depth == 1) {
        return m_data[tar];
    }
    Next operator[](difference_type tar) const requires (depth > 1) {
        size_type offset = calc_offset(tar);
        return Next(m_data + offset, m_dimensions + 1, m_accum_dim + 1);
    }
    pointer operator->() const { return m_data;}

    // walking
    AbstractView& operator++() { m_data += m_accum_dim[0]; return *this; }
    AbstractView operator++(int) { AbstractView tmp(*this); ++(*this); return tmp; }
    AbstractView& operator--() { m_data -= m_accum_dim[0]; return *this; }
    AbstractView operator--(int) { AbstractView tmp(*this); --(*this); return tmp; }
    AbstractView operator+(difference_type tar) const { return This(m_data + calc_offset(tar), m_dimensions, m_accum_dim); }
    AbstractView operator-(difference_type tar) const { return This(m_data - calc_offset(tar), m_dimensions, m_accum_dim); }
    difference_type operator-(const This& other) const { return (m_data - other.m_data) / m_dimensions[0]; }
    AbstractView& operator+=(difference_type delt) { m_data += calc_offset(delt); return *this; }
    AbstractView& operator-=(difference_type delt) { m_data -= calc_offset(delt); return *this; }

    // comparing
    bool operator==(const This& rhs) const = default;
    bool operator!=(const This& rhs) const = default;
    bool operator< (const This& rhs) const { return m_data < rhs.m_data; }
    bool operator> (const This& rhs) const { return m_data > rhs.m_data; }
    bool operator<=(const This& rhs) const { return m_data <= rhs.m_data; }
    bool operator>=(const This& rhs) const { return m_data >= rhs.m_data; }

private:
    size_type calc_offset(size_t index) const {
        return index * m_accum_dim[0];
    }
};

template <typename T, size_t depth>
using NDArrayView = AbstractView<T, depth, false>;

template <typename T, size_t depth>
using NDArrayConstView = AbstractView<T, depth, true>;


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

    using view = NDArrayView<T, depth>;
    using const_view = NDArrayView<T, depth>;



    size_type count() const {return m_dimensions[0];}
    size_type total_count() const {return m_total_size;}
    size_type dim() const {return depth;}

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
        m_dimensions = new size_t[depth];
        m_total_size = 1;
        for (size_t i = 0; i < depth; i++) {
            m_dimensions[i] = 0;
        }
        count_dims<depth>(init);
        fill_accum_dim();
        for (size_t i = 0; i < depth; i++) {
            m_total_size *= m_dimensions[i];
        }
        m_data = new T[m_total_size];
        T* it = m_data;
        fill_data<depth>(init, it);
        // DEBUG
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

    T& operator[](size_t i) const requires (depth == 1) {
        return m_data[i];
    }

    NDArrayView<T, depth - 1> operator[](size_t i) const requires (depth > 1) {
        return NDArrayView<T, depth>(m_data, m_dimensions, m_accum_dim)[i];
    }

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

    view begin() { return NDArrayView<T, depth>(m_data, m_dimensions, m_accum_dim); }
    view end() { return NDArrayView<T, depth>(m_data + m_total_size, m_dimensions, m_accum_dim); }
    const_view begin() const { return NDArrayConstView<T, depth>(m_data, m_dimensions, m_accum_dim); }
    const_view end() const { return NDArrayConstView<T, depth>(m_data + m_total_size, m_dimensions, m_accum_dim); }
    const_view cbegin() const { return begin(); }
    const_view cend() const { return end(); }

    bool operator==(const NDArray& other) const {
        return std::equal(begin(), other.begin(), end(), other.end());
    }

    bool operator!=(const NDArray& other) const {
        return !(this == other);
    }

    bool empty() {
        return begin() == end();
    }

    template <typename SizeList>
    view reshape(const SizeList& sizes) {}

    void swap(NDArray& other) {
        std::swap(m_data, other.m_data);
        std::swap(m_dimensions, other.m_dimensions);
        std::swap(m_accum_dim, other.m_accum_dim);
        size_t tmp = m_total_size;
        m_total_size = other.m_total_size;
        other.m_total_size = tmp;
    }

private:
    void stock_init(const std::array<size_t, depth>& st_dims) {
        m_dimensions = new size_t[depth];
        m_total_size = 1;
        size_t i = 0;
        for (auto cur : st_dims) {
            m_dimensions[i] = cur;
            i++;
        }
        fill_accum_dim();
        for (size_t i = 0; i < depth; i++) {
            m_total_size *= m_dimensions[i];
        }
    }

    void fill_accum_dim() {
        m_accum_dim = new size_t[depth];
        m_accum_dim[depth - 1] = 1;
        if (depth >= 2) {
            for (size_t i = depth - 1; i > 0; i--) {
                m_accum_dim[i - 1] = m_dimensions[i] * m_accum_dim[i];
            }
        }
    }

    template <size_t cur_depth>
    void count_dims(typename mode_init<cur_depth, T>::type& init) {
        const size_t level = depth - cur_depth;
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
                const size_t level = depth - cur_depth;
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
    // DEBUG
    // NDArray<int, 1>
    // arr1 {1, 2};

    NDArray<int, 3> arr({2,2,3}, 3);

    NDArray<int, 3>
    arr2 = {
    {
    {1},
    {2},
    {3}
    },
    {
    {4, 5}
    }
    };


    std::cout << arr2[1][0][1] << "\n";
    std::cout << arr2.at({1, 0, 1}) << "\n";
    std::cout << arr[1][1][2];


    // NDArray<int, 4> arr{
    //     { // Уровень 1
    //         { // Уровень 2
    //             { // Уровень 3
    //                 {1, 2}, {3, 4}  // Уровень 4 (все подсписки одной длины!)
    //             },
    //             {
    //                 {5, 6}, {7, 8}
    //             }
    //         },
    //         {
    //             {
    //                 {9, 10}, {11, 12}
    //             },
    //             {
    //                 {13, 14}, { 16}
    //             }
    //         }
    //     }
    // };

    // std::cout << "\n";

//     NDArray<int, 5>
//     bogdan_pups{
//         {
//             {
//                 {
//                     {10, 20, 14}, {1}
//                 },
//
//                 {
//                     {101, 23, 456, 1, 3}
//                 },
//
//                 {
//                     {1}, {2}, {3}
//                 }
//             },
//
//             {
//                 {
//                     {1, 2, 3, 4}, {129, 23, 4}
//                 },
//
//                 {
//                     {123, 45, 6}, {2}, {34, 5}, {3}
//                 },
//
//                 {
//                     {123, 45, 6}, {2}, {34, 5}, {3}
//                 },
//
//                 {
//                     {0}, {0}, {0}
//                 },
//             },
//
//             {
//                 {
//                     {12, 4, 6, 7}, {1}
//                 },
//             }
//         },
//
//         {
//             {
//                 {
//                     {10, 20, 14}, {1}
//                 },
//
//                 {
//                     {101, 23, 456, 1, 3}
//                 },
//
//                 {
//                     {1}, {2}, {3}
//                 }
//             },
//
//             {
//                 {
//                     {10, 20, 14}, {1}
//                 },
//
//                 {
//                     {101, 23, 456, 1, 3}
//                 },
//
//                 {
//                     {1}, {2}, {3}
//                 }
//             }
//         },
//
//         {
//             {
//                 {
//                     {4}
//                 }
//             },
//
//             {
//                 {
//                     {55}
//                 }
//             },
//
//             {
//                 {
//                     {666}
//                 }
//             }
//         }
// };

    return 0;
}


