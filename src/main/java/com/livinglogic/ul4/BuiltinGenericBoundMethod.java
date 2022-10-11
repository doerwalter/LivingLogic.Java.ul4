/*
** Copyright 2021-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;


/**
<p>A {@code BuiltinGenericBoundMethod} object is a callable that can be returned
from {@link UL4GetAttr#getAttrUL4} to implement an object method that can be
called from UL4.</p>

<p>The difference to {@link BoundMethod} is that the object to which the method
is bound must implement {@link UL4GetAttr} and calling the bound method will
be dispatched to {@link UL4GetAttr#getAttrUL4(EvaluationContext, String)}.</p>

<p>The difference to {@link GenericBoundMethod} is that {@link GenericBoundMethod}
is for classes that implement {@link UL4GetAttr}, while
{@code BuiltinGenericBoundMethod} is for builtin classes that can't implement
{@link UL4GetAttr}, so in this case calling the method is implemented in the
type object.</p>

<p>Furthermore since a {@code BuiltinGenericBoundMethod} can be bound to any method
it doesn't handle binding arguments in its default implementation. this must
be done by the object the bound method is bound to.</p>
**/
public class BuiltinGenericBoundMethod implements UL4Instance, UL4Call, UL4Name, UL4Repr
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
			return object instanceof BuiltinGenericBoundMethod;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected UL4Type typeObject;
	protected Object object;
	protected String methodName;

	/**
	<p>Return a new {@code BuiltinGenericBoundMethod} object bound to {@code object}.</p>

	<p>Calling the bound method object will call the method named {@code methodName}
	on the object to which this method is bound by calling
	{@link UL4Type#callAttr(EvaluationContext, Object, String, List, Map)} on {@code object}s type object passing
	{@code methodName} as the attribute name.</p>

	@param object The object this {@code BuiltinGenericBoundMethod} is bound to.
	@param methodName The name of the method to call.
	**/
	public BuiltinGenericBoundMethod(UL4Type typeObject, Object object, String methodName)
	{
		this.typeObject = typeObject;
		this.object = object;
		this.methodName = methodName;
	}

	@Override
	public String getFullNameUL4()
	{
		return typeObject.getFullNameUL4() + "." + methodName;
	}

	@Override
	public String getNameUL4()
	{
		return methodName;
	}

	@Override
	public Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		return typeObject.callAttr(context, object, methodName, args, kwargs);
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<bound method ");
		formatter.append(getFullNameUL4());
		formatter.append(">");
	}
}
