import math


def build(v, l, r):
    if (r == l + 1):
        mx[v] = a[l] - a[l-1]
        mn[v] = a[l] - a[l-1]
        return
    m = (l + r) // 2
    build(2*v + 1, l, m)
    build(2*v + 2, m, r)
    l = 2*v+1
    r = 2*v+2
    mx[v] = max(mx[l], mx[r])
    mn[v] = min(mn[l], mn[r])

def upd(v, l, r, pos, val):
    if (r == l+1):
        mx[v] += val
        mn[v] += val
        return
    m = (l + r) // 2
    if (pos < m):
        upd(2*v + 1, l, m, pos, val)
    else:
        upd(2*v + 2, m, r, pos, val)
    l = 2*v+1
    r = 2*v+2
    mn[v] = min(mn[l], mn[r])
    mx[v] = max(mx[l], mx[r])

def get_lr(v, l, r, ql, qr):
    if (ql <= l and r <= qr):
        return mx[v]
    if (r <= ql or l >= qr):
        return float('-inf')
    m = (l + r) // 2
    left = get_lr(2 * v + 1, l, m, ql, qr)
    right = get_lr(2 * v + 2, m, r, ql, qr)
    return max(left, right)

def get_rl(v, l, r, ql, qr):
    if (ql <= l and r <= qr):
        return mn[v]
    if (r <= ql or l >= qr):
        return float('inf')
    m = (l + r) // 2
    left = get_rl(2 * v + 1, l, m, ql, qr)
    right = get_rl(2 * v + 2, m, r, ql, qr)
    return min(left, right)


n, m = map(int, input().split())
a = [int(i) for i in input().split()]
n = n - 1
tmp = math.log2(n)
length = 2 ** (int(tmp) + (1 if int(tmp) != tmp else 0) + 1) - 1
mx = [0] * length
mn = [0] * length
build(0, 1, n+1)
# print(mx)
# print(mn)


for i in range(m):
    inp = [int(j) for j in input().split()]
    op = inp[0]
    if op == 1:
        if inp[1] < inp[2]:
            tmp = get_lr(0, 0, n, inp[1] - 1, inp[2] - 1)
            if tmp <= 1:
                print("Yes")
            else:
                print("No")
        else:
            tmp = get_rl(0, 0, n, inp[2] - 1, inp[1] - 1)
            if tmp >= -1:
                print("Yes")
            else:
                print("No")
        # print(tmp)
    else:
        if inp[1] == 1:
            pass
        else:
            upd(0, 0, n, inp[1] - 1 - 1, inp[3])
        if inp[2] == n + 1:
            pass
        else:
            upd(0, 0, n, inp[2] - 1, (-1)*inp[3])
        # print(mx)
        # print(mn)


# 3 4 6 4 5
#



