n = int(input())
a = [int(i) for i in input().split()]
q = int(input())
pairs = []
for i in range(q):
    s = input().split()
    tmpa = [int(s[0]), int(s[1])]
    pairs.append(tmpa)
prefix_right = [a[0]]
for i in range(1, n):
    prefix_right.append(prefix_right[i - 1] + a[i])

for i in range(q):
    #print(i)
    if pairs[i][0] == 1:
        print(prefix_right[pairs[i][1] - 1])
    else:
        print((-1)*prefix_right[pairs[i][0] - 2] + prefix_right[pairs[i][1] - 1])