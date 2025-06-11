
n1 = int(input()) #кепки
a1 = sorted([int(i) for i in input().split()])
n2 = int(input()) #майки
a2 = sorted([int(i) for i in input().split()])
n3 = int(input()) #штаны
a3 = sorted([int(i) for i in input().split()])
n4 = int(input()) #ботинка
a4 = sorted([int(i) for i in input().split()])

def f(a):
    return max(a) - min(a)

i,j,g,k = 0,0,0,0
best = [a1[0], a2[0], a3[0], a4[0]]
best_r = f(best)
min_color = min(best)
while i < len(a1) and j < len(a2) and g < len(a3) and k < len(a4):
    cur_a = [a1[i], a2[j], a3[g], a4[k]]
    min_color = min(cur_a)
    if f(cur_a) < best_r:
        best_r = f(cur_a)
        best = cur_a.copy()
    if min_color == a1[i]:
        i+=1
    if min_color == a2[j]:
        j += 1
    if min_color == a3[g]:
        g+=1
    if min_color == a4[k]:
        k+=1

print(*best)
