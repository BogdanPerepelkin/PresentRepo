#include <iostream>
#include <vector>
#include <algorithm>
#include <cmath>
#include <map>

using namespace std;
using ll = long long;

vector<ll> length;
vector<ll> amount;

pair<ll, ll> get(ll v, ll l, ll r, ll pos, ll n);

void upd(ll v, ll l, ll r, ll pos, ll n) {
    if (r == l + 1) {
        pair<ll, ll> cur = get(0, 0, n, 0, pos);
        length[v] = cur.first + 1;
        if (cur.second == 0) {
            amount[v] += 1;
        } else {
            amount[v] += cur.second;
        }
        return;
    }
    ll m = (l + r) / 2;
    if (pos < m) {
        upd(2 * v + 1, l, m, pos, n);
    } else {
        upd(2 * v + 2, m, r, pos, n);
    }
    ll lInd = 2 * v + 1;
    ll rInd = 2 * v + 2;
    if (length[lInd] > length[rInd]) {
        length[v] = length[lInd];
        amount[v] = amount[lInd];
    } else if (length[rInd] > length[lInd]) {
        length[v] = length[rInd];
        amount[v] = amount[rInd];
    } else {
        length[v] = length[lInd];
        amount[v] = (amount[lInd] + amount[rInd]);
    }
}

pair<ll, ll> get(ll v, ll l, ll r, ll ql, ll qr) {
    if (ql <= l && r <= qr) {
        return make_pair(length[v], amount[v]);
    }
    if (r <= ql || l >= qr) {
        return make_pair(0, 0);
    }
    ll m = (l + r) / 2;
    pair<ll, ll> left = get(2 * v + 1, l, m, ql, qr);
    pair<ll, ll> right = get(2 * v + 2, m, r, ql, qr);
    if (left.first > right.first) {
        return left;
    }
    if (right.first > left.first) {
        return right;
    }
    return make_pair(left.first,
        (left.second + right.second)
        );
}

int main() {
    ll n;
    cin >> n;
    vector<ll> inp(n);
    for (ll i = 0; i < n; i++) {
        cin >> inp[i];
    }

    vector<ll> a(n);

    // for (ll i : a) {
    //     cout << i << " ";
    // }
    // cout << endl;

    vector<ll> sor = inp;
    sort(sor.begin(), sor.end());

    // for (ll i : sor) {
    //     cout << i << " ";
    // }
    // cout << endl;

    map<ll, ll> trans;
    for (ll i = 0; i < sor.size(); i++) {
        trans[sor[i]] = i;
    }

    for (ll i = 0; i < inp.size(); i++) {
        a[i] = (trans[inp[i]]);
    }

    // for (ll i : a) {
    //     cout << i << " ";
    // }
    // cout << endl;
    // cout << a[1] << endl;


    ll tmp = static_cast<ll>(ceil(log2(n))) + 1;
    ll tree_size = pow(2, tmp);
    length.resize(tree_size, 0);
    amount.resize(tree_size, 0);


    for (ll i = 0; i < n; i++) {
        upd(0, 0, n, a[i], n);
    }


    cout << amount[0] % ((long long) 1e9 + 7);
    return 0;
}
