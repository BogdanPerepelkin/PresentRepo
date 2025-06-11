#include <initializer_list>
#include <vector>
#include <type_traits>

template<typename T>
class MyIterator {
    T* ptr;

public:
    using value_type = T;
    using reference = T&;
    using pointer = T*;
    using difference_type = std::ptrdiff_t;
    using iterator_category = std::random_access_iterator_tag;

    MyIterator(T* p = nullptr) : ptr(p) {}

    reference operator*() const { return *ptr; }
    pointer operator->() const { return ptr; }

    MyIterator& operator++() { ++ptr; return *this; }
    MyIterator operator++(int) { MyIterator tmp = *this; ++(*this); return tmp; }

    MyIterator& operator--() { --ptr; return *this; }
    MyIterator operator--(int) { MyIterator tmp = *this; --(*this); return tmp; }

    bool operator==(const MyIterator& other) const { return ptr == other.ptr; }
    bool operator!=(const MyIterator& other) const { return ptr != other.ptr; }

    bool operator<(const MyIterator& other) const { return ptr < other.ptr; }
    bool operator>(const MyIterator& other) const { return ptr > other.ptr; }
    bool operator<=(const MyIterator& other) const { return ptr <= other.ptr; }
    bool operator>=(const MyIterator& other) const { return ptr >= other.ptr; }

    MyIterator operator+(difference_type n) const { return MyIterator(ptr + n); }
    MyIterator operator-(difference_type n) const { return MyIterator(ptr - n); }
    difference_type operator-(const MyIterator& other) const { return ptr - other.ptr; }

    MyIterator& operator+=(difference_type n) { ptr += n; return *this; }
    MyIterator& operator-=(difference_type n) { ptr -= n; return *this; }

    reference operator[](difference_type n) const { return *(ptr + n); }
};


// Определяем, является ли тип initializer_list

template <typename T>
constexpr bool is_initializer_list = false;

template <typename T>
constexpr bool is_initializer_list<std::initializer_list<T>> = true;

// Определяем глубину вложенности initializer_list

template <typename T>
constexpr size_t depth_of_initializer_list() {
    return 0;
}

template <typename T>
constexpr size_t depth_of_initializer_list(const std::initializer_list<T>& list) {
    if constexpr (is_initializer_list<T>) {
        return 1 + depth_of_initializer_list(*list.begin());
    } else {
        return 1;
    }
}

// FixedDims

template <typename T, size_t N>
struct FixedDims {
    size_t data[N]{};
    size_t& operator[](size_t i) { return data[i]; }
    const size_t& operator[](size_t i) const { return data[i]; }
};


// Рекурсивное извлечение размеров с учётом максимальных вложенностей

template <typename T, size_t N>
void extract_max_dimensions(const std::initializer_list<T>& list, FixedDims<size_t, N>& dims, size_t level = 0) {
    dims[level] = std::max(dims[level], list.size());
    if constexpr (is_initializer_list<T>) {
        for (const auto& sub : list) {
            extract_max_dimensions(sub, dims, level + 1);
        }
    }
}

// Универсальное рекурсивное копирование с заполнением нулями

template <typename T>
void flatten_recursive_fill(const T& elem, T*& out) {
    *out++ = elem;
}

template <typename T>
void flatten_recursive_fill(const std::initializer_list<T>& list, typename std::remove_cvref_t<T>*& out, const FixedDims<size_t, 1>& dims, size_t level = 0) {
    size_t count = 0;
    for (const auto& elem : list) {
        flatten_recursive_fill(elem, out);
        ++count;
    }
    for (; count < dims[level]; ++count) {
        *out++ = T();
    }
}

template <typename T, size_t N>
void flatten_recursive_fill(const std::initializer_list<T>& list, typename std::remove_cvref_t<T>*& out, const FixedDims<size_t, N>& dims, size_t level = 0) {
    size_t count = 0;
    for (const auto& elem : list) {
        flatten_recursive_fill(elem, out, dims, level + 1);
        ++count;
    }
    for (; count < dims[level]; ++count) {
        size_t inner_size = 1;
        for (size_t i = level + 1; i < N; ++i) {
            inner_size *= dims[i];
        }
        for (size_t i = 0; i < inner_size; ++i) {
            *out++ = T();
        }
    }
}

// NDArray

template <typename T, size_t N>
class NDArray {
public:
    NDArray() = default;

    template <typename U>
    NDArray(const std::initializer_list<U>& init) {
        static_assert(is_initializer_list<U> || std::is_same_v<U, T>, "Invalid initializer_list nesting");

        extract_max_dimensions(init, dimensions_);

        total_size_ = 1;
        for (size_t i = 0; i < N; ++i) {
            total_size_ *= dimensions_[i];
        }

        data_ = new T[total_size_];

        T* out = data_;
        flatten_recursive_fill(init, out, dimensions_);
    }

    ~NDArray() {
        delete[] data_;
    }

    void print_flat() const {
        for (size_t i = 0; i < total_size_; ++i) {
            std::cout << data_[i] << ' ';
        }
        std::cout << '\n';
    }

private:
    FixedDims<size_t, N> dimensions_;
    size_t total_size_ = 0;
    T* data_ = nullptr;
};

// Демонстрация
int main() {
    NDArray<int, 3> matrix{{{{1, 2}, {3, 4}}, {{5, 6}}}};

    std::cout << "Flattened NDArray: ";
    matrix.print_flat();
    return 0;
}
