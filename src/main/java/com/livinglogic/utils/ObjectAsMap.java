/*
** Copyright 2009-2011 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * The purpose of this class it to provide a base class for classes that want to
 * expose their attributes via a Map interface. For this the subclass must implement
 * the abstract method getValueMakers().
 *
 * For each ObjectAsMap subclass getValueMakers() is supposed to return a singleton map
 * object (which means that each instance of this subclass will have the same keys in their
 * map view). The value stored for a certain key of the instance is produced by the value
 * in the map returned by getValueMakers(). This value is a ValueMaker instance that will
 * be used to extract the appropriate member/value for the instance.
 *
 * An example subclass of ObjectAsMap might look like this:
 *
 * <code>
 * public class Person extends ObjectAsMap
 * {
 * 	public String getFirstName()
 * 	{
 * 		...
 * 	}
 *
 * 	public String getLastName()
 * 	{
 * 		...
 * 	}
 *
 * 	private static Map&lt;String, ValueMaker> valueMakers = null;
 *
 * 	public Map&lt;String, ValueMaker> getValueMakers()
 * 	{
 * 		if (valueMakers == null)
 * 		{
 * 			HashMap&lt;String, ValueMaker> v = new HashMap&lt;String, ValueMaker>();
 * 			v.put("firstname", new ValueMaker(){public Object getValue(Object object){return ((Person)object).getFirstName();}});
 * 			v.put("lastname", new ValueMaker(){public Object getValue(Object object){return ((Person)object).getLastName();}});
 * 			valueMakers = v;
 * 		}
 * 		return valueMakers;
 * 	}
 * }
 * </code>
 *
 * <code>
 * Note that when the Person class above gets subclassed it's vital for its getValueMakers()
 * implementation to make a copy of the Map returned by the base class, otherwise this
 * would add non-existant attributes to the base class:
 *
 * public class Employee extends Person
 * {
 * 	public int getSalary()
 * 	{
 * 		...
 * 	}
 *
 * 	private static Map&lt;String, ValueMaker> valueMakers = null;
 *
 * 	public Map&lt;String, ValueMaker> getValueMakers()
 * 	{
 * 		if (valueMakers == null)
 * 		{
 * 			HashMap&lt;String, ValueMaker> v = new HashMap&lt;String, ValueMaker>(super.getValueMakers());
 * 			v.put("salary", new ValueMaker(){public Object getValue(Object object){return ((Employee)object).getSalary();}});
 * 			valueMakers = v;
 * 		}
 * 		return valueMakers;
 * 	}
 * }
 * </code>
 *
 * ObjectAsMap supports all map methods, except those that modify the map.
 *
 * The main use of ObjectAsMap is for exposing Java objects to UL4 templates
 *
 * @author W. Doerwald
 */

public abstract class ObjectAsMap implements Map<String, Object>
{
	/**
	 * Our own Map.Entry implementation required for entrySet()
	 */
	static class Entry implements Map.Entry<String, Object>
	{
		protected String key;
		protected Object value;

		/**
		 * Constructor
		 * @param key the string to be used as the key of this mapping
		 * @param value the object to be used as the key of this mapping
		 */
		public Entry(String key, Object value)
		{
			this.key = key;
			this.value = value;
		}

		/**
		 * @see Map.Entry#getKey()
		 */
		public String getKey()
		{
			return key;
		}

		/**
		 * @see Map.Entry#getValue()
		 */
		public Object getValue()
		{
			return value;
		}

		/**
		 * @see Map.Entry#setValue(Object)
		 */
		public Object setValue(Object value)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @see Map.Entry#equals(Object)
		 */
		public boolean equals(Object o)
		{
			if (o instanceof Entry)
			{
				Entry o2 = (Entry)o;

				if (key.equals(o2.getKey()))
				{
					return (value == null) ? o2.getValue()==null : value.equals(o2.getValue());
				}
			}
			return false;
		}

		/**
		 * @see Map.Entry#hashCode()
		 */
		public int hashCode()
		{
			return key.hashCode() ^ (value==null ? 0 : value.hashCode());
		}
	}

	/**
	 * Interface for extracting the value of a certain attribute from an object
	 */
	public interface ValueMaker
	{
		/**
		 * Return a certain attribute from the specified object.
		 */
		public Object getValue(Object object);
	}

	/**
	 * @see Map#size()
	 */
	public int size()
	{
		return keySet().size();
	}

	/**
	 * @see Map#isEmpty()
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}

	/**
	 * @see Map#containsKey(Object)
	 */
	public boolean containsKey(Object key)
	{
		return (keySet().contains(key));
	}

	/**
	 * This remains unimplemented, as it's unused by UL4 templates.
	 * @see Map#containsValue()
	 * @throws UnsupportedOperationException since it is unimplemented.
	 */
	public boolean containsValue(Object value)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Map#get(Object)
	 */
	public Object get(Object key)
	{
		ValueMaker valueMaker = getValueMakers().get(key);
		return (valueMaker != null) ? valueMaker.getValue(this) : null;
	}

	/**
	 * @see Map#put(String, Object)
	 */
	public Object put(String key, Object value)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * This is unimplemented, as ObjectAsMap object are immutable (at least via the map interface).
	 * @see Map#remove(ObjectAsMap)
	 * @throws UnsupportedOperationException since it is unimplemented.
	 */
	public Object remove(Object key)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * This is unimplemented, as ObjectAsMap object are immutable (at least via the map interface).
	 * @see Map#putAll(Map)
	 * @throws UnsupportedOperationException since it is unimplemented.
	 */
	public void putAll(Map<? extends String,? extends Object> t)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * This is unimplemented, as ObjectAsMap object are immutable (at least via the map interface).
	 * @see Map#clear(Map)
	 * @throws UnsupportedOperationException since it is unimplemented.
	 */
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @see Map#keySet()
	 */
	public Set<String> keySet()
	{
		return getValueMakers().keySet();
	}

	/**
	 * Abstract method that returns a map that maps attribute name to ValueMakers.
	 * A ValueMaker extracts an attribute from an object.
	 * This method should return a singleton (as for every instance the attribute names
	 * and how the attribute values are extracted from the instance are the same).
	 */
	public abstract Map<String, ValueMaker> getValueMakers();

	/**
	 * @see Map#values()
	 */
	public Collection<Object> values()
	{
		ArrayList<Object> values = new ArrayList<Object>();
		Map<String, ValueMaker> valueMakers = getValueMakers();

		for (ValueMaker valueMaker : valueMakers.values())
			/* extract the attribute specified by <code>valueMaker</code>
			 * from the object <code>this</code> and put it into the result list <code>values</code> */
			values.add(valueMaker.getValue(this));
		return values;
	}

	/**
	 * @see Map#entrySet()
	 */
	public Set<Map.Entry<String, Object>> entrySet()
	{
		Set<Map.Entry<String, Object>> items = new HashSet<Map.Entry<String, Object>>();
		Map<String, ValueMaker> valueMakers = getValueMakers();

		for (String key : keySet())
			/* get the ValueMaker responsible for the attribute with the name <code>key</code>
			 * extract this attribute from the object <code>this</code>
			 * and put the resulting key and value pair into the result set <code>items</code> */
			items.add(new Entry(key, valueMakers.get(key).getValue(this)));
		return items;
	}
}
