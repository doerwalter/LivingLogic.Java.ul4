/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.UL4Type;


/**
A vSQL number constant.

@author W. Doerwald
**/
public class VSQLNumberAST extends VSQLConstAST
{
	/**
	UL4 type for the {@link VSQLNumberAST} class.
	**/
	protected static class Type extends VSQLConstAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "DateTimeAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlnumberast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a number value";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLNumberAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private Number value;

	protected VSQLNumberAST(String source, Number value)
	{
		super(source);
		this.value = value;
	}

	public static VSQLNumberAST make(Number value)
	{
		return new VSQLNumberAST(value.toString(), value);
	}

	@Override
	public String getDescription()
	{
		return "Number constant";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		buffer.append(value.toString());
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONST_NUMBER;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return VSQLDataType.NUMBER;
	}

	@Override
	public String getNodeValue()
	{
		return value.toString();
	}
}
