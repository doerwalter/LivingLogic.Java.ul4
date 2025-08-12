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
The binary vSQL operator {@code //}.

@author W. Doerwald
**/
public class VSQLFloorDivAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLFloorDivAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLFloorDivAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlfloordivast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary truncating division expression (e.g. ``x // y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLFloorDivAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLFloorDivAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLFloorDivAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLFloorDivAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLFloorDivAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLFloorDivAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLFloorDivAST(obj1, " // ", obj2);
			else
				return new VSQLFloorDivAST(obj1, " // (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLFloorDivAST("(", obj1, ") // ", obj2);
			else
				return new VSQLFloorDivAST("(", obj1, ") // (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "Floor division";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_FLOORDIV;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{} / {}", obj1.getDataTypeString(), obj2.getDataTypeString());
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
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.floordiv_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.floordiv_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.floordiv_int_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.floordiv_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.floordiv_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.floordiv_int_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.floordiv_number_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.floordiv_number_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.floordiv_number_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.floordiv_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INT), List.of("vsqlimpl_pkg.floordiv_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.floordiv_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INT), List.of("vsqlimpl_pkg.floordiv_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.floordiv_number_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INT), List.of("vsqlimpl_pkg.floordiv_number_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.floordiv_number_int(", 1, ", ", 2, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
