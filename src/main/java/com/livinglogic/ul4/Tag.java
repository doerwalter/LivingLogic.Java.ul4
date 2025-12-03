/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


/**
The class that records information about a template tag in the template source.
**/
public class Tag extends AST
{
	protected static class Type extends AST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "Tag";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.tag";
		}

		@Override
		public String getDoc()
		{
			return "A template tag in an UL4 template";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Tag;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	The tag type ("print", "printx", "for", "if", "end", etc.)
	**/
	protected String tag;

	/**
	The start index in {@code source} where the code inside the tag starts.
	**/
	protected int codePosStart;

	/**
	The end index in {@code source} where the code inside the tag starts.
	**/
	protected int codePosStop;

	/**
	Create a new {@code Tag} object.
	@param template The template
	@param tag The tag type ("print", "printx", "for", "if", "end", etc.)
	@param tagPosStart The start position in the template source where the source for this tag is located.
	@param tagPosStop The stop position in the template source where the source for this tag is located.
	@param codePosStart The start position in the template source where the code inside the tag starts.
	@param codePosStop The stop position in the template source where the code inside the tag starts.
	**/
	public Tag(Template template, String tag, int tagPosStart, int tagPosStop, int codePosStart, int codePosStop)
	{
		super(template, tagPosStart, tagPosStop);
		this.tag = tag;
		this.codePosStart = codePosStart;
		this.codePosStop = codePosStop;
	}

	// This never gets called
	@Override
	public Object evaluate(EvaluationContext context)
	{
		return null;
	}

	public String getType()
	{
		return "tag";
	}

	public String getUL4ONName()
	{
		throw new UnsupportedOperationException("not implemented");
	}

	public Template getTemplate()
	{
		return template;
	}

	public String getTag()
	{
		return tag;
	}

	public int getCodePosStart()
	{
		return codePosStart;
	}

	public int getCodePosStop()
	{
		return codePosStop;
	}

	public Slice getCodePos()
	{
		return new Slice(codePosStart, codePosStop);
	}

	public String getCode()
	{
		return template.getFullSource().substring(codePosStart, codePosStop);
	}
}
