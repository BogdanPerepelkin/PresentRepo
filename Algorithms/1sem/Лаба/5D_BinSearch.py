n, k = map(int, input().split())
arr = [int(i) for i in input().split()]
arr.sort()
target = [int(i) for i in input().split()]

def bin_search(a, tar):
    l = -1
    r = len(a)
    while (r - l > 1):
        mid = (l + r) // 2
        if (a[mid] > tar):
            r = mid
        elif (a[mid] < tar):
            l = mid
        else:
            return mid
    if (a[l] == tar):
        return l
    return r

for i in target:
    tmp = bin_search(arr, i)
    if (tmp < len(arr) and arr[tmp] == i):
        print("YES")
    else:
        print("NO")