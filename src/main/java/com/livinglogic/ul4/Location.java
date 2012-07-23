/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import com.livinglogic.utils.ObjectAsMap;
import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;

/**
 * A {@code Location} object marks a position in the sourcecode of an UL4
 * template. It marks the position of either a template tag or of the literal
 * text between two template tags.
 */
public class Location extends ObjectAsMap implements UL4ONSerializable
{
	/**
	 * The source code of the UL4 template this location refers to.
	 *
	 * For all {@code Location} objects created for a single UL4 template this
	 * is always the same string, i.e. even for subtemplates in the top-level
	 * template this is the complete sourcecode of the top-level template.
	 */
	public String source;

	/**
	 * The type of the template tag. (i.e. {@code "for"} for the template tag
	 * {@code <?for item in container?>}). For literal text {@code type} is
	 * {@code null}.
	 */
	protected String type;

	/**
	 * The position inside {@code source} where this tag (or literal texts) starts.
	 */
	public int starttag;

	/**
	 * The position inside {@code source} where this tag (or literal texts) end.
	 */
	public int endtag;

	/**
	 * The position inside {@code source} where the code of this template tag
	 * starts (i.e. for the template tag {@code <?for item in container?>} the
	 * template code is {@code item in container}) and {@code startcode} is the
	 * starting position of that string. For literal text {@code startcode}
	 * is the same as {@code starttag}.
	 */
	public int startcode;

	/**
	 * The position inside {@code source} where the code of this template tag
	 * ends. For literal text {@code endcode} is the same as {@code endtag}.
	 */
	public int endcode;

	/**
	 * Create a {@code Location} object from the arguments passed in. This is
	 * called by {@link InterpretedTemplate#tokenizeTags} when splitting the
	 * source code of the template into tags and literal text.
	 */
	public Location(String source, String type, int starttag, int endtag, int startcode, int endcode)
	{
		this.source = source;
		this.type = type;
		this.starttag = starttag;
		this.endtag = endtag;
		this.startcode = startcode;
		this.endcode = endcode;
	}

	/**
	 * Return the type of the tag (for {@code null} for literal text)
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Return the template tag (e.g. {@code <?for item in container?>}). For
	 * literal text the text is returned.
	 */
	public String getTag()
	{
		return source.substring(starttag, endtag);
	}

	/**
	 * Return the template tag (e.g. {@code item in container} for the template
	 * tag {@code <?for item in container?>}). For literal text the text is
	 * returned.
	 */
	public String getCode()
	{
		return source.substring(startcode, endcode);
	}

	public String toString()
	{
		int line = 1;
		int col;
		int lastLineFeed = source.lastIndexOf("\n", starttag);

		if (lastLineFeed == -1)
		{
			col = starttag+1;
		}
		else
		{
			col = 1;
			for (int i = 0; i < starttag; ++i)
			{
				if (source.charAt(i) == '\n')
				{
					++line;
					col = 0;
				}
				++col;
			}
		}
		String tagType = (type != null) ? "<?" + type + "?> tag" : "literal";

		String source = null;

		if (type != null)
		{
			String tag = FunctionRepr.call(getTag());
			source = ": " + tag.substring(1, tag.length()-1);
		}
		else
			source = "";

		return tagType + " at position " + starttag + ":" + endtag + " (line " + line + ", col " + col + ")" + source;
	}

	public String getUL4ONName()
	{
		return "de.livinglogic.ul4.location";
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(source);
		encoder.dump(type);
		encoder.dump(starttag);
		encoder.dump(endtag);
		encoder.dump(startcode);
		encoder.dump(endcode);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		source = (String)decoder.load();
		type = (String)decoder.load();
		starttag = (Integer)decoder.load();
		endtag = (Integer)decoder.load();
		startcode = (Integer)decoder.load();
		endcode = (Integer)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>();
			v.put("type", new ValueMaker(){public Object getValue(Object object){return ((Location)object).getType();}});
			v.put("starttag", new ValueMaker(){public Object getValue(Object object){return ((Location)object).starttag;}});
			v.put("endtag", new ValueMaker(){public Object getValue(Object object){return ((Location)object).endtag;}});
			v.put("startcode", new ValueMaker(){public Object getValue(Object object){return ((Location)object).startcode;}});
			v.put("endcode", new ValueMaker(){public Object getValue(Object object){return ((Location)object).endcode;}});
			v.put("tag", new ValueMaker(){public Object getValue(Object object){return ((Location)object).getTag();}});
			v.put("code", new ValueMaker(){public Object getValue(Object object){return ((Location)object).getCode();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
