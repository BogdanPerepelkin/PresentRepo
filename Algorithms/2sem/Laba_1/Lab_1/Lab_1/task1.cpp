#include <iostream>
#include <vector>
#include <cmath>
//sum, max, pref, suf
using namespace std;

vector<int> merge(vector<int> a, vector<int> b)
{
	int sum = a[0] + b[0];
	int m = max({
		a[1],
		b[1],
		a[3] + b[2]
		});
	int pref = max(a[0] + b[2], a[2]);
	int suf = max(b[3], b[0] + a[3]);
	return { sum, m, pref, suf };
}

void build(int v, int l, int r, vector<int>& sum, vector<int>& mx, vector<int>& pref, vector<int>& suf, vector<int>& a) {
	if (r == l + 1) {
		sum[v] = a[l];
		mx[v] = a[l];
		pref[v] = a[l];
		suf[v] = a[l];
		return;
	}
	int m = (l + r) / 2;
	build(2 * v + 1, l, m, t, a);
	build(2 * v + 2, m, r, t, a);
	int left = 2 * v + 1;
	int right = 2 * v + 2;
	vector<int> tmp = merge(
		{sum[left], mx[left], pref[left], suf[left]}, 
		{sum[right], mx[right], pref[right], suf[right]}
	);
	sum[v] = tmp[0];
	mx[v] = tmp[1];
	pref[v] = tmp[2];
	suf[v] = tmp[3];
}

void upd(int v, int l, int r, int pos, int val, vector<int>& sum, vector<int>& mx, vector<int>& pref, vector<int>& suf) {
	if (r == l + 1) {
		vector<int> tmp = { val,val,val,val };
		t[v] = tmp;
		return;
	}
	int m = (l + r) / 2;
	if (pos < m) {
		upd(2 * v + 1, l, m, pos, val, t);
	}
	else {
		upd(2 * v + 2, m, r, pos, val, t);
	}
	int left = 2 * v + 1;
	int right = 2 * v + 2;
	vector<int> tmp = merge(
		{ sum[left], mx[left], pref[left], suf[left] },
		{ sum[right], mx[right], pref[right], suf[right] }
	);
	sum[v] = tmp[0];
	mx[v] = tmp[1];
	pref[v] = tmp[2];
	suf[v] = tmp[3];
}



vector<int>& get(int v, int l, int r, int ql, int qr, vector<int>& sum, vector<int>& mx, vector<int>& pref, vector<int>& suf) {
	if (ql <= l and r <= qr) {
		return { sum[v], mx[v], pref[v], suf[v]};
	}
	if (r <= ql or l >= qr) {
		return { 0,0,0,0 };
	}
	int m = (l + r) / 2;
	return merge(
		get(2 * v + 1, l, m, ql, qr, sum, mx, pref, suf),
		get(2 * v + 2, m, r, ql, qr, sum, mx, pref, suf)
	);
}

void show(vector<int> a)
{
	for (int i = 0; i < a.size(); i++)
	{
		cout << a[i] << " ";
	}
	cout << '\n';
}

int main() {
	int n, m;
	cin >> n >> m;
	vector<int> a;
	vector<vector<int>> t(n, vector<int>(4, 0));
	int len = ceil(log2(n)) + 1;
	t.resize(pow(2, len) - 1);
	vector<int> sum(pow(2, len) - 1, 0);
	vector<int> mx(pow(2, len) - 1, 0);
	vector<int> pref(pow(2, len) - 1, 0);
	vector<int> suf(pow(2, len) - 1, 0);

	a.resize(n);
	for (int i = 0; i < n; i++) {
		cin >> a[i];
	}
	build(0, 0, n, sum, mx, pref, suf, a);


	for (int j = 0; j < m; j++) {
		string op;
		int a;
		int b;
		cin >> op >> a >> b;
		if (op == "get") {
			vector<int> cur = get(0, 0, n, a - 1, b, sum, mx, pref, suf);
			cout << max({ cur[0], cur[1], cur[2], cur[3] }) << '\n';
		}
		else
		{
			upd(0, 0, n, a - 1, b, t);
		}
	}
	return 0;
}
