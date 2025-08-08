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
The unary vSQL operator {@code ~}.

@author W. Doerwald
**/
public class VSQLBitNotAST extends VSQLUnaryAST
{
	/**
	UL4 type for the {@link VSQLBitNotAST} class.
	**/
	protected static class Type extends VSQLUnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLBitNotAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlbitnotast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a bitwise unary \"not\" expression that returns its operand\nwith its bits inverted (e.g. ``~x``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLBitNotAST;
		}

		@Override
		protected VSQLAST fromul4(String sourcePrefix, VSQLAST ast, String sourceSuffix)
		{
			return new VSQLBitNotAST(sourcePrefix, ast, sourceSuffix);
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLBitNotAST(String sourcePrefix, VSQLAST obj, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceSuffix);
		validate();
	}

	public VSQLBitNotAST(VSQLAST obj, String sourceSuffix)
	{
		this(null, obj, sourceSuffix);
	}

	public VSQLBitNotAST(String sourcePrefix, VSQLAST obj)
	{
		this(sourcePrefix, obj, null);
	}

	public static VSQLBitNotAST make(VSQLAST obj)
	{
		int prec = obj.getPrecedence();
		if (prec > PRECEDENCE)
			return new VSQLBitNotAST("~", obj, "");
		else
			return new VSQLBitNotAST("~(", obj, ")");
	}

	@Override
	public String getDescription()
	{
		return "Bitwise not expression";
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("~{}", obj.getDataTypeString());
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.UNOP_BITNOT;
	}

	private final static int PRECEDENCE = 14;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	protected Map<VSQLDataType, VSQLRule> getRules()
	{
		return rules;
	}

	private static Map<VSQLDataType, VSQLRule> rules = new HashMap<>();

	//BEGIN RULES (don't remove this comment)
	private static void addRulesPart1()
	{
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL), List.of("(-", 1, " - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT), List.of("(-", 1, " - 1)"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
