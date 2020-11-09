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

import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.JdbcConstants;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.COLON;

public class GBasedbtLexer extends Lexer {

	public final static Keywords DEFAULT_GBASEDBT_KEYWORDS;

	static {
		Map<String, Token> map = new HashMap<String, Token>();

		map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

		map.put("BEGIN", Token.BEGIN);
		map.put("COMMENT", Token.COMMENT);
		map.put("COMMIT", Token.COMMIT);
		map.put("CONNECT", Token.CONNECT);
		map.put("CONTINUE", Token.CONTINUE);

		map.put("CROSS", Token.CROSS);
		map.put("CURSOR", Token.CURSOR);
		map.put("DECLARE", Token.DECLARE);
		map.put("ERRORS", Token.ERRORS);
		map.put("EXCEPTION", Token.EXCEPTION);

		map.put("EXCLUSIVE", Token.EXCLUSIVE);
		map.put("EXTRACT", Token.EXTRACT);
		map.put("GOTO", Token.GOTO);
		map.put("IF", Token.IF);
		map.put("ELSIF", Token.ELSIF);

		map.put("LIMIT", Token.LIMIT);
		map.put("LOOP", Token.LOOP);
		map.put("MATCHED", Token.MATCHED);
		map.put("MERGE", Token.MERGE);

		map.put("MODE", Token.MODE);
		map.put("MODEL", Token.MODEL);
		map.put("NOWAIT", Token.NOWAIT);
		map.put("OF", Token.OF);
		map.put("PRIOR", Token.PRIOR);

		map.put("REJECT", Token.REJECT);
		map.put("RETURN", Token.RETURN);
		map.put("RETURNING", Token.RETURNING);
		map.put("SAVEPOINT", Token.SAVEPOINT);
		map.put("SESSION", Token.SESSION);

		map.put("SHARE", Token.SHARE);
		map.put("START", Token.START);
		map.put("SYSDATE", Token.SYSDATE);
		map.put("UNLIMITED", Token.UNLIMITED);
		map.put("USING", Token.USING);

		map.put("WAIT", Token.WAIT);
		map.put("WITH", Token.WITH);

		map.put("IDENTIFIED", Token.IDENTIFIED);

		map.put("PCTFREE", Token.PCTFREE);
		map.put("INITRANS", Token.INITRANS);
		map.put("MAXTRANS", Token.MAXTRANS);
		map.put("SEGMENT", Token.SEGMENT);
		map.put("CREATION", Token.CREATION);
		map.put("IMMEDIATE", Token.IMMEDIATE);
		map.put("DEFERRED", Token.DEFERRED);
		map.put("STORAGE", Token.STORAGE);
		map.put("NEXT", Token.NEXT);
		map.put("MINEXTENTS", Token.MINEXTENTS);
		map.put("MAXEXTENTS", Token.MAXEXTENTS);
		map.put("MAXSIZE", Token.MAXSIZE);
		map.put("PCTINCREASE", Token.PCTINCREASE);
		map.put("FLASH_CACHE", Token.FLASH_CACHE);
		map.put("CELL_FLASH_CACHE", Token.CELL_FLASH_CACHE);
		map.put("NONE", Token.NONE);
		map.put("LOB", Token.LOB);
		map.put("STORE", Token.STORE);
		map.put("ROW", Token.ROW);
		map.put("CHUNK", Token.CHUNK);
		map.put("CACHE", Token.CACHE);
		map.put("NOCACHE", Token.NOCACHE);
		map.put("LOGGING", Token.LOGGING);
		map.put("NOCOMPRESS", Token.NOCOMPRESS);
		map.put("KEEP_DUPLICATES", Token.KEEP_DUPLICATES);
		map.put("EXCEPTIONS", Token.EXCEPTIONS);
		map.put("PURGE", Token.PURGE);
		map.put("INITIALLY", Token.INITIALLY);

		map.put("FETCH", Token.FETCH);
		map.put("TABLESPACE", Token.TABLESPACE);
		map.put("PARTITION", Token.PARTITION);
		map.put("TRUE", Token.TRUE);
		map.put("FALSE", Token.FALSE);

		map.put("，", Token.COMMA);
		map.put("（", Token.LPAREN);
		map.put("）", Token.RPAREN);


		DEFAULT_GBASEDBT_KEYWORDS = new Keywords(map);
	}
	{
		dbType = JdbcConstants.GBASEDBT;
	}

