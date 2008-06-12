package com.livinglogic.ull;

import java.util.Vector;

public class Registers extends Vector
{
	public Registers()
	{
		super(10);
		for (int i = 0; i < 10; ++i)
			add(new Integer(i));
	}

	public int alloc()
	{
		try
		{
			return ((Integer)remove(0)).intValue();
		}
		catch (ArrayIndexOutOfBoundsException ex)
		{
			throw new OutOfRegistersException();
		}
	}

	public void free(int r)
	{
		add(0, new Integer(r));
	}
}
