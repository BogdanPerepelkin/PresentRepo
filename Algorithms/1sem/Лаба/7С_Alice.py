n,m,l = map(int, input().split())
lengths = [int(ksi) for ksi in input().split()]

def find(x):
    global parent
    if parent[x] != x:
        parent[x] = find(parent[x])
    return parent[x]

def union(root_x, root_y):
    global parent, rank
    if rank[root_x] > rank[root_y]:
        root_x, root_y = root_y, root_x
    if rank[root_x] == rank[root_y]:
        rank[root_y] += 1
    parent[root_x] = root_y

def init_grow(x, d):
    global parent, rank, c, n, a
    flag = a[x] > l
    a[x] += d
    root_x = find(x)
    c_merged = 0
    f_edge = 0
    if x != n-1:
        root_y = find(x + 1)
        if a[x] > l and a[x+1] > l and root_x != root_y:
            union(root_x, root_y)
            c_merged += 1
    else:
        f_edge = 1

    if not flag:
        if x != 0:
            root_y = find(x - 1)
            if a[x] > l and a[x-1] > l and root_x != root_y:
                union(root_x, root_y)
                c_merged += 1
        else:
            f_edge = 1

        if f_edge == 0:
            if c_merged == 0 and a[x] > l:
                c+=1
            if c_merged == 2:
                c-=1
        else:
            if c_merged == 0 and a[x] > l:
                c+=1

rank=[0] * n
parent=[i for i in range(n)]
a=[0] * n
c=0

# заполнение изначальными данными
for i in range(len(lengths)):
    init_grow(i, lengths[i])

for _ in range(m):
    inp = input().split()
    type = int(inp[0])
    if type == 1:
        hair = int(inp[1]) - 1
        delta = int(inp[2])
        init_grow(hair, delta)
    else:
        print(c)
        # print(a)

"""
8 11 5
1 6 4 7 5 2 3 8
0
1 1 5
0
1 4 3
0
1 6 5
0
1 8 3
0
1 3 5
0


"""
