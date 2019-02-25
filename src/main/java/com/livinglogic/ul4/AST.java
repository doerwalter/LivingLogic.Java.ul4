/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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
import static com.livinglogic.ul4.Utils.findInnermostException;

/**
 * The base class of all syntax tree nodes. This can be either literal text
 * ({@link TextAST}) between the tags, or compiled tag content ({@link CodeAST}).
 */
public abstract class AST implements UL4ONSerializable, UL4GetAttr, UL4Dir, UL4Repr
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
	 * Source position as a line number/column number pair.
	 * Will be "uninitialized" (i.e. -1), but will be calculated on the first
	 * call to {@see getLine} or {@see getCol}.
	 */
	int line;
	int col;

	/**
	 * Create a new {@code AST} object.
	 * @param pos The slice in the template source, where the source for this object is located.
	 */
	public AST(InterpretedTemplate template, Slice pos)
	{
		this.template = template;
		this.pos = pos;
		this.line = -1;
		this.col = -1;
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

	public Slice getPos()
	{
		return pos;
	}

	// Used by {@link InterpretedTemplate#compile} to fix the position for inner templates
	void setPos(Slice pos)
	{
		this.pos = pos;
		// Reset line and column information
		line = -1;
		col = -1;
	}

	public void setStartPos(int startPos)
	{
		setPos(pos == null ? new Slice(true, false, startPos, -1) : pos.withStart(startPos));
	}

	public void setStopPos(int stopPos)
	{
		setPos(pos == null ? new Slice(false, true, -1, stopPos) : pos.withStop(stopPos));
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

	public String getSourcePrefix()
	{
		String source = getFullSource();
		int outerStartPos = getPos().getStart();
		int innerStartPos = outerStartPos;
		int maxPrefix = 40;
		boolean found = false; // Have we found a natural stopping position?
		while (maxPrefix > 0)
		{
			// We arrived at the start of the source code
			if (outerStartPos == 0)
			{
				found = true;
				break;
			}
			// We arrived at the start of the line
			if (source.charAt(outerStartPos-1) == '\n')
			{
				found = true;
				break;
			}
			--maxPrefix;
			--outerStartPos;
		}
		String result = source.substring(outerStartPos, innerStartPos);
		if (!found)
			result = "\u2026" + result;
		return result;
	}

	public String getSourceSuffix()
	{
		String source = getFullSource();
		int outerStopPos = getPos().getStop();
		int innerStopPos = outerStopPos;
		int maxSuffix = 40;
		boolean found = false; // Have we found a natural stopping position?
		while (maxSuffix > 0)
		{
			// We arrived at the end of the source code
			if (outerStopPos >= source.length())
			{
				found = true;
				break;
			}
			// We arrived at the end of the line
			if (source.charAt(outerStopPos) == '\n')
			{
				found = true;
				break;
			}
			--maxSuffix;
			++outerStopPos;
		}
		String result = source.substring(innerStopPos, outerStopPos);
		if (!found)
			result += "\u2026";
		return result;
	}

	private void calculateLineCol()
	{
		String source = getFullSource();
		int offset = pos.getStart();
		int lastLineFeed = source.lastIndexOf("\n", offset);

		line = 1;
		if (lastLineFeed == -1)
		{
			col = offset+1;
		}
		else
		{
			col = 1;
			for (int i = 0; i < offset; ++i)
			{
				if (source.charAt(i) == '\n')
				{
					++line;
					col = 0;
				}
				++col;
			}
		}
	}

	public int getLine()
	{
		if (line == -1)
			calculateLineCol();
		return line;
	}

	public int getCol()
	{
		if (col == -1)
			calculateLineCol();
		return col;
	}

	public void decorateException(Throwable ex)
	{
		ex = findInnermostException(ex);
		if (!(ex instanceof LocationException))
			ex.addSuppressed(new LocationException(this));
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
		line = -1;
		col = -1;
	}

	protected static Set<String> attributes = makeSet("type", "template", "pos", "line", "col", "fullsource", "source", "sourceprefix", "sourcesuffix");

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
			case "line":
				return getLine();
			case "col":
				return getCol();
			case "fullsource":
				return getFullSource();
			case "source":
				return getSource();
			case "sourceprefix":
				return getSourcePrefix();
			case "sourcesuffix":
				return getSourceSuffix();
			default:
				throw new AttributeException(this, key);
		}
	}

	protected void reprPosLineCol(UL4Repr.Formatter formatter)
	{
		formatter.append(" pos=(");
		formatter.visit(pos.getStart());
		formatter.append(":");
		formatter.visit(pos.getStop());
		formatter.append(") line=");
		formatter.visit(getLine());
		formatter.append(" col=");
		formatter.visit(getCol());
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		reprPosLineCol(formatter);
		formatter.append(">");
	}
}
