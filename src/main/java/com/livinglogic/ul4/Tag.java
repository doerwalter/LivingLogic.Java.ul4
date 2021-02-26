/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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

	public static UL4Type type = new Type();

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
	The start/end index in {@code source} where the code inside the tag starts.
	**/
	protected Slice codePos;

	/**
	Create a new {@code Tag} object.
	@param template The template
	@param tag The tag type ("print", "printx", "for", "if", "end", etc.)
	@param tagPos The slice in the template source, where the source for this tag is located.
	@param codePos The slice in the template source where the code inside the tag starts.
	**/
	public Tag(Template template, String tag, Slice tagPos, Slice codePos)
	{
		super(template, tagPos);
		this.tag = tag;
		this.codePos = codePos;
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

	public Slice getCodePos()
	{
		return codePos;
	}

	public String getCode()
	{
		return codePos.getFrom(template.getFullSource());
	}
}
