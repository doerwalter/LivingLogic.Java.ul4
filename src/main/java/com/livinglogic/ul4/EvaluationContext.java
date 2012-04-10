/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.io.Writer;
import java.io.IOException;

public class EvaluationContext
{
	protected Writer writer;
	protected Map<String, Object> variables;

	public EvaluationContext(Writer writer, Map<String, Object> variables)
	{
		this.writer = writer;
		this.variables = variables;
	}

	public void write(String string) throws IOException
	{
		writer.write(string);
	}
}
