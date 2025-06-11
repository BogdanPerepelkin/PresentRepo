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
    std::cout << std::endl;
    for (size_t i = 0; i < 3; i++) {
        std::cout << arr1D[i] << " ";
    }
    std::cout << "\n\n";

    std::cout << "2D\n";
    NDArray<int, 2> arr2D{{1, 2}, {3, 4}};

    std::cout << "after create\n";
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
    std::cout << "at: "  << arr_sq2D.at(0).at(1);
    // f
    std::cout << "\n\n";

    std::cout << "View test\n";
    NDArray<int, 1> arr_for_view{1, 2};
    NDArrayView<int, 1> view(arr_for_view);
    for (auto i : view) {
        std::cout << i << " ";
    }
    std::cout << "\ncout: " << view.count() << " total: " << view.total_count() << " dim: " << view.dim() << "\n";
    std::cout << "at: "  << view.at(1) << "\n";
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

    std::cout << "reshape test\n";
    NDArray<int, 2> arr_reshape(2, 3);
    std::cout << "old size = " << arr_reshape.total_count() << "\n";
    auto new_arr = arr_reshape.reshape(3, 2);
    std::cout << "new size = " << new_arr.total_count() << "\n";
    for (auto i : new_arr) {
        std::cout << " aga ";
        for (auto j : i) {
            std::cout << j << " ";
        }
    }
    std::cout << "\n\n";

    return 0;
}