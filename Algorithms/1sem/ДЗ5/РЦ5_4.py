import numpy as np
import matplotlib.pyplot as plt
import math

# Функция для вычисления (log(n))!
def log_factorial(n):
    if n > 1:
        log_n = math.log(n)
        return math.factorial(int(log_n))
    else:
        return 1  # log(1) = 0, а 0! = 1

# Создаем массив натуральных чисел от 2 до 100
n_values = np.arange(2, 101)

# Вычисляем (log(n))! для каждого n
log_factorial_values = [log_factorial(n) for n in n_values]

# Построение графика
plt.plot(n_values, log_factorial_values, marker='o', linestyle='-', color='b')
plt.title(r'Значения $(\log(n))!$ для натуральных чисел n')
plt.xlabel('n')
plt.ylabel(r'$(\log(n))!$')
plt.grid(True)
plt.show()
