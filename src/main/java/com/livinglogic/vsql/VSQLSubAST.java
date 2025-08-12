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
The binary vSQL operator {@code -}.

@author W. Doerwald
**/
public class VSQLSubAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLSubAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLSubAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlsubast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the binary subtraction expression (e.g. ``x - y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLSubAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLSubAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLSubAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLSubAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLSubAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLSubAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLSubAST(obj1, " - ", obj2);
			else
				return new VSQLSubAST(obj1, " - (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLSubAST("(", obj1, ") - ", obj2);
			else
				return new VSQLSubAST("(", obj1, ") - (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "Subtraction";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_SUB;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{} - {}", obj1.getDataTypeString(), obj2.getDataTypeString());
	}

	private final static int PRECEDENCE = 11;

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
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATEDELTA), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATE, VSQLDataType.DATE), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIME), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.MONTHDELTA), List.of("vsqlimpl_pkg.add_datetime_months(", 1, ", -", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.MONTHDELTA), List.of("vsqlimpl_pkg.add_datetime_months(", 1, ", -", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATEDELTA), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.MONTHDELTA), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATEDELTA), List.of("(", 1, " - ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(", 1, " - ", 2, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
