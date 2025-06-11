n = int(input())
a = [int(i) for i in input().split()]
answ = [0] * n
stack = []

for i in range(0, n):
    if (len(stack)) > 0:
        while (a[stack[-1]] > a[i]):
            answ[stack[-1]] = i
            stack.pop(-1)
            if len(stack) == 0:
                break
    stack.append(i)
for i in stack:
    answ[i] = -1

print(*answ)