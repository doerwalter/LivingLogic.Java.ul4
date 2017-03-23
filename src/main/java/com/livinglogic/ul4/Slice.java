/*
** Copyright 2009-2017 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeSet;

public class Slice implements UL4GetItemString, UL4Attributes, UL4Repr, UL4Type, Comparable<Slice>
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

	public Slice(int start, int stop)
	{
		this(true, true, start, stop);
	}

	public Slice(int start)
	{
		this(true, false, start, -1);
	}

	public String typeUL4()
	{
		return "slice";
	}

	public Integer getStart()
	{
		return hasStart ? start : null;
	}

	public Integer getStop()
	{
		return hasStop ? stop : null;
	}

	public String getFrom(String string)
	{
		int containerSize = string.length();
		return string.substring(this.getStartIndex(containerSize), this.getStopIndex(containerSize));
	}

	public Slice withStart(int start)
	{
		return new Slice(true, hasStop, start, stop);
	}

	public Slice withStop(int stop)
	{
		return new Slice(hasStart, true, start, stop);
	}

	public int getStartIndex(int containerSize)
	{
		return hasStart ? Utils.getSliceStartPos(containerSize, start) : 0;
	}

	public int getStopIndex(int containerSize)
	{
		return hasStop ? Utils.getSliceEndPos(containerSize, stop) : containerSize;
	}

	public boolean equals(Object other)
	{
		if (!(other instanceof Slice))
			return false;
		Slice slice = (Slice)other;
		if (hasStart != slice.hasStart)
			return false;
		if (hasStart && (start != slice.start))
			return false;
		if (hasStop != slice.hasStop)
			return false;
		if (hasStop && (stop != slice.stop))
			return false;
		return true;
	}

	public int compareTo(Slice other)
	{
		int result;

		result = Utils.cmp(hasStart, other.hasStart);
		if (result == 0 && hasStart)
			result = Utils.cmp(start, other.start);

		if (result == 0)
		{
			result = Utils.cmp(hasStop, other.hasStop);
			if (result == 0 && hasStop)
				result = Utils.cmp(stop, other.stop);
		}

		return result;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("slice(");
		formatter.visit(getStart());
		formatter.append(", ");
		formatter.visit(getStop());
		formatter.append(")");
	}

	protected static Set<String> attributes = makeSet("start", "stop");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "start":
				return getStart();
			case "stop":
				return getStop();
			default:
				throw new AttributeException(this, key);
		}
	}
}
