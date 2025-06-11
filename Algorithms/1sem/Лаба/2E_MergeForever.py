ind = int(input())
c = set()
i = 1
j = 1
while (len(c) < ind):
    while (i**2 < j**3):
        c.add(i**2)
        i+=1
    c.add(j**3)
    j+=1
c = sorted(list(c))
print(c[ind - 1])
