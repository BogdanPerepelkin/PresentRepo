inp=int(input())
a=sorted([int(i) for i in input().split()])

if inp == 2:
    print(a[1]-a[0])
elif inp == 3:
    print(a[2]-a[0])
elif inp == 4:
    print(a[3]-a[2]+a[1]-a[0])
else:
    accum = a[1]-a[0] + a[-1] - a[-2]
    a.pop()
    a.pop(0)
    n = len(a)-1
    #dp[i][0] - возьмем
    #dp[i][1] - не возьмем
    # print(a)
    dp = []
    for i in range(n):
        dp.append([0,0])
    dp[0][0] = a[1] - a[0]
    dp[0][1] = 0
    dp[1][0] = a[2] - a[1]
    dp[1][1] = dp[0][0]

    for i in range(2,n):
        dp[i][0] = min(
            dp[i-1][0],
            dp[i-1][1]
        ) + a[i+1] - a[i]
        dp[i][1] = dp[i-1][0]
    print(min(dp[-1][0], dp[-1][1]) + accum)
    # print(dp)

"""
1 3 4 5 7 9

1 2 5 9 11 16 17 18 20 21
 0 1 2 3  5  1  1  2  1

"""