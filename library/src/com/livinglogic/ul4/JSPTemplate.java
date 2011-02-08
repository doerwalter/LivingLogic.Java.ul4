package com.livinglogic.ul4;

import java.util.Map;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;

/**
 * Copyright 2009-2010 by LivingLogic AG, Bayreuth/Germany
 *
 * All Rights Reserved
 *
 * See LICENSE for the license
 *
 * Base class for template code that has been converted to JSP.
 *
 * @author W. Doerwald
 * @version $Revision$ $Date$
 */

public abstract class JSPTemplate implements Template
{
	public String renders(Map<String, Object> variables)
	{
		StringWriter out = new StringWriter();

		try
		{
			render(out, variables);
		}
		catch (IOException ex)
		{
			// does not happen!
		}
		return out.toString();
	}

	public abstract void render(Writer out, Map<String, Object> variables) throws java.io.IOException;
}
