/*
** Copyright 2021-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;


/**
<p>A {@code GenericBoundMethod} object is a callable that can be returned from
{@link UL4GetAttr#getAttrUL4} to implement an object method that can be called
from UL4.</p>

<p>The difference to {@code BoundMethod} is that the object to which the method
is bound must implement {@link UL4GetAttr} and calling the bound method will
be dispatched to {@link UL4GetAttr#getAttrUL4(EvaluationContext, String)}.</p>

<p>Furthermore since a {@code GenericBoundMethod} can be bound to any method
it doesn't handle binding arguments in its default implementation. This must
be done by the object the bound method is bound to.</p>
**/
public class GenericBoundMethod<T extends UL4GetAttr> implements UL4Instance, UL4Render, UL4Name, UL4Repr
{
	protected static class Type extends AbstractInstanceType
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
			return object instanceof GenericBoundMethod;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected T object;
	protected String methodName;

	/**
	<p>Return a new {@code GenericBoundMethod} object bound to {@code object}.</p>

	<p>Calling the bound method object will call the method named {@code methodName}
	on the object to which this method is bound by calling
	{@link UL4GetAttr#getAttrUL4(EvaluationContext, String)} on {@code object} passing
	{@code methodName} as the attribute name.</p>

	@param object The object this {@code GenericBoundMethod} is bound to.
	@param methodName The name of the method to call.
	**/
	public GenericBoundMethod(T object, String methodName)
	{
		this.object = object;
		this.methodName = methodName;
	}

	@Override
	public String getFullNameUL4()
	{
		String typeName = object instanceof UL4Instance ? ((UL4Instance)object).getTypeUL4().getFullNameUL4() : Utils.objectType(object);
		return typeName + "." + methodName;
	}

	@Override
	public String getNameUL4()
	{
		return methodName;
	}

	@Override
	public Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		return object.callAttrUL4(context, methodName, args, kwargs);
	}

	@Override
	public void renderUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		object.renderAttrUL4(context, methodName, args, kwargs);
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<bound method ");
		formatter.append(getFullNameUL4());
		formatter.append(">");
	}
}
