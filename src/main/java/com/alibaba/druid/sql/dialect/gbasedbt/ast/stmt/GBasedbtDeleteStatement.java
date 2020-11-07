package com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;

public class GBasedbtDeleteStatement extends SQLDeleteStatement {
    private String databaseName;
    private String schemaName;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