	private int startPos;

	public GBasedbtLexer(String input, CommentHandler commentHandler, String dbType){
		super(input, true);
		this.commentHandler = commentHandler;
		this.dbType = dbType;

	}

/*	public GBasedbtLexer(String input) {
		super(input);
	}*/

	public GBasedbtLexer(char[] input, int inputLength, boolean skipComment){
		super(input, inputLength, skipComment);
	}

	public GBasedbtLexer(String input){
		this(input, true, true);
	}

	public GBasedbtLexer(String input, SQLParserFeature... features){
		super(input, true);
		this.keepComments = true;

		for (SQLParserFeature feature : features) {
			config(feature, true);
		}
	}

	public GBasedbtLexer(String input, boolean skipComment, boolean keepComments){
		super(input, skipComment);
		this.skipComment = skipComment;
		this.keepComments = keepComments;
	}



	private final static boolean[] identifierFlags = new boolean[256];
	static {
		for (char c = 0; c < identifierFlags.length; ++c) {
			if (c >= 'A' && c <= 'Z') {
				identifierFlags[c] = true;
			} else if (c >= 'a' && c <= 'z') {
				identifierFlags[c] = true;
			} else if (c >= '0' && c <= '9') {
				identifierFlags[c] = true;
			}
		}
		//TODO 需要增加对标识符的判断

		// identifierFlags['`'] = true;
		identifierFlags['_'] = true;
		//identifierFlags['-'] = true; // mysql
	}

	public static boolean isIdentifierChar(char c) {
		if (c <= identifierFlags.length) {
			return identifierFlags[c];
		}
		return c != '　' && c != '，' && c != '）';
	}

	@Override
	public void scanVariable() {
		if (ch != ':' && ch != '#' && ch != '$') {
			throw new ParserException("illegal variable. " + info());
		}
		mark = pos;
		bufPos = 1;
		if (ch==':') {
//			pos++;
			if (isIdentifierChar(charAt(pos + 1))) {
//				for (; ; ) {
//					ch = charAt(++pos);
//
//					if (!isIdentifierChar(ch)) {
//						break;
//					}
//
//					bufPos++;
//					continue;
//				}
				pos = pos + 1;
			}
			this.ch = charAt(pos);

//			stringVal = subString(mark+1, bufPos-1);
			stringVal=":";
			token = COLON;
		}else {


			if (charAt(pos + 1) == '`') {
				++pos;
				++bufPos;
				char ch;
				for (; ; ) {
					ch = charAt(++pos);

					if (ch == '`') {
						bufPos++;
						ch = charAt(++pos);
						break;
					} else if (ch == EOI) {
						throw new ParserException("illegal identifier. " + info());
					}

					bufPos++;
					continue;
				}

				this.ch = charAt(pos);

				stringVal = subString(mark, bufPos);
				token = Token.VARIANT;
			} else if (charAt(pos + 1) == '{') {
				++pos;
				++bufPos;
				char ch;
				for (; ; ) {
					ch = charAt(++pos);

					if (ch == '}') {
						bufPos++;
						ch = charAt(++pos);
						break;
					} else if (ch == EOI) {
						throw new ParserException("illegal identifier. " + info());
					}

					bufPos++;
					continue;
				}

				this.ch = charAt(pos);

				stringVal = subString(mark, bufPos);
				token = Token.VARIANT;
			} else {
				for (; ; ) {
					ch = charAt(++pos);

					if (!isIdentifierChar(ch)) {
						break;
					}

					bufPos++;
					continue;
				}
			}

			this.ch = charAt(pos);

			stringVal = subString(mark, bufPos);
			token = Token.VARIANT;

		}
	}


}
