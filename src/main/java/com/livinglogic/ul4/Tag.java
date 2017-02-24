/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
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
 * The class that records information about a template tag in the template source.
 */
public class Tag implements UL4ONSerializable, UL4GetItemString, UL4Attributes, SourcePart, UL4Repr
{
	/**
	 * The template
	 */
	protected InterpretedTemplate template;

	/**
	 * The tag type ("print", "printx", "for", "if", "end", etc.)
	 */
	protected String tag;

	/**
	 * The start/end index of this tag in the source
	 */
	protected Slice tagPos;

	/**
	 * The start/end index in {@code source} where the code inside the tag starts.
	 */
	protected Slice codePos;

	/**
	 * Create a new {@code Tag} object.
	 * @param template The template
	 * @param tag The tag type ("print", "printx", "for", "if", "end", etc.)
	 * @param tagPos The slice in the template source, where the source for this tag is located.
	 * @param codePos The slice in the template source where the code inside the tag starts.
	 */
	public Tag(InterpretedTemplate template, String tag, Slice tagPos, Slice codePos)
	{
		this.template = template;
		this.tag = tag;
		this.tagPos = tagPos;
		this.codePos = codePos;
	}

	public String getUL4ONName()
	{
		return "de.livinglogic.ul4.tag";
	}

	public InterpretedTemplate getTemplate()
	{
		return template;
	}

	// Used by {@see InterpretedTemplate#compile} to fix the template references for inner templates
	void setTemplate(InterpretedTemplate template)
	{
		this.template = template;
	}

	public String getSource()
	{
		return template.getSource();
	}

	public String getTag()
	{
		return tag;
	}

	public Slice getPos()
	{
		return tagPos;
	}

	public Slice getCodePos()
	{
		return codePos;
	}

	public String getTagText()
	{
		return tagPos.getFrom(getSource());
	}

	public String getCodeText()
	{
		return codePos.getFrom(getSource());
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(tagPos);
		encoder.dump(template);
		encoder.dump(tag);
		encoder.dump(codePos);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		tagPos = (Slice)decoder.load();
		template = (InterpretedTemplate)decoder.load();
		tag = (String)decoder.load();
		codePos = (Slice)decoder.load();
	}

	protected static Set<String> attributes = makeSet("template", "tag", "pos");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "template":
				return template;
			case "tag":
				return tag;
			case "pos":
				return tagPos;
			case "text":
				return getTagText();
			case "code":
				return getCodeText();
			default:
				throw new AttributeException(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" type=");
		formatter.visit(tag);
		formatter.append(" pos=(");
		formatter.visit(tagPos.getStart());
		formatter.append(":");
		formatter.visit(tagPos.getStop());
		formatter.append(")>");
	}
}
