package com.table.exporter.enums;

import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
public enum Table {

    USER("SELECT ", "", " FROM ", "user"),
    NONE("", "", "", "");

    private final String sel;
    private String cols;
    private final String from;
    private final String tableName;

    Table(String sel, String cols, String from, String tableName) {
        this.sel = sel;
        this.cols = cols;
        this.from = from;
        this.tableName = tableName;
    }

    public static Table getQuery(String tableName, List<String> columns) {
        return Arrays.stream(Table.values())
                .filter(table -> table.tableName.equals(tableName))
                .findFirst()
                .map(table -> {
                    table.cols = String.join(",", columns);
                    return table;
                })
                .orElse(NONE);
    }
}
