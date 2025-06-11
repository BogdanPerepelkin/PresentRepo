#include <stdio.h>
#include <stdlib.h>
#pragma once
#include <algorithm>
#include <cstddef>
#include <bits/fs_fwd.h>

// rule: always (node = node') have to do hSupport
struct node {
    node *l;	// левый сын (или nullptr, если его нет)
    node *r;	// правый сын (или nullptr, если его нет)
    int key;	// ключ
    size_t h;	// высота вершины (высота считается по вершинам, в частности высота листа равна 1)
};

size_t getHeight(node *v) {
    if (v == nullptr) {
        return 0;
    } else {
        return v->h;
    }
}

int diffCnt(node *v) {
    if (v == nullptr) {
        return 0;
    }
    return getHeight(v->r) - getHeight(v->l);
}

void hSupport(node *v) {
    size_t left = getHeight(v->l);
    size_t right = getHeight(v->r);
    if (left > right) {
        v->h = left + 1;
    } else {
        v->h = right + 1;
    }
}

node* rotateRight(node *v) {
    if (v == nullptr) {
        return v;
    }
    node *newV = v->l;
    v->l = newV->r;
    newV->r = v;
    hSupport(v);
    hSupport(newV);
    return newV;
}

node* rotateLeft(node *v) {
    if (v == nullptr) {
        return v;
    }
    node *newV = v->r;
    v->r = newV->l;
    newV->l = v;
    hSupport(v);
    hSupport(newV);
    return newV;
}

// Pre: h - any
// Post: h - correct
node* rotate(node *v) {
    if (v == nullptr) {
        return nullptr;
    }
    hSupport(v);
    if (diffCnt(v) == -2) {
        if (diffCnt(v->l) > 0) {
            v->l = rotateLeft(v->l);
        }
        return rotateRight(v);
    }
    if (diffCnt(v) == 2) {
        if (diffCnt(v->r) < 0) {
            v->r = rotateRight(v->r);
        }
        return rotateLeft(v);
    }
    return v;
}

bool exists(node *root, int key) {
    if (root == nullptr) {
        return false;
    }
    if (key < root->key) {
        return exists(root->l, key);
    }
    if (key > root->key) {
        return exists(root->r, key);
    }
    return true;
}

// Post: h - any
node* insert(node *root, int key) {
    if (exists(root, key)) {
        return root;
    }
    if (root == nullptr) {
        node *newHead = new node();
        newHead->key = key;
        newHead->l = nullptr;
        newHead->r = nullptr;
        newHead->h = 1;
        return newHead;
    }
    if (key < root->key) {
        root->l = insert(root->l, key);
    } else {
        root->r = insert(root->r, key);
    }
    return rotate(root);
}

// Pred: min in v
node* findMin(node *v) {
    if (v->l == nullptr) {
        return v;
    }
    return findMin(v->l);
}

// shift up on one element
node* shiftUpOne(node *v) {
    if (v->l == nullptr) {
        return v->r;
    }
    v->l = shiftUpOne(v->l);
    return rotate(v);
}

// Post: ?
node* remove(node *root, int key) {
    if (root == nullptr) {
        return nullptr;
    }
    if (key < root->key) {
        root->l = remove(root->l, key);
    } else if (key > root->key) {
        root->r = remove(root->r, key);
    } else {
        node *left = root->l;
        node *right = root->r;
        delete root;
        if (right == nullptr) {
            return left;
        }
        node *tar = findMin(right);
        tar->r = shiftUpOne(right);
        tar->l = left;
        return rotate(tar);
    }
    return rotate(root);
}



void dump(node *root) {
    if (root == nullptr) {
        return;
    }
    dump(root->l);
    fprintf(stdout,"%i-%i ", root->key, root->h);
    dump(root->r);
}

// test
int main() {
    node* root = nullptr;
    root = insert(root, 1);


    root = insert(root, 6);
    root = insert(root, 1);
    root = insert(root, 3);
    root = insert(root, 5);
    root = insert(root, 2);
    root = insert(root, 4);
    root = insert(root, 7);
    // fprintf(stdout, "%i\n", exists(root, 1));
    // fprintf(stdout, "%i\n", exists(root, 2));
    // fprintf(stdout, "%i\n", exists(root, 3));
    // fprintf(stdout, "%i\n", exists(root, 4));
    // fprintf(stdout, "%i\n", exists(root, 5));
    // fprintf(stdout, "%i\n", exists(root, 4));
    // fprintf(stdout, "%i\n", exists(root, 7));
    root = remove(root, 3);
    root = remove(root, 4);
    root = remove(root, 12);

    fprintf(stdout, "%i\n", exists(root, 1));
    fprintf(stdout, "%i\n", exists(root, 2));
    fprintf(stdout, "%i\n", exists(root, 3));
    fprintf(stdout, "%i\n", exists(root, 4));
    fprintf(stdout, "%i\n", exists(root, 5));
    fprintf(stdout, "%i\n", exists(root, 6));
    fprintf(stdout, "%i\n", exists(root, 7));



    // // fprintf(stdout,"%i %i\n", root->h, root->key);
    // dump(root);
    // fprintf(stdout, "\n");
    // root = insert(root, 1);
    // dump(root);
    // fprintf(stdout, "\n");
    // // fprintf(stdout,"%i %i\n", root->h, root->key);
    // root = insert(root, 1);
    // dump(root);
    // fprintf(stdout, "\n");
    // // fprintf(stdout,"%i %i\n", root->h, root->key);
    // root = remove(root, 1);
    // dump(root);
    // // fprintf(stdout,"%i %i\n", root->h, root->key);

    return 0;
}
