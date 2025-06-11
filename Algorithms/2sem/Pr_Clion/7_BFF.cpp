#include <iostream>
#include <vector>
#include <fstream>
#include <algorithm>
#include <cmath>

using namespace std;
typedef long long ll;


vector<int> log_table;
vector<vector<ll>> sparse_a, sparse_b;
int n;

ll get_min(int l, int r) {
    int i = log_table[r - l + 1];
    return min(sparse_a[i][l], sparse_a[i][r - (1 << i) + 1]);
}

ll get_max(int l, int r) {
    int i = log_table[r - l + 1];
    return max(sparse_b[i][l], sparse_b[i][r - (1 << i) + 1]);
}

void build_sparce(const vector<ll>& a, vector<vector<ll>>& sparce, int mode) {
    int m = floor(log(n) / log(2)) + 1;
    sparce.resize(m, vector<ll>(n));

    // fill floor
    for (int i = 0; i < n; i++) {
        sparce[0][i] = a[i];
    }

    // fill out rest of sparce
    for (int i = 1; i < m; i++) {
        int shift = pow(2, i - 1);
        int cur_edge = n - 2 * shift;
        for (int j = 0; j + (1 << i) <= n; j++) { // j <= cur_edge
            if (mode == 0) {
                sparce[i][j] =
                    min(sparce[i - 1][j],
                        sparce[i - 1][j + shift]);
            } else {
                sparce[i][j] =
                    max(sparce[i - 1][j],
                        sparce[i - 1][j + shift]);
            }
        }
    }
}

void build_log_table() {
    log_table.resize(n + 1);
    log_table[1] = 0;
    for (int i = 2; i <= n; i++) {
        log_table[i] = log_table[i / 2] + 1;
    }
}

// void printVector(vector<int>& v) {
//     for (int x : v) {
//         cout << x << " ";
//     }
//     cout << endl;
// }

int main() {
    cin >> n;
    vector<ll> a(n), b(n);
    for (int i = 0; i < n; ++i) {
        cin >> a[i];
    }
    for (int i = 0; i < n; ++i) {
        cin >> b[i];
    }

    build_log_table();

    // vector<vector<ll>> sparce_a;
    // vector<vector<ll>> sparce_b;

    build_sparce(a, sparse_a, 0);
    build_sparce(b, sparse_b, 1);

    ll accum = 0;
    for (ll i = 0; i < n; i++) {

        // bin search for l
        ll low = i - 1,
        high = n;
        ll left = -1;
        while (high - low > 1) {
            ll mid = (low + high) / 2;
            ll minA = get_min(i, mid);
            ll maxB = get_max(i, mid);
            // cout << "left: " << i << "; right: " << mid << endl;
            // cout << minA << " " << maxB << endl;

            if (minA == maxB) {
                left = mid;
                high = mid;
            } else if (maxB < minA) {
                low = mid;
            } else {
                high = mid;
            }
        }
        // cout << "bins answ: " << left << endl;


        // bin search for r
        low = i - 1;
        high = n;
        ll right = -1;
        while (high - low > 1) {
            ll mid = (high + low) / 2;
            ll minA = get_min(i, mid);
            ll maxB = get_max(i, mid);

            if (minA == maxB) {
                right = mid;
                low = mid;
            } else if (maxB < minA) {
                low = mid;
            } else {
                high = mid;
            }
        }

        if (left != -1 && right != -1) {
            accum += right - left + 1;
        }
    }
    cout << accum;
    return 0;
}