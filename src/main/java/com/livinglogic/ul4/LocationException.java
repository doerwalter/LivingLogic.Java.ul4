/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LocationException extends RuntimeException
{
	protected Location location;
	protected Opcode opcode;
	protected int opcodeIndex;

	public LocationException(Throwable cause, Location location)
	{
		super(cause);
		this.location = location;
		this.opcode = null;
		this.opcodeIndex = -1;
	}

	public LocationException(Throwable cause, Opcode opcode, int opcodeIndex)
	{
		super(cause);
		this.location = opcode.location;
		this.opcode = opcode;
		this.opcodeIndex = opcodeIndex;
	}

	public String toString()
	{
		String msg = "com.livinglogic.ul4.LocationException: in " + location;
		if (opcode != null)
			msg += " opcode #" + opcodeIndex + ": " + opcode;
		return msg;
	}
}
