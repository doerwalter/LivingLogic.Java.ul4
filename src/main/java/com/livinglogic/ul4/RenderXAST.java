/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends AbstractInstanceType
	{
		public Type()
		{
			super("ul4", "RenderXAST", "de.livinglogic.ul4.renderx", "A renderx tag.");
		}

		@Override
		public RenderXAST create(String id)
		{
			return new RenderXAST(null, null, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof RenderXAST;
		}
	}

	public static UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public RenderXAST(Template template, Slice pos, AST obj)
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
