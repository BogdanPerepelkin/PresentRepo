n = int(input())
a = []
s = input()
for i in s:
    if i == 'w':
        a.append(0) # swamp
    elif i == '"':
        a.append(1) # grass green and tasty
    else:
        a.append(2) # nth
#+1, +3, +5
dp=[-1,-1,-1,-1,-1,0] + [0] * (n-1) # how many cells grass
for i in range(6,n+5):
    if a[i-5] == 0:
        dp[i] = -1
    elif a[i-5] == 1:
        tmp = max(dp[i - 1], dp[i - 3], dp[i - 5])
        if tmp == -1:
            dp[i] = -1
        else:
            dp[i] = tmp + 1
    elif a[i-5] == 2:
        tmp = max(dp[i - 1], dp[i - 3], dp[i - 5])
        if tmp == -1:
            dp[i] = -1
        else:
            dp[i] = tmp
print(dp[n+4])