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
The binary vSQL operator {@code is}.

@author W. Doerwald
**/
public class VSQLIsAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLIsAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLIsAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlisast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary ``is`` comparison expression (e.g. ``x is y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLIsAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLIsAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLIsAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLIsAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLIsAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLIsAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLIsAST(obj1, " is ", obj2);
			else
				return new VSQLIsAST(obj1, " is (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLIsAST("(", obj1, ") is ", obj2);
			else
				return new VSQLIsAST("(", obj1, ") is (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "Identity test";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_IS;
	}

	private final static int PRECEDENCE = 6;

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
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULL), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when ", 1, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.BOOL), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INT), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STR), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.CLOB), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.COLOR), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.GEO), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATE), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INTSET), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STRSET), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATESET), List.of("(case when ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is null then 1 else 0 end)"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
