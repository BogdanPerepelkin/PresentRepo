n = int(input())
a = [int(i) for i in input().split()]
last=[-1] * n #ссылки для восстановления последовательности
dp = [1] * n #dp[i] - длина последовательности, закончившейся на i
dp[0] = 1
last[0] = -1
for i in range(1, n):
    for j in range(i):
        if a[j] < a[i] and dp[j] + 1 > dp[i]:
            dp[i] = dp[j] + 1
            last[i] = j
l=1
ind = 0
for i in range(len(dp)):
    if dp[i] > l:
        ind = i
        l = dp[i]
an_seq = []

while ind != -1:
    an_seq.insert(0,a[ind])
    ind = last[ind]
print(l)
print(*an_seq)