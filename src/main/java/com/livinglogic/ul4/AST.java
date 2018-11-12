/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
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
 * The base class of all syntax tree nodes. This can be either literal text
 * ({@link TextAST}) between the tags, or compiled tag content ({@link CodeAST}).
 */
public abstract class AST implements UL4ONSerializable, UL4GetAttr, UL4Dir, SourcePart, UL4Repr
{
	/**
	 * The template to which this node belongs
	 */
	protected InterpretedTemplate template;

	/**
	 * The start/end index of this node in the source
	 */
	protected Slice pos;

	/**
	 * Create a new {@code AST} object.
	 * @param pos The slice in the template source, where the source for this object is located.
	 */
	public AST(InterpretedTemplate template, Slice pos)
	{
		this.template = template;
		this.pos = pos;
	}

	/**
	 * Return the template to which this node belongs.
	 */
	public InterpretedTemplate getTemplate()
	{
		return template;
	}

	// Used by {@link InterpretedTemplate#compile} to fix the template references for inner templates
	void setTemplate(InterpretedTemplate template)
	{
		this.template = template;
	}

	// Used by {@link InterpretedTemplate#compile} to fix the position for inner templates
	void setPos(Slice pos)
	{
		this.pos = pos;
	}

	public String getSource()
	{
		// Use the full source, as positions are relative to that
		// (and not to the source of any inner template in which {@this} might live)
		return pos.getFrom(getFullSource());
	}

	public String getFullSource()
	{
		return getTemplate().getFullSource();
	}

	/**
	 * <p>Evaluate this node and return the resulting object.</p>
	 *
	 * <p>Evaluating the node might also have several side effects besides the
	 * method return value: It might write to the output stream that is stored
	 * in the {@code context} object (as the {@link PrintAST} and
	 * {@link PrintXAST} nodes do) and it might modify the variables map stored
	 * in the context (like {@link VarAST} does).</p>
	 * 
	 * @param context The context object in which this node has to be evaluated.
	 * @return The result of evaluating the node.
	 */
	abstract public Object evaluate(EvaluationContext context);

	/**
	 * {@code decoratedEvaluate} wraps a call to {@link #evaluate} with exception
	 * handling. {@link #evaluate} should not be called directly. Instead
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
		// Pass through LocationException, as the location is already known.
		// However in CallAST, we always wrap to get the call into the exception stack.
		catch (BreakException|ContinueException|ReturnException|LocationException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			decorateException(ex);
			throw ex;
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

	public Slice getPos()
	{
		return pos;
	}

	public void setStartPos(int startPos)
	{
		this.pos = pos == null ? new Slice(true, false, startPos, -1) : pos.withStart(startPos);
	}

	public void setStopPos(int stopPos)
	{
		this.pos = pos == null ? new Slice(false, true, -1, stopPos) : pos.withStop(stopPos);
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
	 * @param formatter the Formatter object
	 */
	public void toString(Formatter formatter)
	{
		toStringFromSource(formatter);
	}

	public void toStringFromSource(Formatter formatter)
	{
		formatter.write(getSource());
	}

	public String getUL4ONName()
	{
		return "de.livinglogic.ul4." + getType();
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(template);
		encoder.dump(pos);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		template = (InterpretedTemplate)decoder.load();
		pos = (Slice)decoder.load();
	}

	protected static Set<String> attributes = makeSet("type", "template", "pos", "source", "fullsource");

	public Set<String> dirUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "type":
				return getType();
			case "template":
				return template;
			case "pos":
				return pos;
			case "source":
				return getSource();
			case "fullsource":
				return getFullSource();
			default:
				throw new AttributeException(this, key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" pos=(");
		formatter.visit(pos.getStart());
		formatter.append(":");
		formatter.visit(pos.getStop());
		formatter.append(")>");
	}
}
