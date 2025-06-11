#include <iostream>
#include <vector>
#include <fstream>
#include <climits>
#include <queue>
#include <cstdlib>
#include <algorithm>
#include <random>

using namespace std;
typedef long long ll;

struct FenwickTree {
    vector<vector<vector<ll>>> tree;
    ll n;

    ll f(ll x) {
        return (x & (x + 1)) - 1;
    }

    ll get_sum(ll x1, ll y1, ll z1) {
        ll result = 0;
        for (ll i = x1; i >= 0; i = f(i)) {
            for (ll j = y1; j >= 0; j = f(j)) {
                for (ll k = z1; k >= 0; k = f(k)) {
                    result += tree[i][j][k];
                }
            }
        }
        return result;
    }

    ll find_sum(ll x1, ll y1, ll z1, ll x2, ll y2, ll z2) {
        ll result = 0;
        result =
            get_sum(x2, y2, z2)
            - get_sum(x1 - 1, y2, z2)
            - get_sum(x2, y1 - 1, z2)
            - get_sum(x2, y2, z1 - 1)
            + get_sum(x1 - 1, y1 - 1, z2)
            + get_sum(x1 - 1, y2, z1 - 1)
            + get_sum(x2, y1 - 1, z1 - 1)
            - get_sum(x1 - 1, y1 - 1, z1 - 1);
        return result;
    }

    void increase(ll x, ll y, ll z, ll inc) {
        // null-numerate rulit!!!
        for (ll i = x; i < n; i = i | (i + 1)) {
            for (ll j = y; j < n; j = j | (j + 1)) {
                for (ll k = z; k < n; k = k | (k + 1)) {
                    tree[i][j][k] += inc;
                }
            }
        }
    }

};

int main() {
    int n;
    cin >> n;
    // cout << "n = " << n << endl;

    FenwickTree tree;
    tree.tree.assign(n, vector<vector<ll>>(n, vector<ll>(n, 0)));
    tree.n = n;

    while (true) {
        int m;
        cin >> m;
        // cout << "m = " << m << endl;
        if (m == 1) {
            int x, y, z, k;
            cin >> x >> y >> z >> k;
            tree.increase(x, y, z, k);
        } else if (m == 2) {
            int x1, y1, z1, x2, y2, z2;
            cin >> x1 >> y1 >> z1 >> x2 >> y2 >> z2;
            ll answer = tree.find_sum(x1, y1, z1, x2, y2, z2);
            cout << answer << endl;
        } else if (m == 3 ) {
            break;
        } else {
            cout << "error, unexpected m" << endl;
        }
    }

    return 0;
}