/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class For extends Block
{
	/**
	 * This is either a string or a list of strings/lists
	 */
	protected Object varname;
	protected AST container;

	public For(Location location, Object varname, AST container)
	{
		super(location);
		this.varname = varname;
		this.container = container;
	}

	public String getType()
	{
		return "for";
	}

	public void finish(InterpretedTemplate template, Location endlocation)
	{
		super.finish(template, endlocation);
		String type = endlocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("for"))
			throw new BlockException("for ended by end" + type);
	}

	private static void formatVarname(StringBuilder buffer, Object varname)
	{
		if (varname instanceof String)
			buffer.append((String)varname);
		else
		{
			List varnames = (List)varname;
			buffer.append("(");
			int count = 0;
			for (Object subvarname : varnames)
			{
				++count;
				formatVarname(buffer, subvarname);
				if (count == 1 || count != varnames.size())
					buffer.append(", ");
			}
			buffer.append(")");
		}
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("for ");
		formatVarname(buffer, varname);
		buffer.append(" in ");
		buffer.append(container.toString(indent));
		buffer.append("\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}

	public boolean handleLoopControl(String name)
	{
		return true;
	}

	public static void unpackVariable(EvaluationContext context, Object varname, Object item)
	{
		if (varname instanceof String)
		{
			context.put((String)varname, item);
		}
		else
		{
			Iterator<Object> itemIter = Utils.iterator(item);
			Iterator<String> nameIter = ((List)varname).iterator();

			int count = 0;

			for (;;)
			{
				if (itemIter.hasNext())
				{
					if (nameIter.hasNext())
					{
						unpackVariable(context, nameIter.next(), itemIter.next());
						++count;
					}
					else
					{
						throw new UnpackingException("mismatched for loop unpacking: " + count + " varnames, " + count + "+ items");
					}
				}
				else
				{
					if (nameIter.hasNext())
					{
						throw new UnpackingException("mismatched for loop unpacking: " + count + "+ varnames, " + count + " items");
					}
					else
					{
						break;
					}
				}
			}
		}
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		Object container = this.container.decoratedEvaluate(context);

		Iterator iter = Utils.iterator(container);

		while (iter.hasNext())
		{
			unpackVariable(context, varname, iter.next());

			try
			{
				for (AST item : content)
					item.decoratedEvaluate(context);
			}
			catch (BreakException ex)
			{
				break; // breaking this while loop breaks the evaluated for loop
			}
			catch (ContinueException ex)
			{
				// doing nothing here does exactly what we need ;)
			}
		}
		return null;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(varname);
		encoder.dump(container);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		varname = decoder.load();
		container = (AST)decoder.load();
	}
}
