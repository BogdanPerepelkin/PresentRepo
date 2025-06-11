import math


def upd(v, l, r, pos, val):
    if (r == l+1):
        cur = get(0, 0, n, 0, pos)
        length[v] = cur[0] + 1
        amount[v] = (amount[v] + max(cur[1], 1)) % (10**9 + 7)
        return
    m = (l + r) // 2
    if (pos < m):
        upd(2*v + 1, l, m, pos, val)
    else:
        upd(2*v + 2, m, r, pos, val)
    l = 2*v+1
    r = 2*v+2
    if length[l] > length[r]:
        length[v] = length[l]
        amount[v] = amount[l] % (10**9 + 7)
    elif length[r] > length[l]:
        length[v] = length[r]
        amount[v] = amount[r] % (10**9 + 7)
    else:
        length[v] = length[l]
        amount[v] = (amount[l] + amount[r]) % (10**9 + 7)


def get(v, l, r, ql, qr):
    if ql <= l and r <= qr:
        return [length[v], amount[v]]
    if r <= ql or l >= qr:
        return [0, 0]
    m = (l+r)//2
    left = get(2*v+1,l,m,ql,qr)
    right = get(2*v+2, m, r, ql, qr)
    if left[0] > right[0]:
        return left
    elif right[0] > left[0]:
        return right
    return [left[0], (left[1] + right[1]) % (10**9 + 7)]


n = int(input())
# a = [int(i) for i in input().split()]
# n = max(a)
inp = [int(i) for i in input().split()]
sor = sorted(inp)
a = [sor.index(i) for i in inp]

# print(a)

tmp = math.log2(n)
tree_size = 2 ** (int(tmp) + (1 if int(tmp) != tmp else 0) + 1) - 1
length = [0] * tree_size
amount = [0] * tree_size


for i in range(len(a)):
    upd(0, 0, n, a[i], 1)

print(amount[0] % (10**9 + 7))



