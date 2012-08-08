/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
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

public abstract class CompiledTemplate implements Template
{
	public String getName()
	{
		return "unnamed";
	}

	public String renders(Map<String, Object> variables)
	{
		StringWriter out = new StringWriter();

		try
		{
			render(out, variables);
		}
		catch (IOException ex)
		{
			// Can't happen with a StringWriter!
		}
		return out.toString();
	}

	public abstract void render(EvaluationContext context) throws java.io.IOException;

	public void render(Writer out, Map<String, Object> variables) throws java.io.IOException
	{
		render(new EvaluationContext(out, variables));
	}
}
