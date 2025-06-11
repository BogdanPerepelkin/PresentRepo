#include <iostream>
#include <vector>
#include <fstream>
#include <algorithm>
#include <cmath>

using namespace std;


// sparse table: O(1) - req
//               O(nlogn) - data
vector<int> log_table;
vector<vector<int>> sparce;
vector<int> a;
int n;

int find_minimum(int l, int r) {
    int i = log_table[r - l + 1];
    int shift_right_bound = r - pow(2, i) + 1;
    return min(sparce[i][l], sparce[i][shift_right_bound]);
}

void build_sparce(const vector<int>& a) {
    int m = floor(log(n) / log(2)) + 1;
    sparce.resize(m, vector<int>(n));

    // fill floor
    for (int i = 0; i < n; i++) {
        sparce[0][i] = a[i];
    }

    // fill out rest of sparce
    for (int i = 1; i < m; i++) {
        int shift = pow(2, i - 1);
        int cur_edge = n - 2 * shift;
        for (int j = 0; j <= cur_edge; j++) {
            sparce[i][j] =
                min(sparce[i - 1][j],
                    sparce[i - 1][j + shift]);
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

int count_next_a(const int a_i) {
    return (23 * a_i + 21563) % 16714589;
}
void fiint_stock_array(int a1) {
    a.resize(n);
    a[0] = a1;
    for (int i = 1; i < n; i++) {
        a[i] = count_next_a(a[i - 1]);
    }
}

void printVector(vector<int>& v) {
    for (int x : v) {
        cout << x << " ";
    }
    cout << endl;
}

int main() {
    // read input data
    int m, a1;
    cin >> n >> m >> a1;
    int u, v;
    cin >> u >> v;

    // create derivative data in "a"
    fiint_stock_array(a1);

    // create log_table
    build_log_table();

    // create sparce_table
    build_sparce(a);

    // process requests
    // printVector(a);

    // cout << n << endl;

    int answer = 0;
    for (int i = 1; i < m; i++) {
        answer = find_minimum(min(u - 1, v - 1), max(u - 1, v - 1));
        // cout << u << " " << v << " " << answer << endl;
        u = (17 * u + 751 + answer + 2 * i) % n + 1;
        v = (13 * v + 593 + answer + 5 * i) % n + 1;
    }

    // print answer
    cout << u << " " << v << " " << find_minimum(min(u - 1, v - 1), max(u - 1, v - 1)) << endl;

    return 0;
}