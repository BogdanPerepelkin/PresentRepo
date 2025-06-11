#include <algorithm>
#include <gtest/gtest.h>
#include "MyContainer.hpp"

TEST(Construct, NoThrow) {
    EXPECT_NO_THROW(OddNumberContainer<int>{});
}

TEST(Construct, Traits) {
    ASSERT_TRUE(std::is_default_constructible<OddNumberContainer<int>>());
    SUCCEED(); // FAILED();
}

TEST(TestCaseName, TestName) {
    EXPECT_EQ(1, 1);
    EXPECT_TRUE(true);

    // например:
    EXPECT(OddNumberContainer<int> c);
}

TEST(PushBack, Test1) {
    OddNumberContainer<int> c;
    c.push_back(3);
    EXPECT_EQ(*c.begin(), 3);
    // Вихрова Дарья M3135: "pupupupupupuupupupupupu"
}

TEST(Algo, MinElement) {
    OddNumberContainer<int> c;
    c.push_back(3);
    c.push_back(4);
    c.push_back(5);
    c.push_back(6);
    auto mn = std::min_element(c.begin(), c.end());
    EXPICT_EQ(*mn, 3);
    for (auto x : c)
        std::cout << x  << " " << endl;
    bool result = std::all_of(c.begin(), c.end(), [&](const int& n) {return n % 2 == 1;});
    EXPECT_TRUE(result);
}

int main(int argc, char **argv) {
    // 2 аргумента передаем, чтобы настроить тесты
    ::testing::InitGoogleTest(&argc, argv);

    OddNumberContainer<std::string> c; // выдаст ошибку при компиляции, так как не соотв типу в классе

    return RUN_ALL_TESTS();
}
