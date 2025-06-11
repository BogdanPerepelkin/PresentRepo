"""
s1 = []
s2 = []
def push(n):
    if len(s1) != 0:
        s1.append((n, min(s1[-1][1],n)))
    else:
        s1.append((n,n))
def my_pop():
    if len(s2) == 0:
        while len(s1) != 0:
            tmp = s1[-1][0]
            if len(s2) != 0:
                s2.append((tmp, min(s2[-1][1], tmp)))
            else:
                s2.append((tmp,tmp))
            s1.pop()
    if len(s2) != 0:
        s2.pop()
def my_min():
    if len(s1) == 0 and len(s2) == 0:
        print(-1)
    elif len(s1) == 0:
        print(s2[-1][1])
    elif len(s2) == 0:
        print(s1[-1][1])
    else:
        print(min(
            s1[-1][1],
            s2[-1][1]
        ))


for i in range(k):
    push(inp[i])
my_min()
for i in range(1, n-k+1):
    my_pop()
    push(inp[i+k-1])
    my_min()
"""

n,k = map(int, input().split())
a=[int(i) for i in input().split()]
m=9**99
for i in range(k):
    m=min(m,a[i])
print(m)
for i in range(1,n-k+1):
    if m != a[i-1]:
        if a[i+k-1] > m:
            print(m)
        else:
            m=a[i+k-1]
            print(m)
    else:
        m = 9 ** 99
        for j in range(i,i+k):
            m = min(m, a[j])
        print(m)