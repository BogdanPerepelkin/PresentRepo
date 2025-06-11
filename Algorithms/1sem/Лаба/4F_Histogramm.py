inp = [int(i) for i in input().split()]
n = inp[0]
h = inp[1:]

stack = []
maxS = 0
i = 0
while i < len(h):
    if len(stack) == 0 or h[i] >= h[stack[-1]]:
        stack.append(i)
        i += 1
    else:
        curMax = stack[-1]
        stack.pop(len(stack) - 1)
        if (len(stack) == 0):
            width = i
        else:
            width = i - stack[-1] - 1
        maxS = max(maxS, h[curMax] * width)

while len(stack) != 0:
    curMax = stack[-1]
    stack.pop(len(stack) - 1)
    width = 0
    if (len(stack) == 0):
        width = i
    else:
        width = i - stack[-1] - 1
    maxS = max(maxS, h[curMax] * width)

print(maxS)