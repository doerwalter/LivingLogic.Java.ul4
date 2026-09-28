/*
** Copyright 2026 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

/**
An exception that is thrown when a replacement variable (see
{@link VSQLAST#fromsource(String, java.util.Map, java.util.Map)}) references
another replacement variable.
**/
public class VSQLReplacementVariableException extends IllegalArgumentException
{
	protected String name;
	protected String referencedName;

	public VSQLReplacementVariableException(String name, String referencedName)
	{
		super("Replacement variable `" + name + "` references the replacement variable `" + referencedName + "`.");
		this.name = name;
		this.referencedName = referencedName;
	}

	/**
	Return the name of the replacement variable whose expression is invalid.
	**/
	public String getName()
	{
		return name;
	}

	/**
	Return the name of the replacement variable that is referenced.
	**/
	public String getReferencedName()
	{
		return referencedName;
	}
}
