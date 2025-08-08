/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.FunctionRepr;


/**
A vSQL string constant.

@author W. Doerwald
**/
public class VSQLStrAST extends VSQLConstAST
{
	/**
	UL4 type for the {@link VSQLStrAST} class.
	**/
	protected static class Type extends VSQLConstAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLStrAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlstrast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a string value.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLStrAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected String value;

	protected VSQLStrAST(String source, String value)
	{
		super(source);
		this.value = value;
	}

	public static VSQLStrAST make(String value)
	{
		return new VSQLStrAST(FunctionRepr.call(value), value);
	}

	@Override
	public String getDescription()
	{
		return "String constant";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		buffer.append("'");
		buffer.append(value.replace("'", "''"));
		buffer.append("'");
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONST_STR;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return VSQLDataType.STR;
	}

	public String getNodeValue()
	{
		return value;
	}
}
