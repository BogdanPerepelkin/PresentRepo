inp=int(input())
a=sorted([int(i) for i in input().split()])

def dp(i, acc, n, f):
    if i == n - 1 or i == n - 2:
        return acc + a[i + 1] - a[i]
    if f == 0:
        tmp1 = dp(i + 1, acc + a[i + 1] - a[i], n, 0)
        tmp2 = dp(i + 1, acc, n, 1)
        tmp3 = dp(i + 2, acc + a[i + 1] - a[i], n, 1)
        tmp = min(
            tmp1, tmp2, tmp3
        )
    else:
        tmp = min(
            dp(i+1, acc + a[i+1]-a[i], n, 0),
            dp(i+2, acc + a[i+1]-a[i], n, 1)
        )
    return tmp

if inp == 1:
    print(a[1]-a[0])
elif inp == 2:
    print(a[2]-a[0])
elif inp == 3:
    print(a[3]-a[2]+a[1]-a[0])
else:
    accum = a[1]-a[0] + a[-1] - a[-2]
    a.pop()
    a.pop(0)
    n = len(a)-1
    print(dp(0,0, n, 0) + accum)
    # print(accum)
    # print(dp(0,0,n, 0))



"""
0 1 2 5 9 11 16 17 18 20 21
   0 1 2 3  5  1  1  2  1

"""
"""
1-1--1---1-1----1-1
0 2 5 9 11 16 18

0 1 2 5 9 11 16 17 18 20 21
   0 1 2 3  5  1  1  2  1

8
0 1 3 4 5 7 9 10
"""