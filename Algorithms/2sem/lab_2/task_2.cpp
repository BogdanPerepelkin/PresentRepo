#include <iostream>
#include <vector>
#include <fstream>
#include <climits>

using namespace std;

vector<int> tree;
vector<int> stock;

void debug() {
	for (int i : tree) {
		cout << i << " ";
	}
	cout << endl;
}

void build(int v, int l, int r) {
	if (l == r + 1) {
		tree[v] = stock[l];
	} else {
		int mid = (l + r) / 2;
		build(2*v+1, l, mid);
		build(2*v+2, mid, r);
		tree[v] = min(tree[2*v+1], tree[2*v+2]);
	}
}



int main(int argc, char** argv) {
	ifstream infile(argv[1]);
	if (!infile) {
		cerr << "Error opening file" << endl;
		return 1;
	}
	int n, m;
	infile >> n >> m;
	stock.resize(n);
	tree.resize(4*n);

	for (int i = 0; i < n; i++) {
		infile >> stock[i];
	}

	build(0, 0, n);
	debug();

	vector<vector<int>> req(m);
	for (int i = 0; i < m; i++) {
		int type, l, r;
		infile >> type >> l >> r;
	}


	return 0;
}
