/*
** Copyright 2015-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

public class DataTypeException extends RuntimeException
{
	public DataTypeException(String dataType)
	{
		super("data type " + dataType + " unknown");
	}
}
