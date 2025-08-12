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
The binary vSQL operator {@code or}.

@author W. Doerwald
**/
public class VSQLOrAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLOrAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLOrAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlorast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary \"or\" expression (e.g. ``x or y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLOrAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLOrAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLOrAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLOrAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLOrAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLOrAST make(VSQLAST obj1, VSQLAST obj2, VSQLAST ... objs)
	{
		int prec1 = obj1.getPrecedence();

		for (int i = 0; i <= objs.length; ++i)
		{
			int prec2 = obj2.getPrecedence();
			if (prec1 >= PRECEDENCE)
			{
				if (prec2 > PRECEDENCE)
					obj1 = new VSQLOrAST(obj1, " or ", obj2);
				else
					obj1 = new VSQLOrAST(obj1, " or (", obj2, ")");
			}
			else
			{
				if (prec2 > PRECEDENCE)
					obj1 = new VSQLOrAST("(", obj1, ") or ", obj2);
				else
					obj1 = new VSQLOrAST("(", obj1, ") or (", obj2, ")");
			}
			// Prepare for next round (if there is one)
			if (i != objs.length)
			{
				prec1 = PRECEDENCE;
				obj2 = objs[i];
			}
		}
		return (VSQLOrAST)obj1;
	}

	@Override
	public String getDescription()
	{
		return "Logical \"or\" expression";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_OR;
	}

	private final static int PRECEDENCE = 4;

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
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of(1));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.BOOL), List.of(2));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.INT), List.of(2));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER), List.of(2));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.STR), List.of(2));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.CLOB), List.of(2));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.COLOR), List.of(2));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.GEO), List.of(2));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATE), List.of(2));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME), List.of(2));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA), List.of(2));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA), List.of(2));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA), List.of(2));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST), List.of(2));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST), List.of(2));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST), List.of(2));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST), List.of(2));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST), List.of(2));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST), List.of(2));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST), List.of(2));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET), List.of(2));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.INTSET), List.of(2));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET), List.of(2));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.STRSET), List.of(2));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATESET), List.of(2));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET), List.of(2));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(case when ", 1, " = 1 then 1 else ", 2, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.STR), List.of("nvl(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.STR, VSQLDataType.CLOB), List.of("(case when ", 1, " is not null then to_clob(", 1, ") else ", 2, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.CLOB), List.of("(case when ", 1, " is not null and length(", 1, ") != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.STR), List.of("(case when ", 1, " is not null and length(", 1, ") != 0 then ", 1, " else to_clob(", 2, ") end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATE), List.of("nvl(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIME), List.of("nvl(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INTLIST), List.of("(case when nvl(vsqlimpl_pkg.len_intlist(", 1, "), 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERLIST), List.of("(case when nvl(vsqlimpl_pkg.len_numberlist(", 1, "), 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.STRLIST), List.of("(case when nvl(vsqlimpl_pkg.len_strlist(", 1, "), 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOBLIST), List.of("(case when nvl(vsqlimpl_pkg.len_cloblist(", 1, "), 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATELIST), List.of("(case when nvl(vsqlimpl_pkg.len_datetimelist(", 1, "), 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMELIST), List.of("(case when nvl(vsqlimpl_pkg.len_datetimelist(", 1, "), 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMELIST), List.of("(case when nvl(vsqlimpl_pkg.len_datetimelist(", 1, "), 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATELIST), List.of("(case when nvl(vsqlimpl_pkg.len_datetimelist(", 1, "), 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLLIST), List.of("(case when nvl(", 1, ", 0) != 0 then ", 1, " else ", 2, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTLIST), List.of("(case when nvl(", 1, ", 0) != 0 then vsqlimpl_pkg.intlist_fromlen(", 1, ") else ", 2, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 1, ", 0) != 0 then vsqlimpl_pkg.numberlist_fromlen(", 1, ") else ", 2, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRLIST), List.of("(case when nvl(", 1, ", 0) != 0 then vsqlimpl_pkg.strlist_fromlen(", 1, ") else ", 2, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 1, ", 0) != 0 then vsqlimpl_pkg.cloblist_fromlen(", 1, ") else ", 2, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATELIST), List.of("(case when nvl(", 1, ", 0) != 0 then vsqlimpl_pkg.datetimelist_fromlen(", 1, ") else ", 2, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 1, ", 0) != 0 then vsqlimpl_pkg.datetimelist_fromlen(", 1, ") else ", 2, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLLIST), List.of("(case when nvl(vsqlimpl_pkg.len_intlist(", 1, "), 0) != 0 then ", 1, " else vsqlimpl_pkg.intlist_fromlen(", 2, ") end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST), List.of("(case when nvl(vsqlimpl_pkg.len_numberlist(", 1, "), 0) != 0 then ", 1, " else vsqlimpl_pkg.numberlist_fromlen(", 2, ") end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLLIST), List.of("(case when nvl(vsqlimpl_pkg.len_strlist(", 1, "), 0) != 0 then ", 1, " else vsqlimpl_pkg.strlist_fromlen(", 2, ") end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST), List.of("(case when nvl(vsqlimpl_pkg.len_cloblist(", 1, "), 0) != 0 then ", 1, " else vsqlimpl_pkg.cloblist_fromlen(", 2, ") end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLLIST), List.of("(case when nvl(vsqlimpl_pkg.len_datetimelist(", 1, "), 0) != 0 then ", 1, " else vsqlimpl_pkg.datetimelist_fromlen(", 2, ") end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST), List.of("(case when nvl(vsqlimpl_pkg.len_datetimelist(", 1, "), 0) != 0 then ", 1, " else vsqlimpl_pkg.datetimelist_fromlen(", 2, ") end)"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
