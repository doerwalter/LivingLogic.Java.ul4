/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;
import com.livinglogic.utils.ObjectAsMap;

public class Location extends ObjectAsMap
{
	public String source;
	public String name;
	protected String type;
	public int starttag;
	public int endtag;
	public int startcode;
	public int endcode;

	public Location(String source, String name, String type, int starttag, int endtag, int startcode, int endcode)
	{
		this.source = source;
		this.name = name;
		this.type = type;
		this.starttag = starttag;
		this.endtag = endtag;
		this.startcode = startcode;
		this.endcode = endcode;
	}

	public String getName()
	{
		return name;
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

		String templatename;
		if (name != null)
			templatename = "template " + name;
		else
			templatename = "unnamed template";

		return tagType + " at position " + (starttag+1) + " (line " + line + ", col " + col + ", " + templatename + ")";
	}

	public void fixName(String name)
	{
		this.name = name;
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>();
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((Location)object).getName();}});
			v.put("type", new ValueMaker(){public Object getValue(Object object){return ((Location)object).getType();}});
			v.put("starttag", new ValueMaker(){public Object getValue(Object object){return ((Location)object).starttag;}});
			v.put("endtag", new ValueMaker(){public Object getValue(Object object){return ((Location)object).endtag;}});
			v.put("startcode", new ValueMaker(){public Object getValue(Object object){return ((Location)object).startcode;}});
			v.put("endcode", new ValueMaker(){public Object getValue(Object object){return ((Location)object).endcode;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
