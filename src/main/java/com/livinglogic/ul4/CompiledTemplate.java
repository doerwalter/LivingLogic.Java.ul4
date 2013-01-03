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

/**
 * Base class for template code that has been converted to Java source code.
 *
 * @author W. Doerwald
 */

public abstract class CompiledTemplate implements Template, UL4Type
{
	public String getName()
	{
		return "unnamed";
	}

	public abstract void render(EvaluationContext context) throws java.io.IOException;

	public void render(EvaluationContext context, Map<String, Object> variables) throws java.io.IOException
	{
		Map<String, Object> oldVariables = context.setVariables(variables);
		try
		{
			render(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
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
		Map<String, Object> oldVariables = context.setVariables(variables);
		try
		{
			return renders(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	public void render(Writer out, Map<String, Object> variables) throws java.io.IOException
	{
		render(new EvaluationContext(out, variables));
	}

	public String typeUL4()
	{
		return "template";
	}
}
