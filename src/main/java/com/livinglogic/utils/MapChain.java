/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;


/**
 * A subclass of {@code AbstractMapChain} where the second map is passed to the
 * constructor.
 *
 * @author W. Doerwald
 */
public class MapChain<K, V> extends AbstractMapChain<K, V>
{
	private Map<K, V> second;

	public MapChain(Map<K, V> first, Map<K, V> second)
	{
		super(first);
		this.second = second;
	}

	public Map<K, V> getSecond()
	{
		return second;
	}
}
