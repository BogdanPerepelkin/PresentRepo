// i am in process

// 1. imagine margins --DONE--
// 2. copy paste from cppref - contiguouscontainer:
//      2.0. rule fifth --DONE--
//      2.1. container --DONE--
//      2.2. legacy random access iterator --PLUG--
//      2.3. contiguous iterator
// 3. Iterator - LegacyRandomAccessIterator
//      --PLUG--
// 4. Initializer constructor: --DONE--
// 5. NDArrayView --HERE--



#include <algorithm>
#include <array>
#include <iostream>
#include <limits>
#include <utility>

template <typename T>
class Iterator {
public:
    using value_type = T;
    using reference = T&;
    using pointer = T*;
    using difference_type = std::ptrdiff_t;
    using iterator_category = std::random_access_iterator_tag;

    explicit Iterator(const pointer ptr) : m_ptr(ptr) {}
    ~Iterator() = default;

    reference operator*() const {
        return *m_ptr;
    }

    pointer operator->() const {
        return m_ptr;
    }

    Iterator& operator++() {
        ++m_ptr;
        return *this;
    }

    Iterator operator++(int) {
        Iterator tmp = *this;
        ++*this;
        return tmp;
    }

    Iterator& operator--() {
        --m_ptr;
        return *this;
    }

    Iterator operator--(int) {
        Iterator tmp = *this;
        --*this;
        return tmp;
    }

    bool operator==(const Iterator& other) const {
        return m_ptr == other.m_ptr;
    }

    bool operator!=(const Iterator& other) const {
        return !(this == other);
    }

    bool operator<(const Iterator& other) const {
        return m_ptr < other.m_ptr;
    }

    bool operator>(const Iterator& other) const {
        return m_ptr > other.m_ptr;
    }

    bool operator<=(const Iterator& other) const {
        return m_ptr <= other.m_ptr;
    }

    bool operator>=(const Iterator& other) const {
        return m_ptr >= other.m_ptr;
    }

    Iterator operator+(difference_type delta) {
        return Iterator(m_ptr + delta);
    }

    Iterator operator-(difference_type delta) {
        return Iterator(m_ptr - delta);
    }

    difference_type operator-(const Iterator& other) const {
        return m_ptr - other.m_ptr;
    }

    Iterator& operator+=(const difference_type delta) {
        this = this + delta;
        return *this;
    }

    Iterator& operator-=(const difference_type delta) {
        return this - delta;
    }

    reference operator[](difference_type tar) const {
        return *(m_ptr + tar);
    }

private:
    pointer m_ptr;
};

template <typename T>
class AbstractNDArray {
    
};

template <typename T>
class NDArrayView {

};

template <typename T>
class NDArrayConstView {

};

template <typename T, size_t depth>
class NDArray {
public:    
// using (Container)
    using value_type = T;
    using reference = value_type&;
    using const_reference = const value_type&;
    using difference_type = int;
    using size_type = size_t;
    using iterator = Iterator<T>;
    using const_iterator = const iterator;
    using view = NDArrayView<T>;
    using const_view = NDArrayConstView<T>;

// Expressions (Rule fifth) (2 constructors)
    // constructor for copy constructor
    NDArray(const T* impr_data, const size_t* impr_dimensions, const size_t impr_total_size)
    : m_data(nullptr), 
    m_dimensions(nullptr),
    m_total_size(impr_total_size){
        if (impr_data) {
            m_dimensions = new size_t[size];
            for (size_t i = 0; i < size; i++) {
                m_dimensions[i] = impr_dimensions[i];
            }
            m_data = new T[m_total_size];
            for (size_t i = 0; i < m_total_size; i++) {
                m_data[i] = impr_data[i];
            }
        }
    }

    // destructor
    ~NDArray() {
        delete[] m_data;
        delete[] m_dimensions;
    }

    // copy constructor 
    NDArray(const NDArray& other) 
    : NDArray(other.m_data, other.m_dimensions, other.m_total_size) {}

    // move constructor
    NDArray(NDArray&& other) noexcept :
    m_data(std::exchange(other.m_data, nullptr)),
    m_dimensions(std::exchange(other.m_dimensions, nullptr)),
    m_total_size(other.m_total_size) {}

    // copy assigment
    NDArray& operator=(const NDArray& other) {
        // copy-and-swap
        if (this != other) {
            NDArray tmp(other);
            tmp.swap(*this);
        }
        return *this;
    }

