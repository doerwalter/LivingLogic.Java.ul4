/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class TagException extends RuntimeException
{
	protected Location location;

	public TagException(Throwable cause, Location location)
	{
		super("in " + location, cause);
		this.location = location;
	}
}
