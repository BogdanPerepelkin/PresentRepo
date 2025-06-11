e = input()
f = input()
n=len(e)
m=len(f)
dp=[]
#dp[i][j] - минимальное кол-во операций
#для преобразования части строки e[:i] в f[:j]
for i in range(n+1):
    dp.append([0]*(m+1))

for i in range(n+1):
    dp[i][0] = i
for j in range(m+1):
    dp[0][j] = j

for i in range(1,n+1):
    for j in range(1,m+1):
        if e[i-1] == f[j-1]:
            dp[i][j] = dp[i-1][j-1]
        else:
            dp[i][j] = min(
                dp[i][j-1],
                dp[i-1][j],
                dp[i-1][j-1]
            )
            dp[i][j] +=1


# for i in dp:
#     print(i)
print(dp[n][m])