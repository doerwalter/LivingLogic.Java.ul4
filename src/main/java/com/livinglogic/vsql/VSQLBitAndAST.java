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
The binary vSQL operator {@code &}.

@author W. Doerwald
**/
public class VSQLBitAndAST extends VSQLBinaryAST
{
	/**
	UL4 type for the {@link VSQLBitAndAST} class.
	**/
	protected static class Type extends VSQLBinaryAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLBitAndAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlbitandast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a binary bitwise \"and\" expression (e.g ``x & y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLBitAndAST;
		}

		@Override
		public VSQLAST fromul4(String sourcePrefix, VSQLAST ast1, String sourceInfix, VSQLAST ast2, String sourceSuffix)
		{
			return new VSQLBitAndAST(sourcePrefix, ast1, sourceInfix, ast2, sourceSuffix);
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLBitAndAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		validate();
	}

	public VSQLBitAndAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLBitAndAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLBitAndAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	public static VSQLBitAndAST make(VSQLAST obj1, VSQLAST obj2)
	{
		int prec1 = obj1.getPrecedence();
		int prec2 = obj2.getPrecedence();
		if (prec1 >= PRECEDENCE)
		{
			if (prec2 > PRECEDENCE)
				return new VSQLBitAndAST(obj1, " & ", obj2);
			else
				return new VSQLBitAndAST(obj1, " & (", obj2, ")");
		}
		else
		{
			if (prec2 > PRECEDENCE)
				return new VSQLBitAndAST("(", obj1, ") & ", obj2);
			else
				return new VSQLBitAndAST("(", obj1, ") & (", obj2, ")");
		}
	}

	@Override
	public String getDescription()
	{
		return formatMessage("Bitwise \"and\" operation {!r}", getSource());
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.BINOP_BITAND;
	}

	private final static int PRECEDENCE = 9;

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
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("bitand(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT), List.of("bitand(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL), List.of("bitand(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT), List.of("bitand(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.INTSET), List.of("vsqlimpl_pkg.bitand_intset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.bitand_numberset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.STRSET), List.of("vsqlimpl_pkg.bitand_strset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATESET), List.of("vsqlimpl_pkg.bitand_datetimeset(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.bitand_datetimeset(", 1, ", ", 2, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
