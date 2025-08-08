/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
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
The binary vSQL operator {@code +}.

@author W. Doerwald
**/
public class VSQLAddAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLAddAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLAddAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqladdast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary addition expression that adds its two operands and\nreturns the result  (e.g. ``x + y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLAddAST;
		}

		@Override
		public VSQLAST fromul4(String sourcePrefix, VSQLAST ast1, String sourceInfix, VSQLAST ast2, String sourceSuffix)
		{
			return new VSQLAddAST(sourcePrefix, ast1, sourceInfix, ast2, sourceSuffix);
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLAddAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLAddAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLAddAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLAddAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLAddAST make(VSQLAST obj1, VSQLAST obj2, VSQLAST ... objs)
	{
		/*
		Note that all our binary operator are left associative, as "a + b + c"
		gets interpreted as "(a + b) + c".

		So if we have a left operand that has the same precedence than this
		operator we do *not* need brackets.

		But if we have a right operand that has the same precedence, we *do*
		need brackets.
		*/

		int prec1 = obj1.getPrecedence();

		for (int i = 0; i <= objs.length; ++i)
		{
			int prec2 = obj2.getPrecedence();
			if (prec1 >= PRECEDENCE)
			{
				if (prec2 > PRECEDENCE)
					obj1 = new VSQLAddAST(obj1, " + ", obj2);
				else
					obj1 = new VSQLAddAST(obj1, " + (", obj2, ")");
			}
			else
			{
				if (prec2 > PRECEDENCE)
					obj1 = new VSQLAddAST("(", obj1, ") + ", obj2);
				else
					obj1 = new VSQLAddAST("(", obj1, ") + (", obj2, ")");
			}
			// Prepare for next round (if there is one)
			if (i != objs.length)
			{
				prec1 = PRECEDENCE;
				obj2 = objs[i];
			}
		}
		return (VSQLAddAST)obj1;
	}

	@Override
	public String getDescription()
	{
		return "Addition";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_ADD;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{} + {}", obj1.getDataTypeString(), obj2.getDataTypeString());
	}

	private final static int PRECEDENCE = 11;

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
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.STR), List.of("(", 1, " || ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.STR, VSQLDataType.CLOB), List.of("(", 1, " || ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.STR), List.of("(", 1, " || ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.CLOB), List.of("(", 1, " || ", 2, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.add_intlist_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.add_intlist_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.add_numberlist_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.add_numberlist_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.add_strlist_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.add_strlist_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.add_cloblist_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.add_cloblist_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.add_datetimelist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.add_datetimelist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLLIST), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.add_nulllist_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.add_nulllist_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.add_nulllist_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.add_nulllist_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.add_nulllist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.add_nulllist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.add_intlist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.add_numberlist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.add_strlist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.add_cloblist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.add_datetimelist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.add_datetimelist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATEDELTA), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATEDELTA), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.MONTHDELTA), List.of("vsqlimpl_pkg.add_datetime_months(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.MONTHDELTA), List.of("vsqlimpl_pkg.add_datetime_months(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATE), List.of("vsqlimpl_pkg.add_months_datetime(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIME), List.of("vsqlimpl_pkg.add_months_datetime(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATEDELTA), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " + ", 2, ")"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.MONTHDELTA), List.of("(", 1, " + ", 2, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
