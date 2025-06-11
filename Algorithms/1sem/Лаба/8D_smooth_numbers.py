n = int(input())
dp = [0,1,1,1,1,1,1,1,1,1]
dp_n = [0] * 10
for i in range(1,n):
    dp_n[0] = dp[0] + dp[1]
    dp_n[9] = dp[8] + dp[9]
    for j in range(1,9):
        dp_n[j] = dp[j-1] + dp[j] + dp[j+1]
    dp = dp_n
    dp_n = [0] * 10
print(sum(dp))