n = int(input())
source = [int(i) for i in input().split()]
# в обратном порядке
permut = [int(i) - 1 for i in input().split()][::-1]
# Великая и ужасная СНМ (с тяжелой оптимизацией) :(
def find(x):
    global parent
    if parent[x] != x:
        parent[x] = find(parent[x])
    return parent[x]

def union(x, y):
    global parent, rank, sum_trough, max_sum
    root_x = find(x)
    root_y = find(y)
    if root_x != root_y:
        if rank[root_x] > rank[root_y]:
            root_x, root_y = root_y, root_x
        if rank[root_x] == rank[root_y]:
            rank[root_y] += 1
        parent[root_x] = root_y
        sum_trough[root_y] += sum_trough[root_x]
    max_sum = max(max_sum, sum_trough[root_y])

answ = []
max_sum = 0
a = [-1] * n
sum_trough = source.copy()
rank = [1] * n
parent = [i for i in range(n)]
for i in permut:
    a[i] = source[i]
    if a[i-1] != -1 and i != 0:
        union(i, i-1)
    if i != n-1:
        if a[i+1] != -1:
            union(i, i+1)
    union(i, i)
    # print(a)
    # print(parent)
    # print(sum_trough)
    # print(max_sum)
    answ.append(max_sum)
for i in range(len(answ)-2, -1, -1):
    print(answ[i])
print(0)
