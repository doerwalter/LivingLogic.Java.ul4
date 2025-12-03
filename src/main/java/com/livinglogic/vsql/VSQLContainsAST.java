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
The binary vSQL operator {@code in}.

@author W. Doerwald
**/
public class VSQLContainsAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLContainsAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLContainsAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlcontainsast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary containment testing operator (e.g. ``x in y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLContainsAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLContainsAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLContainsAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLContainsAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLContainsAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLContainsAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLContainsAST(obj1, " in ", obj2);
			else
				return new VSQLContainsAST(obj1, " in (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLContainsAST("(", obj1, ") in ", obj2);
			else
				return new VSQLContainsAST("(", obj1, ") in (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "Containment test";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_CONTAINS;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{} in {}", obj1.getDataTypeString(), obj2.getDataTypeString());
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
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.contains_null_intlist(", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.contains_null_numberlist(", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.contains_null_strlist(", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.contains_null_cloblist(", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.contains_null_datetimelist(", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.contains_null_datetimelist(", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.contains_null_nulllist(", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.STR), List.of("vsqlimpl_pkg.contains_str_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.CLOB), List.of("vsqlimpl_pkg.contains_str_clob(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.contains_str_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.contains_str_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.STRSET), List.of("vsqlimpl_pkg.contains_str_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.contains_int_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.contains_int_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.INTSET), List.of("vsqlimpl_pkg.contains_int_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.contains_int_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.contains_number_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.contains_number_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.INTSET), List.of("vsqlimpl_pkg.contains_number_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.contains_number_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.contains_datetime_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATESET), List.of("vsqlimpl_pkg.contains_datetime_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.contains_datetime_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.contains_datetime_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULLLIST), List.of("case when ", 1, " is null then vsqlimpl_pkg.contains_null_nulllist(", 2, ") else 0 end"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}

