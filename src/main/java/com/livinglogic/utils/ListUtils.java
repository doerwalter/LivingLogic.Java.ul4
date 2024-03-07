/*
** Copyright 2013-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.List;
import java.util.ArrayList;

/**
Class that provides utilities for working with lists

@author W. Doerwald
**/

public class ListUtils
{
	/**
	Create a copy of a List, add several new items and return the resulting new List.

	@param <T> The type of the list items.
	@param list The list where items should be added.
	@param objects Items to add to the new list.
	@return A new List containing all items from {@code list} and {@code objects}.
	**/
	public static <T> List<T> makeExtendedList(List<T> list, T... objects)
	{
		List<T> newList = new ArrayList<T>(list);
		for (T o : objects)
			newList.add(o);
		return newList;
	}
}
