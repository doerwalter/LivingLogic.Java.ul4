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
The unary vSQL operator {@code -}.

@author W. Doerwald
**/
public class VSQLNegAST extends VSQLUnaryAST
{
	/**
	UL4 type for the {@link VSQLNegAST} class.
	**/
	protected static class Type extends VSQLUnaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLNegAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlnegast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a unary negation expression (e.g. ``-x``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLNegAST;
		}

		@Override
		protected VSQLAST fromul4(String sourcePrefix, VSQLAST ast, String sourceSuffix)
		{
			return new VSQLNegAST(sourcePrefix, ast, sourceSuffix);
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLNegAST(String sourcePrefix, VSQLAST obj, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceSuffix);
		validate();
	}

	public VSQLNegAST(VSQLAST obj, String sourceSuffix)
	{
		this(null, obj, sourceSuffix);
	}

	public VSQLNegAST(String sourcePrefix, VSQLAST obj)
	{
		this(sourcePrefix, obj, null);
	}

	public static VSQLNegAST make(VSQLAST obj)
	{
		int prec = obj.getPrecedence();
		if (prec > PRECEDENCE)
			return new VSQLNegAST("-", obj, "");
		else
			return new VSQLNegAST("-(", obj, ")");
	}

	@Override
	public String getDescription()
	{
		return "Arithmetic negation expression";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.UNOP_NEG;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("-{}", obj.getDataTypeString());
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
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL), List.of("(-", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT), List.of("(-", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER), List.of("(-", 1, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA), List.of("(-", 1, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA), List.of("(-", 1, ")"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA), List.of("(-", 1, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
