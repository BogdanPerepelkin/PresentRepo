n = int(input())
a = [int(i) for i in input().split()]
accum = 0
l = 0
r = 0
curSum = 0
indLose = 0
for i in range(0, n):
    curSum += a[i]
    if accum < curSum:
        accum = curSum
        if indLose != 0:
            l = indLose + 1
        r = i
    if curSum < 0:
        indLose = i
        curSum = 0
print(accum)
print(l + 1, r + 1)
