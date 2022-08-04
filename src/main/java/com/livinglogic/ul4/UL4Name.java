/*
** Copyright 2012-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public interface UL4Name
{
	String getNameUL4();

	default String getFullNameUL4()
	{
		return getNameUL4();
	}
}
