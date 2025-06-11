cmake_minimum_required(VERSION 3.20)  # Или другая актуальная версия
project(Pr_Clion)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Указываем компилятор ресурсов (если действительно нужен)
set(CMAKE_RC_COMPILER "C:/Program Files (x86)/Windows Kits/10/bin/10.0.22621.0/x64/rc.exe")

# Вывод информации о компиляторах
message(STATUS "C++ Compiler: ${CMAKE_CXX_COMPILER}")
message(STATUS "C Compiler: ${CMAKE_C_COMPILER}")

# Создаём исполняемый файл
add_executable(task_4_kill_me.exe task_4_kill_me.cpp)
add_executable(task_3_swapper_already_dead.exe task_3_swapper_already_dead.cpp)
add_executable(Stars_5.exe Stars_5.cpp)
add_executable(7_BFF.exe 7_BFF.cpp)

# Задаём имя выходного файла (используем правильное имя цели!)
set_target_properties(task_4_kill_me.exe PROPERTIES OUTPUT_NAME "task_4_kill_me.exe")
set_target_properties(task_3_swapper_already_dead.exe PROPERTIES OUTPUT_NAME "task_3_swapper_already_dead.exe")
set_target_properties(Stars_5.exe PROPERTIES OUTPUT_NAME "Stars_5")
set_target_properties(7_BFF.exe PROPERTIES OUTPUT_NAME "7_BFF")