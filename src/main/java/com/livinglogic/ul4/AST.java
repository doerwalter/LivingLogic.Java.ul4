/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
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
 * The base class of all nodes. This can be either literal text ({@code TextAST})
 * between the tags, or compiled tag content ({@code CodeAST}).
 */
public abstract class AST implements UL4ONSerializable, UL4GetItemString, UL4Attributes, SourcePart, UL4Repr
{
	/**
	 * The start index of this node in the source
	 */
	protected int startPos;

	/**
	 * The end index of this node in the source
	 */
	protected int endPos;

	/**
	 * Create a new {@code AST} object.
	 * @param startPos The start offset in the template source, where the source for this object is located.
	 * @param endPos The end offset in the template source, where the source for this object is located.
	 */
	public AST(int startPos, int endPos)
	{
		this.startPos = startPos;
		this.endPos = endPos;
	}

	/**
	 * The template source. This is abstract, because for literal text the
	 * source is a member of {@code TextAST}, but for node that are created
	 * by compiling the source inside a tag, the source is only referenced from
	 * the {@code Tag} object (to which {@code CodeAST} has a reference)
	 */
	abstract public String getSource();

	abstract public String getText();

	abstract public CodeSnippet getSnippet();

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
			context.tick();
			return evaluate(context);
		}
		// Pass through SourceException, as the location is already known.
		// However in CallAST, we always wrap to get the call into the exception stack.
		catch (BreakException|ContinueException|ReturnException|SourceException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new SourceException(ex, context.getTemplate(), this);
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

	public int getStartPos()
	{
		return startPos;
	}

	public int getEndPos()
	{
		return endPos;
	}

	public void setStartPos(int startPos)
	{
		this.startPos = startPos;
	}

	public void setEndPos(int endPos)
	{
		this.endPos = endPos;
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
		formatter.write(getText());
	}

	public String getUL4ONName()
	{
		return "de.livinglogic.ul4." + getType();
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(startPos);
		encoder.dump(endPos);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		startPos = (Integer)decoder.load();
		endPos = (Integer)decoder.load();
	}

	protected static Set<String> attributes = makeSet("type", "startpos", "endpos");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "type":
				return getType();
			case "startpos":
				return startPos;
			case "endpos":
				return endPos;
			default:
				return new UndefinedKey(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" startPos=");
		formatter.visit(startPos);
		formatter.append(" endPos=");
		formatter.visit(endPos);
		formatter.append(">");
	}
}
