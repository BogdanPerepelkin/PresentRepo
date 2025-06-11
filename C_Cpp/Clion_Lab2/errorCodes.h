#pragma once

enum rb_error_codes
{
    RB_SUCCESS = 0,

    RB_ERROR_CREATE_NODE,
    RB_ERROR_INSERT_NODE,
    RB_ERROR_NOT_NULL,
    RB_ERROR_CREATE_TREE,
    RB_ERROR_NOT_INIT_TREE,
};
static const char *rb_error_message_table[] = {
    [RB_SUCCESS] = "",

    [RB_ERROR_CREATE_NODE] = "cannot create node",
    [RB_ERROR_INSERT_NODE] = "cannot create tree",
    [RB_ERROR_NOT_NULL] = "not null, expected NULL",
    [RB_ERROR_CREATE_TREE] = "error when create tree",
    [RB_ERROR_NOT_INIT_TREE] = "expected already inited tree before this",
};