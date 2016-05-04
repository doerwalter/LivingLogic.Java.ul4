/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

class LineEndAST extends TextAST
{
	public LineEndAST(InterpretedTemplate template, int startPos, int endPos)
	{
		super(template, startPos, endPos);
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
