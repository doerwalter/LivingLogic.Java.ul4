/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Iterator;
import java.util.Map;

public class MethodValues extends NormalMethod
{
	public String nameUL4()
	{
		return "values";
	}

	public Object evaluate(EvaluationContext context, Object obj, Object[] args)
	{
		return call(obj);
	}

	public static Object call(Map obj)
	{
		return obj.values();
	}

	public static Object call(UL4Attributes obj)
	{
		return new UL4AttributeValuesIterator(obj);
	}

	public static Object call(Object obj)
	{
		if (obj instanceof Map)
			return call((Map)obj);
		else if (obj instanceof UL4Attributes)
			return call((UL4Attributes)obj);
		throw new ArgumentTypeMismatchException("{}.values()", obj);
	}

	private static class UL4AttributeValuesIterator implements Iterator<Object>
	{
		UL4Attributes obj;
		Iterator<String> iterator;

		public UL4AttributeValuesIterator(UL4Attributes obj)
		{
			this.obj = obj;
			this.iterator = obj.getAttributeNamesUL4().iterator();
		}

		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		public Object next()
		{
			return obj.getItemStringUL4(iterator.next());
		}

		public void remove()
		{
			iterator.remove();
		}
	}
}
