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
The binary vSQL operator {@code %}.

@author W. Doerwald
**/
public class VSQLModAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLModAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLModAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlmodast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary modulo expression (e.g. ``x % y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLModAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLModAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLModAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLModAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLModAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLModAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLModAST(obj1, " % ", obj2);
			else
				return new VSQLModAST(obj1, " % (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLModAST("(", obj1, ") % ", obj2);
			else
				return new VSQLModAST("(", obj1, ") % (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return "Modulo expression";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_MOD;
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
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mod_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.mod_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mod_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.mod_int_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.mod_int_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.mod_int_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.mod_number_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.mod_number_int(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.mod_number_number(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.COLOR), List.of("vsqlimpl_pkg.mod_color_color(", 1, ", ", 2, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
