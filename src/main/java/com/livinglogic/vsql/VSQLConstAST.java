/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Date;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.Color;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.ConstAST;


/**
Base class of all vSQL AST node for constants.

@author W. Doerwald
**/
public abstract class VSQLConstAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLConstAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLConstAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlconstast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a constant value.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLConstAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public VSQLConstAST(String source)
	{
		super(source);
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public int getArity()
	{
		return 0;
	}

	private final static int PRECEDENCE = 20;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	public void validate()
	{
	}
}
