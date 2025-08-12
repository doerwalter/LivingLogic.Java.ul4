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
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.ItemAST;
import com.livinglogic.ul4.SliceAST;
import static com.livinglogic.utils.StringUtils.formatMessage;

import com.livinglogic.utils.VSQLUtils;


/**
The binary vSQL "item index" operator, i.e. {@code a[b]}.

@author W. Doerwald
**/
public class VSQLItemAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLItemAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLItemAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlitemast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for subscripting expression (e.g. ``x[y]``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLItemAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLItemAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLItemAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLItemAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLItemAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLItemAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		if (prec1 >= PRECEDENCE)
			return new VSQLItemAST(obj1, "[", obj2, "]");
		else
			return new VSQLItemAST("(", obj1, ")[", obj2, "]");
	}

	@Override
	public String getDescription()
	{
		return "Item access expression";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_ITEM;
	}

	private final static int PRECEDENCE = 16;

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
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULLLIST, VSQLDataType.BOOL), List.of("null"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULLLIST, VSQLDataType.INT), List.of("null"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.item_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INT), List.of("vsqlimpl_pkg.item_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.CLOB, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.item_clob(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.CLOB, VSQLDataType.INT), List.of("vsqlimpl_pkg.item_clob(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STRLIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.item_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STRLIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.item_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOBLIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.item_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.item_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INTLIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.item_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INTLIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.item_intlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.item_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.item_numberlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATELIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.item_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATELIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.item_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.item_datetimelist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INT), List.of("vsqlimpl_pkg.item_datetimelist(", 1, ", ", 2, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
