n,m = map(int, input().split())
w = [int(i) for i in input().split()]

dp = [[0] * (m+1)]
for i in range(1, n + 1):
    dp.append([0] * (m+1))

for i in range(1, n+1):
    for j in range(1, m+1):
        if j-w[i-1] < 0:
            dp[i][j] = dp[i-1][j]
        else:
            dp[i][j] = max(
                dp[i-1][j],
                dp[i-1][j-w[i-1]] + w[i-1]
            )


# print(w)
# for i in dp:
#     print(i)
print(dp[-1][-1])