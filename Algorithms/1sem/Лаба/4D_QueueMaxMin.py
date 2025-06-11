# идея - обычная очередь на 2 стеках, но когда во 2 пусто при копировании в него из 1 будем поддерживать минимум на 2 стеке
q = int(input())
a = []
s1 = []
s1_min = []
s2 = []
s2_min = []
def push(n):
    if len(s1) != 0:
        s1_min.append(min(s1_min[-1], n))
        s1.append(n)
    else:
        s1_min.append(n)
        s1.append(n)

def my_pop():
    if len(s2) == 0:
        while len(s1) != 0:
            if len(s2) != 0:
                s2.append(s1[-1])
                s2_min.append(min(s2_min[-1], s1[-1]))
            else:
                s2.append(s1[-1])
                s2_min.append(s1[-1])
            s1.pop()
            s1_min.pop()
    if len(s2) != 0:
        s2_min.pop()
        s2.pop()
def my_min():
    if len(s1) ==0 and len(s2)==0:
        print(-1)
    elif len(s1)==0:
        print(s2_min[-1])
    elif len(s2)==0:
        print(s1_min[-1])
    else:
        print(min(
            s1_min[-1],
            s2_min[-1]
        ))

counter=0
while (counter < q):
    s = input().split()
    if s[0] == "push":
        push(int(s[1]))
    elif s[0] == "pop":
        my_pop()
    elif s[0] == "min?":
        my_min()
    # print(s1)
    # print(s1_min)
    # print(s2)
    # print(s2_min)
    # print()
    counter+=1
