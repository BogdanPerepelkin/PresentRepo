n,k = map(int, input().split())
dp=[0]*n
dp[0] = 1
for i in range(1,n):
    accum=0
    for j in range(1,k+1):
        if i - j >= 0:
            accum += dp[i - j]
        else:
            break
    dp[i] = accum
print(dp[-1])