a = []
def push(n):
    a.append(n)
    return "ok"
def pop():
    tmp = a[len(a) - 1]
    a.pop(len(a) - 1)
    return tmp
def back():
    return a[len(a) - 1]
def size():
    return len(a)
def clear(a):
    a.clear()
    return "ok"
def exit():
    return "bye"

while (True):
    s = input().split()
    if s[0] == "push":
        print(push(int(s[1])))
    elif s[0] == "pop":
        print(pop())
    elif s[0] == "back":
        print(back())
    elif s[0] == "size":
        print(size())
    elif s[0] == "clear":
        print(clear(a))
    else:
        print(exit())
        break