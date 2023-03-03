/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.AbstractMap;
import java.util.Set;

/**
The purpose of this class it to provide a base class for classes that want to
expose their attributes via a Map interface. For this the subclass must
implement the abstract method {@link #getValueMakers()}.

For each {@code ObjectAsMap} subclass {@link #getValueMakers()} is supposed to
return a singleton map object (which means that each instance of this subclass
will have the same keys in their map view). The value stored for a certain key
of the instance is produced by the value in the map returned by
{@link #getValueMakers()}. This value is a {@link ObjectAsMap.ValueMaker}
instance that will be used to extract the appropriate member/value for the
instance.

An example subclass of {@code ObjectAsMap} might look like this:

<pre>
public class Person extends ObjectAsMap
{
	public String getFirstName()
	{
		...
	}

	public String getLastName()
	{
		...
	}

	private static Map&lt;String, ValueMaker&gt; valueMakers = null;

	public Map&lt;String, ValueMaker&gt; getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap&lt;String, ValueMaker&gt; v = new HashMap&lt;String, ValueMaker&gt;();
			v.put("firstname", new ValueMaker(){public Object getValue(Object object){return ((Person)object).getFirstName();}});
			v.put("lastname", new ValueMaker(){public Object getValue(Object object){return ((Person)object).getLastName();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
</pre>

Note that when the Person class above gets subclassed it's vital for its
{@link #getValueMakers} implementation to make a copy of the {@code Map}
returned by the base class, otherwise this would add non-existant attributes
to the base class:

<pre>
public class Employee extends Person
{
	public int getSalary()
	{
		...
	}

	private static Map&lt;String, ValueMaker&gt; valueMakers = null;

	public Map&lt;String, ValueMaker&gt; getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap&lt;String, ValueMaker&gt; v = new HashMap&lt;String, ValueMaker&gt;(super.getValueMakers());
			v.put("salary", new ValueMaker(){public Object getValue(Object object){return ((Employee)object).getSalary();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
</pre>

{@code ObjectAsMap} supports all map methods, except those that modify the
map.

The main use of {@code ObjectAsMap} is for exposing Java objects to UL4
templates.

@author W. Doerwald
**/
public abstract class ObjectAsMap implements Map<String, Object>
{
	/**
	Interface for extracting the value of a certain attribute from an object
	**/
	public interface ValueMaker
	{
		/**
		Return a certain attribute from the specified object.
		**/
		public Object getValue(Object object);
	}

	@Override
	public int size()
	{
		return keySet().size();
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}

	@Override
	public boolean containsKey(Object key)
	{
		return (keySet().contains(key));
	}

	/**
	This remains unimplemented, as it's unused by UL4 templates.
	@throws UnsupportedOperationException since it is unimplemented.
	**/
	@Override
	public boolean containsValue(Object value)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(Object key)
	{
		ValueMaker valueMaker = getValueMakers().get(key);
		return (valueMaker != null) ? valueMaker.getValue(this) : null;
	}

	/**
	This is unimplemented, as ObjectAsMap object are immutable (at least via the map interface).
	@throws UnsupportedOperationException since it is unimplemented.
	**/
	@Override
	public Object put(String key, Object value)
	{
		throw new UnsupportedOperationException();
	}

	/**
	This is unimplemented, as ObjectAsMap object are immutable (at least via the map interface).
	@throws UnsupportedOperationException since it is unimplemented.
	**/
	@Override
	public Object remove(Object key)
	{
		throw new UnsupportedOperationException();
	}

	/**
	This is unimplemented, as ObjectAsMap object are immutable (at least via the map interface).
	@throws UnsupportedOperationException since it is unimplemented.
	**/
	@Override
	public void putAll(Map<? extends String,? extends Object> t)
	{
		throw new UnsupportedOperationException();
	}

	/**
	This is unimplemented, as ObjectAsMap object are immutable (at least via the map interface).
	@throws UnsupportedOperationException since it is unimplemented.
	**/
	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet()
	{
		return getValueMakers().keySet();
	}

	/**
	Abstract method that returns a map that maps attribute names to
	{@link ObjectAsMap.ValueMaker} objects. A {@link ObjectAsMap.ValueMaker}
	extracts an attribute from an object.

	This method should return a singleton (as for every instance the attribute
	names and how the attribute values are extracted from the instance are the
	same).
	**/
	public abstract Map<String, ValueMaker> getValueMakers();

	@Override
	public Collection<Object> values()
	{
		ArrayList<Object> values = new ArrayList<Object>();
		Map<String, ValueMaker> valueMakers = getValueMakers();

		for (ValueMaker valueMaker : valueMakers.values())
			/* extract the attribute specified by {@code valueMaker}
			 * from the object {@code this} and put it into the result list {@code values} */
			values.add(valueMaker.getValue(this));
		return values;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet()
	{
		Set<Map.Entry<String, Object>> items = new HashSet<Map.Entry<String, Object>>();
		Map<String, ValueMaker> valueMakers = getValueMakers();

		for (String key : keySet())
			/* get the {@code ValueMaker} responsible for the attribute with the
			 * name {@code key}, extract this attribute from the object {@code this}
			 * and put the resulting key and value pair into the result set
			 * {@code items} */
			items.add(new AbstractMap.SimpleImmutableEntry(key, valueMakers.get(key).getValue(this)));
		return items;
	}
}
