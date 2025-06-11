a = []
def push(n):
    a.insert(0, n)
    print("ok")
def pop():
    print(a[-1])
    a.pop()
def front():
    print(a[-1])
def size():
    print(len(a))
def clear(a):
    a.clear()
    print("ok")
def exit():
    print("bye")

flag = True
while (flag):
    s = input().split()
    if s[0] == "exit":
        exit()
        break
    elif s[0] == "push":
        push(int(s[1]))
    elif s[0] == "pop":
        pop()
    elif s[0] == "front":
        front()
    elif s[0] == "size":
        size()
    elif s[0] == "clear":
        clear(a)
