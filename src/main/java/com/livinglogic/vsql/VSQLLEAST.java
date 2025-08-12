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
The binary vSQL operator {@code <=}.

@author W. Doerwald
**/
public class VSQLLEAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLLEAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLLEAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlleast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary \"less than or equal\" comparison (e.g. ``x <= y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLLEAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLLEAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLLEAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLLEAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLLEAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLLEAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLLEAST(obj1, " <= ", obj2);
			else
				return new VSQLLEAST(obj1, " <= (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLLEAST("(", obj1, ") <= ", obj2);
			else
				return new VSQLLEAST("(", obj1, ") <= (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "Less-than or equal comparison";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CMP_LE;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{} <= {}", obj1.getDataTypeString(), obj2.getDataTypeString());
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
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.BOOL), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INT), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STR), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.CLOB), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.COLOR), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.GEO), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATE), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INTSET), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STRSET), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATESET), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET), List.of("1"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.cmp_int_int(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.cmp_int_int(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.cmp_int_int(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.cmp_int_int(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.cmp_int_number(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.cmp_int_number(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.cmp_number_int(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.cmp_number_int(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.cmp_number_number(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.cmp_str_str(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.cmp_str_clob(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.cmp_clob_str(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.cmp_clob_clob(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATE, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.cmp_datetime_datetime(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.cmp_datetime_datetime(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.cmp_int_int(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.cmp_number_number(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.cmp_intlist_intlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.cmp_intlist_numberlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.cmp_numberlist_intlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.cmp_numberlist_numberlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.cmp_strlist_strlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.cmp_strlist_cloblist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.cmp_cloblist_strlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.cmp_cloblist_cloblist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.cmp_datetimelist_datetimelist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.cmp_datetimelist_datetimelist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.cmp_nulllist_nulllist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.cmp_nulllist_intlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.cmp_nulllist_numberlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.cmp_nulllist_strlist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.cmp_nulllist_cloblist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.cmp_nulllist_datetimelist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.cmp_nulllist_datetimelist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.cmp_intlist_nulllist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.cmp_numberlist_nulllist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.cmp_strlist_nulllist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.cmp_cloblist_nulllist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.cmp_datetimelist_nulllist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.cmp_datetimelist_nulllist(", 1, ", ", 2, ") <= 0 then 1 else 0 end)"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
