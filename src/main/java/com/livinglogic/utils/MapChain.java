/*
** Copyright 2009-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.Map;


/**
A subclass of {@code AbstractMapChain} where the second map is passed to the
constructor.

@author W. Doerwald
**/
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

	public Map<K, V> setSecond(Map<K, V> second)
	{
		Map<K, V> oldSecond = this.second;
		this.second = second;
		return oldSecond;
	}
}
