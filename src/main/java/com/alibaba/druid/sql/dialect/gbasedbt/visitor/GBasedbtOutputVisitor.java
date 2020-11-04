package com.alibaba.druid.sql.dialect.gbasedbt.visitor;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class GBasedbtOutputVisitor extends SQLASTOutputVisitor implements GBasedbtASTVisitor {

	{
		this.dbType = JdbcConstants.GBASEDBT;
	}

	public GBasedbtOutputVisitor(Appendable appender) {
		super(appender);
		// TODO Auto-generated constructor stub
	}
	public GBasedbtOutputVisitor(Appendable appender,boolean parameterized) {
		super(appender,parameterized);
		// TODO Auto-generated constructor stub
	}

	public void endVisit(GBasedbtInsertStatement x) {

	}

	public boolean visit(GBasedbtInsertStatement x) {
		List<SQLCommentHint> headHints = x.getHeadHintsDirect();
		if (headHints != null) {
			for (SQLCommentHint hint : headHints) {
				hint.accept(this);
				println();
			}
		}

		print0(ucase ? "INSERT " : "insert ");

//		if (x.isLowPriority()) {
//			print0(ucase ? "LOW_PRIORITY " : "low_priority ");
//		}
//
//		if (x.isDelayed()) {
//			print0(ucase ? "DELAYED " : "delayed ");
//		}
//
//		if (x.isHighPriority()) {
//			print0(ucase ? "HIGH_PRIORITY " : "high_priority ");
//		}
//
//		if (x.isIgnore()) {
//			print0(ucase ? "IGNORE " : "ignore ");
//		}
//
//		if (x.isRollbackOnFail()) {
//			print0(ucase ? "ROLLBACK_ON_FAIL " : "rollback_on_fail ");
//		}

		print0(ucase ? "INTO " : "into ");

		SQLExprTableSource tableSource = x.getTableSource();
		if (tableSource.getClass() == SQLExprTableSource.class) {
			visit(tableSource);
		} else {
			tableSource.accept(this);
		}

		String columnsString = x.getColumnsString();
		if (columnsString != null) {
			if (!isEnabled(VisitorFeature.OutputSkipInsertColumnsString)) {
				print0(columnsString);
			}
		} else {
			List<SQLExpr> columns = x.getColumns();
			if (columns.size() > 0) {
				this.indentCount++;
				print0(" (");
				for (int i = 0, size = columns.size(); i < size; ++i) {
					if (i != 0) {
						if (i % 5 == 0) {
							println();
						}
						print0(", ");
					}

					SQLExpr column = columns.get(i);
					if (column instanceof SQLIdentifierExpr) {
						print0(((SQLIdentifierExpr) column).getName());
					} else {
						printExpr(column);
					}
				}
				print(')');
				this.indentCount--;
			}
		}

		List<SQLInsertStatement.ValuesClause>  valuesList = x.getValuesList();
		if (!valuesList.isEmpty()) {
			println();
			printValuesList(valuesList);
		}

		if (x.getQuery() != null) {
			println();
			x.getQuery().accept(this);
		}

//		List<SQLExpr> duplicateKeyUpdate = x.getDuplicateKeyUpdate();
//		if (duplicateKeyUpdate.size() != 0) {
//			println();
//			print0(ucase ? "ON DUPLICATE KEY UPDATE " : "on duplicate key update ");
//			for (int i = 0, size = duplicateKeyUpdate.size(); i < size; ++i) {
//				if (i != 0) {
//					if (i % 5 == 0) {
//						println();
//					}
//					print0(", ");
//				}
//				duplicateKeyUpdate.get(i).accept(this);
//			}
//		}

		return false;
	}

	protected void printValuesList(List<SQLInsertStatement.ValuesClause> valuesList) {

		if (this.parameterized && valuesList.size() > 0) {
			print0(ucase ? "VALUES " : "values ");
			this.indentCount++;
			visit(valuesList.get(0));
			this.indentCount--;
			if (valuesList.size() > 1) {
				this.incrementReplaceCunt();
			}
			return;
		}

		print0(ucase ? "VALUES " : "values ");
		if (valuesList.size() > 1) {
			this.indentCount++;
		}
		for (int i = 0, size = valuesList.size(); i < size; ++i) {
			if (i != 0) {
				print(',');
				println();
			}

			SQLInsertStatement.ValuesClause item = valuesList.get(i);
			visit(item);
		}
		if (valuesList.size() > 1) {
			this.indentCount--;
		}
	}

}
