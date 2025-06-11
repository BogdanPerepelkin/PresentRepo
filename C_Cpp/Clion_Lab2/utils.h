#pragma once

#include <stdio.h>
#include <graphviz/cgraph.h>

#include "RBTree.h"

enum error_codes {
    SUCCESS = 0,
    ERROR_OPEN_FILE,
    ERROR_RBTREE,

    WRONG_DATA,
    ERROR_READ_GRAPH,
    ERROR_CREATE_GRAPH,
    ERROR_AGWRITE,
    ERROR_CREATE_GV_NODE,
    ERROR_READ_GV_NODE,
    ERROR_READ_GV_NAME,
    ERROR_SET_COLOR,
    ERROR_JOIN,
    ERROR_CLOSE_GRAPH,
    ERROR_WRITE_GRAPH,
};

static const char *error_message_table[] = {
    [SUCCESS] = "",
    [ERROR_OPEN_FILE] = "error when open file:",
    [ERROR_RBTREE] = "rb_tree error",

    [WRONG_DATA] = "wrong data",
    [ERROR_READ_GRAPH] = "error when read graph",
    [ERROR_CREATE_GRAPH] = "error when create graph",
    [ERROR_CREATE_GV_NODE] = "cannot create gv node",
    [ERROR_SET_COLOR] = "cannot set color",
    [ERROR_AGWRITE] = "error while agWrite",
    [ERROR_JOIN] = "cannot do connection",
    [ERROR_READ_GV_NODE] = "error when read gv node",
    [ERROR_READ_GV_NAME] = "cannot read node name",
    [ERROR_CLOSE_GRAPH] = "cannot close graph",
    [ERROR_WRITE_GRAPH] = "error when write graph",
};

typedef struct local_cont {
    FILE *file;
    rb_tree *tree;
    Agraph_t *graph;
} local_cont;

int release_context(const local_cont *context, int errorCode, const char *subject);
int rb_write(local_cont *context);
int rb_read(local_cont *context);