package com.livinglogic.ul4;

public class LocationException extends RuntimeException
{
	protected Location location;

	public LocationException(Throwable cause, Location location)
	{
		super(cause);
		this.location = location;
	}

	public String toString()
	{
		return "com.livinglogic.ul4.LocationException: in " + location;
	}
}