n = int(input())
a = [int(i) for i in input().split()]
count = 0
def merge_sort(a, count):
    if len(a) == 1:
        return a, count
    if len(a) == 2:
        if a[0] < a[1]:
            return a, count
        else:
            return a[::-1], count + 1
    la, c1 = merge_sort(a[:len(a)//2], count)
    ra, c2 = merge_sort(a[len(a)//2:], count)
    return merge(la, ra, c1 + c2)

def merge(a, s, count):
    i = 0
    j = 0
    res = []
    while (i < len(a)) and (j < len(s)):
        if (a[i] <= s[j]):
            res.append(a[i])
            i+=1
        else:
            res.append(s[j])
            j+=1
            count = count + len(a) - i
    if (i == len(a)):
        while j < len(s):
            res.append(s[j])
            j+=1
    else:
        while i < len(a):
            res.append(a[i])
            #count = count + len(a) - i
            i+=1
    return res, count
print(merge_sort(a, 0)[1])