n,k = map(int, input().split())
if n == 1:
    print(1)
else:
    dp=[0]*n
    dp[0] = 1
    dp[1] = 1
    for i in range(2,n):
        accum=0
        if i - k > 0:
            subtrahead = dp[i - k - 1]
        else:
            subtrahead = 0
        dp[i] = (dp[i - 1] * 2 - subtrahead) % (10**9 + 7)
    print(dp[-1])