/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
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

/**
 * A subclass of {@code HashMap} where when a key or entry isn't found a second
 * ("chained") {@code Map} is consulted.
 *
 * Methods of {@code ChainedHashMap} that modify the mapping only modify the
 * {@code ChainedHashMap} itself not the chained {@code Map}. Modifying the
 * {@code ChainedHashMap} via the sets returned by {@code keySet()} or
 * {@code entrySet()} is forbidden (i.e. those methods throws a
 * {@code UnsupportedOperationException} exception).
 *
 * @author W. Doerwald
 */
public abstract class ChainedHashMap<K, V> extends HashMap<K, V>
{
	private static final long serialVersionUID = 5436650168502819105L;

	/**
	 * {@code getChainedMap()} returns the map where lookup of keys/entries
	 * continues ff they can't be found in the {@code ChainedHashMap} itself.
	 * If {@code getChainedMap()} returns {@code null}, no further lookup is
	 * done and a {@code ChainedHashMap} behaves exactly like a {@code HashMap}
	 * (except that modification via {@code keySet()}/{@code entrySet()}
	 * are not allowed).
	 * @return the chained {@code Map} or {@code null}
	 */
	abstract public Map<K, V> getChainedMap();

	class KeySet extends AbstractSet<K>
	{
		class KeySetIterator implements Iterator<K>
		{
			private Iterator<K> mapIterator;
			private Iterator<K> chainedMapIterator;
			private boolean haveBufferedKey;
			private K bufferedKey;

			public KeySetIterator()
			{
				mapIterator = ChainedHashMap.super.keySet().iterator();
				Map<K, V> chainedMap = getChainedMap();
				chainedMapIterator = (chainedMap != null) ? chainedMap.keySet().iterator() : null;
				haveBufferedKey = false;
				bufferedKey = null;
			}

			public boolean hasNext()
			{
				if (haveBufferedKey)
					return true;
				if (mapIterator.hasNext())
					return true;

				// our own iterator is exhausted; use the chained iterator (if it exists)
				if (chainedMapIterator != null)
				{
					 // ignore all keys that our own iterator has already produced
					while (chainedMapIterator.hasNext())
					{
						K item = chainedMapIterator.next();
						// Is this a key we haven't seen before?
						if (!ChainedHashMap.super.containsKey(item))
						{
							haveBufferedKey = true;
							bufferedKey = item;
							return true;
						}
					}
				}
				return false;
			}

			public K next()
			{
				if (haveBufferedKey)
				{
					K result = bufferedKey;
					bufferedKey = null;
					haveBufferedKey = false;
					return result;
				}
				if (mapIterator.hasNext())
					return mapIterator.next();

				// our own iterator is exhausted; use the chained iterator (if it exists)
				if (chainedMapIterator != null)
				{
					 // ignore all keys that our own iterator has already produced
					while (chainedMapIterator.hasNext())
					{
						K item = chainedMapIterator.next();
						// Is this a key we haven't seen before?
						if (!ChainedHashMap.super.containsKey(item))
							return item;
					}
				}
				throw new NoSuchElementException();
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		}

		public boolean contains(Object o)
		{
			return containsKey(o);
		}

		public boolean containsAll(Collection<?> c)
		{
			for (Object obj: c)
			{
				if (!containsKey(obj))
					return false;
			}
			return true;
		}

		public Iterator<K> iterator()
		{
			return new KeySetIterator();
		}

		public boolean isEmpty()
		{
			return ChainedHashMap.this.isEmpty();
		}

		public int size()
		{
			return ChainedHashMap.this.size();
		}
	}

	class EntrySet extends AbstractSet<Map.Entry<K, V>>
	{
		class EntrySetIterator implements Iterator<Map.Entry<K, V>>
		{
			private Iterator<Map.Entry<K, V>> mapIterator;
			private Iterator<Map.Entry<K, V>> chainedMapIterator;
			private Map.Entry<K, V> bufferedEntry;

			public EntrySetIterator()
			{
				mapIterator = ChainedHashMap.super.entrySet().iterator();
				Map<K, V> chainedMap = getChainedMap();
				chainedMapIterator = (chainedMap != null) ? chainedMap.entrySet().iterator() : null;
				bufferedEntry = null;
			}

			public boolean hasNext()
			{
				if (bufferedEntry != null)
					return true;
				if (mapIterator.hasNext())
					return true;

				// our own iterator is exhausted; use the chained iterator (if it exists)
				if (chainedMapIterator != null)
				{
					 // ignore all entries that our own iterator has already produced
					while (chainedMapIterator.hasNext())
					{
						Map.Entry<K, V> entry = chainedMapIterator.next();
						// Is this an entry we haven't seen before?
						if (!ChainedHashMap.super.containsKey(entry.getKey()))
						{
							bufferedEntry = entry;
							return true;
						}
					}
				}
				return false;
			}

			public Map.Entry<K, V> next()
			{
				if (bufferedEntry != null)
				{
					Map.Entry<K, V> result = bufferedEntry;
					bufferedEntry = null;
					return result;
				}
				if (mapIterator.hasNext())
					return mapIterator.next();

				// our own iterator is exhausted; use the chained iterator (if it exists)
				if (chainedMapIterator != null)
				{
					 // ignore all entries that our own iterator has already produced
					while (chainedMapIterator.hasNext())
					{
						Map.Entry<K, V> entry = chainedMapIterator.next();
						// Is this an entry we haven't seen before?
						if (!ChainedHashMap.super.containsKey(entry.getKey()))
							return entry;
					}
				}
				throw new NoSuchElementException();
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		}

		public Iterator<Map.Entry<K, V>> iterator()
		{
			return new EntrySetIterator();
		}

		public boolean isEmpty()
		{
			return ChainedHashMap.this.isEmpty();
		}

		public int size()
		{
			return ChainedHashMap.this.size();
		}
	}

	public boolean containsKey(Object key)
	{
		boolean containsKey = super.containsKey(key);
		Map<K, V> chainedMap = getChainedMap();
		if (!containsKey && chainedMap != null)
			containsKey = chainedMap.containsKey(key);
		return containsKey;
	}

	public boolean containsValue(Object value)
	{
		boolean containsValue = super.containsValue(value);
		Map<K, V> chainedMap = getChainedMap();
		if (!containsValue && chainedMap != null)
			containsValue = chainedMap.containsValue(value);
		return containsValue;
	}

	public Set<Map.Entry<K,V>> entrySet()
	{
		return new EntrySet();
	}

	public V get(Object key)
	{
		V value = super.get(key);
		Map<K, V> chainedMap = getChainedMap();
		if (value == null && chainedMap != null && !super.containsKey(key))
			value = chainedMap.get(key);
		return value;
	}

	public boolean isEmpty()
	{
		boolean isEmpty = super.isEmpty();
		Map<K, V> chainedMap = getChainedMap();
		if (isEmpty && chainedMap != null)
			isEmpty = chainedMap.isEmpty();
		return isEmpty;
	}

	public Set<K> keySet()
	{
		return new KeySet();
	}

	public int size()
	{
		int size = super.size();

		Map<K, V> chainedMap = getChainedMap();
		if (chainedMap != null)
		{
			for (Object key : chainedMap.keySet())
			{
				if (!super.containsKey(key))
					++size;
			}
		}
		return size;
	}

	// FIXME: Implement this
	public Collection<V> values()
	{
		throw new UnsupportedOperationException();
	}
}
