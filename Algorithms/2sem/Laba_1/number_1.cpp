// #include <iostream>
// #include <vector>
// #include <cmath>
// //sum, max, pref, suf
// using namespace std;
// using ll = long long;
//
// ll merge_sum(vector<ll>& sum, ll l, ll r) {
// 	return sum[l] + sum[r];
// }
//
// ll merge_mx(vector<ll>& mx, vector<ll>& pref, vector<ll>& suf, ll l, ll r) {
// 	return max(
// 		mx[l],
// 		max(mx[r],
// 		suf[l] + pref[r])
// 		);
// }
//
// ll merge_pref(vector<ll>& pref, vector<ll>& sum, ll l, ll r) {
// 	return max(
// 		pref[l],
// 		sum[l] + pref[r]
// 	);
// }
//
// ll merge_suf(vector<ll>& suf, vector<ll>& sum, ll l, ll r) {
// 	return max(
// 		suf[r],
// 		sum[r] + suf[l]
// 	);
// }
//
// void merge(ll v, ll l, ll r, vector<ll>& sum, vector<ll>& mx, vector<ll>& pref, vector<ll>& suf) {
// 	ll left = 2 * v + 1;
// 	ll right = 2 * v + 2;
// 	sum[v] = merge_sum(sum, left, right);
// 	mx[v] = merge_mx(mx, pref, suf, left, right);
// 	pref[v] = merge_pref(pref, sum, left, right);
// 	suf[v] = merge_suf(suf, sum, left, right);
// }
//
// void build(ll v, ll l, ll r, vector<ll>& sum, vector<ll>& mx, vector<ll>& pref, vector<ll>& suf, vector<ll>& a) {
// 	if (r == l + 1) {
// 		sum[v] = a[l];
// 		mx[v] = a[l];
// 		pref[v] = a[l];
// 		suf[v] = a[l];
// 		return;
// 	}
// 	ll m = (l + r) / 2;
// 	build(2 * v + 1, l, m, sum, mx, pref, suf, a);
// 	build(2 * v + 2, m, r, sum, mx, pref, suf, a);
// 	merge(v, l, r, sum, mx, pref, suf);
// }
//
// void upd(ll v, ll l, ll r, ll pos, ll val, vector< ll>& sum, vector< ll>& mx, vector< ll>& pref, vector< ll>& suf) {
// 	if (r == l + 1) {
// 		sum[v] = val;
// 		mx[v] = val;
// 		pref[v] = val;
// 		suf[v] = val;
// 		return;
// 	}
// 	ll m = (l + r) / 2;
// 	if (pos < m) {
// 		upd(2 * v + 1, l, m, pos, val, sum, mx, pref, suf);
// 	}
// 	else {
// 		upd(2 * v + 2, m, r, pos, val, sum, mx, pref, suf);
// 	}
// 	merge(v, l, r, sum, mx, pref, suf);
// }
//
// vector<ll>& get(ll v, ll l, ll r, ll ql, ll qr, vector< ll>& sum, vector< ll>& mx, vector< ll>& pref, vector< ll>& suf) {
// 	if (ql <= l and r <= qr) {
// 		vector<ll> in = {sum[v], mx[v], pref[v], suf[v]};
// 		return in;
// 	}
// 	if (r <= ql or l >= qr) {
// 		vector<ll> out = { 0, INT_MIN, INT_MIN, INT_MIN };
// 		return out;
// 	}
// 	ll m = (l + r) / 2;
// 	ll left = 2 * v + 1;
// 	ll right = 2 * v + 2;
// 	vector<ll> left_node = get(left, l, m, ql, qr, sum, mx, pref, suf);
// 	vector<ll> right_node = get(right, m, r, ql, qr, sum, mx, pref, suf);
// 	ll new_sum = left_node[0] + right_node[0];
// 	ll new_mx = max(
// 		left_node[1],
// 		max(right_node[1],
// 		left_node[3] + right_node[2])
// 		);
// 	ll new_pref = max(left_node[2], left_node[0] + right_node[2]);
// 	ll new_suf = max(right_node[3], right_node[0] + left_node[3]);
// 	vector<ll> new_node = { new_sum, new_mx, new_pref, new_suf };
// 	return new_node;
// }
//
// int main() {
// 	ll n, m;
// 	cin >> n >> m;
// 	vector< ll> a;
// 	vector<vector< ll>> t(n, vector< ll>(4, 0));
// 	ll len = ceil(log2(n)) + 1;
// 	t.resize(pow(2, len) - 1);
// 	vector< ll> sum(pow(2, len) - 1, 0);
// 	vector< ll> mx(pow(2, len) - 1, 0);
// 	vector< ll> pref(pow(2, len) - 1, 0);
// 	vector< ll> suf(pow(2, len) - 1, 0);
//
// 	a.resize(n);
// 	for (ll i = 0; i < n; i++) {
// 		cin >> a[i];
// 	}
// 	build(0, 0, n, sum, mx, pref, suf, a);
//
// 	for (ll j = 0; j < m; j++) {
// 		string op;
// 		ll a;
// 		ll b;
// 		cin >> op >> a >> b;
// 		if (op == "get") {
// 			vector< ll> cur = get(0, 0, n, a - 1, b, sum, mx, pref, suf);
// 			cout << max(cur[1], 0ll) << '\n';
// 		}
// 		else
// 		{
// 			upd(0, 0, n, a-1, b, sum, mx, pref, suf);
// 		}
// 	}
// 	return 0;
// }