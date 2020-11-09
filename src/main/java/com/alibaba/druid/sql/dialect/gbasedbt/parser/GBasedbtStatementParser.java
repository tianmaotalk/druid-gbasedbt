package com.alibaba.druid.sql.dialect.gbasedbt.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtDeleteStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtInsertStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

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
        GBasedbtDeleteStatement stmt = new GBasedbtDeleteStatement();
        SQLName tableName = null;
        if (lexer.token() == Token.DELETE) {
            lexer.nextToken();
            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            String val = lexer.stringVal();

            lexer.nextToken();

            if (lexer.token() == Token.COLON) {
                String dbName = val;
//				 System.out.println("dbname=" + dbName);
                stmt.setDatabaseName(dbName);
                lexer.nextToken();
                val = lexer.stringVal();
                lexer.nextToken();
            }
            if (lexer.token() == Token.DOT) {
                val = lexer.stringVal();
                String schema = val;
//                System.out.println("schema=" + schema);
                stmt.setSchemaName(schema);
                lexer.nextToken();
                val = lexer.stringVal();

                lexer.nextToken();
            }

            SQLName name = new SQLIdentifierExpr(val, 0);
            name = this.exprParser.nameRest(name);
            tableName = name;

            stmt.setTableName(tableName);
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    @Override
    public SQLInsertStatement parseInsert() {
        GBasedbtInsertStatement stmt = new GBasedbtInsertStatement();
        SQLName tableName = null;
        if (lexer.token() == Token.INSERT) {
            accept(Token.INSERT);
            if (lexer.token() == Token.INTO) {
                lexer.nextToken();
            }
            String val = lexer.stringVal();
//            int pos = lexer.pos();

            lexer.nextToken();

            if (lexer.token() == Token.COLON) {
                String dbName = val;
//				 System.out.println("dbname=" + dbName);
                stmt.setDatabaseName(dbName);
                lexer.nextToken();
                val = lexer.stringVal();
                lexer.nextToken();
            }
//            String temp = lexer.stringVal();
//            System.out.println(temp);
            if (lexer.token() == Token.DOT) {
                val = lexer.stringVal();
                String schema = val;
//                System.out.println("schema=" + schema);
                stmt.setSchemaName(schema);
                lexer.nextToken();
                val = lexer.stringVal();
            }

            SQLName name = new SQLIdentifierExpr(val, 0);
            name = this.exprParser.nameRest(name);
            tableName = name;
//			tableName = this.exprParser.name();
            stmt.setTableName(tableName);


            if (lexer.token() == Token.IDENTIFIER
                    && !lexer.identifierEquals(FnvHash.Constants.VALUE)) {
                stmt.setAlias(lexer.stringVal());
                lexer.nextToken();
            }
        }

        int columnSize = 0;
        if (lexer.token() == Token.LPAREN) {
            boolean useInsertColumnsCache = lexer.isEnabled(SQLParserFeature.UseInsertColumnsCache);
            InsertColumnsCache insertColumnsCache = null;

            InsertColumnsCache.Entry cachedColumns = null;
            if (useInsertColumnsCache) {
                insertColumnsCache = this.insertColumnsCache;
                if (insertColumnsCache == null) {
                    insertColumnsCache = InsertColumnsCache.global;
                }

                if (tableName != null) {
                    cachedColumns = insertColumnsCache.get(tableName.hashCode64());
                }
            }

            int pos = lexer.pos();
            if (cachedColumns != null
                    && lexer.text.startsWith(cachedColumns.columnsString, pos)) {
                if (!lexer.isEnabled(SQLParserFeature.OptimizedForParameterized)) {
                    List<SQLExpr> columns = stmt.getColumns();
                    List<SQLExpr> cachedColumns2 = cachedColumns.columns;
                    for (int i = 0, size = cachedColumns2.size(); i < size; i++) {
                        columns.add(cachedColumns2.get(i).clone());
                    }
                }
                stmt.setColumnsString(cachedColumns.columnsFormattedString, cachedColumns.columnsFormattedStringHash);
                int p2 = pos + cachedColumns.columnsString.length();
                lexer.reset(p2);
                lexer.nextToken();
            } else {
                lexer.nextToken();
                if (lexer.token() == Token.SELECT) {
                    SQLSelect select = this.exprParser.createSelectParser().select();
                    select.setParent(stmt);
                    stmt.setQuery(select);
                } else {
                    List<SQLExpr> columns = stmt.getColumns();

                    if (lexer.token() != Token.RPAREN) {
                        for (; ; ) {
                            String identName;
                            long hash;

                            Token token = lexer.token();
                            if (token == Token.IDENTIFIER) {
                                identName = lexer.stringVal();
                                hash = lexer.hash_lower();
                            } else if (token == Token.LITERAL_CHARS) {
                                identName = '\'' + lexer.stringVal() + '\'';
                                hash = 0;
                            } else {
                                identName = lexer.stringVal();
                                hash = 0;
                            }
                            lexer.nextTokenComma();
                            SQLExpr expr = new SQLIdentifierExpr(identName, hash);
                            while (lexer.token() == Token.DOT) {
                                lexer.nextToken();
                                String propertyName = lexer.stringVal();
                                lexer.nextToken();
                                expr = new SQLPropertyExpr(expr, propertyName);
                            }

                            expr.setParent(stmt);
                            columns.add(expr);
                            columnSize++;

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextTokenIdent();
                                continue;
                            }

                            break;
                        }
                        columnSize = stmt.getColumns().size();

                        if (insertColumnsCache != null && tableName != null) {
                            String columnsString = lexer.subString(pos, lexer.pos() - pos);

                            List<SQLExpr> clonedColumns = new ArrayList<SQLExpr>(columnSize);
                            for (int i = 0; i < columns.size(); i++) {
                                clonedColumns.add(columns.get(i).clone());
                            }

                            StringBuilder buf = new StringBuilder();
                            SQLASTOutputVisitor outputVisitor = SQLUtils.createOutputVisitor(buf, dbType);
                            outputVisitor.printInsertColumns(columns);

                            String formattedColumnsString = buf.toString();
                            long columnsFormattedStringHash = FnvHash.fnv1a_64_lower(formattedColumnsString);

                            insertColumnsCache.put(tableName.hashCode64(), columnsString, formattedColumnsString, clonedColumns);
                            stmt.setColumnsString(formattedColumnsString, columnsFormattedStringHash);
                        }
                    }
                }
                accept(Token.RPAREN);
            }
        }

        if (lexer.token() == Token.VALUES || lexer.identifierEquals(FnvHash.Constants.VALUE)) {
            lexer.nextTokenLParen();
            parseValueClause(stmt.getValuesList(), columnSize, stmt);
        } else if (lexer.token() == Token.SET) {
            lexer.nextToken();

            SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
            stmt.addValueCause(values);

            for (; ; ) {
                SQLName name = this.exprParser.name();
                stmt.addColumn(name);
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                } else {
                    accept(Token.COLONEQ);
                }
                values.addValue(this.exprParser.expr());

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

        } else if (lexer.token() == (Token.SELECT)) {
            SQLSelect select = this.exprParser.createSelectParser().select();
            select.setParent(stmt);
            stmt.setQuery(select);
        } else if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            SQLSelect select = this.exprParser.createSelectParser().select();
            select.setParent(stmt);
            stmt.setQuery(select);
            accept(Token.RPAREN);
        }

        return stmt;
    }

    @Override
    public SQLUpdateStatement parseUpdateStatement() {
        return new GBasedbtUpdateParser(this.lexer).parseUpdateStatement();
    }


}
