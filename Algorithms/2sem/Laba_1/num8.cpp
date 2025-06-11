#include <iostream>
#include <vector>
#include <set>
#include <map>
#include <algorithm>
#include <sstream>
#include <string>
#include <cstdlib>
#include <climits>

using namespace std;

class Number8 {
public:
    static int n;
    static set<int> setX;
    static set<int> setY;
    static vector<int> uniqX;
    static vector<int> uniqY;
    static vector< vector<int> > request; // index, type, x, y1, y2
    static vector<int> minArr;
    static vector<long long> amount;
    static vector<int> lazy; // TODO потенциально long
    static int flag;

    // fill tree with addition inspired stock y_values
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
        int m = (r + l) / 2;
        build(2 * v + 1, l, m);
        build(2 * v + 2, m, r);
        int left = 2 * v + 1;
        int right = 2 * v + 2;
        if (minArr[left] < minArr[right]) {
            minArr[v] = minArr[left];
            amount[v] = amount[left];
        } else if (minArr[left] > minArr[right]) {
            minArr[v] = minArr[right];
            amount[v] = amount[right];
        } else {
            minArr[v] = minArr[left];
            amount[v] = amount[left] + amount[right];
        }
    }

    static void push(int v) {
        if (lazy[v] != 0) {
            apply(2 * v + 1, lazy[v]);
            apply(2 * v + 2, lazy[v]);
            lazy[v] = 0;
        }
    }

    static void apply(int v, int val) {
        minArr[v] = minArr[v] + val;
        lazy[v] += val;
    }

    static void upd(int v, int l, int r, int ql, int qr, int var) {
        if (ql <= l && r <= qr) {
            apply(v, var);
            return;
        }
        if (r <= ql || l >= qr) {
            return;
        }
        push(v);
        int m = (l + r) / 2;
        int left = 2 * v + 1;
        int right = 2 * v + 2;
        upd(left, l, m, ql, qr, var);
        upd(right, m, r, ql, qr, var);
        if (minArr[left] < minArr[right]) {
            minArr[v] = minArr[left];
            amount[v] = amount[left];
        } else if (minArr[left] > minArr[right]) {
            minArr[v] = minArr[right];
            amount[v] = amount[right];
        } else {
            minArr[v] = minArr[left];
            amount[v] = amount[left] + amount[right];
        }
    }

    static vector<long long> get(int v, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr) {
            return vector<long long>{minArr[v], amount[v]};
        }
        if (r <= ql || l >= qr) {
            return vector<long long>{LLONG_MAX, 0};
        }
        push(v);
        int m = (l + r) / 2;
        vector<long long> left = get(2 * v + 1, l, m, ql, qr);
        vector<long long> right = get(2 * v + 2, m, r, ql, qr);
        if (left[0] < right[0]) {
            return left;
        } else if(left[0] > right[0]) {
            return right;
        } else {
            return vector<long long>{left[0], left[1] + right[1]};
        }
    }

    static void main() {
        prepareData();
        if (flag == 1) {
            cout << 0 << endl;
            return;
        }
        
        amount.assign(uniqY.size() * 4 + 1, 0);
        minArr.assign(uniqY.size() * 4 + 1, 0);
        lazy.assign(uniqY.size() * 4 + 1, 0);
        build(0, 0, uniqY.size() - 1);
        
        long long answer = 0;
        int j = 0;
        int g = 0;
        while (j < (int)uniqX.size() - 1) {
            while (g < (int)request.size() && request[g][2] == j) {
                if (request[g][1] == 0) {
                    upd(0, 0, setY.size() - 1, request[g][3], request[g][4], +1);
                } else if (request[g][1] == 1) {
                    upd(0, 0, setY.size() - 1, request[g][3], request[g][4], -1);
                } else {
                    cout << "smth went wrong" << endl;
                }
                g++;
            }
            // vector<long long> partGet = get(0, 0, uniqY.size() - 1, 0, uniqY.size() - 1);
            long minEl = minArr[0];
            long amountEl = amount[0];
            if (minEl == 0) {
                long long tmp;
                if (j != (int)uniqX.size() - 1) {
                    tmp = abs(uniqX[j + 1] - uniqX[j]);
                } else {
                    tmp = 0;
                }
                answer += amountEl * tmp;
            }
            j++;
        }
        
        long long area = ((long long)uniqY[uniqY.size() - 1] - uniqY[0])
                * ((long long)uniqX[uniqX.size() - 1] - uniqX[0]);
        
        cout << area - answer << endl;
    }

    static void prepareData() {
        string line;
        getline(cin, line);
        n = stoi(line);
        if (n == 0) {
            flag = 1;
            return;
        }
        
        request.resize(n * 2);
        for (int i = 0; i < n; i++) {
            getline(cin, line);
            // split the line by space
            istringstream iss(line);
            int x1, y1, x2, y2;
            iss >> x1;
            iss >> y1;
            iss >> x2;
            iss >> y2;
            setX.insert(x1);
            setX.insert(x2);
            setY.insert(y1);
            setY.insert(y2);
            request[2 * i] = vector<int>{i, 0, x1, y1, y2};
            request[2 * i + 1] = vector<int>{i, 1, x2, y1, y2};
        }
        
        uniqX = vector<int>(setX.begin(), setX.end());
        uniqY = vector<int>(setY.begin(), setY.end());
        map<int, int> mapX;
        map<int, int> mapY;
        
        // prepare addition
        for (int i = 0; i < (int)uniqX.size(); i++) {
            mapX[uniqX[i]] = i;
        }
        for (int i = 0; i < (int)uniqY.size(); i++) {
            mapY[uniqY[i]] = i;
        }
        
        // squeeze request
        for (size_t i = 0; i < request.size(); i++) {
            request[i][2] = mapX[request[i][2]];
            request[i][3] = mapY[request[i][3]];
            request[i][4] = mapY[request[i][4]];
        }
        
        // sort by x, then by type
        sort(request.begin(), request.end(), [](const vector<int>& a, const vector<int>& b) {
            if (a[2] != b[2]) {
                return (long long)a[2] < (long long)b[2];
            }
            return (long long)a[1] < (long long)b[1];
        });
    }

    static void debug() {
        for (int i = 0; i < (int)request.size(); i++) {
            cout << "[";
            for (int j = 0; j < (int)request[i].size(); j++) {
                cout << request[i][j];
                if(j < (int)request[i].size()-1) cout << ", ";
            }
            cout << "]" << endl;
        }
    }
};

int Number8::n = 0;
set<int> Number8::setX;
set<int> Number8::setY;
vector<int> Number8::uniqX;
vector<int> Number8::uniqY;
vector< vector<int> > Number8::request;
vector<int> Number8::minArr;
vector<long long> Number8::amount;
vector<int> Number8::lazy;
int Number8::flag = 0;

int main(){
    Number8::main();
    return 0;
}
