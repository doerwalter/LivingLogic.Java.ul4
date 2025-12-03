/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
<p>A {@code MethodDescriptor} is used by classes that implement
{@link UL4GetAttr} to store names and signatures of methods.</p>
**/
public class MethodDescriptor<T extends UL4GetAttr>
{
	protected String typeName;
	protected String methodName;
	protected Signature signature;

	public MethodDescriptor(UL4Type type, String methodName, Signature signature)
	{
		this(type.getFullNameUL4(), methodName, signature);
	}

	public MethodDescriptor(String typeName, String methodName, Signature signature)
	{
		this.typeName = typeName;
		this.methodName = methodName;
		this.signature = signature;
	}

	public GenericBoundMethod<T> bindMethod(T object)
	{
		return new GenericBoundMethod<T>(object, methodName);
	}

	public BoundArguments bindArguments(List<Object> args, Map<String, Object> kwargs)
	{
		return signature.bind(typeName + "." + methodName, null, args, kwargs);
	}

	public BoundArguments bindArguments(T object, List<Object> args, Map<String, Object> kwargs)
	{
		return signature.bind(typeName + "." + methodName, object, args, kwargs);
	}
}
