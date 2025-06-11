k,s=map(int,input().split())
#k - кол-во цифр в числе
#s - сумма цифр

dp = [1]*min(s+1,10)
dp_new = [0] * (s + 1)

for i in range(1,k):
    for j in range(len(dp)):
        for digit in range(min(s-j+1,10)): #min(10, j+1)
            dp_new[j+digit] = (dp_new[j+digit] + dp[j]) % (10**9 + 7)
    dp = dp_new
    dp_new = [0]*(s+1)


print(dp[s])
# print(dp)
# print(dp_new)