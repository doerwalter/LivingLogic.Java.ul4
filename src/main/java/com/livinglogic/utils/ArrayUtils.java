/*
** Copyright 2013-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.List;
import java.util.ArrayList;

/**
Class that provides utilities for working with arrays.

@author W. Doerwald
**/

public class ArrayUtils
{
	/**
	Create a copy of an string array, add several new strings and return the
	resulting new array.
	@param array The array.
	@param strings String to add to the new array.
	@return A new array containing all items from array and objects.
	**/
	public static String[] makeExtendedStringArray(String[] array, String... strings)
	{
		String[] newArray = new String[array.length + strings.length];

		int i = 0;
		for (String s : array)
			newArray[i++] = s;
		for (String s : strings)
			newArray[i++] = s;
		return newArray;
	}
}
