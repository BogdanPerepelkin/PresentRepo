n, w = map(int, input().split())
weights = [int(i) for i in input().split()]
costs = [int(i) for i in input().split()]
dp = []
for i in range(n+1):
    dp.append([0] * (w+1))

#заполнение dp
for i in range(1, n+1):
    for j in range(1, w+1):
        if j - weights[i-1] >= 0:
            dp[i][j] = max(
                dp[i-1][j],
                dp[i-1][j-weights[i-1]] + costs[i-1]
            )
        else:
            dp[i][j] = dp[i-1][j]

# for i in range(len(dp)):
#     print(dp[i])

#восстановление
i = n
j = w
seq = []
while i != 0:
    # if j == 0:
    #     seq.append(1)
    #     break
    if dp[i][j] == dp[i-1][j]:
        pass
    else:
        seq.append(i)
        j = max(0, j - weights[i-1])
    i-=1

print(len(seq))
print(*seq)

"""
4 6
2 4 1 2
7 2 5 1

"""