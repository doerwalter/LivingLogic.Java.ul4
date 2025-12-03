/*
** Copyright 2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

/**
An exception that is thrown when an aggregation function is used incorrectly.

This might mans that the aggregation is no function calls, or that the
name of the function or the number of arguments is wrong.
**/
public class VSQLAggregationException extends RuntimeException
{
	public VSQLAggregationException(String message)
	{
		super(message);
	}
}
