n = int(input())
a = [int(i) for i in input().split()]
a = a + a
curA = [0] * 2 * n
#first iteration
for i in range(n):
    if a[i] != 0:
        for j in range(i + 1, n + i):
            if a[j] > a[i]:
                curA[i] -= 1
                curA[j] += 1
                break
for i in range(n * 2):
    a[i % n] += curA[i]
    curA[i] = 0
a = a[:n] * 2
print(a)
#second one
for i in range(2 * n - 1, n - 1, -1):
    if a[i] != 0:
        for j in range(i - 1, i - n, -1):
            if a[j] > a[i]:
                curA[i] -= 1
                curA[j] += 1
                break
for i in range(n * 2):
    a[i % n] += curA[i]
    curA[i] = 0
print(a)
print(max(a))