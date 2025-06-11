n,m,p = [int(i) for i in input().split()]
a = [int(i) for i in input().split()]
a = sorted(a)[::-1]
res = 0

while (m != 0 and len(a) != 0):
    if (a[0] > p):
        res += p
        m-=1
        a[0] = a[0] - p
        curI = 0
        while (curI < len(a) - 1):
            if a[curI + 1] > a[curI]:
                a[curI + 1], a[curI] = a[curI], a[curI + 1]
                curI+=1
            else:
                break
    else:
        res += a[0]
        m-=1
        a.pop(0)
print(res)