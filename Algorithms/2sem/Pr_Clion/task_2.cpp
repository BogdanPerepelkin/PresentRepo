#include <iostream>
#include <vector>
#include <fstream>
#include <climits>
#include <queue>
#include <cstdlib>
#include <algorithm>
#include <random>


using namespace std;

struct Node {
    int key, prior, minVal, size;
    Node *l = nullptr, *r = nullptr;
    bool rev;
    Node (int _key) {
        key = _key;
        static random_device rd;
        static mt19937 gen(rd());
        prior = gen();
        minVal = _key;
        size = 1;
        rev = false;
    }
};

int getMin (Node* v) { return v ? v->minVal : INT_MAX; }

int supSize (Node *v) { return v ? v->size : 0; }

void upd (Node* v) {
    if (!v) return;
    v->minVal = min(getMin(v->l), min(getMin(v->r), v->key));
    v->size = 1 + supSize(v->l) + supSize(v->r);
}

void push (Node *v) {
    if (!v) return;
    if (!v->rev) return;
    if (v->rev) {
        // NOTE: swap?
        swap(v->l, v->r);
        if (v->l) v->l->rev ^= true;
        if (v->r) v->r->rev ^= true;
    }
    v->rev = false;
}

Node* merge (Node *l, Node *r) {
    push(l);
    push(r);
    if (!l) return r;
    if (!r) return l;
    if (l->prior > r->prior) {
        l->r = merge(l->r, r);
        upd(l);
        return l;
    }
    r->l = merge(l, r->l);
    upd(r);
    return r;
}

typedef pair<Node*, Node*> Pair;

// 1-numerate
Pair split (Node *p, int k) {
    push(p);
    if (!p) return {nullptr, nullptr};
    if (supSize(p->l) + 1 <= k) {
        Pair q = split(p->r, k - supSize(p->l) - 1);
        p->r = q.first;
        upd(p);
        // upd(q.second);
        return {p, q.second};
    }
    Pair q = split(p->l, k);
    p->l = q.second;
    upd(p);
    // upd(q.first);
    return {q.first, p};
}

Node *root = nullptr;

void reverse (int l, int r) {
    Pair q1 = split(root, r);
    // NOTE: -1
    Pair q2 = split(q1.first, l - 1);
    if (q2.second) {
        q2.second->rev = true;
    }
    root = merge(q2.first, merge(q2.second, q1.second));
}

void printTreap(Node* root) {
    if (!root) {
        cout << "Treap is empty" << endl;
        return;
    }

    queue<Node*> q;
    q.push(root);
    int level = 0;

    cout << "Treap structure:" << endl;
    cout << "(key, prior, min, size, rev)" << endl;

    while (!q.empty()) {
        int levelSize = q.size();
        cout << "Level " << level++ << ": ";

        for (int i = 0; i < levelSize; ++i) {
            Node* current = q.front();
            q.pop();

            cout << "(" << current->key << ", "
                 << current->prior << ", "
                 << current->minVal << ", "
                 << current->size << ", "
                 << (current->rev ? "T" : "F") << ") ";

            if (current->l) q.push(current->l);
            if (current->r) q.push(current->r);
        }
        cout << endl;
    }
    cout << endl;
}

void insert(int key, int pos) {
    Node* newNode = new Node(key);
    Pair q = split(root, pos);
    root = merge(q.first, merge(newNode, q.second));

    // Node* newNode = new Node(key);
    // root = merge(root, newNode);

    // Pair q = split(root, x);
    // Node *t = new Node(x);
    // root = merge(q.first, merge(t, q.second));
}

int minimum (int l, int r) {
    Pair rq = split(root, r);
    Pair lq = split(rq.first, l - 1);
    // printTreap(lq.first);
    // printTreap(lq.second);
    // printTreap(rq.first);
    // printTreap(rq.second);
    int result = getMin(lq.second);
    root = merge(lq.first, merge(lq.second, rq.second));
    // printTreap(root);
    return result;
}

int main(int argc, char** argv) {
    int n, m;
    cin >> n >> m;

    vector<int> stock(n);
    for (int i = 0; i < n; i++) {
        cin >> stock[i];
    }
    // DEBUG
    // printf("stock\n");
    // for (int i = 0; i < n; i++) {
    //     cout << stock[i] << " ";
    // }
    // cout << endl << endl;

    vector<vector<int>> req(m);
    for (int i = 0; i < m; i++) {
        int type, l, r;
        cin >> type >> l >> r;
        req[i] = {type, l, r};
    }
    // DEBUG
    // printf("req\n");
    // for (int i = 0; i < m; i++) {
    //     cout << req[i][0] << " " << req[i][1] << " " << req[i][2] << endl;
    // }
    // cout << endl;

    // fill pivo
    for (int i = 0; i < n; i++) {
        insert(stock[i], i + 1);
    }

    for (int i = 0; i < m; i++) {
        vector<int> cur = req[i];
        if (cur[0] == 2) {
            cout << minimum(cur[1], cur[2]) << endl;
        } else {
            reverse(cur[1], cur[2]);
        }
        // printTreap(root);
    }

    return 0;
}
