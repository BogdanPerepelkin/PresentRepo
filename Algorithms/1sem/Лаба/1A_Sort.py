n = int(input())
a = [int(i) for i in input().split()]
def merge_sort(a):
    if len(a) == 1:
        return a
    if len(a) == 2:
        if a[0] < a[1]:
            return a
        else:
            return a[::-1]
    la = merge_sort(a[:len(a)//2])
    ra = merge_sort(a[len(a)//2:])
    return merge(la, ra)
def merge(a, s):
    i = 0
    j = 0
    res = []
    while (i < len(a)) and (j < len(s)):
        if (a[i] < s[j]):
            res.append(a[i])
            i+=1
        else:
            res.append(s[j])
            j+=1
    if (i == len(a)):
        while j < len(s):
            res.append(s[j])
            j+=1
    else:
        while i < len(a):
            res.append(a[i])
            i+=1
    return res
print(*merge_sort(a))