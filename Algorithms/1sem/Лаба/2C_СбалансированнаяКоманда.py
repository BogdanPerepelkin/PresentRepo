n = int(input())
a = [int(i) for i in input().split()]
a = sorted(a)
l = 0
r = 0
max_diff = 0
while (r < len(a)):
    if (a[r] - a[l] > 5):
        l+=1
    else:
        r+=1
    max_diff = max(max_diff, r - l)

print(max_diff)


"""
1 2
10 12 15
17 12 15
"""