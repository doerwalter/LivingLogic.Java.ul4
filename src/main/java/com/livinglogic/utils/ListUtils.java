/*
** Copyright 2013-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.List;
import java.util.ArrayList;

/**
 * Class that provides utilities for working with lists
 *
 * @author W. Doerwald
 */

public class ListUtils
{
	/**
	Create a copy of a List, add several new items and return the resulting new List.
	@param set The list.
	@param objects Items to add to the new list.
	@return A new List containing all items from List and objects.
	**/
	public static <T> List<T> makeExtendedList(List<T> list, T... objects)
	{
		List<T> newList = new ArrayList<T>(list);
		for (T o : objects)
			newList.add(o);
		return newList;
	}
}
