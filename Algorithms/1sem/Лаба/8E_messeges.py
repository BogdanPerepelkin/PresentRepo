s = input()
dp=[0]*len(s)
dp[0] = 1
tmp = int(s[:2])
if tmp <= 33 and tmp >= 10:
    dp[1] = 2
else:
    dp[1] = 1
for i in range(2,len(s)):
    tmp = int(s[i-1:i+1])
    if tmp >= 10 and tmp <= 33:
        dp[i] = dp[i-2] + dp[i-1]
    else:
        dp[i] = dp[i-1]
print(dp[-1])