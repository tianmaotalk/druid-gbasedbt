package com.alibaba.druid.sql.dialect.gbasedbt.visitor;

import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;

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

}
