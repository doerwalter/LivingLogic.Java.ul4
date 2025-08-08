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
The binary vSQL operator {@code /}.

@author W. Doerwald
**/
public class VSQLTrueDivAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLTrueDivAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLTrueDivAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqltruedivast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary true division expression (e.g. ``x / y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLTrueDivAST;
		}

		@Override
		public VSQLAST fromul4(String sourcePrefix, VSQLAST ast1, String sourceInfix, VSQLAST ast2, String sourceSuffix)
		{
			return new VSQLTrueDivAST(sourcePrefix, ast1, sourceInfix, ast2, sourceSuffix);
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLTrueDivAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLTrueDivAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLTrueDivAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLTrueDivAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLTrueDivAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLTrueDivAST(obj1, " / ", obj2);
			else
				return new VSQLTrueDivAST(obj1, " / (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLTrueDivAST("(", obj1, ") / ", obj2);
			else
				return new VSQLTrueDivAST("(", obj1, ") / (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "True division";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_TRUEDIV;
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
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INT), List.of("(", 1, " / ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER), List.of("(", 1, " / ", 2, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
