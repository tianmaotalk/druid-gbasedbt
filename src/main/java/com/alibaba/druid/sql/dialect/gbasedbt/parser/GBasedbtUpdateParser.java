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

import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtInsertStatement;
import com.alibaba.druid.sql.dialect.gbasedbt.ast.stmt.GBasedbtUpdateStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class GBasedbtUpdateParser extends SQLStatementParser {

	public GBasedbtUpdateParser(String sql) {
		super(new GBasedbtExprParser(sql));
	}

	public GBasedbtUpdateParser(Lexer lexer) {
		super(new GBasedbtExprParser(lexer));
	}


	@Override
	public SQLUpdateStatement parseUpdateStatement() {
		GBasedbtUpdateStatement udpateStatement = new GBasedbtUpdateStatement();

		if (lexer.token() == Token.UPDATE) {
			lexer.nextToken();

			SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
			udpateStatement.setTableSource(tableSource);
		}

		parseUpdateSet(udpateStatement);

		if (lexer.token() == (Token.WHERE)) {
			lexer.nextToken();
			udpateStatement.setWhere(this.exprParser.expr());
		}

		return udpateStatement;
	}
}