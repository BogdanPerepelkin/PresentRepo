#include <iostream>

#include <vector>

using namespace std;

enum Direction {
    DirectionNone,
    DirectionUp,
    DirectionLeft,
    DirectionDown,
    DirectionRight
};

template <typename C>
class Iterator :
public std::iterator<std::forward_iterator_tag, C> {
public:
    using table_type = vector<vector<C>>;
    using const_table_type = const table_type &;
    using value_type = C;

    using iterator_category = std::forward_iterator_tag;
    using reference = value_type&;
    using pointer = value_type*;

    Iterator(const_table_type ptr, Direction dir0, Direction dir1, bool is_end) :
    m_table(ptr),
    m_is_end(is_end)
    {
        // start position + direction
        size_t start_row = 0;
        size_t start_col = 0;
        if (dir0 == DirectionRight && dir1 == DirectionDown ||
            dir0 == DirectionDown && dir1 == DirectionRight) {
            start_row = 0;
            start_col = 0;
            if (dir0 == DirectionRight) {
                m_first_dir = 1;
                m_second_dir = 1;
            }
        } else if (dir0 == DirectionUp && dir1 == DirectionLeft ||
            dir0 == DirectionLeft && dir1 == DirectionUp) {
            start_row = m_table.size() - 1;
            start_col = m_table[0].size() - 1;
            m_first_dir = -1;
            m_second_dir = -1;
        } else if (dir0 == DirectionUp && dir1 == DirectionRight ||
            dir0 == DirectionRight && dir1 == DirectionUp) {
            start_row = m_table.size() - 1;
            start_col = 0;
            if (dir0 == DirectionUp) {
                m_first_dir = 1;
                m_second_dir = -1;
            } else {
                m_first_dir = -1;
                m_second_dir = 1;
            }
        } else if (dir0 == DirectionDown && dir1 == DirectionLeft ||
            dir0 == DirectionLeft && dir1 == DirectionDown) {
            start_row = 0;
            start_col = m_table[0].size() - 1;
            if (dir0 == DirectionDown) {
                m_first_dir = -1;
                m_second_dir = 1;
            } else {
                m_first_dir = 1;
                m_second_dir = -1;
            }
        }

        // limit
        if (dir0 == DirectionLeft || dir0 == DirectionRight) {
            m_is_rightLeft = true;
            m_first_size = m_table.size();
            m_second_size = m_table[0].size();
            m_wide = start_row;
            m_tar = start_col;
            m_second_start = start_col;
        } else {
            m_is_rightLeft = false;
            m_first_size = m_table[0].size();
            m_second_size = m_table.size();
            m_wide = start_col;
            m_tar = start_row;
            m_second_start = start_row;
        }
    }

    ~Iterator() = default;

    Iterator& operator++() {
        cout << "m_tar = " << m_tar << endl;
        if (m_is_end) return *this;

        if (m_tar == 0 && m_second_dir == -1 || (m_tar+=m_second_dir) >= m_second_size) {
            if (m_wide == 0 && m_first_dir == -1 || (m_wide+=m_first_dir) >= m_first_size) {
                m_is_end = true;
            }
            m_tar = m_second_start;
        }
        return *this;
    }
    Iterator& operator++(int) {
        Iterator tmp = *this;
        ++(*this);
        return tmp;
    }
    reference operator*() const {
        return const_cast<reference>(m_is_rightLeft ? m_table[m_wide][m_tar] : m_table[m_tar][m_wide]);
    }
    pointer operator->() const {
        return &const_cast<reference>(m_is_rightLeft ? m_table[m_wide][m_tar] : m_table[m_tar][m_wide]);
    }
    bool operator==(const Iterator& rhs) const {
        return m_is_end == rhs.m_is_end;
    }
    bool operator!=(const Iterator& rhs) const {
        return m_is_end != rhs.m_is_end;
    }

private:
    const_table_type m_table;
    size_t m_wide = 0;
    size_t m_tar = 0;
    int m_first_dir = 1;
    int m_second_dir = 1;
    size_t m_first_size = 0;
    size_t m_second_size = 0;
    size_t m_second_start = 0;
    bool m_is_end = false;
    bool m_is_rightLeft = false;
};

template <typename T>
class Table {
public:
    using value_type = T;
    using reference = T&;
    using const_reference = const T&;
    using size_type = size_t;
    using iterator = Iterator<T>;

    void push_back(const vector<T>& row) {
        m_data.push_back(row);
    }

    iterator begin(Direction dir0, Direction dir1) {
        return iterator(m_data, dir0, dir1, false);
    }
    iterator end() {
        return iterator(m_data, DirectionNone, DirectionNone, true);
    }

protected:
    vector<vector<T> > m_data;
};


int main() {
    Table<int> t;
    t.push_back({5, 6, 7, 8, 9});
    t.push_back({10, 11, 12, 13, 14});
    t.push_back({15, 16, 17, 18, 19});
    t.push_back({20, 21, 22, 23, 24});

    vector<int> t4;

    cout << "tmp_1\n";

    copy(t.begin(DirectionLeft,DirectionUp),t.end(),back_inserter(t4));
    for (auto it = t.begin(DirectionRight, DirectionDown); it != t.end(); it++) {
        cout << *it << "\n";
    }

    cout << "tmp_2\n\n\n\n";

    for (size_t i = 0; i < t4.size(); i++) {
        cout << "in for\n";
        cout << t4[i] << " ";
    }

    return 0;
}
