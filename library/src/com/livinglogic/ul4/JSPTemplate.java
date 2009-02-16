package com.livinglogic.ul4;

import java.util.Map;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

/**
 * Copyright 2009 by LivingLogic AG, Bayreuth/Germany
 *
 * All Rights Reserved
 *
 * See LICENSE for the license
 *
 * Base class for template code that has been converted to JSP.
 *
 * @author W. Dörwald
 * @version $Revision$ $Date$
 */

public abstract class JSPTemplate implements Template
{
	private Writer writer = null;

	public void ship(String output)
	{
		try
		{
			writer.write(output);
		}
		catch (IOException ex) // can not happen when reading from a StringWriter
		{
			throw new RuntimeException("writing failed", ex);
		}
	}

	public String renders(Map variables)
	{
		writer = new StringWriter();

		execute(variables);
		String result = writer.toString();
		writer = null;
		return result;
	}

	public void renderjsp(JspWriter out, Map variables) throws java.io.IOException
	{
		writer = out;
		execute(variables);
		writer = null;
	}

	abstract void execute(Map variables);
}
