n=int(input())
a=[]
for i in range(n):
    a.insert(0, [int(i) for i in input().split()])
#fill the base
dp=[0] * n
dp[0]=a[0][0]
if n >= 2:
    dp[1] = min(dp[0]+a[1][0], a[1][1])
if n >= 3:
    dp[2] = min(a[2][0]+dp[1], a[2][1]+dp[0], a[2][2])

for i in range(3, len(dp)):
    dp[i] = min(
        a[i][0] + dp[i-1],
        a[i][1] + dp[i-2],
        a[i][2] + dp[i-3]
    )
print(dp[-1])