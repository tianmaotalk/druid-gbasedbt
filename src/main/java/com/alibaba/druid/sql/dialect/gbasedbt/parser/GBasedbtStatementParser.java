package com.alibaba.druid.sql.dialect.gbasedbt.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtDeleteStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtInsertStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class GBasedbtStatementParser extends SQLStatementParser {

	{
		this.dbType = JdbcConstants.GBASEDBT;
	}

	public GBasedbtStatementParser(Lexer lexer, String dbType) {
		super(lexer, dbType);
		// TODO Auto-generated constructor stub
	}

	public GBasedbtStatementParser(String sql) {
		super(sql);
		// TODO Auto-generated constructor stub
	}

	@Override
	public SQLDeleteStatement parseDeleteStatement() {
		GBasedbtDeleteStatement gbasedbtDeleteStatement = new GBasedbtDeleteStatement();

		if (lexer.token() == Token.DELETE) {
			lexer.nextToken();
			if (lexer.token() == (Token.FROM)) {
				lexer.nextToken();
			}

			if (lexer.token() == Token.COMMENT) {
				lexer.nextToken();
			}

			SQLName tableName = exprParser.name();

			gbasedbtDeleteStatement.setTableName(tableName);

			if (lexer.token() == Token.FROM) {
				lexer.nextToken();
				SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
				gbasedbtDeleteStatement.setFrom(tableSource);
			}
		}

		if (lexer.token() == (Token.WHERE)) {
			lexer.nextToken();
			SQLExpr where = this.exprParser.expr();
			gbasedbtDeleteStatement.setWhere(where);
		}

		return gbasedbtDeleteStatement;
	}

	@Override
	public SQLInsertStatement parseInsert() {
		GBasedbtInsertStatement gbasedbtInsertStatement = new GBasedbtInsertStatement();
		if (lexer.token() == Token.INSERT) {
			accept(Token.INSERT);
		}
		parseInsert0(gbasedbtInsertStatement);
		return gbasedbtInsertStatement;
	}

	@Override
	public SQLUpdateStatement parseUpdateStatement() {
		return new GBasedbtUpdateParser(this.lexer).parseUpdateStatement();
	}
}
