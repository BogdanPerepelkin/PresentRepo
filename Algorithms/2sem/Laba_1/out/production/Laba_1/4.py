import math

n = int(input())
a=[]

for i in input().split():
    j = int(i)
    a.append(int(j))
m = int(input())

def build(v, l, r):
    if r == l+1:
        if l%2 == 0:
            t[v] = a[l]
        else:
            t[v] = (-1)*a[l]
        return
    m = (l+r) // 2
    build(2*v+1, l, m)
    build(2*v+2, m, r)
    t[v] = t[2*v+1] + t[2*v+2]

def upd(v, l, r, pos, val):
    if (r == l+1):
        if l % 2 == 0:
            t[v] = val
        else:
            t[v] = (-1) * val
        return
    m = (l + r) // 2
    if pos < m:
        upd(2*v+1, l, m, pos, val)
    else:
        upd(2*v+2, m, r, pos, val)
    t[v] = t[2*v+1] + t[2*v+2]

def get(v, l, r, ql, qr):
    if ql <= l and r <= qr:
        return t[v]
    if r <= ql or l >= qr:
        return 0
    m = (l+r)//2
    return get(2*v+1,l,m,ql,qr) + get(2*v+2, m, r, ql, qr)

tmp = math.log2(n)
length = int(tmp) + (1 if int(tmp) != tmp else 0) + 1
t = [0] * (2 ** length - 1)
build(0, 0, n)
# upd(0, 0, n, 0, 4)
# print(t)


for i in range(m):
    op, a, b = map(int, input().split())
    if op == 0:
        upd(0, 0, n, a-1, b)
    else:
        if (a-1) % 2 != 0:
            print((-1) * get(0, 0, n, a-1, b))
        else:
            print(get(0, 0, n, a-1, b))
    # print(t)
# print(t)
# print(get(0,0, n, 1, 3))


"""
[7, 1, 6, 0, 0, 2, 4]
"""