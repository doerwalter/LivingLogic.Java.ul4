/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import static java.util.Arrays.asList;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class RenderXAST extends RenderAST
{
	public RenderXAST(InterpretedTemplate template, Slice pos, AST obj)
	{
		super(template, pos, obj);
	}

	public RenderXAST(CallAST call)
	{
		super(call);
	}

	public String getType()
	{
		return "renderx";
	}

	public void call(EvaluationContext context, UL4RenderWithContext obj, List<Object> args, Map<String, Object> kwargs)
	{
		context.pushEscape(XMLStringEscape.function);
		try
		{
			super.call(context, obj, args, kwargs);
		}
		finally
		{
			context.popEscape();
		}
	}
}
