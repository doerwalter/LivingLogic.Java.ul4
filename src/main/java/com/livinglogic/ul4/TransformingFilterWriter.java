/*
** Copyright 2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.Writer;
import java.io.FilterWriter;
import java.io.IOException;

public abstract class TransformingFilterWriter extends FilterWriter
{
	protected TransformingFilterWriter(Writer out)
	{
		super(out);
	}

	protected abstract String transform(String str);

	public Writer getWrappedWriter()
	{
		return out;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException
	{
		String originalString = new String(cbuf, off, len);
		String transformedString = transform(originalString);
		out.write(transformedString);
	}

	@Override
	public void write(int c) throws IOException
	{
		char originalChar = (char)c;
		String transformedString = transform(Character.toString(originalChar));
		out.write(transformedString);
	}

	@Override
	public void write(String str, int off, int len) throws IOException
	{
		String originalString = str.substring(off, off+len);
		String transformedString = transform(originalString);
		out.write(transformedString);
	}
}