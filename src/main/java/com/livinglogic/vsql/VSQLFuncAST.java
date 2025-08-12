/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.CallAST;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import static com.livinglogic.utils.StringUtils.formatMessage;

import com.livinglogic.utils.VSQLUtils;


/**
The vSQL operator for function calls.

@author W. Doerwald
**/
public class VSQLFuncAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLFuncAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLFuncAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlfuncast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a function call.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLFuncAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected VSQLDataType dataType;

	protected String name;
	protected List<VSQLAST> args;

	public VSQLFuncAST(String sourcePrefix, String name, String sourceSuffix)
	{
		super(sourcePrefix, name, sourceSuffix);
		this.name = name;
		this.args = Collections.EMPTY_LIST;
		validate();
	}

	public VSQLFuncAST(String name, String sourceSuffix)
	{
		this(null, name, sourceSuffix);
	}

	public VSQLFuncAST(String sourcePrefix, String name, String sourceInfix1, VSQLAST arg1, String sourceSuffix)
	{
		super(sourcePrefix, name, sourceInfix1, arg1, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1);
		validate();
	}

	public VSQLFuncAST(String name, String sourceInfix1, VSQLAST arg1, String sourceSuffix)
	{
		this(null, name, sourceInfix1, arg1, sourceSuffix);
	}

	public VSQLFuncAST(String sourcePrefix, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceSuffix)
	{
		super(sourcePrefix, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1, arg2);
		validate();
	}

	public VSQLFuncAST(String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceSuffix)
	{
		this(null, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceSuffix);
	}

	public VSQLFuncAST(String sourcePrefix, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceSuffix)
	{
		super(sourcePrefix, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1, arg2, arg3);
		validate();
	}

	public VSQLFuncAST(String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceSuffix)
	{
		this(null, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceSuffix);
	}

	public VSQLFuncAST(String sourcePrefix, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceInfix4, VSQLAST arg4, String sourceSuffix)
	{
		super(sourcePrefix, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceInfix4, arg4, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1, arg2, arg3, arg4);
		validate();
	}

	public VSQLFuncAST(String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceInfix4, VSQLAST arg4, String sourceSuffix)
	{
		this(null, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceInfix4, arg4, sourceSuffix);
	}

	public VSQLFuncAST(String sourcePrefix, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceInfix4, VSQLAST arg4, String sourceInfix5, VSQLAST arg5, String sourceSuffix)
	{
		super(sourcePrefix, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceInfix4, arg4, sourceInfix5, arg5, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1, arg2, arg3, arg4, arg5);
		validate();
	}

	public VSQLFuncAST(String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceInfix4, VSQLAST arg4, String sourceInfix5, VSQLAST arg5, String sourceSuffix)
	{
		this(null, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceInfix4, arg4, sourceInfix5, arg5, sourceSuffix);
	}

	public VSQLFuncAST(String sourcePrefix, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceInfix4, VSQLAST arg4, String sourceInfix5, VSQLAST arg5, String sourceInfix6, VSQLAST arg6, String sourceSuffix)
	{
		super(sourcePrefix, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceInfix4, arg4, sourceInfix5, arg5, sourceInfix6, arg6, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1, arg2, arg3, arg4, arg5, arg6);
		validate();
	}

	public VSQLFuncAST(String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceInfix4, VSQLAST arg4, String sourceInfix5, VSQLAST arg5, String sourceInfix6, VSQLAST arg6, String sourceSuffix)
	{
		this(null, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceInfix4, arg4, sourceInfix5, arg5, sourceInfix6, arg6, sourceSuffix);
	}

	public VSQLFuncAST(List<Object> content)
	{
		super(content.toArray());
		int i = 0;
		int size = content.size();

		int firstAST = size;
		for (Object item : content)
		{
			if (item instanceof VSQLAST)
			{
				firstAST = i;
				break;
			}
			++i;
		}

		if (firstAST == 2)
			this.name = (String)content.get(0);
		else if (firstAST == 3)
			this.name = (String)content.get(1);
		else
			throw new IllegalArgumentException(formatMessage("Illegal VSQLFuncAST(...) arguments " + firstAST));

		args = new ArrayList<>();

		for (Object item : content)
		{
			if (item instanceof VSQLAST)
				this.args.add((VSQLAST)item);
		}

		validate();
	}

	public static VSQLFuncAST make(String name, VSQLAST ... args)
	{
		List<Object> content = new ArrayList<>(2 * args.length + 2);
		content.add(name);
		content.add("(");
		boolean first = true;
		for (VSQLAST arg : args)
		{
			if (first)
				first = false;
			else
				content.add(", ");
			content.add(arg);
		}
		content.add(")");
		return new VSQLFuncAST(content);
	}

	@Override
	public String getDescription()
	{
		return "Function call";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.FUNC;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{}({})", name, VSQLUtils.getTypeSignature(args));
	}

	@Override
	public VSQLDataType getDataType()
	{
		return dataType;
	}

	@Override
	public String getNodeValue()
	{
		return name;
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return args;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public VSQLRule getRule()
	{
		List<Object> signature = new ArrayList<Object>();
		signature.add(name);

		for (VSQLAST arg : args)
		{
			signature.add(arg.getDataType());
		}

		return rules.get(signature);
	}

	@Override
	public int getArity()
	{
		return args.size();
	}

	private final static int PRECEDENCE = 18;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	public void validate()
	{
		for (VSQLAST arg : args)
		{
			if (arg.error != null)
			{
				error = VSQLError.SUBNODEERROR;
				dataType = null;
				return;
			}
		}

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

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(name);
		encoder.dump(args);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		name = (String)decoder.load();
		args = (List<VSQLAST>)decoder.load();
	}

	private static Map<List<Object>, VSQLRule> rules = new HashMap<>();

	//BEGIN RULES (don't remove this comment)
	private static void addRulesPart1()
	{
		addRule(rules, VSQLDataType.DATE, List.of("today"), List.of("trunc(sysdate)"));
		addRule(rules, VSQLDataType.DATETIME, List.of("now"), List.of("sysdate"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool"), List.of("0"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.NULL), List.of("0"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.BOOL), List.of(1));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.INT), List.of("(case when nvl(", 1, ", 0) = 0 then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.NUMBER), List.of("(case when nvl(", 1, ", 0) = 0 then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.DATEDELTA), List.of("(case when nvl(", 1, ", 0) = 0 then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.DATETIMEDELTA), List.of("(case when nvl(", 1, ", 0) = 0 then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.MONTHDELTA), List.of("(case when nvl(", 1, ", 0) = 0 then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.NULLLIST), List.of("(case when nvl(", 1, ", 0) = 0 then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.NULLSET), List.of("(case when nvl(", 1, ", 0) = 0 then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.DATE), List.of("(case when ", 1, " is null then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.DATETIME), List.of("(case when ", 1, " is null then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.STR), List.of("(case when ", 1, " is null then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.COLOR), List.of("(case when ", 1, " is null then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.GEO), List.of("(case when ", 1, " is null then 0 else 1 end)"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.CLOB), List.of("vsqlimpl_pkg.bool_clob(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.bool_intlist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.bool_numberlist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.bool_strlist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.bool_cloblist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.bool_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.bool_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.INTSET), List.of("vsqlimpl_pkg.bool_intlist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.bool_numberlist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.STRSET), List.of("vsqlimpl_pkg.bool_strlist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.DATESET), List.of("vsqlimpl_pkg.bool_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of("bool", VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.bool_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("int"), List.of("0"));
		addRule(rules, VSQLDataType.INT, List.of("int", VSQLDataType.BOOL), List.of(1));
		addRule(rules, VSQLDataType.INT, List.of("int", VSQLDataType.INT), List.of(1));
		addRule(rules, VSQLDataType.INT, List.of("int", VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.int_number(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("int", VSQLDataType.STR), List.of("vsqlimpl_pkg.int_str(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("int", VSQLDataType.CLOB), List.of("vsqlimpl_pkg.int_clob(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("float"), List.of("0.0"));
		addRule(rules, VSQLDataType.NUMBER, List.of("float", VSQLDataType.BOOL), List.of(1));
		addRule(rules, VSQLDataType.NUMBER, List.of("float", VSQLDataType.INT), List.of(1));
		addRule(rules, VSQLDataType.NUMBER, List.of("float", VSQLDataType.NUMBER), List.of(1));
		addRule(rules, VSQLDataType.NUMBER, List.of("float", VSQLDataType.STR), List.of("vsqlimpl_pkg.float_str(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("float", VSQLDataType.CLOB), List.of("vsqlimpl_pkg.float_clob(", 1, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", null)"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.GEO, List.of("geo", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.STR), List.of("vsqlimpl_pkg.geo_number_number_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str"), List.of("null"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.NULL), List.of("null"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.STR), List.of(1));
		addRule(rules, VSQLDataType.CLOB, List.of("str", VSQLDataType.CLOB), List.of(1));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.BOOL), List.of("(case ", 1, " when 0 then 'False' when null then 'None' else 'True' end)"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.INT), List.of("to_char(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.str_number(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.GEO), List.of("vsqlimpl_pkg.repr_geo(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.DATE), List.of("to_char(", 1, ", 'YYYY-MM-DD')"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.DATETIME), List.of("to_char(", 1, ", 'YYYY-MM-DD HH24:MI:SS')"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.repr_nulllist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.repr_datelist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.repr_intlist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.repr_numberlist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.repr_strlist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.repr_cloblist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.repr_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.NULLSET), List.of("vsqlimpl_pkg.repr_nullset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.INTSET), List.of("vsqlimpl_pkg.repr_intset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.repr_numberset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.STRSET), List.of("vsqlimpl_pkg.repr_strset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.DATESET), List.of("vsqlimpl_pkg.repr_dateset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.repr_datetimeset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.COLOR), List.of("vsqlimpl_pkg.str_color(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.DATEDELTA), List.of("vsqlimpl_pkg.str_datedelta(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.DATETIMEDELTA), List.of("vsqlimpl_pkg.str_datetimedelta(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("str", VSQLDataType.MONTHDELTA), List.of("vsqlimpl_pkg.str_monthdelta(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.NULL), List.of("'None'"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.BOOL), List.of("(case ", 1, " when 0 then 'False' when null then 'None' else 'True' end)"));
		addRule(rules, VSQLDataType.CLOB, List.of("repr", VSQLDataType.CLOB), List.of("vsqlimpl_pkg.repr_clob(", 1, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of("repr", VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.repr_cloblist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.DATE), List.of("vsqlimpl_pkg.repr_date(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.repr_datelist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.NULLSET), List.of("vsqlimpl_pkg.repr_nullset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.INTSET), List.of("vsqlimpl_pkg.repr_intset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.repr_numberset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.STRSET), List.of("vsqlimpl_pkg.repr_strset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.DATESET), List.of("vsqlimpl_pkg.repr_dateset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.repr_datetimeset(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.INT), List.of("vsqlimpl_pkg.repr_int(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.repr_number(", 1, ")"));
	}

	private static void addRulesPart2()
	{
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.STR), List.of("vsqlimpl_pkg.repr_str(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.COLOR), List.of("vsqlimpl_pkg.repr_color(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.GEO), List.of("vsqlimpl_pkg.repr_geo(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.DATETIME), List.of("vsqlimpl_pkg.repr_datetime(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.DATEDELTA), List.of("vsqlimpl_pkg.repr_datedelta(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.DATETIMEDELTA), List.of("vsqlimpl_pkg.repr_datetimedelta(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.MONTHDELTA), List.of("vsqlimpl_pkg.repr_monthdelta(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.NULLLIST), List.of("vsqlimpl_pkg.repr_nulllist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.repr_intlist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.repr_numberlist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.repr_strlist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("repr", VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.repr_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.DATE, List.of("date", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.date_int(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATE, List.of("date", VSQLDataType.DATETIME), List.of("trunc(", 1, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of("datetime", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.datetime_int(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of("datetime", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.datetime_int(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of("datetime", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.datetime_int(", 1, ", ", 2, ", ", 3, ", ", 4, ", ", 5, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of("datetime", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.datetime_int(", 1, ", ", 2, ", ", 3, ", ", 4, ", ", 5, ", ", 6, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of("datetime", VSQLDataType.DATE), List.of(1));
		addRule(rules, VSQLDataType.DATETIME, List.of("datetime", VSQLDataType.DATE, VSQLDataType.INT), List.of("(", 1, " + ", 2, "/24)"));
		addRule(rules, VSQLDataType.DATETIME, List.of("datetime", VSQLDataType.DATE, VSQLDataType.INT, VSQLDataType.INT), List.of("(", 1, " + ", 2, "/24 + ", 3, "/24/60)"));
		addRule(rules, VSQLDataType.DATETIME, List.of("datetime", VSQLDataType.DATE, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("(", 1, " + ", 2, "/24 + ", 3, "/24/60 + ", 4, "/24/60/60)"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.STR), List.of("nvl(length(", 1, "), 0)"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.CLOB), List.of("nvl(length(", 1, "), 0)"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.NULLLIST), List.of(1));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.len_intlist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.len_numberlist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.len_strlist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.len_cloblist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.len_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.len_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.NULLSET), List.of("case when ", 1, " > 0 then 1 else ", 1, " end"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.INTSET), List.of("vsqlimpl_pkg.len_intlist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.NUMBERSET), List.of("vsqlimpl_pkg.len_numberlist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.STRSET), List.of("vsqlimpl_pkg.len_strlist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.DATESET), List.of("vsqlimpl_pkg.len_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("len", VSQLDataType.DATETIMESET), List.of("vsqlimpl_pkg.len_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of("timedelta"), List.of("0"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of("timedelta", VSQLDataType.INT), List.of(1));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of("timedelta", VSQLDataType.INT, VSQLDataType.INT), List.of("(", 1, " + ", 2, "/86400)"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of("monthdelta"), List.of("0"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of("monthdelta", VSQLDataType.INT), List.of(1));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of("years", VSQLDataType.INT), List.of("(12 * ", 1, ")"));
		addRule(rules, VSQLDataType.MONTHDELTA, List.of("months", VSQLDataType.INT), List.of(1));
		addRule(rules, VSQLDataType.DATEDELTA, List.of("weeks", VSQLDataType.INT), List.of("(7 * ", 1, ")"));
		addRule(rules, VSQLDataType.DATEDELTA, List.of("days", VSQLDataType.INT), List.of(1));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of("hours", VSQLDataType.INT), List.of("(", 1, " / 24)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of("minutes", VSQLDataType.INT), List.of("(", 1, " / 1440)"));
		addRule(rules, VSQLDataType.DATETIMEDELTA, List.of("seconds", VSQLDataType.INT), List.of("(", 1, " / 86400)"));
		addRule(rules, VSQLDataType.STR, List.of("md5", VSQLDataType.STR), List.of("lower(rawtohex(dbms_crypto.hash(utl_raw.cast_to_raw(", 1, "), 2)))"));
		addRule(rules, VSQLDataType.NUMBER, List.of("random"), List.of("dbms_random.value"));
		addRule(rules, VSQLDataType.INT, List.of("randrange", VSQLDataType.INT, VSQLDataType.INT), List.of("floor(dbms_random.value(", 1, ", ", 2, "))"));
		addRule(rules, VSQLDataType.INT, List.of("seq"), List.of("livingapi_pkg.seq()"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
	}

	private static void addRulesPart3()
	{
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.BOOL, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.INT, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.INT), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.COLOR, List.of("rgb", VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NUMBER, VSQLDataType.NUMBER), List.of("vsqlimpl_pkg.rgb(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of("list", VSQLDataType.STR), List.of("vsqlimpl_pkg.list_str(", 1, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of("list", VSQLDataType.CLOB), List.of("vsqlimpl_pkg.list_clob(", 1, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of("list", VSQLDataType.NULLLIST), List.of(1));
		addRule(rules, VSQLDataType.INTLIST, List.of("list", VSQLDataType.INTLIST), List.of(1));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of("list", VSQLDataType.NUMBERLIST), List.of(1));
		addRule(rules, VSQLDataType.STRLIST, List.of("list", VSQLDataType.STRLIST), List.of(1));
		addRule(rules, VSQLDataType.CLOBLIST, List.of("list", VSQLDataType.CLOBLIST), List.of(1));
		addRule(rules, VSQLDataType.DATELIST, List.of("list", VSQLDataType.DATELIST), List.of(1));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of("list", VSQLDataType.DATETIMELIST), List.of(1));
		addRule(rules, VSQLDataType.NULLLIST, List.of("list", VSQLDataType.NULLSET), List.of(1));
		addRule(rules, VSQLDataType.INTLIST, List.of("list", VSQLDataType.INTSET), List.of(1));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of("list", VSQLDataType.NUMBERSET), List.of(1));
		addRule(rules, VSQLDataType.STRLIST, List.of("list", VSQLDataType.STRSET), List.of(1));
		addRule(rules, VSQLDataType.DATELIST, List.of("list", VSQLDataType.DATESET), List.of(1));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of("list", VSQLDataType.DATETIMESET), List.of(1));
		addRule(rules, VSQLDataType.STRSET, List.of("set", VSQLDataType.STR), List.of("vsqlimpl_pkg.set_str(", 1, ")"));
		addRule(rules, VSQLDataType.STRSET, List.of("set", VSQLDataType.CLOB), List.of("vsqlimpl_pkg.set_clob(", 1, ")"));
		addRule(rules, VSQLDataType.INTSET, List.of("set", VSQLDataType.INTSET), List.of(1));
		addRule(rules, VSQLDataType.NUMBERSET, List.of("set", VSQLDataType.NUMBERSET), List.of(1));
		addRule(rules, VSQLDataType.STRSET, List.of("set", VSQLDataType.STRSET), List.of(1));
		addRule(rules, VSQLDataType.DATESET, List.of("set", VSQLDataType.DATESET), List.of(1));
		addRule(rules, VSQLDataType.DATETIMESET, List.of("set", VSQLDataType.DATETIMESET), List.of(1));
		addRule(rules, VSQLDataType.NULLSET, List.of("set", VSQLDataType.NULLLIST), List.of("case when ", 1, " > 0 then 1 else ", 1, " end"));
		addRule(rules, VSQLDataType.INTSET, List.of("set", VSQLDataType.INTLIST), List.of("vsqlimpl_pkg.set_intlist(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBERSET, List.of("set", VSQLDataType.NUMBERLIST), List.of("vsqlimpl_pkg.set_numberlist(", 1, ")"));
		addRule(rules, VSQLDataType.STRSET, List.of("set", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.set_strlist(", 1, ")"));
		addRule(rules, VSQLDataType.DATESET, List.of("set", VSQLDataType.DATELIST), List.of("vsqlimpl_pkg.set_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.DATETIMESET, List.of("set", VSQLDataType.DATETIMELIST), List.of("vsqlimpl_pkg.set_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("dist", VSQLDataType.GEO, VSQLDataType.GEO), List.of("vsqlimpl_pkg.dist_geo_geo(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.INT, List.of("abs", VSQLDataType.BOOL), List.of(1));
		addRule(rules, VSQLDataType.INT, List.of("abs", VSQLDataType.INT), List.of("abs(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("abs", VSQLDataType.NUMBER), List.of("abs(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("cos", VSQLDataType.BOOL), List.of("cos(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("cos", VSQLDataType.INT), List.of("cos(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("cos", VSQLDataType.NUMBER), List.of("cos(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("sin", VSQLDataType.BOOL), List.of("sin(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("sin", VSQLDataType.INT), List.of("sin(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("sin", VSQLDataType.NUMBER), List.of("sin(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("tan", VSQLDataType.BOOL), List.of("tan(", 1, ")"));
	}

	private static void addRulesPart4()
	{
		addRule(rules, VSQLDataType.NUMBER, List.of("tan", VSQLDataType.INT), List.of("tan(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("tan", VSQLDataType.NUMBER), List.of("tan(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("sqrt", VSQLDataType.BOOL), List.of("sqrt(case when ", 1, " >= 0 then ", 1, " else null end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of("sqrt", VSQLDataType.INT), List.of("sqrt(case when ", 1, " >= 0 then ", 1, " else null end)"));
		addRule(rules, VSQLDataType.NUMBER, List.of("sqrt", VSQLDataType.NUMBER), List.of("sqrt(case when ", 1, " >= 0 then ", 1, " else null end)"));
		addRule(rules, VSQLDataType.STR, List.of("request_id"), List.of("livingapi_pkg.reqid"));
		addRule(rules, VSQLDataType.STR, List.of("request_method"), List.of("livingapi_pkg.reqmethod"));
		addRule(rules, VSQLDataType.STR, List.of("request_url"), List.of("livingapi_pkg.requrl"));
		addRule(rules, VSQLDataType.STR, List.of("request_header_str", VSQLDataType.STR), List.of("livingapi_pkg.reqheader_str(", 1, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of("request_header_strlist", VSQLDataType.STR), List.of("livingapi_pkg.reqheader_str(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("request_cookie", VSQLDataType.STR), List.of("livingapi_pkg.reqcookie_str(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("request_param_str", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_str(", 1, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of("request_param_strlist", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_strlist(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of("request_param_int", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_int(", 1, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of("request_param_intlist", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_intlist(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of("request_param_float", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_float(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of("request_param_floatlist", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_floatlist(", 1, ")"));
		addRule(rules, VSQLDataType.DATE, List.of("request_param_date", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_date(", 1, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of("request_param_datelist", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_datelist(", 1, ")"));
		addRule(rules, VSQLDataType.DATETIME, List.of("request_param_datetime", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_datetime(", 1, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of("request_param_datetimelist", VSQLDataType.STR), List.of("livingapi_pkg.reqparam_datetimelist(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of("search"), List.of("livingapi_pkg.global_search"));
		addRule(rules, VSQLDataType.STR, List.of("lang"), List.of("livingapi_pkg.global_lang"));
		addRule(rules, VSQLDataType.STR, List.of("mode"), List.of("livingapi_pkg.global_mode"));
	}

	static
	{
		addRulesPart1();
		addRulesPart2();
		addRulesPart3();
		addRulesPart4();
	}
	//END RULES (don't remove this comment)
}
