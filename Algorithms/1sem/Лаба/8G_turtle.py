n,m = map(int, input().split())
field = []
for i in range(n):
    field.append([int(j) for j in input().split()])
dp=[[0]*(m+1)]
for i in range(n):
    dp.append([0]+[0]*m)

for i in range(1,n+1):
    for j in range(1,m+1):
        if i-1==0:
            dp[i][j] = dp[i][j - 1] + field[i - 1][j - 1]
        elif j-1==0:
            dp[i][j] = dp[i - 1][j] + field[i - 1][j - 1]
        else:
            if dp[i][j-1] >= dp[i-1][j]:
                dp[i][j] = dp[i][j-1] + field[i-1][j-1]
            else:
                dp[i][j] = dp[i-1][j] + field[i-1][j-1]

#восстановление sequence
s=''
i=n
j=m
while i != 1 or j != 1:
    if i - 1 == 0:
        j-=1
        s = 'R' + s
    elif j - 1 == 0:
        i-=1
        s = 'D' + s
    else:
        if dp[i][j-1] >= dp[i-1][j]:
            j-=1
            s = 'R' + s
        else:
            i-=1
            s = 'D' + s
print(dp[n][m])
print(s)


# for i in range(len(field)):
#     print(field[i])
# print()
# for i in range(len(dp)):
#     print(dp[i])