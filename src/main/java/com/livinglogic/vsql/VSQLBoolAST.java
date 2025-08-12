/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.UL4Type;


/**
A vSQL boolean constant.

@author W. Doerwald
**/
public class VSQLBoolAST extends VSQLConstAST
{
	/**
	UL4 type for the {@link VSQLBoolAST} class.
	**/
	protected static class Type extends VSQLConstAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLBoolAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlboolast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a boolean value (`True` or `False`)";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLBoolAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private boolean value;

	private VSQLBoolAST(String source, boolean value)
	{
		super(source);
		this.value = value;
	}

	public static VSQLBoolAST make(boolean value)
	{
		return new VSQLBoolAST(value ? "True" : "False", value);
	}

	@Override
	public String getDescription()
	{
		return "Bool constant";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		buffer.append(value ? "1" : "0");
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONST_BOOL;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return VSQLDataType.BOOL;
	}

	@Override
	public String getNodeValue()
	{
		return value ? "1" : "0";
	}

	public void validate()
	{
		error = null;
	}
}
