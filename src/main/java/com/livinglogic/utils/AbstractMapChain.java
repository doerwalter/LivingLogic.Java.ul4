/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;


/**
 * A {@code Map} implemention that forwards all operations to one or two mapps.
 * When a key or entry isn't found in the first map the second ("chained")
 * {@code Map} is consulted.
 *
 * Methods of {@code AbstractMapChain} that modify the mapping only modify the first
 * {@code Map} not the second one. Modifying the {@code AbstractMapChain} via the sets
 * returned by {@code keySet()} or{@code entrySet()} is forbidden (i.e. those
 * methods throw a {@code UnsupportedOperationException} exception).
 *
 * @author W. Doerwald
 */
public abstract class AbstractMapChain<K, V> implements Map<K, V>
{
	class KeySet extends AbstractSet<K>
	{
		class KeySetIterator implements Iterator<K>
		{
			private Iterator<K> firstIterator;
			private Iterator<K> secondIterator;
			private boolean haveBufferedKey;
			private K bufferedKey;

			public KeySetIterator()
			{
				firstIterator = first.keySet().iterator();
				secondIterator = getSecond().keySet().iterator();
				haveBufferedKey = false;
				bufferedKey = null;
			}

			public boolean hasNext()
			{
				if (haveBufferedKey)
					return true;
				if (firstIterator.hasNext())
					return true;

				// the first iterator is exhausted; use the second one
				while (secondIterator.hasNext())
				{
					K item = secondIterator.next();
					// ignore all keys that the first iterator has already produced
					if (!first.containsKey(item))
					{
						haveBufferedKey = true;
						bufferedKey = item;
						return true;
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
				if (firstIterator.hasNext())
					return firstIterator.next();

				// the first iterator is exhausted; use the second one
				while (secondIterator.hasNext())
				{
					K item = secondIterator.next();
					// ignore all keys that the first iterator has already produced
					if (!first.containsKey(item))
						return item;
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
			for (Object obj : c)
			{
				if (!containsKey(obj))
					return false;
			}
			return true;
		}

		public boolean equals(Object other)
		{
			if (!(other instanceof Set))
				return false;

			int size = ((Set)other).size();
			for (Object key : this)
			{
				if (!((Set)other).contains(key))
					return false;
				if (--size < 0)
					return false;
			}
			if (size != 0)
				return false;
			return true;
		}

		public Iterator<K> iterator()
		{
			return new KeySetIterator();
		}

		public boolean isEmpty()
		{
			return AbstractMapChain.this.isEmpty();
		}

		public int size()
		{
			return AbstractMapChain.this.size();
		}
	}

	class EntrySet extends AbstractSet<Map.Entry<K, V>>
	{
		class EntrySetIterator implements Iterator<Map.Entry<K, V>>
		{
			private Iterator<Map.Entry<K, V>> firstIterator;
			private Iterator<Map.Entry<K, V>> secondIterator;
			private Map.Entry<K, V> bufferedEntry;

			public EntrySetIterator()
			{
				firstIterator = first.entrySet().iterator();
				secondIterator = getSecond().entrySet().iterator();
				bufferedEntry = null;
			}

			public boolean hasNext()
			{
				if (bufferedEntry != null)
					return true;
				if (firstIterator.hasNext())
					return true;

				// the first iterator is exhausted; use the second one
				// ignore all entries that the first iterator has already produced
				while (secondIterator.hasNext())
				{
					Map.Entry<K, V> entry = secondIterator.next();
					// Is this an entry we haven't seen before?
					if (!first.containsKey(entry.getKey()))
					{
						bufferedEntry = entry;
						return true;
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
				if (firstIterator.hasNext())
					return firstIterator.next();

				// the first iterator is exhausted; use the second one
				// ignore all entries that the first iterator has already produced
				while (secondIterator.hasNext())
				{
					Map.Entry<K, V> entry = secondIterator.next();
					// Is this an entry we haven't seen before?
					if (!first.containsKey(entry.getKey()))
						return entry;
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
			if (!(o instanceof Map.Entry))
				return false;
			Object key = ((Map.Entry)o).getKey();
			Object value = ((Map.Entry)o).getValue();
			Object ourValue = get(key);

			if (ourValue == null && !containsKey(key))
				return false;
			return ObjectUtils.equals(value, ourValue);
		}

		public boolean containsAll(Collection<?> c)
		{
			for (Object obj : c)
			{
				if (!contains(obj))
					return false;
			}
			return true;
		}

		public boolean equals(Object other)
		{
			if (!(other instanceof Set))
				return false;

			int size = ((Set)other).size();
			for (Object entry : this)
			{
				if (!((Set)other).contains(entry))
					return false;
				if (--size < 0)
					return false;
			}
			if (size != 0)
				return false;
			return true;
		}

		public Iterator<Map.Entry<K, V>> iterator()
		{
			return new EntrySetIterator();
		}

		public boolean isEmpty()
		{
			return AbstractMapChain.this.isEmpty();
		}

		public int size()
		{
			return AbstractMapChain.this.size();
		}
	}

	private Map<K, V> first;

	public AbstractMapChain(Map<K, V> first)
	{
		this.first = first;
	}

	public boolean containsKey(Object key)
	{
		boolean containsKey = first.containsKey(key);
		if (!containsKey)
		{
			Map<K, V> second = getSecond();
			if (second != null)
				containsKey = second.containsKey(key);
		}
		return containsKey;
	}

	public boolean containsValue(Object value)
	{
		boolean containsValue = first.containsValue(value);
		if (!containsValue)
		{
			Map<K, V> second = getSecond();
			if (second != null)
				containsValue = second.containsValue(value);
		}
		return containsValue;
	}

	public Set<Map.Entry<K,V>> entrySet()
	{
		Map<K, V> second = getSecond();
		return second != null ? new EntrySet() : first.entrySet();
	}

	public V get(Object key)
	{
		V value = first.get(key);
		if (value == null && !first.containsKey(key))
		{
			Map<K, V> second = getSecond();
			if (second != null)
				value = second.get(key);
		}
		return value;
	}

	public boolean isEmpty()
	{
		boolean isEmpty = first.isEmpty();
		if (isEmpty)
		{
			Map<K, V> second = getSecond();
			if (second != null)
				isEmpty = second.isEmpty();
		}
		return isEmpty;
	}

	public Set<K> keySet()
	{
		Map<K, V> second = getSecond();
		return second != null ? new KeySet() : first.keySet();
	}

	public int hashCode()
	{
		int hashCode = first.hashCode();
		Map<K, V> second = getSecond();
		if (second != null)
			hashCode ^= second.hashCode();
		return hashCode;
	}

	public int size()
	{
		int size = first.size();

		Map<K, V> second = getSecond();
		if (second != null)
		{
			for (Object key : second.keySet())
			{
				if (!first.containsKey(key))
					++size;
			}
		}
		return size;
	}

	public boolean equals(Object other)
	{
		Map<K, V> second = getSecond();
		if (second != null)
		{
			if (!(other instanceof Map))
				return false;
			Map otherMap = (Map)other;

			int size = otherMap.size();

			for (Object key : keySet())
			{
				if (!((Map)other).containsKey(key))
					return false;
				if (--size < 0)
					return false;

				Object thisValue = get(key);
				Object otherValue = ((Map)other).get(key);
				if (!ObjectUtils.equals(thisValue, otherValue))
					return false;
			}
			if (size != 0)
				return false;
			return true;
		}
		else
			return first.equals(other);
	}

	public void clear()
	{
		first.clear();
	}

	public V put(K key, V value)
	{
		return first.put(key, value);
	}

	public V remove(Object key)
	{
		return first.remove(key);
	}

	public void putAll(Map<? extends K,? extends V> m)
	{
		first.putAll(m);
	}

	// FIXME: Implement this
	public Collection<V> values()
	{
		throw new UnsupportedOperationException();
	}

	public Map<K, V> setFirst(Map<K, V> first)
	{
		Map<K, V> oldFirst = this.first;
		this.first = first;
		return oldFirst;
	}

	public Map<K, V> getFirst()
	{
		return first;
	}

	public abstract Map<K, V> getSecond();
}
