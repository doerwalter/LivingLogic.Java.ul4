/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;

/**
<p>A {@code BuiltinMethodDescriptor} is used by type object to implement
method calls. A {@code BuiltinMethodDescriptor} object stores the name and
signature of a method.</p>
**/
public class BuiltinMethodDescriptor
{
	protected UL4Type type;
	protected String methodName;
	protected Signature signature;

	public BuiltinMethodDescriptor(UL4Type type, String methodName, Signature signature)
	{
		this.type = type;
		this.methodName = methodName;
		this.signature = signature;
	}

	public BuiltinGenericBoundMethod bindMethod(Object object)
	{
		return new BuiltinGenericBoundMethod(type, object, methodName);
	}

	public BoundArguments bindArguments(List<Object> args, Map<String, Object> kwargs)
	{
		return signature.bind(type.getFullNameUL4() + "." + methodName, args, kwargs);
	}

	public BoundArguments bindArguments(Object self, List<Object> args, Map<String, Object> kwargs)
	{
		return signature.bind(type.getFullNameUL4() + "." + methodName, self, args, kwargs);
	}
}
