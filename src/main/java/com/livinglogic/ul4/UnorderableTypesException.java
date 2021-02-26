/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

/**
Thrown by comparisons when the types can't be compared.
**/
public class UnorderableTypesException extends ArgumentTypeMismatchException
{
	public UnorderableTypesException(String operator, Object arg1, Object arg2)
	{
		super("{!t} " + operator + " {!t}", arg1, arg2);
	}
}
