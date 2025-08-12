/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.livinglogic.ul4.UL4Type;


/**
A vSQL date constant.

@author W. Doerwald
**/
public class VSQLDateAST extends VSQLConstAST
{
	/**
	UL4 type for the {@link VSQLDateAST} class.
	**/
	protected static class Type extends VSQLConstAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLDateAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqldateast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a date value (e.g. `@(2000-02-29)`)";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLDateAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private LocalDate value;

	private VSQLDateAST(String source, LocalDate value)
	{
		super(source);
		this.value = value;
	}

	public static VSQLDateAST make(LocalDate value)
	{
		return new VSQLDateAST(formatterRepr.format(value), value);
	}

	@Override
	public String getDescription()
	{
		return "Date constant";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		buffer.append(formatterSQL.format(value));
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONST_DATE;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return VSQLDataType.DATE;
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US);
	private static DateTimeFormatter formatterRepr = DateTimeFormatter.ofPattern("@(yyyy-MM-dd)", Locale.US);
	private static DateTimeFormatter formatterSQL = DateTimeFormatter.ofPattern("'to_date('''yyyy-MM-dd''', ''YYYY-MM-DD'')'", Locale.US);

	@Override
	public String getNodeValue()
	{
		return formatter.format(value);
	}
}
