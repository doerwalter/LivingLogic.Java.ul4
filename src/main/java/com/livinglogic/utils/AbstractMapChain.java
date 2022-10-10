/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.Objects;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Map;
import java.util.AbstractMap;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;


/**
A {@code Map} implemention that forwards all operations to one or two maps.
When a key or entry isn't found in the first map the second ("chained")
{@code Map} is consulted.

Methods of {@code AbstractMapChain} that modify the mapping only modify the first
{@code Map} not the second one. Modifying the {@code AbstractMapChain} via the sets
returned by {@code keySet()} or {@code entrySet()} is forbidden (i.e. those
methods throw a {@code UnsupportedOperationException} exception).

@author W. Doerwald
**/
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

			@Override
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

			@Override
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

		@Override
		public boolean contains(Object o)
		{
			return containsKey(o);
		}

		@Override
		public boolean containsAll(Collection<?> c)
		{
			for (Object obj : c)
			{
				if (!containsKey(obj))
					return false;
			}
			return true;
		}

		@Override
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

		@Override
		public Iterator<K> iterator()
		{
			return new KeySetIterator();
		}

		@Override
		public boolean isEmpty()
		{
			return AbstractMapChain.this.isEmpty();
		}

		@Override
		public int size()
		{
			return AbstractMapChain.this.size();
		}
	}

	class EntrySet extends AbstractSet<Map.Entry<K, V>>
	{
		/* Our entry iterator is based on our key iterator instead of
		using the entry iterators of the first and second map.
		
		This simplifies our implementation, as we don't need any caching logic
		(like the key iterator does) and it simplifies the subclass
		{@code AbstractCombiningMapChain} (as it doesn't have to implement any
		entry set functionality, because our implementation falls back to
		calling {@code get}, which {@code AbstractCombiningMapChain}
		overwrites).
		**/
		class EntrySetIterator implements Iterator<Map.Entry<K, V>>
		{
			private Iterator<K> keyIterator;

			public EntrySetIterator()
			{
				keyIterator = keySet().iterator();
			}

			public boolean hasNext()
			{
				return keyIterator.hasNext();
			}

			public Map.Entry<K, V> next()
			{
				K key = keyIterator.next();
				return new AbstractMap.SimpleImmutableEntry(key, get(key));
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

	class ValuesCollection extends AbstractCollection<V>
	{
		ValuesCollection()
		{
		}

		@Override
		public int size()
		{
			return AbstractMapChain.this.size();
		}

		@Override
		public boolean isEmpty()
		{
			return AbstractMapChain.this.isEmpty();
		}

		@Override
		public Iterator<V> iterator()
		{
			return new ValuesIterator();
		}

		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof Collection))
				return false;
			Collection c = (Collection)o;
			if (size() != c.size())
				return false;
			Iterator<V> thisIterator = iterator();
			Iterator<V> otherIterator = c.iterator();

			// We assume here that both iterator return the same amount of
			// items, which is reasonable, because their collection have
			// the same size.
			while (thisIterator.hasNext())
			{
				if (!Objects.equals(thisIterator.next(), otherIterator.next()))
					return false;
			}
			return true;
		}
	}

	class ValuesIterator implements Iterator<V>
	{
		private Iterator<Map.Entry<K, V>> iterator;

		ValuesIterator()
		{
			iterator = entrySet().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public V next()
		{
			if (iterator.hasNext())
				return iterator.next().getValue();
			return null;
		}
	}

	protected Map<K, V> first;

	public AbstractMapChain(Map<K, V> first)
	{
		this.first = first;
	}

	@Override
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

	@Override
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

	@Override
	public Set<Map.Entry<K,V>> entrySet()
	{
		Map<K, V> second = getSecond();
		return second != null ? new EntrySet() : first.entrySet();
	}

	@Override
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

	@Override
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

	@Override
	public Set<K> keySet()
	{
		Map<K, V> second = getSecond();
		return second != null ? new KeySet() : first.keySet();
	}

	@Override
	public int hashCode()
	{
		int hashCode = first.hashCode();
		Map<K, V> second = getSecond();
		if (second != null)
			hashCode ^= second.hashCode();
		return hashCode;
	}

	@Override
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

	@Override
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

	@Override
	public void clear()
	{
		first.clear();
	}

	@Override
	public V put(K key, V value)
	{
		return first.put(key, value);
	}

	@Override
	public V remove(Object key)
	{
		return first.remove(key);
	}

	@Override
	public void putAll(Map<? extends K,? extends V> m)
	{
		first.putAll(m);
	}

	@Override
	public Collection<V> values()
	{
		return new ValuesCollection();
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
