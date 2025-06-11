n, m = map(int, input().split())
arr = [int(i) for i in input().split()]
arr.sort()
target = [int(i) for i in input().split()]

def bin_left(a, tar):
    l = -1
    r = len(a)
    while (r - l > 1):
        mid = (l + r) // 2
        if a[mid] < tar:
            l = mid
        else:
            r = mid
    if l >= 0 and a[l] == tar:
        return l
    if r >= len(a):
        return r - 1
    return r

def bin_right(a, tar):
    l = -1
    r = len(a)
    while (r - l > 1):
        mid = (l + r) // 2
        if (a[mid] > tar):
            r = mid
        else:
            l = mid
    if l >= 0 and a[l] == tar:
        return l
    if r >= len(a):
        return r - 1
    return r

for i in target:
    if (arr[bin_left(arr, i)] == i):
        print(bin_left(arr, i) + 1, bin_right(arr, i) + 1)
    else:
        print(0)