n, k = map(int, input().split())
a = [int(i) for i in input().split()]
a = sorted(a)
#print(a)

def able2place(a, k, dist):
    count = 1
    last = a[0]
    for i in range(1, len(a)):
        if a[i] - last >= dist:
            count+=1
            last = a[i]
    if count >= k:
        return True
    return False

min_dist = 1
max_dist = a[-1] - a[0]
answ = 0
while min_dist <= max_dist:
    mid = (max_dist + min_dist) // 2
    if able2place(a, k, mid):
        answ = mid
        min_dist = mid + 1
    else:
        max_dist = mid - 1

print(answ)