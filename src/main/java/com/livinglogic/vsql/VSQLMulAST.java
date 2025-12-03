/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.livinglogic.ul4.UL4Type;
import static com.livinglogic.utils.StringUtils.formatMessage;


/**
The binary vSQL operator {@code *}.

@author W. Doerwald
**/
public class VSQLMulAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLMulAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLMulAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlmulast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary multiplication expression (e.g. ``x * y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLMulAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLMulAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLMulAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLMulAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLMulAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLMulAST make(VSQLAST obj1, VSQLAST obj2, VSQLAST ... objs)
	{
		int prec1 = obj1.getPrecedence();

		for (int i = 0; i <= objs.length; ++i)
		{
			int prec2 = obj2.getPrecedence();
			if (prec1 >= PRECEDENCE)
			{
				if (prec2 > PRECEDENCE)
					obj1 = new VSQLMulAST(obj1, " * ", obj2);
				else
					obj1 = new VSQLMulAST(obj1, " * (", obj2, ")");
			}
			else
			{
				if (prec2 > PRECEDENCE)
					obj1 = new VSQLMulAST("(", obj1, ") * ", obj2);
				else
					obj1 = new VSQLMulAST("(", obj1, ") * (", obj2, ")");
			}
			// Prepare for next round (if there is one)
			if (i != objs.length)
			{
				prec1 = PRECEDENCE;
				obj2 = objs[i];
			}
		}
		return (VSQLMulAST)obj1;
	}

	@Override
	public String getDescription()
	{
		return "Multiplication";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_MUL;
	}

	private final static int PRECEDENCE = 12;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	protected Map<List<VSQLDataType>, VSQLRule> getRules()
	{
		return rules;
	}

	private static Map<List<VSQLDataType>, VSQLRule> rules = new HashMap<>();

	//BEGIN RULES (don't remove this comment)
	private static void addRulesPart1()
	{
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.BOOL, VSQLDataType.DATEDELTA), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.BOOL, VSQLDataType.MONTHDELTA), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.INT, VSQLDataType.DATEDELTA), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.INT, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.INT, VSQLDataType.MONTHDELTA), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.BOOL, VSQLDataType.STR), List.of("vsqlimpl_pkg.mul_int_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.BOOL, VSQLDataType.CLOB), List.of("vsqlimpl_pkg.mul_int_clob(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.INT, VSQLDataType.STR), List.of("vsqlimpl_pkg.mul_int_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.INT, VSQLDataType.CLOB), List.of("vsqlimpl_pkg.mul_int_clob(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mul_str_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INT), List.of("vsqlimpl_pkg.mul_str_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mul_clob_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INT), List.of("vsqlimpl_pkg.mul_clob_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.BOOL, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.mul_int_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.mul_int_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.BOOL, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.mul_int_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.BOOL, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.mul_int_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.BOOL, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.mul_int_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.mul_int_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INT, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.mul_int_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.INT, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.mul_int_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.INT, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.mul_int_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.INT, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.mul_int_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.INT, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.mul_int_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.INT, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.mul_int_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mul_intlist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.mul_intlist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mul_numberlist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.mul_numberlist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mul_strlist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.mul_strlist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mul_cloblist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.mul_cloblist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mul_datetimelist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.mul_datetimelist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mul_datetimelist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.mul_datetimelist_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.BOOL, VSQLDataType.NULLLIST), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.INT, VSQLDataType.NULLLIST), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.BOOL), List.of("(", 1, " * ", 2, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INT), List.of("(", 1, " * ", 2, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
