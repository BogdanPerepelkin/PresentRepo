n = int(input())
a = [int(i) for i in input().split()]

cur = 0
maxSum = a[0]
for i in range(len(a)):
    cur += a[i]
    if cur > maxSum:
        maxSum = cur
    if cur < 0:
        cur = 0
print(maxSum)
