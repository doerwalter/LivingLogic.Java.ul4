package com.livinglogic.pull;

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
}
