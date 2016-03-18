/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class BoundUL4GetAttributesMethodItems extends BoundMethod<UL4Attributes>
{
	public BoundUL4GetAttributesMethodItems(UL4Attributes object)
	{
		super(object);
	}

	public String nameUL4()
	{
		return "object.items";
	}

	public static Object call(UL4Attributes object)
	{
		return new UL4AttributeItemsIterator(object);
	}

	public Object evaluate(BoundArguments args)
	{
		return call(object);
	}

	private static class UL4AttributeItemsIterator implements Iterator<Vector>
	{
		UL4Attributes object;
		Iterator<String> iterator;

		public UL4AttributeItemsIterator(UL4Attributes object)
		{
			this.object = object;
			this.iterator = object.getAttributeNamesUL4().iterator();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public Vector next()
		{
			Vector retVal = new Vector(2);
			String attributeName = iterator.next();
			retVal.add(attributeName);
			retVal.add(((UL4GetItemString)object).getItemStringUL4(attributeName));
			return retVal;
		}

		public void remove()
		{
			iterator.remove();
		}
	}
}
