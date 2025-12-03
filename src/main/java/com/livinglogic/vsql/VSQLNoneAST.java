/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.UL4Type;


/**
The vSQL constant {@code None}.

@author W. Doerwald
**/
public class VSQLNoneAST extends VSQLConstAST
{
	/**
	UL4 type for the {@link VSQLNoneAST} class.
	**/
	protected static class Type extends VSQLConstAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLNoneAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlnoneast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for the constant ``None``";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLNoneAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected VSQLNoneAST(String source)
	{
		super(source);
	}

	public static VSQLNoneAST make()
	{
		return new VSQLNoneAST("None");
	}

	@Override
	public String getDescription()
	{
		return "None constant";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		buffer.append("null");
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONST_NONE;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return VSQLDataType.NULL;
	}

	public void validate()
	{
		error = null;
	}
}
