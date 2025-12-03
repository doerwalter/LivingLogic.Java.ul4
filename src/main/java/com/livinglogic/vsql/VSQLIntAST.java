/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.UL4Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
A vSQL integer constant.

@author W. Doerwald
**/
public class VSQLIntAST extends VSQLConstAST
{
	/**
	UL4 type for the {@link VSQLIntAST} class.
	**/
	protected static class Type extends VSQLConstAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLIntAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlintast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an integer value.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLIntAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private Number value;

	protected VSQLIntAST(String source, Number value)
	{
		super(source);
		this.value = value;
	}

	public static VSQLIntAST make(Number value)
	{
		return new VSQLIntAST(value.toString(), value);
	}

	@Override
	public String getDescription()
	{
		return "Integer constant";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		buffer.append(value.toString());
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONST_INT;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return VSQLDataType.INT;
	}

	@Override
	public String getNodeValue()
	{
		return value.toString();
	}
}
