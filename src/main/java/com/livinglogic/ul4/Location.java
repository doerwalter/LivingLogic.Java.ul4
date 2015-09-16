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
 * A {@code Location} object marks a position in the sourcecode of an UL4
 * template. It marks the position of either a template tag or of the literal
 * text between two template tags.
 */
public class Location implements UL4ONSerializable, UL4GetItemString, UL4Attributes
{
	/**
	* The template object this location belongs to
	*/
	public InterpretedTemplate root;

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
	public Location(InterpretedTemplate root, String source, String type, int starttag, int endtag, int startcode, int endcode)
	{
		this.root = root;
		this.source = source;
		this.type = type;
		this.starttag = starttag;
		this.endtag = endtag;
		this.startcode = startcode;
		this.endcode = endcode;
	}

	/**
	 * Return the {@link InterpretedTemplate} object this {@code Location} belongs to
	 */
	public InterpretedTemplate getRoot()
	{
		return root;
	}

	/**
	 * Return the template source code
	 */
	public String getSource()
	{
		return source;
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

	public int getStartTag()
	{
		return starttag;
	}

	public int getEndTag()
	{
		return endtag;
	}

	public int getStartCode()
	{
		return startcode;
	}

	public int getEndCode()
	{
		return endcode;
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
		encoder.dump(root);
		encoder.dump(source);
		encoder.dump(type);
		encoder.dump(starttag);
		encoder.dump(endtag);
		encoder.dump(startcode);
		encoder.dump(endcode);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		root = (InterpretedTemplate)decoder.load();
		source = (String)decoder.load();
		type = (String)decoder.load();
		starttag = (Integer)decoder.load();
		endtag = (Integer)decoder.load();
		startcode = (Integer)decoder.load();
		endcode = (Integer)decoder.load();
	}

	private static Set<String> attributes = makeSet("root", "source", "type", "starttag", "endtag", "startcode", "endcode", "tag", "code");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("root".equals(key))
			return root;
		else if ("source".equals(key))
			return source;
		else if ("type".equals(key))
			return type;
		else if ("starttag".equals(key))
			return starttag;
		else if ("endtag".equals(key))
			return endtag;
		else if ("startcode".equals(key))
			return startcode;
		else if ("endcode".equals(key))
			return endcode;
		else if ("tag".equals(key))
			return getTag();
		else if ("code".equals(key))
			return getCode();
		else
			return new UndefinedKey(key);
	}
}
