/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;

import static com.livinglogic.utils.MapUtils.makeMap;
import com.livinglogic.utils.ObjectAsMap;
import com.livinglogic.utils.MapChain;

/**
 * Base class for template code that has been converted to Java source code.
 *
 * @author W. Doerwald
 */

public abstract class CompiledTemplate extends ObjectAsMap implements Template, UL4Type
{
	public String getName()
	{
		return "unnamed";
	}

	public abstract void renderImpl(EvaluationContext context) throws java.io.IOException;

	public void render(EvaluationContext context) throws java.io.IOException
	{
		context.pushTemplate(this);
		try
		{
			renderImpl(context);
		}
		finally
		{
			context.popTemplate();
		}
	}

	public void render(EvaluationContext context, Map<String, Object> variables) throws java.io.IOException
	{
		context.pushVariables(variables);
		try
		{
			renderImpl(context);
		}
		finally
		{
			context.popVariables();
		}
	}

	public void render(Writer out, Map<String, Object> variables, boolean keepWhitespace) throws java.io.IOException
	{
		render(new EvaluationContext(out, variables, keepWhitespace));
	}

	public String renders(EvaluationContext context)
	{
		StringWriter out = new StringWriter();

		Writer oldWriter = context.setWriter(out);
		try
		{
			render(context);
		}
		catch (IOException ex)
		{
			// Can't happen with a StringWriter!
		}
		finally
		{
			context.setWriter(oldWriter);
		}
		return out.toString();
	}

	public String renders(EvaluationContext context, Map<String, Object> variables)
	{
		context.pushVariables(variables);
		try
		{
			return renders(context);
		}
		finally
		{
			context.popVariables();
		}
	}

	public String renders(Map<String, Object> variables, boolean keepWhitespace)
	{
		return renders(new EvaluationContext(null, variables, keepWhitespace));
	}

	public String typeUL4()
	{
		return "template";
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>();
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((CompiledTemplate)object).getName();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
