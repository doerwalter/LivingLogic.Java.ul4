/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.Map;
import java.io.IOException;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.Slice;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.BinaryAST;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import com.livinglogic.utils.VSQLUtils;


/**
The base class of all binary vSQL operators.

@author W. Doerwald
**/
public abstract class VSQLBinaryAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLBinaryAST} class.
	**/
	abstract protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLBinaryAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlbinaryast";
		}

		@Override
		public String getDoc()
		{
			return "Base class for all AST nodes implementing unary expressions\n(i.e. operators with one operand).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLUnaryAST;
		}

		protected abstract VSQLAST fromul4(String sourcePrefix, VSQLAST ast1, String sourceInfix, VSQLAST ast2, String sourceSuffix);

		@Override
		public VSQLAST fromul4(AST ast, Map<String, VSQLField> vars)
		{
			AST arg1AST = ((BinaryAST)ast).getObj1();
			AST arg2AST = ((BinaryAST)ast).getObj2();
			return fromul4(
				VSQLUtils.getSourcePrefix(ast, arg1AST),
				VSQLAST.type.fromul4(arg1AST, vars),
				VSQLUtils.getSourceInfix(arg1AST, arg2AST),
				VSQLAST.type.fromul4(arg2AST, vars),
				VSQLUtils.getSourceSuffix(arg2AST, ast)
			);
		}
	}

	// No type object since this is abstract
	// public static final Type type = new Type();

	// @Override
	// public UL4Type getTypeUL4()
	// {
	// 	return type;
	// }

	protected VSQLDataType dataType;

	protected VSQLAST obj1;
	protected VSQLAST obj2;

	public VSQLBinaryAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		super(sourcePrefix, obj1, sourceInfix, obj2, sourceSuffix);
		this.obj1 = obj1;
		this.obj2 = obj2;
	}

	public VSQLBinaryAST(String sourcePrefix, VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(sourcePrefix, obj1, sourceInfix, obj2, null);
	}

	public VSQLBinaryAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2, String sourceSuffix)
	{
		this(null, obj1, sourceInfix, obj2, sourceSuffix);
	}

	public VSQLBinaryAST(VSQLAST obj1, String sourceInfix, VSQLAST obj2)
	{
		this(null, obj1, sourceInfix, obj2, null);
	}

	@Override
	public VSQLDataType getDataType()
	{
		return dataType;
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return List.of(obj1, obj2);
	}

	@Override
	public VSQLRule getRule()
	{
		return getRules().get(List.of(obj1.getDataType(), obj2.getDataType()));
	}

	@Override
	public int getArity()
	{
		return 2;
	}

	public void validate()
	{
		if (obj1.error != null || obj2.error != null)
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

	protected abstract Map<List<VSQLDataType>, VSQLRule> getRules();

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj1);
		encoder.dump(obj2);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj1 = (VSQLAST)decoder.load();
		obj2 = (VSQLAST)decoder.load();
	}
}
