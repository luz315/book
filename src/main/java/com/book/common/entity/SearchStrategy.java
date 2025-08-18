package com.book.common.entity;

public enum SearchStrategy {
    SIMPLE,
    OR_OPERATION,
    NOT_OPERATION;

    public static SearchStrategy fromQuery(String query) {
        if (query == null || query.isBlank()) {
            return SIMPLE;
        } else if (query.contains("|")) {
            return OR_OPERATION;
        } else if (query.contains("-")) {
            return NOT_OPERATION;
        } else {
            return SIMPLE;
        }
    }
}
