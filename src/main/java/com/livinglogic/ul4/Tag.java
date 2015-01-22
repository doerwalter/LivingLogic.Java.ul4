/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
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
public class Tag implements UL4ONSerializable, UL4GetAttributes
{
	/**
	 * The template source code
	 */
	protected String source;

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
	 * @param source The template source
	 * @param tag The tag type ("print", "printx", "for", "if", "end", etc.)
	 * @param startPos The start offset in the template source, where the source for this tag is located.
	 * @param endPos The end offset in the template source, where the source for this tag is located.
	 * @param startPosCode The offset in {@code source} where the code inside the tag starts.
	 * @param endPosCode The offset in {@code source} where the code inside the tag ends.
	 */
	public Tag(String source, String tag, int startPos, int endPos, int startPosCode, int endPosCode)
	{
		this.source = source;
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

	public String getSource()
	{
		return source;
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
		return source.substring(startPos, endPos);
	}

	public String getCode()
	{
		return source.substring(startPosCode, endPosCode);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(startPos);
		encoder.dump(endPos);
		encoder.dump(source);
		encoder.dump(tag);
		encoder.dump(startPosCode);
		encoder.dump(endPosCode);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		startPos = (Integer)decoder.load();
		endPos = (Integer)decoder.load();
		source = (String)decoder.load();
		tag = (String)decoder.load();
		startPosCode = (Integer)decoder.load();
		endPosCode = (Integer)decoder.load();
	}

	protected static Set<String> attributes = makeSet("source", "tag", "startpos", "endpos", "startposcode", "endposcode");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("source".equals(key))
			return source;
		else if ("tag".equals(key))
			return tag;
		else if ("startpos".equals(key))
			return startPos;
		else if ("endpos".equals(key))
			return endPos;
		else if ("startposcode".equals(key))
			return startPosCode;
		else if ("endposcode".equals(key))
			return endPosCode;
		else if ("text".equals(key))
			return getText();
		else if ("code".equals(key))
			return getCode();
		else
			return new UndefinedKey(key);
	}
}
