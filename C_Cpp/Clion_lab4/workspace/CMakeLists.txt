cmake_minimum_required(VERSION 3.31)
project(workspace)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_compile_options(-std=c++23)

add_executable(workspace main.cpp
        MyContainer.h
        NDArray.h)
