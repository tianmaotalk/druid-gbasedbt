package com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.visitor.GBasedbtASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class GBasedbtInsertStatement extends SQLInsertStatement implements GBasedbtStatement {
    private String databaseName;
    private String schemaName;
    private String sql;

    {
        dbType = JdbcConstants.GBASEDBT;
    }

    public GBasedbtInsertStatement(){
        dbType = JdbcConstants.GBASEDBT;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    public String getDatabaseName() {
        return databaseName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void accept0(SQLASTVisitor visitor) {
        accept0((GBasedbtASTVisitor) visitor);
    }

    public void accept0(GBasedbtASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getColumns());
            this.acceptChild(visitor, getValues());
            this.acceptChild(visitor, getQuery());
        }

        visitor.endVisit(this);
    }

    @Override
    public String toString() {
        return
                SQLUtils.toSQLString(this, dbType);
    }
}
