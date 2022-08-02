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
			return "AST node for literal text that is the end of a line.";
		}

		@Override
		public LineEndAST create(String id)
		{
			return new LineEndAST(null, "", 0, 0);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof LineEndAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public LineEndAST(Template template, String source, int startPos, int stopPos)
	{
		super(template, source, startPos, stopPos);
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
