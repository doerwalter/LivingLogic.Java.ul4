/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class LineEndAST extends TextAST
{
	protected static class Type extends TextAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "LineEndAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.lineend";
		}

		@Override
		public String getDoc()
		{
			return "Literal text in the template source that is an indentation at the start of a line.";
		}

		@Override
		public LineEndAST create(String id)
		{
			return new LineEndAST(null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof LineEndAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public LineEndAST(Template template, Slice pos)
	{
		super(template, pos);
	}

	public void toString(Formatter formatter)
	{
		formatter.write("lineend ");
		formatter.write(FunctionRepr.call(getText()));
	}

	public String getType()
	{
		return "lineend";
	}
}
