#pragma once

typedef enum {
    BLACK,
    RED,
} color;

typedef struct rb_node {
    int key;
    color color;
    struct rb_node *child[2];
    struct rb_node *parent;
} rb_node;

typedef struct rb_tree {
    rb_node *root;
} rb_tree;

int init_tree(rb_tree **potential_tree);
int rb_insert(rb_tree *tree, int key);
int rb_free(rb_tree *tree);


