#include <iostream>
#include <vector>
#include <fstream>
#include <climits>
#include <queue>
#include <cstdlib>
#include <algorithm>
#include <random>

typedef long long ll;
using namespace std;

struct Node {
    ll key, prior, sum, size;
    Node *l = nullptr, *r = nullptr;
    Node (ll _key) {
        key = _key;
        static random_device rd;
        static mt19937 gen(rd());
        prior = gen();
        sum = _key;
        size = 1;
    }
};

void prTreap(Node* root) {
    if (!root) {
        cout << "Treap is empty" << endl;
        return;
    }

    queue<Node*> q;
    q.push(root);
    ll level = 0;

    cout << "Treap structure:" << endl;
    cout << "(key, prior, min, size, rev)" << endl;

    while (!q.empty()) {
        ll levelSize = q.size();
        cout << "Level " << level++ << ": ";

        for (ll i = 0; i < levelSize; ++i) {
            Node* current = q.front();
            q.pop();

            cout << "(" << current->key << ", "
                 << current->prior << ", "
                 << current->sum << ", "
                 << current->size << ") ";

            if (current->l) q.push(current->l);
            if (current->r) q.push(current->r);
        }
        cout << endl;
    }
    cout << endl;
}

ll getSum (Node* v) { return v ? v->sum : 0; }

ll getSize (Node *v) { return v ? v->size : 0; }

void upd (Node* v) {
    if (!v) return;
    v->sum = getSum(v->l) + getSum(v->r) + v->key;
    v->size = 1 + getSize(v->l) + getSize(v->r);
}


Node* merge (Node *l, Node *r) {
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
Pair split (Node *p, ll k) {
    if (!p) return {nullptr, nullptr};
    if (getSize(p->l) + 1 <= k) {
        Pair q = split(p->r, k - getSize(p->l) - 1);
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

void insert(Node*& root, ll key, ll pos) {
    Node* newNode = new Node(key);
    Pair q = split(root, pos);
    root = merge(q.first, merge(newNode, q.second));
}

// TODO исправить багу. Задача - понять как работает split (за что отвечает 2 паарметр)
// TODO для этого написать тест a=[1,2,3,4,5] и делать сплиты, смотреть на что он
// TODO делит, так понять по каким правилам выбирается k.
ll fnd_sum (Node*& root, ll l, ll r) {
    Pair rq = split(root, r);
    Pair lq = split(rq.first, l - 1);
    ll result = getSum(lq.second);
    // cout << result << endl;
    root = merge(lq.first, merge(lq.second, rq.second));
    return result;
}

void swapper (Node*& root_first, Node*& root_second, ll l_f, ll r_f, ll l_s, ll r_s) {
    Pair first_right = split(root_first, r_f);
    // l_f -1
    Pair first_left = split(first_right.first, l_f - 1);
    Pair second_right = split(root_second, r_s);
    // l_s -1
    Pair second_left = split(second_right.first, l_s - 1);
    root_first = merge(first_left.first, merge(second_left.second, first_right.second));
    root_second = merge(second_left.first, merge(first_left.second, second_right.second));
}

int main(int argc, char** argv) {
    ll global_counter = 1;
    while (true) {
        cout << "inside" << endl;
        ll n, m;
        cin >> n >> m;
        cout << "n, m -> " << n << m << endl;
        if (n == 0 && m == 0) break;
        cout << "Swapper " << global_counter  << ":" << endl;
        vector<ll> a(n);
        for (ll i = 0; i < n; i++) {
            cin >> a[i];
        }

        // declare pivos
        Node *even = nullptr;
        Node *odd = nullptr;

        // fill pivo
        ll counter = 1;
        for (ll i = 0; i < n; i = i + 2) {
            insert(odd, a[i], counter);
            counter++;
        }
        counter = 1;
        for (ll i = 1; i < n; i = i + 2) {
            insert(even, a[i], counter);
            counter++;
        }

        // // prTreap(odd);
        // // prTreap(even);

        for (ll i = 0; i < m; i++) {
            // cout << "begin " << i << endl;

            ll op, l, r;
            cin >> op >> l >> r;
            // cout << l << " " << r << endl;
            ll left_odd = l / 2 + 1;
            ll right_even = r / 2;
            ll right_odd, left_even;
            if (l % 2 != 0) { left_even = l / 2 + 1; } else { left_even = l / 2; }
            if (r % 2 != 0) { right_odd = r / 2 + 1; } else { right_odd = r / 2; }
            // cout << left_odd << " " << right_odd << " " << left_even << " " << right_even << endl;
            // prTreap(odd);
            // prTreap(even);
            if (op == 1) {
                swapper(odd, even, left_odd, right_odd, left_even, right_even);
            } else {
                ll sum = fnd_sum(odd, left_odd, right_odd) + fnd_sum(even, left_even, right_even);
                cout << sum << endl;
            }
            // cout << "end " << i << endl;
            // prTreap(odd);
            // prTreap(even);
        }
        global_counter++;
        cout << "now will next" << endl;
    }
    cout << "end blyat " << endl;
    return 0;
}






