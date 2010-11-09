package com.livinglogic.ul4;

public class Location
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
		return "<?" + type + "?> tag at " + (starttag+1) + " (line " + line + ", col " + col + ")";

	}
}
