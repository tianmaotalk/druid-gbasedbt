/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.gbasedbt.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

public class GBasedbtExprParser extends SQLExprParser {

    public GBasedbtExprParser(Lexer lexer){
        super(lexer, JdbcConstants.GBASEDBT);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public GBasedbtExprParser(String sql){
        this(new GBasedbtLexer(sql));
        this.lexer.nextToken();
    }

    public GBasedbtExprParser(String sql, SQLParserFeature... features){
        super(new GBasedbtLexer(sql, features), JdbcConstants.GBASEDBT);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
        if (sql.length() > 6) {
            char c0 = sql.charAt(0);
            char c1 = sql.charAt(1);
            char c2 = sql.charAt(2);
            char c3 = sql.charAt(3);
            char c4 = sql.charAt(4);
            char c5 = sql.charAt(5);
            char c6 = sql.charAt(6);

            if (c0 == 'S' && c1 == 'E' && c2 == 'L' && c3 == 'E' && c4 == 'C' && c5 == 'T' && c6 == ' ') {
                lexer.reset(6, ' ', Token.SELECT);
                return;
            }

            if (c0 == 's' && c1 == 'e' && c2 == 'l' && c3 == 'e' && c4 == 'c' && c5 == 't' && c6 == ' ') {
                lexer.reset(6, ' ', Token.SELECT);
                return;
            }

            if (c0 == 'I' && c1 == 'N' && c2 == 'S' && c3 == 'E' && c4 == 'R' && c5 == 'T' && c6 == ' ') {
                lexer.reset(6, ' ', Token.INSERT);
                return;
            }

            if (c0 == 'i' && c1 == 'n' && c2 == 's' && c3 == 'e' && c4 == 'r' && c5 == 't' && c6 == ' ') {
                lexer.reset(6, ' ', Token.INSERT);
                return;
            }

            if (c0 == 'U' && c1 == 'P' && c2 == 'D' && c3 == 'A' && c4 == 'T' && c5 == 'E' && c6 == ' ') {
                lexer.reset(6, ' ', Token.UPDATE);
                return;
            }

            if (c0 == 'u' && c1 == 'p' && c2 == 'd' && c3 == 'a' && c4 == 't' && c5 == 'e' && c6 == ' ') {
                lexer.reset(6, ' ', Token.UPDATE);
                return;
            }

            if (c0 == '/' && c1 == '*' && isEnabled(SQLParserFeature.OptimizedForParameterized)) {
                MySqlLexer mySqlLexer = (MySqlLexer) lexer;
                mySqlLexer.skipFirstHintsOrMultiCommentAndNextToken();
                return;
            }
        }
        this.lexer.nextToken();

    }

    public GBasedbtExprParser(String sql, boolean keepComments){
        this(new GBasedbtLexer(sql, true, keepComments));
        this.lexer.nextToken();
    }


    public GBasedbtExprParser(String sql, boolean skipComment,boolean keepComments){
        this(new GBasedbtLexer(sql, skipComment, keepComments));
        this.lexer.nextToken();
    }


    public SQLExpr primary() {
        final Token tok = lexer.token();

        if (lexer.identifierEquals(FnvHash.Constants.OUTFILE)) {
            lexer.nextToken();
            SQLExpr file = primary();
            SQLExpr expr = new MySqlOutFileExpr(file);

            return primaryRest(expr);

        }

        switch (tok) {
            case VARIANT:
                SQLVariantRefExpr varRefExpr = new SQLVariantRefExpr(lexer.stringVal());
                lexer.nextToken();
                if (varRefExpr.getName().equalsIgnoreCase("@@global")) {
                    accept(Token.DOT);
                    varRefExpr = new SQLVariantRefExpr(lexer.stringVal(), true);
                    lexer.nextToken();
                } else if (varRefExpr.getName().equals("@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                } else if (varRefExpr.getName().equals("@@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                }
                return primaryRest(varRefExpr);
            case VALUES:
                lexer.nextToken();
                if (lexer.token() != Token.LPAREN) {
                    throw new ParserException("syntax error, illegal values clause. " + lexer.info());
                }
                return this.methodRest(new SQLIdentifierExpr("VALUES"), true);
            case BINARY:
                lexer.nextToken();
                if (lexer.token() == Token.COMMA || lexer.token() == Token.SEMI || lexer.token() == Token.EOF) {
                    return new SQLIdentifierExpr("BINARY");
                } else {
                    SQLUnaryExpr binaryExpr = new SQLUnaryExpr(SQLUnaryOperator.BINARY, expr());
                    return primaryRest(binaryExpr);
                }
            default:
                return super.primary();
        }

    }
}
