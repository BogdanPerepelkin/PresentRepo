#include <iostream>
#include <vector>
#include <cmath>
using namespace std;

void build(long v, long l, long r, vector<long>& t, vector<long>& a) {
	if (r == l + 1) {
		t[v] = a[l];
		return;
	}
	long m = (l + r) / 2;
	build(2 * v + 1, l, m, t, a);
	build(2 * v + 2, m, r, t, a);
	t[v] = t[2 * v + 1] + t[2 * v + 2];
}

void upd(long v, long l, long r, long pos, long val, vector<long>& t) {
	if (r == l + 1) {
		t[v] = val;
		return;
	}
	long m = (l + r) / 2;
	if (pos < m) {
		upd(2 * v + 1, l, m, pos, val, t);
	}
	else {
		upd(2 * v + 2, m, r, pos, val, t);
	}
	t[v] = t[2 * v + 1] + t[2 * v + 2];
}

long get(long v, long l, long r, long ql, long qr, vector<long>& t) {
	if (ql <= l and r <= qr) {
		return t[v];
	}
	if (r <= ql or l >= qr) {
		return 0;
	}
	long m = (l + r) / 2;
	return get(2 * v + 1, l, m, ql, qr, t) +
		get(2 * v + 2, m, r, ql, qr, t);
}


void main()
{
	long n = 0;
	vector<long> a(n,0);
	vector<long> t(4 * n, 0);
	build(0, 0, n, t, a);
	for (long i = 0; i < n; i++)
	{
		cin >> a[i];
	}
	long counter = 0;
	for (long j = n-1; j >= 0; j--)
	{
		long before = get(0, 0, n, 0, a[j], t);
		counter = counter + before;
		upd(0, 0, n, a[j], 1, t);
	}
}