    // move assigment
    NDArray& operator=(NDArray&& other) noexcept {
        std::swap(m_data, other.m_data);
        std::swap(m_dimensions, other.m_dimensions);
        size_t tmp = m_total_size;
        other.m_total_size = m_total_size;
        m_total_size = tmp;
        return *this;
    }

// Expressions (Container)
    NDArray() :
    m_data(nullptr),
    m_dimensions(nullptr) {}

    // i <=> j
    // TODO

    bool operator==(const NDArray& other) const {
        return std::equal(begin(), other.begin(), end(), other.end());
    }

    bool operator!=(const NDArray& other) const {
        return !(this == other);
    }

    size_type size() const {
        return m_total_size;
    }

    size_type max_size() const {
        return std::numeric_limits<size_t>::max() / sizeof(value_type);
    }

    bool empty() {
        return begin() == end();
    }

// specification constructors (3)
    template <size_t N, typename U>
    struct mode_init {
        using type = std::initializer_list<typename mode_init<N - 1, U>::type>;
    };

    template <typename U>
    struct mode_init<1, U> {
        using type = std::initializer_list<U>;
    };

    NDArray(typename mode_init<depth, T>::type init) :
    m_data(nullptr),
    m_dimensions(new size_t[depth]),
    m_total_size(1)
    {
        // m_dimensions = new size_t[depth];
        for (size_t i = 0; i < depth; i++) {
            m_dimensions[i] = 0;
        }
        count_dims<depth>(init);
        for (size_t i = 0; i < depth; i++) {
            m_total_size *= m_dimensions[i];
        }

        size_t* accum_dim = new size_t[depth];
        accum_dim[depth - 1] = m_dimensions[depth - 1];
        if (depth >= 2) {
            for (size_t i = depth - 1; i > 0; i--) {
                accum_dim[i - 1] = m_dimensions[i - 1] * accum_dim[i];
            }
        }

        m_data = new T[m_total_size];
        T* it = m_data;
        fill_data<depth>(init, it, accum_dim);

        delete[] accum_dim;

        // DEBUG
        for (size_t i = 0; i < m_total_size; i++) {
            std::cout << m_data[i] << " ";
        }
        std::cout << "\n";
    }

    NDArray(const std::array<size_t, depth>& st_dims, T number = NULL) :
    m_data(nullptr),
    m_dimensions(new size_t[depth]),
    m_total_size(1)
    {
        size_t i = 0;
        for (auto cur : st_dims) {
            m_dimensions[i] = cur;
            i++;
        }

        for (size_t i = 0; i < depth; i++) {
            m_total_size *= m_dimensions[i];
        }

        m_data = new T[m_total_size];
        for (size_t i = 0; i < m_total_size; i++) {
            m_data[i] = number;
        }

        // DEBUG
        for (size_t i = 0; i < m_total_size; i++) {
            std::cout << m_data[i] << " ";
        }
        std::cout << "\n";
    }

// specification functions:
    size_type count() const {return m_dimensions[0];}
    size_type total_count() const {return m_total_size;}
    size_type dim() const {return depth;}
    view operator[] (size_type i) const {}
    template <typename SizeList>
    view at(std::initializer_list<size_type> indexex) const {    }
    bool is_equal(const NDArray& rhs) const {}

    view begin() {}
    view end() {}
    const_view begin() const {}
    const_view end() const {};
    const_view cbegin() const {};
    const_view cend() const {};
    template <typename SizeList>
    view reshape(const SizeList& sizes) {}
    void swap(NDArray& other) {}

    iterator begin() {return iterator(m_data);}
    iterator end() {return iterator(m_data + m_total_size);}
    const_iterator cbegin() {return const_cast<const NDArray&>(*this).begin();}
    const_iterator cend() {return const_cast<const NDArray&>(*this).end();}
    void swap(NDArray& other) {
        std::swap(m_data, other.m_data);
        std::swap(m_dimensions, other.m_dimensions);
        size_t tmp = m_total_size;
        m_total_size = other.m_total_size;
        other.m_total_size = tmp;
    }


private:
    T* m_data;
    size_t* m_dimensions;
    size_t m_total_size;

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
    void fill_data(typename mode_init<cur_depth, T>::type& init, T*& it, const size_t* accum_dim) {
        if constexpr (cur_depth > 1) {
            for (auto cur_init : init) {
                T* old = it;
                const size_t level = depth - cur_depth;
                fill_data<cur_depth - 1>(cur_init, it, accum_dim);
                const size_t shortage = accum_dim[level + 1] - (it - old);
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


