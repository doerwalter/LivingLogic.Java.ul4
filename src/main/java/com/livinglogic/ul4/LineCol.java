/*
** Copyright 2009-2020 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
 * {@code LineCol} objects store a line and column number
 */
public class LineCol
{
	private int line;
	private int col;

	public LineCol(int line, int col)
	{
		this.line = line;
		this.col = col;
	}

	public LineCol(String source, int pos)
	{
		int lastLineFeed = source.lastIndexOf("\n", pos);

		line = 1;
		if (lastLineFeed == -1)
		{
			col = pos+1;
		}
		else
		{
			col = 1;
			for (int i = 0; i < pos; ++i)
			{
				if (source.charAt(i) == '\n')
				{
					++line;
					col = 0;
				}
				++col;
			}
		}
	}

	public int getLine()
	{
		return line;
	}

	public int getCol()
	{
		return col;
	}
}
