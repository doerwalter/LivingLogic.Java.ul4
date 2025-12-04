/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.util.Date;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import com.livinglogic.vsql.VSQLAST;
import com.livinglogic.vsql.VSQLNoneAST;
import com.livinglogic.vsql.VSQLBoolAST;
import com.livinglogic.vsql.VSQLNumberAST;
import com.livinglogic.vsql.VSQLIntAST;
import com.livinglogic.vsql.VSQLStrAST;
import com.livinglogic.vsql.VSQLColorAST;
import com.livinglogic.vsql.VSQLDateAST;
import com.livinglogic.vsql.VSQLDateTimeAST;
import com.livinglogic.vsql.VSQLField;


/**
AST node for a constant value.
**/
public class ConstAST extends CodeAST
{
	protected static class Type extends CodeAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "ConstAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.const";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a constant value.";
		}

		@Override
		public ConstAST create(String id)
		{
			return new ConstAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof ConstAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected Object value;

	public ConstAST(Template template, int posStart, int posStop, Object value)
	{
		super(template, posStart, posStop);
		this.value = value;
	}

	public String getType()
	{
		return "const";
	}

	public Object getValue()
	{
		return value;
	}

	public String toString(int indent)
	{
		return FunctionRepr.call(value);
	}

	@Override
	public Object evaluate(EvaluationContext context)
	{
		return value;
	}

	@Override
	public VSQLAST asVSQL(Map<String, VSQLField> vars)
	{
		if (value == null)
		{
			return VSQLNoneAST.make();
		}
		else if (value instanceof Boolean booleanValue)
		{
			return VSQLBoolAST.make(booleanValue);
		}
		else if (value instanceof Number numberValue)
		{
			if (value instanceof Float || value instanceof Double || value instanceof BigDecimal)
			{
				return VSQLNumberAST.make(numberValue);
			}
			else
			{
				return VSQLIntAST.make(numberValue);
			}
		}
		else if (value instanceof String stringValue)
		{
			return VSQLStrAST.make(stringValue);
		}
		else if (value instanceof Color colorValue)
		{
			return VSQLColorAST.make(colorValue);
		}
		else if (value instanceof LocalDate localDateValue)
		{
			return VSQLDateAST.make(localDateValue);
		}
		else if (value instanceof Date dateValue)
		{
			return VSQLDateTimeAST.make(Utils.toLocalDateTime(dateValue));
		}
		else if (value instanceof LocalDateTime localDateTimeValue)
		{
			return VSQLDateTimeAST.make(localDateTimeValue);
		}
		else
		{
			throw new ArgumentTypeMismatchException("Constant of type {!t} is not supported by vSQL", value);
		}
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(value);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		value = decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "value");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "value":
				return value;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
