package com.alibaba.druid.sql.dialect.gbasedbt.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtDeleteStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtInsertStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GBasedbtStatementParser extends SQLStatementParser {

	{
		this.dbType = JdbcConstants.GBASEDBT;
	}

	public GBasedbtStatementParser(String sql) {
		super(new GBasedbtExprParser(sql));
	}

	public GBasedbtStatementParser(String sql, SQLParserFeature... features) {
		super(new GBasedbtExprParser(sql, features));
	}

	public GBasedbtStatementParser(String sql, boolean keepComments) {
		super(new GBasedbtExprParser(sql, keepComments));
	}

	public GBasedbtStatementParser(String sql, boolean skipComment, boolean keepComments) {
		super(new GBasedbtExprParser(sql, skipComment, keepComments));
	}

	public GBasedbtStatementParser(Lexer lexer) {
		super(new GBasedbtExprParser(lexer));
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

			lexer.nextToken();
			String val = lexer.stringVal();
			int pos = lexer.pos();

			lexer.nextToken();

			if (lexer.token() == Token.COLON) {
				String dbName = val;
				 System.out.println("dbname=" + dbName);
//				gbasedbtInsertStatement.setDatabaseName(dbName);
				val = lexer.stringVal();
				lexer.nextToken();
			}
//            String temp = lexer.stringVal();
//            System.out.println(temp);
			if (lexer.token() == Token.DOT) {
				val = lexer.stringVal();
				String schema = val;
                System.out.println("schema=" + schema);
//				gbasedbtInsertStatement.setSchemaName(schema);
				lexer.nextToken();
				val = lexer.stringVal();
			}

		}


		parseInsert0(gbasedbtInsertStatement);
		return gbasedbtInsertStatement;
	}

	@Override
	public SQLUpdateStatement parseUpdateStatement() {
		return new GBasedbtUpdateParser(this.lexer).parseUpdateStatement();
	}


	public GBasedbtInsertStatement parseDatabase(String sql) {
		GBasedbtInsertStatement stmt = new GBasedbtInsertStatement();
		Pattern p = Pattern.compile("(?:\\s+)((\\S+)(?::{1}))(?:\\S+)");
		Matcher m = p.matcher(sql);
		boolean result = m.find();
		if (result) {
			String db = m.group(2);
			stmt.setDatabaseName(db);
			sql = sql.replaceAll(m.group(1), "");
		}
		stmt.setSql(sql);
		return stmt;
	}
}
