/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
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
The base class of all syntax tree nodes. This can be either literal text
({@link TextAST}) between the tags, or compiled tag content ({@link CodeAST}).
**/
public abstract class AST implements UL4Instance, UL4ONSerializable, UL4Dir, UL4Repr
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "AST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.ast";
		}

		@Override
		public String getDoc()
		{
			return "Base class for all UL4 syntax tree nodes.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof AST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	The template to which this node belongs
	**/
	protected Template template;

	/**
	The start index of this node in the source (or of the start tag of a block)
	**/
	protected int startPosStart;

	/**
	The end index of this node in the source (or of the start tag of a block)
	**/
	protected int startPosStop;

	/**
	Source position as a line number/column number pair.
	Will be {@code null}, but will be calculated on the first
	call to {@link #getStartLine} or {@link #getStartCol}.
	**/
	protected LineCol startLineCol;

	/**
	The start index of this end tag for this node in the source (or
	-1 if this is not a block).
	**/
	protected int stopPosStart;

	/**
	The end index of this end tag for this node in the source (or
	-1 if this is not a block).
	**/
	protected int stopPosStop;

	/**
	Source position of the end tag (if this is a blog) as a line
	number/column number pair.
	Will be {@code null}, but will be calculated on the first
	call to {@link #getStopLine} or {@link #getStopCol}.
	**/
	protected LineCol stopLineCol;

	/**
	Create a new {@code AST} object for a block (i.e. something with
	a start and end tag).
	@param startPosStart The start index in the template source, where the source
	                     for this object (or its start tag) is located.
	@param startPosStop The end index in the template source, where the source
   	                 for this object (or its start tag) is located.
	@param stopPosStart The start index in the template source, where the end tag
	                    is located (or -1 if this is not a block).
	@param stopPosStop The end index in the template source, where the end tag
	                   is located (or -1 if this is not a block).
	**/
	protected AST(Template template, int startPosStart, int startPosStop, int stopPosStart, int stopPosStop)
	{
		this.template = template;
		this.startPosStart = startPosStart;
		this.startPosStop = startPosStop;
		this.startLineCol = null;
		this.stopPosStart = stopPosStart;
		this.stopPosStop = stopPosStop;
		this.stopLineCol = null;
	}

	/**
	Create a new {@code AST} object for a non-block.
	@param startPosStart The start position in the template source, where the source for
	                     this object (or its start tag) is located.
	 @param startPosStop The stop position in the template source, where the source for
	                     this object (or its start tag) is located.
	**/
	public AST(Template template, int startPosStart, int startPosStop)
	{
		this(template, startPosStart, startPosStop, -1, -1);
	}

	/**
	Return the template to which this node belongs.

	@return The owning template.
	**/
	public Template getTemplate()
	{
		return template;
	}

	// Used by {@link Template#compile} to fix the template references for inner templates
	void setTemplate(Template template)
	{
		this.template = template;
	}

	public int getStartPosStart()
	{
		return startPosStart;
	}

	public int getStartPosStop()
	{
		return startPosStop;
	}

	public Slice getStartPos()
	{
		return new Slice(startPosStart, startPosStop);
	}

	// Used by {@link Template#compile} to fix the position for inner templates
	void setStartPos(int start, int stop)
	{
		this.startPosStart = start;
		this.startPosStop = stop;
		// Reset line and column information
		startLineCol = null;
	}

	public void setStartPosStart(int start)
	{
		this.startPosStart = start;
		// Reset line and column information
		startLineCol = null;
	}

	public void setStartPosStop(int stop)
	{
		this.startPosStop = stop;
		// Reset line and column information
		startLineCol = null;
	}

	public int getStopPosStart()
	{
		return stopPosStart;
	}

	public int getStopPosStop()
	{
		return stopPosStop;
	}

	public Slice getStopPos()
	{
		return stopPosStart >= 0 ? new Slice(stopPosStart, stopPosStop) : null;
	}

	public Slice getPos()
	{
		return new Slice(startPosStart, stopPosStop >= 0 ? stopPosStop : startPosStop);
	}

	void setStopPos(int start, int stop)
	{
		this.stopPosStart = start;
		this.stopPosStop = stop;
		// Reset line and column information
		startLineCol = null;
	}

	public void setStopPosStart(int start)
	{
		this.stopPosStart = start;
		// Reset line and column information
		startLineCol = null;
	}

	public void setStopPosStop(int stop)
	{
		this.stopPosStop = stop;
		// Reset line and column information
		startLineCol = null;
	}

	// public Slice getPos()
	// {
	// 	return stopPos == null ? startPos : new Slice(startPos.getStart(), stopPos.getStop());
	// }

	public String getStartSource()
	{
		// Use the full source, as positions are relative to that
		// (and not to the source of any inner template in which {@this} might live)
		return getFullSource().substring(startPosStart, startPosStop);
	}

	public String getSource()
	{
		return getFullSource().substring(startPosStart, stopPosStop >= 0 ? stopPosStop : startPosStop);
	}

	public String getStopSource()
	{
		return getFullSource().substring(stopPosStart, stopPosStop);
	}

	public String getFullSource()
	{
		return getTemplate().getFullSource();
	}

	public String getSourcePrefix()
	{
		return Utils.getSourcePrefix(getFullSource(), startPosStart);
	}

	public String getSourceSuffix()
	{
		return Utils.getSourceSuffix(getFullSource(), stopPosStop >= 0 ? stopPosStop : startPosStop);
	}

	public String getStartSourcePrefix()
	{
		return Utils.getSourcePrefix(getFullSource(), startPosStart);
	}

	public String getStartSourceSuffix()
	{
		return Utils.getSourceSuffix(getFullSource(), startPosStop);
	}

	public String getStopSourcePrefix()
	{
		return stopPosStart >= 0 ? Utils.getSourcePrefix(getFullSource(), stopPosStart) : null;
	}

	public String getStopSourceSuffix()
	{
		return stopPosStart >= 0 ? Utils.getSourceSuffix(getFullSource(), stopPosStop) : null;
	}

	public int getStartLine()
	{
		if (startLineCol == null)
			startLineCol = new LineCol(getFullSource(), startPosStart);
		return startLineCol.getLine();
	}

	public int getStartCol()
	{
		if (startLineCol == null)
			startLineCol = new LineCol(getFullSource(), startPosStart);
		return startLineCol.getCol();
	}

	public int getStopLine()
	{
		if (stopPosStop < 0)
			return -1;
		if (stopLineCol == null)
			stopLineCol = new LineCol(getFullSource(), stopPosStop);
		return stopLineCol.getLine();
	}

	public int getStopCol()
	{
		if (stopPosStop < 0)
			return -1;
		if (stopLineCol == null)
			stopLineCol = new LineCol(getFullSource(), stopPosStop);
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
	Return a description of the template that this AST node is part of
	in text form.

	The return value looks something like this:
	{@code in local template 'foo' in 'bar'}.
	**/
	public String getTemplateDescriptionText()
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		Template template = getTemplate();
		buffer.append(template.parentTemplate != null ? "in local template " : "in template ");
		boolean first = true;
		while (template != null)
		{
			if (first)
				first = false;
			else
				buffer.append(" in ");
			name = template.getFullNameUL4();
			buffer.append(name == null ? "(unnamed)" : FunctionRepr.call(name));
			template = template.parentTemplate;
		}
		return buffer.toString();
	}

	/**
	Return a description of the template that this AST node is part of
	in Markdown form.

	The return value looks something like this:
	<code>in local template `foo` in `bar`</code>.
	**/
	public String getTemplateDescriptionMarkdown()
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		Template template = getTemplate();
		buffer.append(template.parentTemplate != null ? "in local template " : "in template ");
		boolean first = true;
		while (template != null)
		{
			if (first)
				first = false;
			else
				buffer.append(" in ");
			name = template.getFullNameUL4();
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
	Return a description of the template that this AST node is part of
	in HTML form.

	The return value looks something like this:
	<code>in local template &lt;b&gt;foo&lt;/b&gt; in &lt;b&gt;bar&lt;/b&gt;</code>.
	**/
	public String getTemplateDescriptionHTML()
	{
		StringBuilder buffer = new StringBuilder();
		String name = null;
		Template template = getTemplate();
		buffer.append(template.parentTemplate != null ? "in local template " : "in template ");
		boolean first = true;
		while (template != null)
		{
			if (first)
				first = false;
			else
				buffer.append(" in ");
			name = template.getFullNameUL4();
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
	Return a description of the location of this AST node in text form.

	The return value looks something like this:
	<code>offset 75:88; line 5, column 10</code>.
	**/
	public String getLocationDescriptionText()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("offset ");
		buffer.append(startPosStart);
		buffer.append(":");
		buffer.append(startPosStop);
		buffer.append("; line ");
		buffer.append(getStartLine());
		buffer.append("; column ");
		buffer.append(getStartCol());
		return buffer.toString();
	}

	/**
	Return a description of the location of this AST node in Markdown form.

	The return value looks something like this:
	<code>offset **75:88**; line **5**, column **10**</code>.
	**/
	public String getLocationDescriptionMarkdown()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("offset ");
		buffer.append("**");
		buffer.append(startPosStart);
		buffer.append(":");
		buffer.append(startPosStop);
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
	Return a description of the location of this AST node in HTML form.

	The return value looks something like this:
	<code>offset &lt;b&gt;75:88&lt;/b&gt;; line &lt;b&gt;5&lt;/b&gt;, column &lt;b&gt;10&lt;/b&gt;</code>.
	**/
	public String getLocationDescriptionHTML()
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append("offset <b>");
		buffer.append(startPosStart);
		buffer.append(":");
		buffer.append(startPosStop);
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
		Template template = getTemplate();
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
		Template template = getTemplate();
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
		Template template = getTemplate();
		String source = template.getFullSource();

		String prefix = getStartSourcePrefix();
		String code = getStartSource();
		String suffix = getStartSourceSuffix();
		buffer.append(FunctionXMLEscape.call(prefix));
		buffer.append("<b>");
		buffer.append(FunctionXMLEscape.call(code));
		buffer.append("</b>");
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
	<p>Evaluate this node and return the resulting object.</p>

	<p>Evaluating the node might also have several side effects besides the
	method return value: It might write to the output stream that is stored
	in the {@code context} object (as the {@link PrintAST} and
	{@link PrintXAST} nodes do) and it might modify the variables map stored
	in the context (like {@link VarAST} does).</p>
	
	@param context The context object in which this node has to be evaluated.
	@return The result of evaluating the node.
	**/
	abstract public Object evaluate(EvaluationContext context);

	/**
	{@code decoratedEvaluate} wraps a call to {@link #evaluate} with exception
	handling. {@link #evaluate} should not be called directly. Instead
	{@code decoratedEvaluate} should be used. When an exception bubbles up
	the call stack, {@code decoratedEvaluate} creates a exception chain
	containing information about the location of the exception.
	**/
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
	Return a unique name for this type of AST node.
	**/
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
	Format this object using a Formatter object.
	@param formatter the Formatter object
	**/
	public void toString(Formatter formatter)
	{
		toStringFromSource(formatter);
	}

	public void toStringFromSource(Formatter formatter)
	{
		formatter.write(getSource());
	}

	@Override
	public String getUL4ONName()
	{
		return getTypeUL4().getUL4ONName();
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(template);
		encoder.dump(startPosStart);
		encoder.dump(startPosStop);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		template = (Template)decoder.load();
		setStartPos((int)decoder.load(), (int)decoder.load());
		setStopPos(-1, -1);
	}

	protected static Set<String> attributes = makeSet("type", "template", "pos", "startpos", "startline", "startcol", "fullsource", "startsource", "source", "sourceprefix", "sourcesuffix", "startsourceprefix", "startsourcesuffix");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
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
				return UL4Instance.super.getAttrUL4(context, key);
		}
	}

	@Override
	public void setAttrUL4(EvaluationContext context, String key, Object value)
	{
		throw new ReadonlyException(this, key);
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
