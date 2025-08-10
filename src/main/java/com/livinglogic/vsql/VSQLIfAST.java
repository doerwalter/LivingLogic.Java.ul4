/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.IfAST;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import static com.livinglogic.utils.StringUtils.formatMessage;

import com.livinglogic.utils.VSQLUtils;


/**
The ternary vSQL "if/else" operator, i.e. {@code a if b else c}.

@author W. Doerwald
**/
public class VSQLIfAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLIfAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLIfAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlifast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the ternary ``if/else`` operator (e.g. ``x if y else z``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLIfAST;
		}

		@Override
		public VSQLAST fromul4(AST ast, Map<String, VSQLField> vars)
		{
			AST ifAST = ((IfAST)ast).getObjIf();
			AST condAST = ((IfAST)ast).getObjCond();
			AST elseAST = ((IfAST)ast).getObjElse();

			return new VSQLIfAST(
				VSQLUtils.getSourcePrefix(ast, ifAST),
				VSQLAST.type.fromul4(ifAST, vars),
				VSQLUtils.getSourceInfix(ifAST, condAST),
				VSQLAST.type.fromul4(condAST, vars),
				VSQLUtils.getSourceInfix(condAST, elseAST),
				VSQLAST.type.fromul4(elseAST, vars),
				VSQLUtils.getSourceSuffix(elseAST, ast)
			);
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected VSQLDataType dataType;

	protected String sourcePrefix;
	protected VSQLAST objIf;
	protected String sourceInfix1;
	protected VSQLAST objCond;
	protected String sourceInfix2;
	protected VSQLAST objElse;
	protected String sourceSuffix;

	public VSQLIfAST(String sourcePrefix, VSQLAST objIf, String sourceInfix1, VSQLAST objCond, String sourceInfix2, VSQLAST objElse, String sourceSuffix)
	{
		super(sourcePrefix, objIf, sourceInfix1, objCond, sourceInfix2, objElse, sourceSuffix);
		this.objIf = objIf;
		this.objCond = objCond;
		this.objElse = objElse;
		validate();
	}

	public VSQLIfAST(String sourcePrefix, VSQLAST objIf, String sourceInfix1, VSQLAST objCond, String sourceInfix2, VSQLAST objElse)
	{
		this(sourcePrefix, objIf, sourceInfix1, objCond, sourceInfix2, objElse, null);
	}

	public VSQLIfAST(VSQLAST objIf, String sourceInfix1, VSQLAST objCond, String sourceInfix2, VSQLAST objElse, String sourceSuffix)
	{
		this(null, objIf, sourceInfix1, objCond, sourceInfix2, objElse, sourceSuffix);
	}

	public VSQLIfAST(VSQLAST objIf, String sourceInfix1, VSQLAST objCond, String sourceInfix2, VSQLAST objElse)
	{
		this(null, objIf, sourceInfix1, objCond, sourceInfix2, objElse, null);
	}

	public static VSQLIfAST make(VSQLAST objIf, VSQLAST objCond, VSQLAST objElse)
	{
		int precIf = objIf.getPrecedence();
		int precCond = objCond.getPrecedence();
		int precElse = objElse.getPrecedence();
		if (precIf > PRECEDENCE)
		{
			if (precCond > PRECEDENCE)
			{
				if (precElse > PRECEDENCE)
					return new VSQLIfAST(objIf, " if ", objCond, " else ", objElse);
				else
					return new VSQLIfAST(objIf, " if ", objCond, " else (", objElse, ")");
			}
			else
			{
				if (precElse > PRECEDENCE)
					return new VSQLIfAST(objIf, " if (", objCond, ") else ", objElse);
				else
					return new VSQLIfAST(objIf, " if (", objCond, ") else (", objElse, ")");
			}
		}
		else
		{
			if (precCond > PRECEDENCE)
			{
				if (precElse > PRECEDENCE)
					return new VSQLIfAST("(", objIf, ") if ", objCond, " else ", objElse);
				else
					return new VSQLIfAST("(", objIf, ") if ", objCond, " else (", objElse, ")");
			}
			else
			{
				if (precElse > PRECEDENCE)
					return new VSQLIfAST("(", objIf, ") if (", objCond, ") else ", objElse);
				else
					return new VSQLIfAST("(", objIf, ") if (", objCond, ") else (", objElse, ")");
			}
		}
	}

	@Override
	public String getDescription()
	{
		return "if/else expression";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.TERNOP_IF;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{} if {} else {}", objIf.getDataTypeString(), objCond.getDataTypeString(), objElse.getDataTypeString());
	}

	@Override
	public VSQLDataType getDataType()
	{
		return dataType;
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return List.of(objIf, objCond, objElse);
	}

	@Override
	public VSQLRule getRule()
	{
		return rules.get(List.of(objIf.getDataType(), objCond.getDataType(), objElse.getDataType()));
	}

	@Override
	public int getArity()
	{
		return 3;
	}

	private final static int PRECEDENCE = 3;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	public void validate()
	{
		if (objIf.error != null || objCond.error != null || objElse.error != null)
		{
			error = VSQLError.SUBNODEERROR;
			dataType = null;
		}
		else
		{
			VSQLRule rule = getRule();
			if (rule == null)
			{
				error = VSQLError.SUBNODETYPES;
				dataType = null;
			}
			else
			{
				dataType = rule.getResultType();
				error = null;
			}
		}
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(objIf);
		encoder.dump(objCond);
		encoder.dump(objElse);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		objIf = (VSQLAST)decoder.load();
		objCond = (VSQLAST)decoder.load();
		objElse = (VSQLAST)decoder.load();
	}

	protected Map<List<VSQLDataType>, VSQLRule> getRules()
	{
		return rules;
	}

	private static Map<List<VSQLDataType>, VSQLRule> rules = new HashMap<>();

	//BEGIN RULES (don't remove this comment)
	private static void addRulesPart1()
	{
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULL, VSQLDataType.BOOL), List.of(3));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULL, VSQLDataType.INT), List.of(3));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULL, VSQLDataType.NUMBER), List.of(3));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.STR), List.of(3));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.CLOB), List.of(3));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NULL, VSQLDataType.COLOR), List.of(3));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NULL, VSQLDataType.GEO), List.of(3));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NULL, VSQLDataType.DATE), List.of(3));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NULL, VSQLDataType.DATETIME), List.of(3));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULL, VSQLDataType.DATEDELTA), List.of(3));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA), List.of(3));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULL, VSQLDataType.MONTHDELTA), List.of(3));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULL, VSQLDataType.NULLLIST), List.of(3));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULL, VSQLDataType.INTLIST), List.of(3));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULL, VSQLDataType.NUMBERLIST), List.of(3));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULL, VSQLDataType.STRLIST), List.of(3));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULL, VSQLDataType.CLOBLIST), List.of(3));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULL, VSQLDataType.DATELIST), List.of(3));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULL, VSQLDataType.DATETIMELIST), List.of(3));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NULL, VSQLDataType.NULLSET), List.of(3));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NULL, VSQLDataType.INTSET), List.of(3));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULL, VSQLDataType.NUMBERSET), List.of(3));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NULL, VSQLDataType.STRSET), List.of(3));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NULL, VSQLDataType.DATESET), List.of(3));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULL, VSQLDataType.DATETIMESET), List.of(3));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.NULL, VSQLDataType.INT), List.of(3));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULL, VSQLDataType.BOOL), List.of(3));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NULL, VSQLDataType.NUMBER), List.of(3));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NULL, VSQLDataType.NUMBER), List.of(3));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULL, VSQLDataType.BOOL), List.of(3));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULL, VSQLDataType.INT), List.of(3));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULL, VSQLDataType.NULL), List.of(3));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.BOOL), List.of(3));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.INT), List.of(3));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.NUMBER), List.of(3));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.STR), List.of(3));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.CLOB), List.of(3));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.COLOR), List.of(3));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.GEO), List.of(3));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.DATE), List.of(3));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.DATETIME), List.of(3));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.DATEDELTA), List.of(3));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA), List.of(3));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.MONTHDELTA), List.of(3));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.NULLLIST), List.of(3));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.INTLIST), List.of(3));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.NUMBERLIST), List.of(3));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.STRLIST), List.of(3));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.CLOBLIST), List.of(3));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.DATELIST), List.of(3));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.DATETIMELIST), List.of(3));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.NULLSET), List.of(3));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.INTSET), List.of(3));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.NUMBERSET), List.of(3));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.STRSET), List.of(3));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.DATESET), List.of(3));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.NULL, VSQLDataType.DATETIMESET), List.of(3));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.COLOR, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATEDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.MONTHDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.COLOR, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart2()
	{
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATEDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMEDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.MONTHDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.COLOR, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATEDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.MONTHDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.BOOL, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NUMBER, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.COLOR, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATEDELTA, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATETIMEDELTA, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.MONTHDELTA, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.BOOL, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBER, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.COLOR, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATEDELTA, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.MONTHDELTA, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.BOOL, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.INT, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBER, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.COLOR, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATEDELTA, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMEDELTA, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.MONTHDELTA, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.BOOL, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.INT, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NUMBER, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.COLOR, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATEDELTA, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMEDELTA, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.MONTHDELTA, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.BOOL, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.INT, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NUMBER, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.COLOR, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATEDELTA, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.MONTHDELTA, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.BOOL, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.INT, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBER, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.COLOR, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATEDELTA, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.MONTHDELTA, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.BOOL, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INT, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBER, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.COLOR, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.MONTHDELTA, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INT, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.COLOR, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.BOOL, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INT, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBER, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.COLOR, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATEDELTA, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMEDELTA, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.MONTHDELTA, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.BOOL, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INT, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBER, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.COLOR, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATEDELTA, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.MONTHDELTA, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.BOOL, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INT, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBER, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.COLOR, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATEDELTA, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.MONTHDELTA, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.BOOL, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INT, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBER, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.COLOR, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATEDELTA, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.BOOL, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INT, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBER, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.COLOR, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATEDELTA, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart3()
	{
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.MONTHDELTA, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.BOOL, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INT, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBER, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.COLOR, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATEDELTA, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.MONTHDELTA, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.BOOL, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INT, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBER, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.COLOR, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATEDELTA, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.MONTHDELTA, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.BOOL, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INT, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBER, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.COLOR, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATEDELTA, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.BOOL, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.INT, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBER, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.COLOR, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATEDELTA, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.MONTHDELTA, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.BOOL, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.INT, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBER, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.COLOR, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATEDELTA, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMEDELTA, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.MONTHDELTA, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.BOOL, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INT, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBER, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.COLOR, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATEDELTA, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.BOOL, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.INT, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBER, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.COLOR, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATEDELTA, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMEDELTA, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.MONTHDELTA, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.BOOL, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.INT, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBER, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.COLOR, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATEDELTA, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.MONTHDELTA, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.BOOL, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INT, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBER, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.COLOR, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATEDELTA, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.COLOR, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.DATEDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMEDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.MONTHDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.COLOR, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATEDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.MONTHDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.COLOR, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.DATEDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.MONTHDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.COLOR, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.DATEDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.MONTHDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.COLOR, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.COLOR, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart4()
	{
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATEDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATEDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMEDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.MONTHDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.MONTHDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart5()
	{
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INT, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBER, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.COLOR, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.MONTHDELTA, VSQLDataType.NULL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart6()
	{
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.BOOL, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.INT, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBER, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.COLOR, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart7()
	{
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATEDELTA, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.BOOL), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.INT), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.NUMBER), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.STR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.CLOB), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.COLOR), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.GEO), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.DATE), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.DATETIME), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.DATEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.NULLLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.INTLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.STRLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.CLOBLIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.DATELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMELIST), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.NULLSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.INTSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.STRSET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.DATESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMESET), List.of("(case when nvl(", 2, ", 0) != 0 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATE, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIME, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STR, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.GEO, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATE, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIME, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STR, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.GEO, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATE, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIME, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STR, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.GEO, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATE, VSQLDataType.STR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATETIME, VSQLDataType.STR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.STR, VSQLDataType.STR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.GEO, VSQLDataType.STR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATE, VSQLDataType.CLOB), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIME, VSQLDataType.CLOB), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.STR, VSQLDataType.CLOB), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.GEO, VSQLDataType.CLOB), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATE, VSQLDataType.COLOR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIME, VSQLDataType.COLOR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.STR, VSQLDataType.COLOR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.GEO, VSQLDataType.COLOR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATE, VSQLDataType.GEO), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATETIME, VSQLDataType.GEO), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.STR, VSQLDataType.GEO), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.GEO, VSQLDataType.GEO), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATE, VSQLDataType.DATE), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATETIME, VSQLDataType.DATE), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.STR, VSQLDataType.DATE), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.GEO, VSQLDataType.DATE), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATE, VSQLDataType.DATETIME), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIME, VSQLDataType.DATETIME), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.STR, VSQLDataType.DATETIME), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.GEO, VSQLDataType.DATETIME), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATE, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIME, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STR, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.GEO, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart8()
	{
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATE, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIME, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STR, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.GEO, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATE, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIME, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STR, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.GEO, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATE, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIME, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.STR, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.GEO, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATE, VSQLDataType.INTLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIME, VSQLDataType.INTLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.STR, VSQLDataType.INTLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.GEO, VSQLDataType.INTLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATE, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIME, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STR, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.GEO, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATE, VSQLDataType.STRLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIME, VSQLDataType.STRLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.STR, VSQLDataType.STRLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.GEO, VSQLDataType.STRLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATE, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIME, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STR, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.GEO, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATE, VSQLDataType.DATELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIME, VSQLDataType.DATELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.STR, VSQLDataType.DATELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.GEO, VSQLDataType.DATELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATE, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIME, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STR, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.GEO, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATE, VSQLDataType.NULLSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIME, VSQLDataType.NULLSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.STR, VSQLDataType.NULLSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.GEO, VSQLDataType.NULLSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATE, VSQLDataType.INTSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIME, VSQLDataType.INTSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.STR, VSQLDataType.INTSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.GEO, VSQLDataType.INTSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATE, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIME, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STR, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.GEO, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATE, VSQLDataType.STRSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIME, VSQLDataType.STRSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.STR, VSQLDataType.STRSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.GEO, VSQLDataType.STRSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATE, VSQLDataType.DATESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIME, VSQLDataType.DATESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.STR, VSQLDataType.DATESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.GEO, VSQLDataType.DATESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATE, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIME, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STR, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.GEO, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.DATE, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIME, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.STR, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.GEO, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATE, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIME, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STR, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.GEO, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.DATE, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIME, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.STR, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.GEO, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.DATE, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.DATETIME, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.STR, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.GEO, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATE, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATE, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIME, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIME, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STR, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STR, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.GEO, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.GEO, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart9()
	{
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATE, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIME, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STR, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.GEO, VSQLDataType.NULL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.STR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.CLOB), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.COLOR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.GEO), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.DATE), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.DATETIME), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.INTLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.STRLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart10()
	{
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.DATELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.NULLSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.INTSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.STRSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.DATESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATE, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.STR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.CLOB), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.COLOR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.GEO), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.DATE), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.DATETIME), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.INTLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.STRLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.DATELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.NULLSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.INTSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.STRSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.DATESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIME, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.STR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.CLOB), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.COLOR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.GEO), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.DATE), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.DATETIME), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.INTLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.STRLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.DATELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.NULLSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.INTSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.STRSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.DATESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.STR, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.BOOL), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.INT), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.NUMBER), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.STR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.CLOB), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.COLOR), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.GEO), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.DATE), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.DATETIME), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.DATEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.DATETIMEDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.MONTHDELTA), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.NULLLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.INTLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.NUMBERLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.STRLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.CLOBLIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.DATELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.DATETIMELIST), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.NULLSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.INTSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.NUMBERSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.STRSET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.DATESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.GEO, VSQLDataType.DATETIMESET), List.of("(case when ", 2, " is not null then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.CLOB, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULLLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart11()
	{
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INTLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STRLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.CLOBLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATELIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMELIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULLSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INTSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STRSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATESET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMESET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.CLOB, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULLLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INTLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBERLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STRLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.CLOBLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATELIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMELIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULLSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INTSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBERSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STRSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATESET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMESET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOB, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INTLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STRLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOBLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATELIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMELIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INTSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STRSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATESET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMESET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.CLOB, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULLLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INTLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NUMBERLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.STRLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.CLOBLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATELIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATETIMELIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULLSET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INTSET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NUMBERSET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.STRSET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATESET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATETIMESET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.CLOB, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULLLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INTLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBERLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.STRLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.CLOBLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATELIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMELIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULLSET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INTSET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBERSET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.STRSET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATESET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMESET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.CLOB, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NULLLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.INTLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBERLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.STRLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.CLOBLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATELIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMELIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NULLSET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.INTSET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBERSET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.STRSET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATESET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMESET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.CLOB, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NULLLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.INTLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NUMBERLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.STRLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.CLOBLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATELIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMELIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NULLSET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.INTSET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NUMBERSET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.STRSET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATESET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMESET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.CLOB, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NULLLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.INTLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NUMBERLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart12()
	{
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.STRLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.CLOBLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATELIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMELIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NULLSET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.INTSET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NUMBERSET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.STRSET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATESET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMESET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.CLOB, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NULLLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.INTLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBERLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.STRLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.CLOBLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATELIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMELIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NULLSET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.INTSET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBERSET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.STRSET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATESET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMESET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.CLOB, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULLLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INTLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBERLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STRLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.CLOBLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATELIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMELIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULLSET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INTSET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBERSET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STRSET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATESET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMESET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOB, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INTLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STRLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOBLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATELIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLSET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INTSET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERSET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STRSET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATESET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMESET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.CLOB, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULLLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INTLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STRLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.CLOBLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATELIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMELIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULLSET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INTSET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERSET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STRSET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATESET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMESET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOB, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATELIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLSET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTSET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERSET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRSET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATESET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMESET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.CLOB, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INTLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBERLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.STRLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.CLOBLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATELIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMELIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLSET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INTSET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBERSET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.STRSET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATESET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMESET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.CLOB, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INTLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STRLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.CLOBLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart13()
	{
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATELIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLSET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INTSET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERSET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STRSET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATESET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMESET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.CLOB, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INTLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBERLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.STRLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.CLOBLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATELIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMELIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLSET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INTSET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBERSET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.STRSET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATESET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMESET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOB, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INTLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBERLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STRLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOBLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATELIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMELIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLSET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INTSET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBERSET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STRSET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATESET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMESET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.CLOB, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INTLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBERLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.STRLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.CLOBLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATELIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMELIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLSET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INTSET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBERSET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.STRSET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATESET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMESET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.CLOB, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INTLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STRLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.CLOBLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATELIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLSET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INTSET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERSET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STRSET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATESET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMESET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.CLOB, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NULLLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.INTLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBERLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.STRLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.CLOBLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATELIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMELIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NULLSET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.INTSET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBERSET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.STRSET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATESET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMESET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.CLOB, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NULLLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.INTLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBERLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.STRLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.CLOBLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATELIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMELIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NULLSET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.INTSET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBERSET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.STRSET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATESET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMESET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.CLOB, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULLLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INTLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STRLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.CLOBLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATELIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart14()
	{
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULLSET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INTSET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBERSET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STRSET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATESET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMESET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.CLOB, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NULLLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.INTLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBERLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.STRLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.CLOBLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATELIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMELIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NULLSET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.INTSET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBERSET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.STRSET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATESET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMESET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.CLOB, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NULLLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.INTLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBERLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.STRLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.CLOBLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATELIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMELIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NULLSET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.INTSET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBERSET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.STRSET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATESET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMESET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.CLOB, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULLLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INTLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STRLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.CLOBLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATELIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULLSET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INTSET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBERSET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STRSET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATESET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMESET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.CLOB, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.NULLLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INTLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.STRLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.CLOBLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.DATELIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMELIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.NULLSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.INTSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.STRSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.DATESET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMESET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.CLOB, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULLLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INTLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBERLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STRLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.CLOBLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATELIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMELIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULLSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INTSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBERSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STRSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATESET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMESET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.CLOB, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NULLLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.INTLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.STRLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.CLOBLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.DATELIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMELIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NULLSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.INTSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.STRSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.DATESET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMESET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.CLOB, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NULLLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.INTLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBERLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.STRLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.CLOBLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.DATELIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.DATETIMELIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NULLSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.INTSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart15()
	{
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.NUMBERSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.STRSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.DATESET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.INT, VSQLDataType.DATETIMESET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOB, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOB, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INTLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INTLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STRLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STRLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOBLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOBLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATELIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATELIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMELIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMELIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INTSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INTSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STRSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STRSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATESET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATESET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMESET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMESET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.BOOL, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.INT, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NUMBER, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart16()
	{
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.COLOR, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.GEO, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.DATE, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.DATETIME, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.DATEDELTA, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.DATETIMEDELTA, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.MONTHDELTA, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart17()
	{
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart18()
	{
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULLSET, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.INTSET, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NUMBERSET, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.STRSET, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.DATESET, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULLLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INTLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBERLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STRLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.CLOBLIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMELIST, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NULLSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.INTSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.NUMBERSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.STRSET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.DATETIMESET, VSQLDataType.DATETIMESET, VSQLDataType.NULL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart19()
	{
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.CLOB, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_clob(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.NULLLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_nulllist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.INTLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart20()
	{
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.STRLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.CLOBLIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_cloblist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATELIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMELIST, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart21()
	{
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.NULLSET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_nullset(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.INTSET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_intlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.NUMBERSET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_numberlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.STRSET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_strlist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	private static void addRulesPart22()
	{
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATESET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.BOOL), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.INT), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.NUMBER), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.STR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.CLOB), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.COLOR, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.COLOR), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.GEO, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.GEO), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATE, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.DATE), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIME, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.DATETIME), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.DATEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.DATETIMEDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.MONTHDELTA), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.NULLLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.INTLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.NUMBERLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.STRLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.CLOBLIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.DATELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.DATETIMELIST), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NULLSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.NULLSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.INTSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.INTSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.NUMBERSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.STRSET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.STRSET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.DATESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of(VSQLDataType.NULL, VSQLDataType.DATETIMESET, VSQLDataType.DATETIMESET), List.of("(case when vsqlimpl_pkg.bool_datetimelist(", 2, ") = 1 then ", 1, " else ", 3, " end)"));
	}

	static
	{
		addRulesPart1();
		addRulesPart2();
		addRulesPart3();
		addRulesPart4();
		addRulesPart5();
		addRulesPart6();
		addRulesPart7();
		addRulesPart8();
		addRulesPart9();
		addRulesPart10();
		addRulesPart11();
		addRulesPart12();
		addRulesPart13();
		addRulesPart14();
		addRulesPart15();
		addRulesPart16();
		addRulesPart17();
		addRulesPart18();
		addRulesPart19();
		addRulesPart20();
		addRulesPart21();
		addRulesPart22();
	}
	//END RULES (don't remove this comment)
}
