package com.livinglogic.ull;

public class LocationException extends Exception
{
	protected Location location;
	
	public LocationException(Throwable cause, Location location)
	{
		super(cause);
		this.location = location;
	}

	public String toString()
	{
		return "in " + location + ": " + super.toString();
	}
}
