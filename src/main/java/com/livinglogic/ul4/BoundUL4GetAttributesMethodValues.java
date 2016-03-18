/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Map;

public class BoundUL4GetAttributesMethodValues extends BoundMethod<UL4Attributes>
{
	public BoundUL4GetAttributesMethodValues(UL4Attributes object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "object.values";
	}

	public static Object call(UL4Attributes object)
	{
		return new UL4GetAttributeValuesIterator(object);
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}

	private static class UL4GetAttributeValuesIterator implements Iterator<Object>
	{
		UL4Attributes object;
		Iterator<String> iterator;

		public UL4GetAttributeValuesIterator(UL4Attributes object)
		{
			this.object = object;
			this.iterator = object.getAttributeNamesUL4().iterator();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public Object next()
		{
			return ((UL4GetItemString)object).getItemStringUL4(iterator.next());
		}

		public void remove()
		{
			iterator.remove();
		}
	}
}
