/*
** Copyright 2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class that provides utilities for working with sets
 *
 * @author W. Doerwald
 */

public class SetUtils
{
	/**
	 * Add entries to a Set.
	 * @param set The set where entries are added.
	 * @param args The objects to be put into the set.
	 */
	public static void putSet(Set set, Object... args)
	{
		for (Object arg : args)
		{
			set.add(arg);
		}
	}

	/**
	 * Create a Set from arguments.
	 * @param args The object to be put into the set.
	 * @return A Set containing the variables
	 */
	public static Set makeSet(Object... args)
	{
		LinkedHashSet set = new LinkedHashSet();
		putSet(set, args);
		return set;
	}

	/**
	 * Create a Set as a union of two sets.
	 * @param set1 The first set.
	 * @param set2 The second set.
	 * @return A Set all values for the first and second set.
	 */
	public static Set union(Set set1, Set set2)
	{
		LinkedHashSet set = new LinkedHashSet();
		set.addAll(set1);
		set.addAll(set2);
		return set;
	}
}
