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
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.CallAST;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import static com.livinglogic.utils.StringUtils.formatMessage;

import com.livinglogic.utils.VSQLUtils;


/**
The vSQL operator for method calls.

@author W. Doerwald
**/
public class VSQLMethAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLMethAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLMethAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlmethast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a method call.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLMethAST;
		}

		@Override
		public VSQLAST fromul4(AST ast, Map<String, VSQLField> vars)
		{
			CallAST callAST = (CallAST)ast;
			callAST.getObj();
			return null;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected VSQLDataType dataType;

	protected VSQLAST obj;
	protected String name;
	protected List<VSQLAST> args;

	public VSQLMethAST(String sourcePrefix, VSQLAST obj, String sourceInfix0, String name, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceInfix0, name, sourceSuffix);
		this.name = name;
		this.args = Collections.EMPTY_LIST;
		validate();
	}

	public VSQLMethAST(VSQLAST obj, String sourceInfix0, String name, String sourceSuffix)
	{
		this(null, obj, sourceInfix0, name, sourceSuffix);
	}

	public VSQLMethAST(String sourcePrefix, VSQLAST obj, String sourceInfix0, String name, String sourceInfix1, VSQLAST arg1, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceInfix0, name, sourceInfix1, arg1, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1);
		validate();
	}

	public VSQLMethAST(VSQLAST obj, String sourceInfix0, String name, String sourceInfix1, VSQLAST arg1, String sourceSuffix)
	{
		this(null, obj, sourceInfix0, name, sourceInfix1, arg1, sourceSuffix);
	}

	public VSQLMethAST(String sourcePrefix, VSQLAST obj, String sourceInfix0, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceInfix0, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1, arg2);
		validate();
	}

	public VSQLMethAST(VSQLAST obj, String sourceInfix0, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceSuffix)
	{
		this(null, obj, sourceInfix0, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceSuffix);
	}

	public VSQLMethAST(String sourcePrefix, VSQLAST obj, String sourceInfix0, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceInfix0, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceSuffix);
		this.name = name;
		this.args = List.of(arg1, arg2, arg3);
		validate();
	}

	public VSQLMethAST(VSQLAST obj, String sourceInfix0, String name, String sourceInfix1, VSQLAST arg1, String sourceInfix2, VSQLAST arg2, String sourceInfix3, VSQLAST arg3, String sourceSuffix)
	{
		this(null, obj, sourceInfix0, name, sourceInfix1, arg1, sourceInfix2, arg2, sourceInfix3, arg3, sourceSuffix);
	}

	public VSQLMethAST(List<Object> content)
	{
		super(content.toArray());

		int size = content.size();

		if (size < 4)
			throw new IllegalArgumentException("Illegal VSQLMethAST(...) arguments");

		Object firstItem = content.get(0);
		int objOffset = (firstItem == null || firstItem instanceof String) ? 1 : 0;

		this.args = new ArrayList<>();

		for (int i = objOffset; i < size; ++i)
		{
			Object item = content.get(i);
			if (item instanceof String itemString)
			{
				if (i - objOffset == 2)
				{
					this.name = itemString;
				}
			}
			else if (item instanceof VSQLAST itemAST)
			{
				if (obj == null)
					obj = itemAST;
				else
					args.add(itemAST);
			}
			else if (item != null)
			{
				throw new IllegalArgumentException(formatMessage("VSQLMethAST() doesn't support an argument of type {!t}", item));
			}
		}
		if (name == null)
			throw new IllegalArgumentException(formatMessage("Can't find method name in VSQLMethAST() call"));
		validate();
	}

	public static VSQLMethAST make(VSQLAST obj, String name, VSQLAST ... args)
	{
		List<Object> content = new ArrayList<>();
		int prec = obj.getPrecedence();
		if (prec >= PRECEDENCE)
		{
			content.add(obj);
			content.add(".");
		}
		else
		{
			content.add("(");
			content.add(obj);
			content.add(").");
		}
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
		return new VSQLMethAST(content);
	}

	@Override
	public String getDescription()
	{
		return "Method call";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.METH;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{}.{}({})", obj.getDataTypeString(), name, VSQLUtils.getTypeSignature(args));
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
		List<VSQLAST> children = new ArrayList<>(1+args.size());

		children.add(obj);
		children.addAll(args);
		return children;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public VSQLRule getRule()
	{
		List<Object> signature = new ArrayList<Object>();
		signature.add(obj.getDataType());
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
		return 1 + args.size();
	}

	private final static int PRECEDENCE = 17;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	public void validate()
	{
		if (obj.error != null)
		{
			error = VSQLError.SUBNODEERROR;
			dataType = null;
			return;
		}
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
		encoder.dump(obj);
		encoder.dump(name);
		encoder.dump(args);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (VSQLAST)decoder.load();
		name = (String)decoder.load();
		args = (List<VSQLAST>)decoder.load();
	}

	private static Map<List<Object>, VSQLRule> rules = new HashMap<>();

	//BEGIN RULES (don't remove this comment)
	private static void addRulesPart1()
	{
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "lower"), List.of("lower(", 1, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "lower"), List.of("lower(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "upper"), List.of("upper(", 1, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "upper"), List.of("upper(", 1, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, "startswith", VSQLDataType.STR), List.of("vsqlimpl_pkg.startswith_str_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, "startswith", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.startswith_str_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, "startswith", VSQLDataType.STR), List.of("vsqlimpl_pkg.startswith_clob_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, "startswith", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.startswith_clob_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, "endswith", VSQLDataType.STR), List.of("vsqlimpl_pkg.endswith_str_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.STR, "endswith", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.endswith_str_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, "endswith", VSQLDataType.STR), List.of("vsqlimpl_pkg.endswith_clob_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.BOOL, List.of(VSQLDataType.CLOB, "endswith", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.endswith_clob_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "strip"), List.of("vsqlimpl_pkg.strip_str(", 1, ", null, 1, 1)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "strip"), List.of("vsqlimpl_pkg.strip_clob(", 1, ", null, 1, 1)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "strip", VSQLDataType.STR), List.of("vsqlimpl_pkg.strip_str(", 1, ", ", 2, ", 1, 1)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "strip", VSQLDataType.STR), List.of("vsqlimpl_pkg.strip_clob(", 1, ", ", 2, ", 1, 1)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "lstrip"), List.of("vsqlimpl_pkg.strip_str(", 1, ", null, 1, 0)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "lstrip"), List.of("vsqlimpl_pkg.strip_clob(", 1, ", null, 1, 0)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "lstrip", VSQLDataType.STR), List.of("vsqlimpl_pkg.strip_str(", 1, ", ", 2, ", 1, 0)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "lstrip", VSQLDataType.STR), List.of("vsqlimpl_pkg.strip_clob(", 1, ", ", 2, ", 1, 0)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "rstrip"), List.of("vsqlimpl_pkg.strip_str(", 1, ", null, 0, 1)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "rstrip"), List.of("vsqlimpl_pkg.strip_clob(", 1, ", null, 0, 1)"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "rstrip", VSQLDataType.STR), List.of("vsqlimpl_pkg.strip_str(", 1, ", ", 2, ", 0, 1)"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "rstrip", VSQLDataType.STR), List.of("vsqlimpl_pkg.strip_clob(", 1, ", ", 2, ", 0, 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.STR), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.CLOB), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.STR), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.CLOB), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.STR, VSQLDataType.NULL), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.STR, VSQLDataType.NULL), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.CLOB, VSQLDataType.NULL), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.NULL), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.NULL), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.NULL), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.NULL), List.of("(instr(", 1, ", ", 2, ") - 1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.STR, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_str_str(", 1, ", ", 2, ", ", 3, ", null)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.CLOB, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_str_clob(", 1, ", ", 2, ", ", 3, ", null)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.STR, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_clob_str(", 1, ", ", 2, ", ", 3, ", null)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.CLOB, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_clob_clob(", 1, ", ", 2, ", ", 3, ", null)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_str_str(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.find_str_str(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_str_str(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_str_clob(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.find_str_clob(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.STR, "find", VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_str_clob(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_clob_str(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.find_clob_str(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_clob_str(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_clob_clob(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.find_clob_clob(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.CLOB, "find", VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.find_clob_clob(", 1, ", ", 2, ", ", 3, ", ", 4, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "replace", VSQLDataType.STR, VSQLDataType.STR), List.of("replace(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, "replace", VSQLDataType.STR, VSQLDataType.STR), List.of("replace(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STR, "split"), List.of("vsqlimpl_pkg.split_str_str(", 1, ", null)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOB, "split"), List.of("vsqlimpl_pkg.split_clob_str(", 1, ", null)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STR, "split", VSQLDataType.NULL), List.of("vsqlimpl_pkg.split_str_str(null, null)"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOB, "split", VSQLDataType.NULL), List.of("vsqlimpl_pkg.split_clob_str(null, null)"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STR, "split", VSQLDataType.STR), List.of("vsqlimpl_pkg.split_str_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOB, "split", VSQLDataType.STR), List.of("vsqlimpl_pkg.split_clob_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STR, "split", VSQLDataType.STR, VSQLDataType.NULL), List.of("vsqlimpl_pkg.split_str_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOB, "split", VSQLDataType.STR, VSQLDataType.NULL), List.of("vsqlimpl_pkg.split_clob_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STR, "split", VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.split_str_str(", 1, ", null, ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STR, "split", VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.split_str_str(", 1, ", null, ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOB, "split", VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.split_clob_str(", 1, ", null, ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOB, "split", VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.split_clob_str(", 1, ", null, ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STR, "split", VSQLDataType.STR, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.split_str_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STR, "split", VSQLDataType.STR, VSQLDataType.INT), List.of("vsqlimpl_pkg.split_str_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOB, "split", VSQLDataType.STR, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.split_clob_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOB, "split", VSQLDataType.STR, VSQLDataType.INT), List.of("vsqlimpl_pkg.split_clob_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "join", VSQLDataType.STR), List.of("vsqlimpl_pkg.join_str_str(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, "join", VSQLDataType.STRLIST), List.of("vsqlimpl_pkg.join_str_strlist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.STR, "join", VSQLDataType.CLOB), List.of("vsqlimpl_pkg.join_str_clob(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.STR, "join", VSQLDataType.CLOBLIST), List.of("vsqlimpl_pkg.join_str_cloblist(", 1, ", ", 2, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.COLOR, "lum"), List.of("vsqlimpl_pkg.lum(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATE, "week"), List.of("to_number(to_char(", 1, ", 'IW'))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "week"), List.of("to_number(to_char(", 1, ", 'IW'))"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
