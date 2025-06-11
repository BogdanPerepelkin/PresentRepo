#include "RBTree.h"

#include "errorCodes.h"

#include <stdlib.h>


int init_tree(rb_tree **potential_tree) {
    *potential_tree = malloc(sizeof(rb_tree));
    if (*potential_tree == NULL) {
        return RB_ERROR_INSERT_NODE;
    }
    (*potential_tree)->root = NULL;
    return RB_SUCCESS;
}

static rb_node * create_node(const int key) {
    rb_node *newNode = malloc(sizeof(rb_node));
    if (newNode == NULL) {
        return NULL;
    }
    newNode->key = key;
    newNode->color = RED;
    newNode->child[0] = NULL;
    newNode->child[1] = NULL;
    newNode->parent = NULL;
    return newNode;
}

static int rotate_dir(rb_tree *tree, rb_node *node, const int dir) {
    rb_node *pivot = node->child[!dir];
    pivot->parent = node->parent;
    if (node->parent != NULL) {
        if (node->parent->child[0] == node) {
            node->parent->child[0] = pivot;
        } else {
            node->parent->child[1] = pivot;
        }
    } else {
        tree->root = pivot;
    }

    node->child[!dir] = pivot->child[dir];

    if (pivot->child[dir] != NULL) {
        pivot->child[dir]->parent = node;
    }

    pivot->child[dir] = node;
    node->parent = pivot;

    return RB_SUCCESS;
}

static int ins_fix(rb_tree *tree, rb_node *node) {
    while (node->parent && node->parent->color == RED) {
        int dir;
        if (node->parent == node->parent->parent->child[0]) { // parent left son
            dir = 1;
        } else { // parent right son
            dir = 0;
        }
        rb_node *uncle = node->parent->parent->child[dir];
        if (uncle != NULL && uncle->color == RED) { // uncle and dad - red
            node->parent->color = BLACK;
            uncle->color = BLACK;
            node->parent->parent->color = RED;
            node = node->parent->parent;
        } else {
            if (node == node->parent->child[dir]) { // dad - red ==>> zig-zag
                node = node->parent;
                rotate_dir(tree, node, !dir);
            }
            node->parent->color = BLACK; // straight line ==>> right rotate
            node->parent->parent->color = RED;
            rotate_dir(tree, node->parent->parent, dir);
        }
    }
    tree->root->color = BLACK;
    return RB_SUCCESS;
}

int rb_insert(rb_tree *tree, int key) {
    if (tree == NULL) {
        return RB_ERROR_NOT_INIT_TREE;
    }
    rb_node *node = create_node(key);
    if (node == NULL) {
        return RB_ERROR_CREATE_NODE;
    }
    if (tree->root == NULL) {
        node->color = BLACK;
        tree->root = node;
        return RB_SUCCESS;
    }
    rb_node *newParent = NULL;
    rb_node *cur = tree->root;
    while (cur != NULL) {
        newParent = cur;
        if (cur->key < key) {
            cur = cur->child[1];
        } else {
            cur = cur->child[0];
        }
    }

    node->parent = newParent;

    if (newParent->key < key) {
        newParent->child[1] = node;
    } else {
        newParent->child[0] = node;
    }

    ins_fix(tree, node);

    return RB_SUCCESS;
}

static int rec_rb_free(rb_node *node) {
    if (node == NULL) {
        return RB_SUCCESS;
    }
    if (node->child[0]) {
        rec_rb_free(node->child[0]);
    }
    if (node->child[1]) {
        rec_rb_free(node->child[1]);
    }
    node->child[0] = NULL;
    node->child[1] = NULL;
    free(node);
    return RB_SUCCESS;
}

int rb_free(rb_tree *tree) {
    if (tree == NULL) {
        return RB_SUCCESS;
    }
    int rec_res = rec_rb_free(tree->root);
    if (rec_res != 0) {
        return rec_res;
    }
    free(tree);
    return RB_SUCCESS;
}

