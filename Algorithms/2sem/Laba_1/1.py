import math

def show():
    print(sm)
    print(mx)
    print(pref)
    print(suf)
    print()

def build(v, l, r):
    # show()
    if (r == l + 1):
        sm[v] = a[l]
        mx[v] = a[l]
        pref[v] = a[l]
        suf[v] = a[l]
        return
    m = (l + r) // 2
    build(2*v + 1, l, m)
    build(2*v + 2, m, r)
    l = 2*v+1
    r = 2*v+2
    mx[v] = max(mx[l], mx[r], suf[l] + pref[r])
    sm[v] = sm[l] + sm[r]
    pref[v] = max(sm[l] + pref[r], pref[l])
    suf[v] = max(suf[r], suf[l] + sm[r])

def upd(v, l, r, pos, val):
    if (r == l+1):
        sm[v] = val
        mx[v] = val
        pref[v] = val
        suf[v] = val
        return
    m = (l + r) // 2
    if (pos < m):
        upd(2*v + 1, l, m, pos, val)
    else:
        upd(2*v + 2, m, r, pos, val)
    l = 2*v+1
    r = 2*v+2
    mx[v] = max(mx[l], mx[r], suf[l] + pref[r])
    sm[v] = sm[l] + sm[r]
    pref[v] = max(sm[l] + pref[r], pref[l])
    suf[v] = max(suf[r], suf[l] + sm[r])

def get(v, l, r, ql, qr):
    if (ql <= l and r <= qr):
        return [sm[v], mx[v], pref[v], suf[v]]
    if (r <= ql or l >= qr):
        return [0, -99999, -99999, -99999]
    m = (l + r) // 2
    left = get(2 * v + 1, l, m, ql, qr)
    right = get(2 * v + 2, m, r, ql, qr)
    cur_sm = left[0] + right[0]
    cur_mx = max(left[1], right[1], left[3] + right[2])
    cur_pref = max(left[2], left[0] + right[2])
    cur_suf = max(right[3], right[0] + left[3])
    return [cur_sm, cur_mx, cur_pref, cur_suf]


n, m = map(int, input().split())
a = [int(i) for i in input().split()]

tmp = math.log2(n)
length = int(tmp) + (1 if int(tmp) != tmp else 0) + 1
pref = [0] * (2 ** length - 1)
suf = [0] * (2 ** length - 1)
mx = [0] * (2 ** length - 1)
sm = [0] * (2 ** length - 1)
build(0, 0, n)


for i in range(m):
    inp = input().split()
    op = inp[0]
    a = int(inp[1])
    b = int(inp[2])
    # print(sm)
    # print(mx)
    # print(pref)
    # print(suf)
    if op == "change":
        upd(0, 0, n, a-1, b)
    else:
        print(max(0, get(0, 0, n, a-1, b)[1]))