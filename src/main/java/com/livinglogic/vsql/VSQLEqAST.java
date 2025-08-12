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
The binary vSQL operator {@code ==}.

@author W. Doerwald
**/
public class VSQLEqAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLEqAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLEqAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqleqast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary equality comparison (e.g. ``x == y``.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLEqAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLEqAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLEqAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLEqAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLEqAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLEqAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLEqAST(obj1, " == ", obj2);
			else
				return new VSQLEqAST(obj1, " == (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLEqAST("(", obj1, ") == ", obj2);
			else
				return new VSQLEqAST("(", obj1, ") == (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "Equality comparison";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CMP_EQ;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{} == {}", obj1.getDataTypeString(), obj2.getDataTypeString());
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
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.eq_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.eq_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.eq_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.eq_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.eq_int_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.eq_int_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.eq_number_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.eq_number_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.eq_number_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.GEO), List.of("vsqlimpl_pkg.eq_str_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.COLOR), List.of("vsqlimpl_pkg.eq_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.STR), List.of("vsqlimpl_pkg.eq_str_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.CLOB), List.of("vsqlimpl_pkg.eq_str_clob(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.STR), List.of("vsqlimpl_pkg.eq_clob_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.CLOB), List.of("vsqlimpl_pkg.eq_clob_clob(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATE), List.of("vsqlimpl_pkg.eq_datetime_datetime(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIME), List.of("vsqlimpl_pkg.eq_datetime_datetime(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA), List.of("vsqlimpl_pkg.eq_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.MONTHDELTA), List.of("vsqlimpl_pkg.eq_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA), List.of("vsqlimpl_pkg.eq_datetimedelta_datetimedelta(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.eq_nulllist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.eq_nulllist_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.eq_nulllist_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.eq_nulllist_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.eq_nulllist_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.eq_nulllist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.eq_nulllist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.eq_intlist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.eq_numberlist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.eq_strlist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.eq_cloblist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.eq_datetimelist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.eq_datetimelist_nulllist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.eq_intlist_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.eq_intlist_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.eq_numberlist_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.eq_numberlist_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.eq_strlist_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.eq_strlist_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.eq_cloblist_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.eq_cloblist_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.eq_datetimelist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.eq_datetimelist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.eq_datetimelist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.eq_datetimelist_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.NULLSET), List.of("vsqlimpl_pkg.eq_nullset_nullset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.INTSET), List.of("vsqlimpl_pkg.eq_nullset_intset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.eq_nullset_numberset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.STRSET), List.of("vsqlimpl_pkg.eq_nullset_strset(", 1, ", ", 2, ")"));
	}

	private static void addRulesPart2()
	{
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.DATESET), List.of("vsqlimpl_pkg.eq_nullset_datetimeset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.eq_nullset_datetimeset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.NULLSET), List.of("vsqlimpl_pkg.eq_intset_nullset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULLSET), List.of("vsqlimpl_pkg.eq_numberset_nullset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.NULLSET), List.of("vsqlimpl_pkg.eq_strset_nullset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.NULLSET), List.of("vsqlimpl_pkg.eq_datetimeset_nullset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULLSET), List.of("vsqlimpl_pkg.eq_datetimeset_nullset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.INTSET), List.of("vsqlimpl_pkg.eq_intset_intset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.eq_numberset_numberset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.STRSET), List.of("vsqlimpl_pkg.eq_strset_strset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.DATESET), List.of("vsqlimpl_pkg.eq_datetimeset_datetimeset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.eq_datetimeset_datetimeset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATESET), List.of("vsqlimpl_pkg.eq_datetimeset_datetimeset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.eq_datetimeset_datetimeset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
	}

	private static void addRulesPart3()
	{
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
	}

	private static void addRulesPart4()
	{
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
	}

	private static void addRulesPart5()
	{
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
	}

	private static void addRulesPart6()
	{
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
	}

	private static void addRulesPart7()
	{
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.DATESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMESET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATESET, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.BOOL), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INT), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBER), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.CLOB), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.COLOR), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.GEO), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATE), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIME), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.MONTHDELTA), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULLLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INTLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBERLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STRLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.CLOBLIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMELIST), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INTSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBERSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STRSET), List.of("(case when ", 1, " is null and ", 2, " is null then 1 else 0 end)"));
	}

	static
	{
		addRulesPart1();
		addRulesPart2();
		addRulesPart3();
		addRulesPart4();
		addRulesPart5();
		addRulesPart6();
		addRulesPart7();
	}
	//END RULES (don't remove this comment)
}
