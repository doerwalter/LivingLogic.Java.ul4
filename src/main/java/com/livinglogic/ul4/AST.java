/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeSet;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

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
	 * The start/end index of this node in the source (or of the start tag of a block)
	 */
	protected Slice startPos;

	/**
	 * Source position as a line number/column number pair.
	 * Will be {@code null}, but will be calculated on the first
	 * call to {@see getStartLine} or {@see getStartCol}.
	 */
	protected LineCol startLineCol;

	/**
	 * The start/end index of this end tag for this node in the source (or
	 * ``null`` if this is not a block).
	 */
	protected Slice stopPos;

	/**
	 * Source position of the end tag (if this is a blog) as a line
	 * number/column number pair.
	 * Will be {@code null}, but will be calculated on the first
	 * call to {@see getStopLine} or {@see getStopCol}.
	 */
	protected LineCol stopLineCol;

	/**
	 * Create a new {@code AST} object for a block (i.e. something with
	 * a start and end tag).
	 * @param startPos The slice in the template source, where the source for
	 *                 this object (or its start tag) is located.
	 * @param stopPos The slice in the template source, where the end tag is
	 *                located (or ``null`` if this is not a block).
	 */
	protected AST(InterpretedTemplate template, Slice startPos, Slice stopPos)
	{
		this.template = template;
		this.startPos = startPos;
		this.startLineCol = null;
		this.stopPos = stopPos;
		this.stopLineCol = null;
	}

	/**
	 * Create a new {@code AST} object for a non-block.
	 * @param startPos The slice in the template source, where the source for
	 *                 this object (or its start tag) is located.
	 */
	public AST(InterpretedTemplate template, Slice startPos)
	{
		this(template, startPos, null);
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

	public Slice getStartPos()
	{
		return startPos;
	}

	// Used by {@link InterpretedTemplate#compile} to fix the position for inner templates
	void setStartPos(Slice pos)
	{
		this.startPos = pos;
		// Reset line and column information
		startLineCol = null;
	}

	void setStartPos(int start, int stop)
	{
		setStartPos(new Slice(start, stop));
	}

	public void setStartPosStart(int start)
	{
		setStartPos(startPos == null ? new Slice(true, false, start, -1) : startPos.withStart(start));
	}

	public void setStartPosStop(int stop)
	{
		setStartPos(startPos == null ? new Slice(false, true, -1, stop) : startPos.withStop(stop));
	}

	public Slice getStopPos()
	{
		return stopPos;
	}

	void setStopPos(Slice pos)
	{
		this.stopPos = pos;
		// Reset line and column information
		stopLineCol = null;
	}

	void setStopPos(int start, int stop)
	{
		setStopPos(new Slice(start, stop));
	}

	public void setStopPosStart(int start)
	{
		setStopPos(stopPos == null ? new Slice(true, false, start, -1) : stopPos.withStart(start));
	}

	public void setStopPosStop(int stop)
	{
		setStopPos(stopPos == null ? new Slice(false, true, -1, stop) : stopPos.withStop(stop));
	}

	public Slice getPos()
	{
		return stopPos == null ? startPos : new Slice(startPos.getStart(), stopPos.getStop());
	}

	public String getStartSource()
	{
		// Use the full source, as positions are relative to that
		// (and not to the source of any inner template in which {@this} might live)
		return startPos.getFrom(getFullSource());
	}

	public String getSource()
	{
		return getPos().getFrom(getFullSource());
	}

	public String getStopSource()
	{
		return stopPos == null ? null : stopPos.getFrom(getFullSource());
	}

	public String getFullSource()
	{
		return getTemplate().getFullSource();
	}

	public String getSourcePrefix()
	{
		return Utils.getSourcePrefix(getFullSource(), getPos().getStart());
	}

	public String getSourceSuffix()
	{
		return Utils.getSourceSuffix(getFullSource(), getPos().getStop());
	}

	public String getStartSourcePrefix()
	{
		return Utils.getSourcePrefix(getFullSource(), startPos.getStart());
	}

	public String getStartSourceSuffix()
	{
		return Utils.getSourceSuffix(getFullSource(), startPos.getStop());
	}

	public String getStopSourcePrefix()
	{
		return stopPos != null ? Utils.getSourcePrefix(getFullSource(), stopPos.getStart()) : null;
	}

	public String getStopSourceSuffix()
	{
		return stopPos != null ? Utils.getSourceSuffix(getFullSource(), stopPos.getStop()) : null;
	}

	public int getStartLine()
	{
		if (startLineCol == null)
			startLineCol = new LineCol(getFullSource(), getStartPos().getStart());
		return startLineCol.getLine();
	}

	public int getStartCol()
	{
		if (startLineCol == null)
			startLineCol = new LineCol(getFullSource(), getStartPos().getStart());
		return startLineCol.getCol();
	}

	public int getStopLine()
	{
		if (stopPos == null)
			return -1;
		if (stopLineCol == null)
			stopLineCol = new LineCol(getFullSource(), getStopPos().getStop());
		return stopLineCol.getLine();
	}

	public int getStopCol()
	{
		if (stopPos == null)
			return -1;
		if (stopLineCol == null)
			stopLineCol = new LineCol(getFullSource(), getStopPos().getStop());
		return stopLineCol.getCol();
	}

	private static String rawRepr(String string)
	{
		string = FunctionRepr.call(string);
		return string.substring(1, string.length()-1);
	}

	private static String maskString(String string, char mask)
	{
		int length = string.length();
		StringBuilder buffer = new StringBuilder(length);
		for (int offset = 0; offset < length; offset++)
		{
			char c = string.charAt(offset);
			if (c == '\t')
				buffer.append(c);
			else
				buffer.append(mask);
		}
		return buffer.toString();
	}

	/**
	 * Return a description of the template that this AST node is part of
	 * in text form.
	 *
	 * The return value looks something like this:
	 * <code>in local template 'foo' in 'bar'</code>.
	 */
	public String getTemplateDescriptionText()
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		InterpretedTemplate template = getTemplate();
		buffer.append(template.parentTemplate != null ? "in local template " : "in template ");
		boolean first = true;
		while (template != null)
		{
			if (first)
				first = false;
			else
				buffer.append(" in ");
			name = template.nameUL4();
			buffer.append(name == null ? "(unnamed)" : FunctionRepr.call(name));
			template = template.parentTemplate;
		}
		return buffer.toString();
	}

	/**
	 * Return a description of the template that this AST node is part of
	 * in Markdown form.
	 *
	 * The return value looks something like this:
	 * <code>in local template `foo` in `bar`</code>.
	 */
	public String getTemplateDescriptionMarkdown()
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		InterpretedTemplate template = getTemplate();
		buffer.append(template.parentTemplate != null ? "in local template " : "in template ");
		boolean first = true;
		while (template != null)
		{
			if (first)
				first = false;
			else
				buffer.append(" in ");
			name = template.nameUL4();
			if (name == null)
				buffer.append("(unnamed)");
			else
			{
				buffer.append("`");
				buffer.append(rawRepr(name));
				buffer.append("`");
			}
			template = template.parentTemplate;
		}
		return buffer.toString();
	}

	/**
	 * Return a description of the template that this AST node is part of
	 * in HTML form.
	 *
	 * The return value looks something like this:
	 * <code>in local template &lt;b&gt;foo&lt;/b&gt; in &lt;b&gt;bar&lt;/b&gt;</code>.
	 */
	public String getTemplateDescriptionHTML()
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		InterpretedTemplate template = getTemplate();
		buffer.append(template.parentTemplate != null ? "in local template " : "in template ");
		boolean first = true;
		while (template != null)
		{
			if (first)
				first = false;
			else
				buffer.append(" in ");
			name = template.nameUL4();
			buffer.append("<code>");
			if (name == null)
				buffer.append("(unnamed)");
			else
				buffer.append(FunctionXMLEscape.call(rawRepr(name)));
			buffer.append("</code>");
			template = template.parentTemplate;
		}
		return buffer.toString();
	}

	/**
	 * Return a description of the location of this AST node in text form.
	 *
	 * The return value looks something like this:
	 * <code>offset 75:88; line 5, column 10</code>.
	 */
	public String getLocationDescriptionText()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("offset ");
		Slice pos = getStartPos();
		buffer.append(pos.getStart());
		buffer.append(":");
		buffer.append(pos.getStop());
		buffer.append("; line ");
		buffer.append(getStartLine());
		buffer.append("; column ");
		buffer.append(getStartCol());
		return buffer.toString();
	}

	/**
	 * Return a description of the location of this AST node in Markdown form.
	 *
	 * The return value looks something like this:
	 * <code>offset **75:88**; line **5**, column **10**</code>.
	 */
	public String getLocationDescriptionMarkdown()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("offset ");
		Slice pos = getStartPos();
		buffer.append("**");
		buffer.append(pos.getStart());
		buffer.append(":");
		buffer.append(pos.getStop());
		buffer.append("**");
		buffer.append("; line ");
		buffer.append("**");
		buffer.append(getStartLine());
		buffer.append("**");
		buffer.append("; column ");
		buffer.append("**");
		buffer.append(getStartCol());
		buffer.append("**");
		return buffer.toString();
	}

	/**
	 * Return a description of the location of this AST node in HTML form.
	 *
	 * The return value looks something like this:
	 * <code>offset &lt;b&gt;75:88&lt;/b&gt;; line &lt;b&gt;5&lt;/b&gt;, column &lt;b&gt;10&lt;/b&gt;</code>.
	 */
	public String getLocationDescriptionHTML()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("offset <b>");
		Slice pos = getStartPos();
		buffer.append(pos.getStart());
		buffer.append(":");
		buffer.append(pos.getStop());
		buffer.append("</b>; line <b>");
		buffer.append(getStartLine());
		buffer.append("</b>; column <b>");
		buffer.append(getStartCol());
		buffer.append("</b>");
		return buffer.toString();
	}

	public String getSourceSnippetText()
	{
		StringBuilder buffer = new StringBuilder();
		Slice pos = getStartPos();
		InterpretedTemplate template = getTemplate();
		String source = template.getFullSource();

		String prefix = getStartSourcePrefix();
		String code = getStartSource();
		String suffix = getStartSourceSuffix();
		buffer.append(prefix);
		buffer.append(code);
		buffer.append(suffix);
		buffer.append("\n");
		buffer.append(maskString(prefix, ' '));
		buffer.append(maskString(code, '~'));
		return buffer.toString();
	}

	public String getSourceSnippetMarkdown()
	{
		StringBuilder buffer = new StringBuilder();
		Slice pos = getStartPos();
		InterpretedTemplate template = getTemplate();
		String source = template.getFullSource();

		String prefix = getStartSourcePrefix().replace("```", "");
		String code = getStartSource().replace("```", "");
		String suffix = getStartSourceSuffix().replace("```", "");
		buffer.append("```\n");
		buffer.append(prefix);
		buffer.append(code);
		buffer.append(suffix);
		buffer.append("\n");
		buffer.append(maskString(prefix, ' '));
		buffer.append(maskString(code, '~'));
		buffer.append("\n```\n");
		return buffer.toString();
	}

	public String getSourceSnippetHTML()
	{
		StringBuilder buffer = new StringBuilder();
		Slice pos = getStartPos();
		InterpretedTemplate template = getTemplate();
		String source = template.getFullSource();

		String prefix = getStartSourcePrefix();
		String code = getStartSource();
		String suffix = getStartSourceSuffix();
		buffer.append(FunctionXMLEscape.call(prefix));
		buffer.append("<u>");
		buffer.append(FunctionXMLEscape.call(code));
		buffer.append("</u>");
		buffer.append(FunctionXMLEscape.call(suffix));
		return buffer.toString();
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
		encoder.dump(startPos);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		template = (InterpretedTemplate)decoder.load();
		setStartPos((Slice)decoder.load());
		setStopPos(null);
	}

	protected static Set<String> attributes = makeSet("type", "template", "pos", "startpos", "startline", "startcol", "fullsource", "startsource", "source", "sourceprefix", "sourcesuffix", "startsourceprefix", "startsourcesuffix");

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
				return getPos();
			case "startpos":
				return getStartPos();
			case "startline":
				return getStartLine();
			case "startcol":
				return getStartCol();
			case "fullsource":
				return getFullSource();
			case "startsource":
				return getStartSource();
			case "source":
				return getSource();
			case "sourceprefix":
				return getSourcePrefix();
			case "sourcesuffix":
				return getSourceSuffix();
			case "startsourceprefix":
				return getStartSourcePrefix();
			case "startsourcesuffix":
				return getStartSourceSuffix();
			default:
				throw new AttributeException(this, key);
		}
	}

	protected void reprPosLineCol(UL4Repr.Formatter formatter)
	{
		formatter.append(" pos=(");
		Slice pos = getPos();
		formatter.visit(pos.getStart());
		formatter.append(":");
		formatter.visit(pos.getStop());
		formatter.append(") line=");
		formatter.visit(getStartLine());
		formatter.append(" column=");
		formatter.visit(getStartCol());
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		reprPosLineCol(formatter);
		formatter.append(">");
	}
}
