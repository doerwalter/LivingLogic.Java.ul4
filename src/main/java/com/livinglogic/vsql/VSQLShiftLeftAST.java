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
The binary vSQL operator {@code <<}.

@author W. Doerwald
**/
public class VSQLShiftLeftAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLShiftLeftAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLShiftLeftAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlshiftleftast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a bitwise left shift expression (e.g. ``x << y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLShiftLeftAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLShiftLeftAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLShiftLeftAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLShiftLeftAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLShiftLeftAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLShiftLeftAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLShiftLeftAST(obj1, " << ", obj2);
			else
				return new VSQLShiftLeftAST(obj1, " << (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLShiftLeftAST("(", obj1, ") << ", obj2);
			else
				return new VSQLShiftLeftAST("(", obj1, ") << (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "Left shift expression";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_SHIFTLEFT;
	}

	private final static int PRECEDENCE = 10;

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
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("trunc(", 1, " * power(2, ", 2, "))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("trunc(", 1, " * power(2, ", 2, "))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("trunc(", 1, " * power(2, ", 2, "))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("trunc(", 1, " * power(2, ", 2, "))"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
