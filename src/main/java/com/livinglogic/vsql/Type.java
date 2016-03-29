/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

public enum Type
{
	BOOL, INT, NUMBER, DATE, DATETIME, TIMESTAMP, STR, CLOB;

	public String toString()
	{
		switch (this)
		{
			case BOOL:
				return "bool";
			case INT:
				return "int";
			case NUMBER:
				return "number";
			case DATE:
				return "date";
			case DATETIME:
				return "datetime";
			case TIMESTAMP:
				return "timestamp";
			case STR:
				return "str";
			case CLOB:
				return "clob";
		}
		return null;
	}
}

