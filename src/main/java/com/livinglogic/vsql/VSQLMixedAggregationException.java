/*
** Copyright 2015-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

/**
An exception that is thrown when aggregated and non-aggregated values
are mixed in a {@link VSQLQUery}.
**/
public class VSQLMixedAggregationException extends RuntimeException
{
	public VSQLMixedAggregationException()
	{
		super("Can't mix non-aggregated and aggregated select expressions");
	}
}
