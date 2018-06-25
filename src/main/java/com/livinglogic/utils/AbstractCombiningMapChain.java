/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.Map;


/**
 * A subclass of {@link AbstractMapChain} where, if the key exists in both maps,
 * getting the value can be customized by implementing the abstract method
 * {@code getCombinedValue}.
 *
 * Furthermore the method {@link #containsValue} will always throw an
 * {@link UnsupportedOperationException} exception).
 *
 * @author W. Doerwald
 */
public abstract class AbstractCombiningMapChain<K, V> extends AbstractMapChain<K, V>
{
	public AbstractCombiningMapChain(Map<K, V> first)
	{
		super(first);
	}

	/**
	 * @throws UnsupportedOperationException when called.
	 */
	@Override
	public boolean containsValue(Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public V get(Object key)
	{
		Map<K, V> second = getSecond();
		V value = first.get(key);
		if (second == null)
			return value;

		V secondValue = second.get(key);
		if (value == null && !first.containsKey(key))
			return secondValue;
		if (secondValue == null && !second.containsKey(key))
			return value;
		return getCombinedValue(key, value, secondValue);
	}

	public abstract V getCombinedValue(Object key, V firstValue, V secondValue);
}
