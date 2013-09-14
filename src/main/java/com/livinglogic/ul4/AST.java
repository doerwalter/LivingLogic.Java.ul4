/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;

import java.io.IOException;
import java.util.Set;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.UL4ONSerializable;

/**
 * The base class of all nodes in the abstract syntax tree.
 */
public abstract class AST implements UL4ONSerializable, UL4GetAttributes
{
	/**
	 * The source code location where this node appears in.
	 */
	protected Location location = null;

	/**
	 * The start index of this node in the source
	 */
	protected int start;

	/**
	 * The end index of this node in the source
	 */
	protected int end;

	/**
	 * Create a new {@code AST} object.
	 * @param location The source code location where this node appears in.
	 */
	public AST(Location location, int start, int end)
	{
		this.location = location;
		this.start = start;
		this.end = end;
	}

	/**
	 * Evaluate this node and return the resulting object.
	 *
	 * Evaluating the node might also have several side effects besides the
	 * method return value: It might write to the output stream that is stored
	 * in the {@code context} object (as the {@link Print} and {@link PrintX}
	 * nodes do) and it might modify the variables map stored in the context
	 * (like {@link StoreVar}, {@link DelVar}, {@link AddVar} etc. do)
	 * 
	 * @param context The context object in which this node has to be evaluated.
	 * @return The result of evaluating the node.
	 */
	abstract public Object evaluate(EvaluationContext context);

	/**
	 * {@code decoratedEvaluate} wraps a call to {@link evaluate} with exception
	 * handling. {@link evaluate} should not be called directly. Instead
	 * {@code decoratedEvaluate} should be used. When an exception bubbles up
	 * the call stack, {@code decoratedEvaluate} creates a exception chain
	 * containing information about the location of the exception.
	 */
	public Object decoratedEvaluate(EvaluationContext context)
	{
		try
		{
			return evaluate(context);
		}
		catch (BreakException ex)
		{
			throw ex;
		}
		catch (ContinueException ex)
		{
			throw ex;
		}
		catch (ReturnException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new ASTException(ex, this);
		}
	}

	/**
	 * Return a unique name for this type of AST node.
	 */
	abstract public String getType();

	public String toString()
	{
		Formatter formatter = new Formatter();
		toString(formatter);
		formatter.finish();
		return formatter.toString();
	}

	/**
	 * Return the source code location where this node appears in.
	 */
	public Location getLocation()
	{
		return location;
	}

	public int getStart()
	{
		return start;
	}

	public int getEnd()
	{
		return end;
	}

	public void setStart(int start)
	{
		this.start = start;
	}

	public void setEnd(int end)
	{
		this.end = end;
	}

	protected static class Formatter
	{
		private StringBuilder builder = new StringBuilder();
		private int level = 0;
		private boolean needsLF = false;

		public Formatter()
		{
		}

		public void indent()
		{
			++level;
		}

		public void dedent()
		{
			--level;
		}

		public void lf()
		{
			needsLF = true;
		}

		public void write(String string)
		{
			if (needsLF)
			{
				builder.append("\n");
				for (int i = 0; i < level; ++i)
					builder.append("\t");
				needsLF = false;
			}
			builder.append(string);
		}

		public void finish()
		{
			if (needsLF)
				builder.append("\n");
		}
		public String toString()
		{
			return builder.toString();
		}
	}
	/**
	 * Format this object using a Formatter object.
	 * @param formmatter the Fomatter object
	 */
	public void toString(Formatter formatter)
	{
		toStringFromSource(formatter);
	}

	public void toStringFromSource(Formatter formatter)
	{
		formatter.write(location.getSource().substring(start, end));
	}

	public String getUL4ONName()
	{
		return "de.livinglogic.ul4." + getType();
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(location);
		encoder.dump(start);
		encoder.dump(end);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		location = (Location)decoder.load();
		start = (Integer)decoder.load();
		end = (Integer)decoder.load();
	}

	protected static Set<String> attributes = makeSet("type", "location", "start", "end");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("type".equals(key))
			return getType();
		else if ("location".equals(key))
			return location;
		else if ("start".equals(key))
			return start;
		else if ("end".equals(key))
			return end;
		else
			return new UndefinedKey(key);
	}
}
