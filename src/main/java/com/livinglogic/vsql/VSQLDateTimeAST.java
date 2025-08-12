/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Locale;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.livinglogic.ul4.UL4Type;


/**
A vSQL datetime constant.

@author W. Doerwald
**/
public class VSQLDateTimeAST extends VSQLConstAST
{
	/**
	UL4 type for the {@link VSQLDateTimeAST} class.
	**/
	protected static class Type extends VSQLConstAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLDateTimeAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqldatetimeast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a datetime value (e.g. `@(2000-02-29T12:34:56)`)";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLDateTimeAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private LocalDateTime value;

	private VSQLDateTimeAST(String source, LocalDateTime value)
	{
		super(source);
		this.value = value;
	}

	public static VSQLDateTimeAST make(LocalDateTime value)
	{
		return new VSQLDateTimeAST(formatterRepr.format(value), value);
	}

	@Override
	public String getDescription()
	{
		return "Datetime constant";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		buffer.append(formatterSQL.format(value));
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.CONST_DATETIME;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return VSQLDataType.DATETIME;
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
	private static DateTimeFormatter formatterRepr = DateTimeFormatter.ofPattern("@(yyyy-MM-dd'T'HH:mm:ss)", Locale.US);
	private static DateTimeFormatter formatterSQL = DateTimeFormatter.ofPattern("'to_date('''yyyy-MM-dd HH:mm:ss'', '''YYYY-MM-DD HH24:MI:SS'')'", Locale.US);

	public String getNodeValue()
	{
		return formatter.format((LocalDateTime)value);
	}
}
