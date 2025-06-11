#include "utils.h"

#include <stdio.h>
#include <stdlib.h>
#include <limits.h>

#include "errorCodes.h"
#include <graphviz/cgraph.h>

int release_context(const local_cont *context, const int errorCode, const char *subject) {
    if (context->tree != NULL) {
        rb_free(context->tree);
    }
    if (context->file != NULL) {
        fclose(context->file);
    }
    if (context->graph != NULL) {
        // :CHANGE:
        // int errors_amount = agerrors();
        // agclose(context->graph);
        // if (agerrors() > errors_amount) {
        //     return ERROR_CLOSE_GRAPH;
        // }
        agreseterrors();
        agclose(context->graph);
        if (agerrors() >= AGERR) {
            return ERROR_CLOSE_GRAPH;
        }
    }
    if (errorCode) {
        fprintf(stderr, "%s %s\n", error_message_table[errorCode], subject);
        return 1;
    }
    return 0;
}

static int parse_value(char *str, int *result) {
    long long parsed_val;
    char trash_check;
    int amount = sscanf(str, "%lld %c", &parsed_val, &trash_check);
    if (amount != 1 || parsed_val < INT_MIN || parsed_val > INT_MAX) {
        fprintf(stderr, "'%s'\n", str);
        return WRONG_DATA;
    }
    *result = (int)parsed_val;
    return SUCCESS;
}

static int process_node(rb_tree *tree, Agnode_t *node, char **error_info) {
    if (agerrors() >= AGERR) {
        return ERROR_READ_GV_NODE;
    }
    char *name = agnameof(node);
    if (agerrors() >= AGERR) {
        return ERROR_READ_GV_NAME;
    }
    *error_info = name;
    int parsed_key;
    int parse_res = parse_value(name, &parsed_key);
    if (parse_res != SUCCESS) {
        return parse_res;
    }
    int insert_res = rb_insert(tree, parsed_key);

    if (insert_res != SUCCESS) {
        fprintf(stderr, "%s %s\n", rb_error_message_table[insert_res], name);
        return insert_res;
    }
    return SUCCESS;
}

int rb_read(local_cont *context) {
    context->graph = agread(context->file, NULL);
    if (agerrors() >= AGERR) {
        return ERROR_CREATE_GRAPH;
    }

    for (Agnode_t *cur = agfstnode(context->graph); cur; cur = agnxtnode(context->graph, cur)) {
        char *error_info;
        int process_res = process_node(context->tree, cur, &error_info);
        if (process_res != SUCCESS) {
            return process_res;
        }
    }

    agclose(context->graph);
    if (agerrors() >= AGERR) {
        return ERROR_CLOSE_GRAPH;
    }
    context->graph = NULL;
    return SUCCESS;
}

static int create_gv_node(Agraph_t *graph, const rb_node *inspiration, Agnode_t **target) {
    char buffer[12];
    snprintf(buffer, sizeof(buffer), "%d", inspiration->key);
    *target = agnode(graph, buffer, 1);
    if (agerrors() >= AGERR) {
        return ERROR_CREATE_GV_NODE;
    }

    agsafeset(*target, "color", (inspiration->color == RED) ? "red" : "black", "");
    if (agerrors() >= AGERR) {
        return ERROR_SET_COLOR;
    }
    return SUCCESS;
}

static int rec_graph(Agraph_t *graph, const rb_node *node, Agnode_t **gv_node) {
    if (node == NULL) {
        return SUCCESS;
    }
    *gv_node = NULL;
    int gv_res = create_gv_node(graph, node, gv_node);
    if (gv_res != 0) {
        return gv_res;
    }

    // go to left son
    Agnode_t *left_son = NULL;
    int rec_res = rec_graph(graph, node->child[0], &left_son);
    if (rec_res != SUCCESS) {
        return rec_res;
    }
    if (left_son != NULL) {
        agedge(graph, *gv_node, left_son, NULL, 1);
        if (agerrors() >= AGERR) {
            return ERROR_JOIN;
        }
    }

    // go to right son
    Agnode_t *right_son = NULL;
    rec_res = rec_graph(graph, node->child[1], &right_son);
    if (rec_res != SUCCESS) {
        return rec_res;
    }
    if (right_son !=NULL) {
        agedge(graph, *gv_node, right_son, NULL, 1);
        if (agerrors() >= AGERR) {
            return ERROR_JOIN;
        }
    }

    return SUCCESS;
}

int rb_write(local_cont *context) {
    // :CHANGE:
    // Agdesc_t agdir = {0, 0, 0, 0, 0, 0, 0};
    // context->graph = agopen("gg", agdir, NULL);
    Agdesc_t agdir = {
        .directed = 0,
        .strict = 0,
        .no_loop = 0,
        .maingraph = 0,
        .has_attrs = 0,
    };
    context->graph = agopen("gg", agdir, NULL);
    if (agerrors() >= AGERR) {
        return ERROR_CREATE_GRAPH;
    }

    Agnode_t *gv_tree = NULL;
    int rec_res = rec_graph(context->graph, context->tree->root, &gv_tree);
    if (rec_res != 0) {
        return rec_res;
    }

    agwrite(context->graph, context->file);
    if (agerrors() >= AGERR) {
        return ERROR_WRITE_GRAPH;
    }

    agclose(context->graph);
    if (agerrors() >= AGERR) {
        return ERROR_CLOSE_GRAPH;
    }
    context->graph = NULL;
    return SUCCESS;
}
