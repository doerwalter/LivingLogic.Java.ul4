/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Map;

import java.util.List;
import java.util.Map;

public class BoundUL4GetAttributesMethodValues extends BoundMethod<UL4GetAttributes>
{
	private static final Signature signature = new Signature("values");

	public BoundUL4GetAttributesMethodValues(UL4GetAttributes object)
	{
		super(object);
	}

	public Signature getSignature()
	{
		return signature;
	}

	public static Object call(UL4GetAttributes object)
	{
		return new UL4GetAttributeValuesIterator(object);
	}

	public Object callUL4(Object[] args, Map<String, Object> kwargs)
	{
		args = signature.makeArgumentArray(args, kwargs);

		return call(object);
	}

	private static class UL4GetAttributeValuesIterator implements Iterator<Object>
	{
		UL4GetAttributes object;
		Iterator<String> iterator;

		public UL4GetAttributeValuesIterator(UL4GetAttributes object)
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
			return object.getItemStringUL4(iterator.next());
		}

		public void remove()
		{
			iterator.remove();
		}
	}
}