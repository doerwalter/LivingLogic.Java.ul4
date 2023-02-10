/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;

/**
<p>A {@code BoundMethod} object is a callable that can be returned from
{@link UL4GetAttr#getAttrUL4} to implement an object method that can be called
from UL4.</p>

<p>{@code BoundMethod} is abstract. Subclasses must implemented the method
{@link Function#evaluate}. Also when the method requires arguments
{@link Function#getSignature} must be overwritten.</p>
**/
public abstract class BoundMethod<T> extends Function
{
	protected static class Type extends Function.Type
	{
		@Override
		public String getNameUL4()
		{
			return "bound method";
		}

		@Override
		public String getDoc()
		{
			return "A bound method. Calling it calls the original method on the bound object.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BoundMethod;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected T object;

	@Override
	public String getFullNameUL4()
	{
		String typeName = object instanceof UL4Instance ? ((UL4Instance)object).getTypeUL4().getFullNameUL4() : Utils.objectType(object);
		return typeName + "." + getNameUL4();
	}

	/**
	Return a new {@code BoundMethod} object bound to {@code object}.

	@param object The object this new {@code BoundMethod} is bound to.
	**/
	public BoundMethod(T object)
	{
		this.object = object;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<bound method ");
		formatter.append(getFullNameUL4());
		formatter.append(">");
	}
}
