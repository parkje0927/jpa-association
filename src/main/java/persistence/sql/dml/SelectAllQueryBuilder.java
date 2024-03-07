package persistence.sql.dml;

import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

import java.util.List;
import java.util.stream.Collectors;

public class SelectAllQueryBuilder {
    private static final String FIND_ALL_QUERY_TEMPLATE = "SELECT %s FROM %s";
    private static final String COLUMN_DELIMITER = ", ";

    private static class InstanceHolder {
        private static final SelectAllQueryBuilder INSTANCE = new SelectAllQueryBuilder();
    }

    public static SelectAllQueryBuilder getInstance() {
        return InstanceHolder.INSTANCE;
    }


    public String build(Class<?> target) {
        Table table = Table.from(target);
        String columnNames = getColumnsNames(table.getColumns());
        return String.format(FIND_ALL_QUERY_TEMPLATE, columnNames, table.getName());
    }

    private String getColumnsNames(List<Column> columns) {
        return columns.stream()
                .map(Column::getName)
                .collect(Collectors.joining(COLUMN_DELIMITER));
    }
}
