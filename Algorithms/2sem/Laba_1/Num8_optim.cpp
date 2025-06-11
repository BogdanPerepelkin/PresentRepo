#include <iostream>
#include <vector>
#include <algorithm>
#include <map>

using namespace std;

class Number8 {
public:
    static int n;
    static vector<int> uniqX, uniqY;
    static vector<vector<int>> request;
    static vector<int> minArr, lazy;
    static vector<long long> amount;
    static int flag;

    static void build(int v, int l, int r) {
        if (r == l + 1) {
            minArr[v] = 0;
            if (l != (int)uniqY.size() - 1) {
                int tmp = abs(uniqY[l + 1] - uniqY[l]);
                amount[v] = tmp;
            } else {
                amount[v] = 0;
            }
            return;
        }
        int m = (l + r) / 2;
        build(2 * v + 1, l, m);
        build(2 * v + 2, m, r);
        merge(v);
    }

    static void apply(int v, int val) {
        minArr[v] += val;
        lazy[v] += val;
    }

    static void push(int v) {
        if (lazy[v] != 0) {
            apply(2 * v + 1, lazy[v]);
            apply(2 * v + 2, lazy[v]);
            lazy[v] = 0;
        }
    }

    static void upd(int v, int l, int r, int ql, int qr, int var) {
        if (ql <= l && r <= qr) {
            apply(v, var);
            return;
        }
        if (r <= ql || l >= qr) return;
        push(v);
        int m = (l + r) / 2;
        upd(2 * v + 1, l, m, ql, qr, var);
        upd(2 * v + 2, m, r, ql, qr, var);
        merge(v);
    }

    static void merge(int v) {
        if (minArr[2 * v + 1] < minArr[2 * v + 2]) {
            minArr[v] = minArr[2 * v + 1];
            amount[v] = amount[2 * v + 1];
        } else if (minArr[2 * v + 1] > minArr[2 * v + 2]) {
            minArr[v] = minArr[2 * v + 2];
            amount[v] = amount[2 * v + 2];
        } else {
            minArr[v] = minArr[2 * v + 1];
            amount[v] = amount[2 * v + 1] + amount[2 * v + 2];
        }
    }

    // static vector<long long> get(int v, int l, int r, int ql, int qr) {
    //     if (ql <= l && r <= qr) {
    //         return vector<long long>{minArr[v], amount[v]};
    //     }
    //     if (r <= ql || l >= qr) {
    //         return vector<long long>{LLONG_MAX, 0};
    //     }
    //     push(v);
    //     int m = (l + r) / 2;
    //     vector<long long> left = get(2 * v + 1, l, m, ql, qr);
    //     vector<long long> right = get(2 * v + 2, m, r, ql, qr);
    //     if (left[0] < right[0]) {
    //         return left;
    //     } else if(left[0] > right[0]) {
    //         return right;
    //     } else {
    //         return vector<long long>{left[0], left[1] + right[1]};
    //     }
    // }

    static void main() {
        prepareData();
        if (flag) {
            cout << 0 << endl;
            return;
        }
        int treeSize = uniqY.size() * 4;
        minArr.assign(treeSize, 0);
        lazy.assign(treeSize, 0);
        amount.assign(treeSize, 0);
        build(0, 0, uniqY.size() - 1);

        long long answer = 0;
        for (size_t j = 0, g = 0; j < uniqX.size() - 1; ++j) {
            while (g < request.size() && request[g][2] == (int)j) {
                upd(0, 0, uniqY.size() - 1, request[g][3], request[g][4], request[g][1] == 0 ? 1 : -1);
                g++;
            }
            if (minArr[0] == 0) {
                answer += amount[0] * (uniqX[j + 1] - uniqX[j]);
            }
        }
        cout << ((long long)(uniqY.back() - uniqY.front()) * (uniqX.back() - uniqX.front())) - answer << endl;
    }

    static void prepareData() {
        cin >> n;
        if (n == 0) {
            flag = 1;
            return;
        }
        vector<int> x, y;
        request.reserve(n * 2);
        for (int i = 0; i < n; i++) {
            int x1, y1, x2, y2;
            cin >> x1 >> y1 >> x2 >> y2;
            x.push_back(x1);
            x.push_back(x2);
            y.push_back(y1);
            y.push_back(y2);
            request.push_back({i, 0, x1, y1, y2});
            request.push_back({i, 1, x2, y1, y2});
        }

        // realize unique and sorted (replace set + ArrayList)
        sort(x.begin(), x.end());
        sort(y.begin(), y.end());
        x.erase(unique(x.begin(), x.end()), x.end());
        y.erase(unique(y.begin(), y.end()), y.end());
        uniqX = move(x);
        uniqY = move(y);

        // squeeze
        map<int, int> mapX, mapY;
        for (size_t i = 0; i < uniqX.size(); ++i) mapX[uniqX[i]] = i;
        for (size_t i = 0; i < uniqY.size(); ++i) mapY[uniqY[i]] = i;
        for (auto& req : request) {
            req[2] = mapX[req[2]];
            req[3] = mapY[req[3]];
            req[4] = mapY[req[4]];
        }
        sort(request.begin(), request.end(), [](const vector<int>& a, const vector<int>& b) {
            return a[2] != b[2] ? a[2] < b[2] : a[1] < b[1];
        });
    }
};

int Number8::n = 0;
vector<int> Number8::uniqX, Number8::uniqY;
vector<vector<int>> Number8::request;
vector<int> Number8::minArr, Number8::lazy;
vector<long long> Number8::amount;
int Number8::flag = 0;

int main() {
    ios::sync_with_stdio(false);
    cin.tie(nullptr);
    Number8::main();
    return 0;
}
