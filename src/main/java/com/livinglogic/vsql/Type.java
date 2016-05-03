/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

public enum Type
{
	NULL, BOOL, INT, NUMBER, DATE, DATETIME, TIMESTAMP, DATEDELTA, DATETIMEDELTA, TIMESTAMPDELTA, MONTHDELTA, STR, CLOB;

	public String toString()
	{
		switch (this)
		{
			case NULL:
				return "null";
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
			case DATEDELTA:
				return "datedelta";
			case DATETIMEDELTA:
				return "datetimedelta";
			case TIMESTAMPDELTA:
				return "timestampdelta";
			case MONTHDELTA:
				return "monthdelta";
			case STR:
				return "str";
			case CLOB:
				return "clob";
		}
		return null;
	}
}
