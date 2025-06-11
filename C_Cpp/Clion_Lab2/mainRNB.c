#include <stdio.h>
#include <stdlib.h>

#include "RBTree.h"

#include "utils.h"

int main(int argc, char **argv) {
    if (argc != 3) {
        fprintf(stderr, "Wrong amount of args. Expect: inFile, outFile\n");
        return 1;
    }

    local_cont context[1] = { NULL, NULL, NULL };

    int init_res = init_tree(&context->tree);
    if (init_res != 0) {
        return release_context(context, init_res, "");
    }

    context->file = fopen(argv[1], "r");
    if (!context->file) {
        return release_context(context, ERROR_OPEN_FILE, NULL);
    }
    int read_res = rb_read(context);
    if (read_res != 0) {
        return release_context(context, read_res, "");
    }

    fclose(context->file);
    context->file = NULL;

    context->file = fopen(argv[2], "w");
    if (!context->file) {
        return release_context(context, ERROR_OPEN_FILE, "output file");
    }
    int write_res = rb_write(context);
    if (write_res != 0) {
        return release_context(context, write_res, "");
    }

    return release_context(context, SUCCESS, "");
}