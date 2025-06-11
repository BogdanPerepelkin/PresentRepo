n, k = map(int, input().split())
a = [int(i) for i in input().split()]

# окно со сдвигом
l=0
plased=0
answ_l=-1
answ_r=-1
min_quant = 9**99
for i in range(n):
    plased += a[i]
    while plased >= k:
        if i - l + 1 < min_quant and plased == k:
            answ_l = l
            answ_r = i
            min_quant = i - l + 1
        plased -= a[l]
        l+=1
if answ_l == -1:
    print(-1)
else:
    print(answ_l+1, answ_r + 1)