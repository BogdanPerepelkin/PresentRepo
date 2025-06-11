#include <iostream>
#include <vector>
#include <cmath>
using namespace std;

void build(int v, int l, int r, vector<int> t, vector<int> a) {
	if (r == l + 1) {
		t[v] = a[l];
		return;
	}
	int m = (l + r) / 2;
	build(2 * v + 1, l, m, t, a);
	build(2 * v + 2, m, r, t, a);
	t[v] = t[2 * v + 1] + t[2 * v + 2];
}

void upd(int v, int l, int r, int pos, int val, vector<int> t) {
	if (r == l + 1) {
		t[v] = val;
		return;
	}
	int m = (l + r) / 2;
	if (pos < m) {
		upd(2 * v + 1, l, m, pos, val, t);
	}
	else {
		upd(2 * v + 2, m, r, pos, val, t);
	}
	t[v] = t[2 * v + 1] + t[2 * v + 2];
}

int get(int v, int l, int r, int ql, int qr, vector<int> t) {
	if (ql <= l and r <= qr) {
		return t[v];
	}
	if (r <= ql or l >= qr) {
		return -9999;
	}
	int m = (l + r) / 2;
	return get(2 * v + 1, l, m, ql, qr, t) + get(2 * v + 2, m, r, ql, qr, t);
}

int main() {
	int n, m;
	cin >> n >> m;
	vector<int> a;
	vector<int> t;
	a.resize(n);
	int len = ceil(log2(n)) + 1;
	t.resize(pow(2, len) - 1);
	for (int i = 0; i < n; i++) {
		cin >> a[i];
	}
	for (int j = 0; j < m; j++) {
		string op;
		int a;
		int b;
		cin >> op >> a >> b;
		if (op == "get") {
			cout << get(0, 0, n, a - 1, b, t);
		}
		else
		{
			upd(0, 0, n, a, b, t);
		}

	}
	return 0;
}