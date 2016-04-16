/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

public enum Type
{
	NULL, BOOL, INT, NUMBER, DATE, DATETIME, TIMESTAMP, STR, CLOB;

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
			case STR:
				return "str";
			case CLOB:
				return "clob";
		}
		return null;
	}

	public static Type widenNumber(Type type1, Type type2, Node node, String message, Object... args)
	{
		if (type1 == Type.BOOL)
		{
			if (type2 == Type.BOOL)
				return Type.BOOL;
			else if (type2 == Type.INT)
				return Type.INT;
			else if (type2 == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (type1 == Type.INT)
		{
			if (type2 == Type.BOOL || type2 == Type.INT)
				return Type.INT;
			else if (type2 == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (type1 == Type.NUMBER)
		{
			if (type2 == Type.BOOL || type2 == Type.INT || type2 == Type.NUMBER)
				return Type.NUMBER;
		}

		throw node.error(message, args);
	}

	public static Type widen(Type type1, Type type2, Node node, String message, Object... args)
	{
		if (type1 == Type.BOOL)
		{
			if (type2 == Type.BOOL)
				return Type.BOOL;
			else if (type2 == Type.INT)
				return Type.INT;
			else if (type2 == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (type1 == Type.INT)
		{
			if (type2 == Type.BOOL || type2 == Type.INT)
				return Type.INT;
			else if (type2 == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (type1 == Type.NUMBER)
		{
			if (type2 == Type.BOOL || type2 == Type.INT || type2 == Type.NUMBER)
				return Type.NUMBER;
		}
		else if (type1 == Type.STR)
		{
			if (type2 == Type.STR)
				return Type.STR;
			else if (type2 == Type.CLOB)
				return Type.CLOB;
		}
		else if (type1 == Type.CLOB)
		{
			if (type2 == Type.STR || type2 == Type.CLOB)
				return Type.CLOB;
		}

		throw node.error(message, args);
	}
}
