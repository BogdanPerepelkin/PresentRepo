import math


def build(v, l, r):
    if r == l+1:
        t[v] = w
        return
    m = (l+r) // 2
    build(2*v+1, l, m)
    build(2*v+2, m, r)
    t[v] = max(t[2*v+1], t[2*v+2])


def upd(v, l, r, req):
    if (r == l+1):
        if t[v] >= req:
            t[v] -= req
            print(l + 1)
        else:
            print(-1)
        return
    m = (l + r) // 2
    left = 2*v + 1
    right = 2*v + 2
    if t[left] >= req:
        upd(left, l, m, req)
    elif t[right] >= req:
        upd(right, m, r, req)
    else:
        print(-1)
    t[v] = max(t[2*v+1], t[2*v+2])


def get(v, l, r, ql, qr):
    if ql <= l and r <= qr:
        return t[v]
    if r <= ql or l >= qr:
        return 0
    m = (l+r)//2
    return get(2*v+1,l,m,ql,qr) + get(2*v+2, m, r, ql, qr)


# в дереве храним максимум
h, w, n = map(int, input().split())

tmp = math.log2(min(n + 1, h))
length = int(tmp) + (1 if int(tmp) != tmp else 0) + 1
t = [0] * (2 ** length - 1)
build(0, 0, min(n + 1, h))
# print(t)

for i in range(n):
    req = int(input())
    upd(0, 0, min(n + 1, h), req)





