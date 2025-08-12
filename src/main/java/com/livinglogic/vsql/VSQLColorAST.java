/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.Color;


/**
A vSQL color constant.

@author W. Doerwald
**/
public class VSQLColorAST extends VSQLConstAST
{
	/**
	UL4 type for the {@link VSQLColorAST} class.
	**/
	protected static class Type extends VSQLConstAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLColorAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlcolorast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a color value (e.g. `#fff`)";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLColorAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private Color value;

	private VSQLColorAST(String source, Color value)
	{
		super(source);
		this.value = value;
	}

	public static VSQLColorAST make(Color value)
	{
		return new VSQLColorAST(value.repr(), value);
	}

	@Override
	public String getDescription()
	{
		return "Color constant";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		long longValue = value.getR();
		longValue = (longValue << 8) | value.getG();
		longValue = (longValue << 8) | value.getB();
		longValue = (longValue << 8) | value.getA();
		buffer.append(longValue);
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONST_COLOR;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return VSQLDataType.COLOR;
	}

	@Override
	public String getNodeValue()
	{
		return value.dump();
	}
}
