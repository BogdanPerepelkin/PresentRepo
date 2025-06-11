n,m = map(int, input().split())
dp=[]
dp.append([0,0] + [0]*m)
dp.append([0,0] + [0]*m)

for i in range(n):
    dp.append([0,0] + [0]*m)

for i in range(2,n+2):
    for j in range(2, m+2):
        if i==2 and j==2:
            dp[i][j] = 1
        else:
            dp[i][j] = dp[i-2][j-1] + dp[i-1][j-2]



# for i in range(len(dp)):
#     print(dp[i])

print(dp[-1][-1])