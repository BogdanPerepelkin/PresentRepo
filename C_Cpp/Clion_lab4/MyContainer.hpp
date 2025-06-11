

#include <concepts>
#include <cstring>
#include <type_traits>
#include <memory> // cstring

int main() {return 0;}

// контейнер, тип элементов которого, удовлетворяет некоторому требованию:
// 1. целое
// 2. четное
template <typename T>
concept OddNameConcept = std::is_integral_v<T> && requires (T a) {
    // (a & 1) == 1;
    { (a % 2) == 1 } -> std::convertible_to<bool>;
};

template <typename C>
class Iterator : public std::iterator<std::forward_iterator_tag, typename C::value_type> {
public:
    using iterator_category = std::forward_iterator_tag;
    using value_type = C::value_type;
    using reference = value_type&;
    using pointer = value_type*;

    explicit Iterator(pointer ptr) : m_ptr(ptr) {};
    ~Iterator() = default;

    reference operator*() const {
        return *ptr;
    }

    pointer operator->() const {
        return m_ptr;
    }

    Iterator& operator++() {
        ++m_ptr;
        return *this;
    }

    Iterator& operator++(int) {
        Iterator tmp = *this;
        ++*this;
        return tmp;
    }

    bool operator==(const Iterator& rhs) const {
        return m_ptr == rhs.m_ptr;
    }

    bool operator!=(const Iterator& rhs) const {
        return m_ptr != rhs.m_ptr;
    }

private:
    pointer *m_ptr;
};

template <typename T> requires OddNameConcept<T>
class OddNumberContainer {
public:
    using value_type = T;
    using reference = T&;
    using const_reference = const T&;
    using size_type = size_t;
    using iterator = Iterator<OddNumberContainer>;

    OddNumberContainer() : m_data(nullptr), m_size(0), m_capacity(0) {};
    // или OddNumberContainer() = default;

    // копирования
    OddNumberContainer(const OddNumberContainer& other) : m_data(nullptr),
    m_size(other.m_size), m_capacity(other.m_capacity) {
        T *m_data = new T[m_capacity];
        std::memcpy(m_data, other.m_data, m_size * sizeof(T));
    }

    // копирующего присваивания
    OddNumberContainer& operator=(const OddNumberContainer& other) {
        // copy-and-swap
        if (this != other) {
            OddNumberContainer tmp(other);
            tmp.swap(*this);
        }
        return *this;

        // bad practice - нет гарантий, что не поломаем объект
        // delete[] m_data;
        // m_data = new T[m_capacity];
    }

    OddNumberContainer(OddNumberContainer&& other) = delete;
    OddNumberContainer& operator=(OddNumberContainer&& other) = delete; // если не delete, то move-and-swap

    void swap(OddNumberContainer& other) {
        std::swap(m_data, other.m_data);
        std::swap(m_size, other.m_size);
        std::swap(m_capacity, other.m_capacity);
    }

    bool ppuah_back(const value_type &value) {
        if ((value & 1) != 1) {
            return false;
        }
        if (m_size == m_capacity) {
            size_t size = m_size + 1;
            size_t cap = size * 2;
            // какая же она хитрая 💚💚💚Виктория💚💚💚
            value_type *temp = new value_type[cap];
            std::memcpy(temp, m_data, m_size * sizeof(T));
            delete[] m_data;
            m_data = temp;
            m_capacity = cap;
        }
        m_data[m_size] = value;
        m_size++;
    }

    iterator begin() {
        return iterator(m_data);
    }

    iterator end() {
        return iterator(m_data + m_size);
    }

    ~OddNumberContainer() noexcept {
        if (m_data != nullptr) {
            delete[] m_data;
        }
    }

private:
    T* m_data;
    size_t m_size, m_capacity;
};
