/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class BoundUL4GetAttributesMethodItems extends BoundMethod<UL4GetAttributes>
{
	private static final Signature signature = new Signature("items");

	public BoundUL4GetAttributesMethodItems(UL4GetAttributes object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(UL4GetAttributes object)
	{
		return new UL4AttributeItemsIterator(object);
	}

	public Object callUL4(Object[] args)
	{
		return call(object);
	}

	private static class UL4AttributeItemsIterator implements Iterator<Vector>
	{
		UL4GetAttributes object;
		Iterator<String> iterator;

		public UL4AttributeItemsIterator(UL4GetAttributes object)
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
			retVal.add(object.getItemStringUL4(attributeName));
			return retVal;
		}

		public void remove()
		{
			iterator.remove();
		}
	}
}
