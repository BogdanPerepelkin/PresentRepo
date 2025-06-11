import math


def build(v, l, r):
    if (r == l + 1):
        t[v] = a[l]
        return
    m = (l + r) // 2
    build(2*v + 1, l, m)
    build(2*v + 2, m, r)
    l = 2*v+1
    r = 2*v+2
    t[v] = t[l] + t[r]

def upd(v, l, r, pos, val):
    if (r == l+1):
        t[v] = val
        return
    m = (l + r) // 2
    if (pos < m):
        upd(2*v + 1, l, m, pos, val)
    else:
        upd(2*v + 2, m, r, pos, val)
    l = 2*v+1
    r = 2*v+2
    t[v] = t[l] + t[r]

def get(v, l, r, ql, qr):
    if (ql <= l and r <= qr):
        return t[v]
    if (r <= ql or l >= qr):
        return 0
    m = (l + r) // 2
    left = get(2 * v + 1, l, m, ql, qr)
    right = get(2 * v + 2, m, r, ql, qr)
    return left + right

n = int(input())
a = [int(i) for i in input().split()]
# print(a)
sa = sorted(a)
na = [sa.index(i) for i in a]


tmp = math.log2(n)
length = 2 ** (int(tmp) + (1 if int(tmp) != tmp else 0) + 1) - 1
t = [0] * length

# directly algorithm
accum = 0
for i in range(n - 1, -1, -1):
    cur_ind = na[i]
    sm = get(0, 0, n, 0, cur_ind + 1)
    accum += sm
    upd(0, 0, n, cur_ind, 1)
print(accum)
