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

public class Location extends ObjectAsMap implements UL4ONSerializable
{
	public String source;
	protected String type;
	public int starttag;
	public int endtag;
	public int startcode;
	public int endcode;

	public Location(String source, String type, int starttag, int endtag, int startcode, int endcode)
	{
		this.source = source;
		this.type = type;
		this.starttag = starttag;
		this.endtag = endtag;
		this.startcode = startcode;
		this.endcode = endcode;
	}

	public String getType()
	{
		return type;
	}

	public String getTag()
	{
		return source.substring(starttag, endtag);
	}

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
			String tag = Utils.repr(getTag());
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
