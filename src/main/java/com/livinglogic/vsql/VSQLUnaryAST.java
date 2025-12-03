/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.Map;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.Slice;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.UnaryAST;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import com.livinglogic.utils.VSQLUtils;


/**
The base class of all unary vSQL operators.

@author W. Doerwald
**/
public abstract class VSQLUnaryAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLUnaryAST} class.
	**/
	abstract protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLUnaryAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlunaryast";
		}

		@Override
		public String getDoc()
		{
			return "Base class for all vSQL AST nodes implementing binary expressions\n(i.e. operators with two operands).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLUnaryAST;
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

	protected VSQLAST obj;

	public VSQLUnaryAST(String sourcePrefix, VSQLAST obj, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceSuffix);
		this.obj = obj;
	}

	public VSQLUnaryAST(VSQLAST obj, String sourceSuffix)
	{
		this(null, obj, sourceSuffix);
	}

	public VSQLUnaryAST(String sourcePrefix, VSQLAST obj)
	{
		this(sourcePrefix, obj, null);
	}

	// UnaryAST(AST obj) doesn't make sense, there must be an operator somewhere

	@Override
	public VSQLDataType getDataType()
	{
		return dataType;
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return List.of(obj);
	}

	@Override
	public VSQLRule getRule()
	{
		return getRules().get(List.of(obj.getDataType()));
	}

	@Override
	public int getArity()
	{
		return 1;
	}

	@Override
	public void validate()
	{
		if (obj.error != null)
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

	protected abstract Map<VSQLDataType, VSQLRule> getRules();

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (VSQLAST)decoder.load();
	}
}
