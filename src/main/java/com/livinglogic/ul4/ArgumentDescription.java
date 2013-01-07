/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class ArgumentDescription
{
	protected String name;
	protected int position;
	protected boolean hasDefaultValue;
	protected Object defaultValue;

	public ArgumentDescription(String name, int position)
	{
		this.name = name;
		this.position = position;
		this.hasDefaultValue = false;
		this.defaultValue = null;
	}

	public ArgumentDescription(String name, int position, Object defaultValue)
	{
		this.name = name;
		this.position = position;
		this.hasDefaultValue = true;
		this.defaultValue = defaultValue;
	}

	public String getName()
	{
		return name;
	}

	public int getPosition()
	{
		return position;
	}

	public boolean hasDefaultValue()
	{
		return hasDefaultValue;
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}
}
