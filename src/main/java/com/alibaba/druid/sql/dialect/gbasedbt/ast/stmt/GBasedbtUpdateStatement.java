package com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.visitor.GBasedbtASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class GBasedbtUpdateStatement extends SQLUpdateStatement implements GBasedbtStatement {
    private String databaseName;
    private String schemaName;

    private SQLLimit limit;

    private boolean             lowPriority        = false;
    private boolean             ignore             = false;
    private boolean             commitOnSuccess    = false;
    private boolean             rollBackOnFail     = false;
    private boolean             queryOnPk          = false;
    private SQLExpr targetAffectRow;

    // for petadata
    private boolean             forceAllPartitions = false;
    private SQLName forcePartition;

    public GBasedbtUpdateStatement(){
        super(JdbcConstants.GBASEDBT);
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit limit) {
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof GBasedbtASTVisitor) {
            accept0((GBasedbtASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(GBasedbtASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, items);
            acceptChild(visitor, where);
            acceptChild(visitor, orderBy);
            acceptChild(visitor, limit);
        }
        visitor.endVisit(this);
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isCommitOnSuccess() {
        return commitOnSuccess;
    }

    public void setCommitOnSuccess(boolean commitOnSuccess) {
        this.commitOnSuccess = commitOnSuccess;
    }

    public boolean isRollBackOnFail() {
        return rollBackOnFail;
    }

    public void setRollBackOnFail(boolean rollBackOnFail) {
        this.rollBackOnFail = rollBackOnFail;
    }

    public boolean isQueryOnPk() {
        return queryOnPk;
    }

    public void setQueryOnPk(boolean queryOnPk) {
        this.queryOnPk = queryOnPk;
    }

    public SQLExpr getTargetAffectRow() {
        return targetAffectRow;
    }

    public void setTargetAffectRow(SQLExpr targetAffectRow) {
        if (targetAffectRow != null) {
            targetAffectRow.setParent(this);
        }
        this.targetAffectRow = targetAffectRow;
    }

    public boolean isForceAllPartitions() {
        return forceAllPartitions;
    }

    public void setForceAllPartitions(boolean forceAllPartitions) {
        this.forceAllPartitions = forceAllPartitions;
    }

    public SQLName getForcePartition() {
        return forcePartition;
    }

    public void setForcePartition(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.forcePartition = x;
    }

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
