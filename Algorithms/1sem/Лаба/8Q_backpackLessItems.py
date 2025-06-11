n,m = map(int, input().split())
w = [int(i) for i in input().split()]

dp = [0] + [99**9]*m
for i in range(n):
    for j in range(m, w[i]-1, -1):
        dp[j] = min(
            dp[j],
            dp[j- w[i]] + 1
        )

if dp[-1] != 99**9:
    print(dp[-1])
else:
    print(0)
