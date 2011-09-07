/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Vector;

public class Registers extends Vector<Integer>
{
	public Registers()
	{
		super(10);
		for (int i = 0; i < 10; ++i)
			add(i);
	}

	public int alloc()
	{
		try
		{
			return remove(0);
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			throw new OutOfRegistersException();
		}
	}

	public void free(int r)
	{
		add(0, r);
	}
}
