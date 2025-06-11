n1 = int(input())
a1 = [int(i) for i in input().split()]
n2 = int(input())
a2 = [int(i) for i in input().split()]
a1 = sorted(a1)
a2 = sorted(a2)

i,j = 0,0
bi = i
bj = j
while i < n1 and j < n2:
    if (abs(a1[i] - a2[j]) < abs(a1[bi] - a2[bj])):
        bi = i
        bj = j
    if (a1[i] < a2[j]):
        i+=1
    else:
        j+=1
print(a1[bi], a2[bj])