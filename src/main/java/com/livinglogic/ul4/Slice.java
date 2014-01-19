/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeSet;

public class Slice implements UL4Attributes, UL4Repr
{
	protected boolean hasStart;
	protected boolean hasStop;
	protected int start;
	protected int stop;

	public Slice(boolean hasStart, boolean hasStop, int start, int stop)
	{
		this.hasStart = hasStart;
		this.hasStop = hasStop;
		this.start = start;
		this.stop = stop;
	}

	public int getStartIndex(int containerSize)
	{
		return hasStart ? Utils.getSliceStartPos(containerSize, start) : 0;
	}

	public int getStopIndex(int containerSize)
	{
		return hasStop ? Utils.getSliceEndPos(containerSize, stop) : containerSize;
	}

	public String reprUL4()
	{
		StringBuilder builder = new StringBuilder();

		builder.append("slice(");
		if (hasStart)
			builder.append(start);
		else
			builder.append("None");
		builder.append(", ");
		if (hasStop)
			builder.append(stop);
		else
			builder.append("None");
		builder.append(")");

		return builder.toString();
	}

	protected static Set<String> attributes = makeSet("start", "stop");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("start".equals(key))
			return hasStart ? start : null;
		else if ("stop".equals(key))
			return hasStop ? stop : null;
		else
			return new UndefinedKey(key);
	}
}
