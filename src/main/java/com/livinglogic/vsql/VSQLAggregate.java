/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.EnumValueException;

/**
An enum that specifies which aggration functions are available in vSQL.
**/
public enum VSQLAggregate
{
	GROUP
	{
		@Override
		public String toString()
		{
			return "group";
		}
	},
	COUNT
	{
		@Override
		public String toString()
		{
			return "count";
		}
	},
	MIN
	{
		@Override
		public String toString()
		{
			return "min";
		}
	},
	MAX
	{
		@Override
		public String toString()
		{
			return "max";
		}
	},
	SUM
	{
		@Override
		public String toString()
		{
			return "sum";
		}
	};

	public String toString()
	{
		return null;
	}

	public static VSQLAggregate fromString(String value)
	{
		if (value == null)
			return null;
		switch (value)
		{
			case "group":
				return GROUP;
			case "count":
				return COUNT;
			case "min":
				return MIN;
			case "max":
				return MAX;
			case "sum":
				return SUM;
		}
		throw new EnumValueException("com.livinglogic.livingapps.vsql.VSQLAggregate", value);
	}
}
