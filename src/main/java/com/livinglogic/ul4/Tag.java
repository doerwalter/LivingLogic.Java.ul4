/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
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
public class Tag implements UL4ONSerializable, UL4GetItemString, UL4Attributes, SourcePart
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
	 * The start index of this tag in the source
	 */
	protected int startPos;

	/**
	 * The end index of this tag in the source
	 */
	protected int endPos;

	/**
	 * The offset in {@code source} where the code inside the tag starts.
	 */
	protected int startPosCode;

	/**
	 * The offset in {@code source} where the code inside the tag ends.
	 */
	protected int endPosCode;

	/**
	 * Create a new {@code Tag} object.
	 * @param template The template
	 * @param tag The tag type ("print", "printx", "for", "if", "end", etc.)
	 * @param startPos The start offset in the template source, where the source for this tag is located.
	 * @param endPos The end offset in the template source, where the source for this tag is located.
	 * @param startPosCode The offset in the template source where the code inside the tag starts.
	 * @param endPosCode The offset in the template source where the code inside the tag ends.
	 */
	public Tag(InterpretedTemplate template, String tag, int startPos, int endPos, int startPosCode, int endPosCode)
	{
		this.template = template;
		this.tag = tag;
		this.startPos = startPos;
		this.endPos = endPos;
		this.startPosCode = startPosCode;
		this.endPosCode = endPosCode;
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

	public int getStartPos()
	{
		return startPos;
	}

	public int getEndPos()
	{
		return endPos;
	}

	public int getStartPosCode()
	{
		return startPosCode;
	}

	public int getEndPosCode()
	{
		return endPosCode;
	}

	public String getText()
	{
		return getSource().substring(startPos, endPos);
	}

	public String getCode()
	{
		return getSource().substring(startPosCode, endPosCode);
	}

	public CodeSnippet getSnippet()
	{
		return new CodeSnippet(getSource(), startPos, endPos);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(startPos);
		encoder.dump(endPos);
		encoder.dump(template);
		encoder.dump(tag);
		encoder.dump(startPosCode);
		encoder.dump(endPosCode);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		startPos = (Integer)decoder.load();
		endPos = (Integer)decoder.load();
		template = (InterpretedTemplate)decoder.load();
		tag = (String)decoder.load();
		startPosCode = (Integer)decoder.load();
		endPosCode = (Integer)decoder.load();
	}

	protected static Set<String> attributes = makeSet("template", "tag", "startpos", "endpos", "startposcode", "endposcode");

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
			case "startpos":
				return startPos;
			case "endpos":
				return endPos;
			case "startposcode":
				return startPosCode;
			case "endposcode":
				return endPosCode;
			case "text":
				return getText();
			case "code":
				return getCode();
			default:
				return new UndefinedKey(key);
		}
	}
}
