n, k = map(int, input().split())
a = []
for i in range(n):
    a.append(int(input()))
# print(a)
# бинаримся по длине отрезков
f = 1
l = 1
r = max(a)
mid = (l+r)//2
answ = 0
while l <= r:
    mid = (l + r) // 2
    accum = 0
    for i in a:
        accum += i // mid
    if accum >= k:
        answ = mid
        l = mid + 1
    else:
        r = mid - 1
print(answ)