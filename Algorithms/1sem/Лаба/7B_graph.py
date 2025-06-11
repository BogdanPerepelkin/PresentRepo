# Великая и ужасная СНМ :)
def find(x):
    global parent
    if parent[x] != x:
        parent[x] = find(parent[x])
    return parent[x]

def union(x, y):
    global parent, rank, edge
    root_x = find(x)
    root_y = find(y)
    if root_x != root_y:
        if rank[root_x] > rank[root_y]:
            root_x, root_y = root_y, root_x
        if rank[root_x] == rank[root_y]:
            rank[root_y] += 1
        parent[root_x] = root_y
    edge.append([x, y])

n,m,k = map(int, input().split())
beg_edge=[]
for _ in range(m):
    beg_edge.append([int(g)-1 for g in input().split()])
ask_rev=[]
fill=[]
for _ in range(k):
    s=input()
    if s[:3] == 'ask':
        ask_rev.insert(0, [int(g)-1 for g in s[3:].split()])
    else:
        ask_rev.insert(0,[-1,0])
        fill.insert(0, [int(g)-1 for g in s[3:].split()])

rank=[0] * n
edge=[]
parent=[i for i in range(n)]
answ_rev=[]
j=0
for i in range(len(fill)):
    while ask_rev[j][0] != -1:
        ask_1 = ask_rev[j][0]
        ask_2 = ask_rev[j][1]
        r1 = find(ask_1)
        r2 = find(ask_2)
        if r1 == r2:
            answ_rev.append("YES")
        else:
            answ_rev.append("NO")
        j+=1
    union(fill[i][0],fill[i][1])
    j+=1
while j < len(ask_rev):
    ask_1 = ask_rev[j][0]
    ask_2 = ask_rev[j][1]
    r1 = find(ask_1)
    r2 = find(ask_2)
    if r1 == r2:
        answ_rev.append("YES")
    else:
        answ_rev.append("NO")
    j+=1
# print('ask_rev', ask_rev)
# print('fill', fill)
# print('answ_rev', answ_rev)
for i in range(len(answ_rev)-1,-1,-1):
    print(answ_rev[i])
