/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class LocationException extends RuntimeException
{
	protected Location location;

	public LocationException(Throwable cause, Location location)
	{
		super("in tag " + location, cause);
		this.location = location;
	}
}
