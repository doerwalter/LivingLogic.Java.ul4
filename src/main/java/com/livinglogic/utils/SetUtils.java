/*
** Copyright 2013-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.LinkedHashSet;
import java.util.HashSet;
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
	public static <T> void putSet(Set<T> set, T... args)
	{
		for (T arg : args)
		{
			set.add(arg);
		}
	}

	/**
	 * Create a Set from arguments.
	 * @param args The object to be put into the set.
	 * @return A Set containing the variables
	 */
	public static <T> Set<T> makeSet(T... args)
	{
		LinkedHashSet<T> set = new LinkedHashSet<T>();
		putSet(set, args);
		return set;
	}

	/**
	 * Create a Set as a union of two sets.
	 * @param set1 The first set.
	 * @param set2 The second set.
	 * @return A Set all values for the first and second set.
	 */
	public static <T> Set<T> union(Set<T> set1, Set<T> set2)
	{
		LinkedHashSet<T> set = new LinkedHashSet<T>();
		set.addAll(set1);
		set.addAll(set2);
		return set;
	}

	/**
	 * Create a copy of a Set, add several new items and return the resulting new Set.
	 * @param set The set.
	 * @param objects Items to add to the new set.
	 * @return A new Set containing all items from Set and objects.
	 */
	public static <T> Set<T> makeExtendedSet(Set<T> set, T... objects)
	{
		Set<T> newSet = new HashSet<T>(set);
		for (T o : objects)
			newSet.add(o);
		return newSet;
	}
}
