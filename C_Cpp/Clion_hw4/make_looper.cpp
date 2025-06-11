#include <iostream>
#include <string>

auto makeLooper(const std::string& str) {
    // Answer goes here. Be mindful of scope!
    size_t cnt = 0;
    auto lambda = [cnt, str]() mutable {
        return str[cnt++ % str.size()];
    };
    return lambda;
}

int main() {
    auto abc = makeLooper("abc");
    std::cout << abc();
    std::cout << abc();

    auto std = makeLooper("std");
    std::cout << std();
    std::cout << std();
    std::cout << std();
    std::cout << std();
    std::cout << std();

    return 0;

}
