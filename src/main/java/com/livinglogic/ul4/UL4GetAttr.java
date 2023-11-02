/*
** Copyright 2012-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Map;


/**
<p>Implementing the {@code UL4GetAttr} interface allows to fetch the UL4
accessible attributes of an object.</p>

<p>These attributes can either be normal "data attributes" or they can be
methods. To implement a method define a subclass of {@link BoundMethod} and
return an instance of it in {@link #getAttrUL4(EvaluationContext, String)} and/or
{@link #getAttrUL4(EvaluationContext, String)}.</p>
**/
public interface UL4GetAttr
{
	/**
	<p>Return the attribute named {@code key} of this object to UL4.</p>

	<p>The default implementation returns an {@link UndefinedAttribute} for
	all attributes.</p>

	@param context The evaluation context.
	@param key The name of the requested attribute.
	@return the value of the requested attribute or an appropriate
	        {@link UndefinedAttribute} object if the attribute doesn't exist.
	**/
	default Object getAttrUL4(EvaluationContext context, String key)
	{
		return new UndefinedAttribute(this, key);
	}

	/**
	<p>Call the attribute (i.e. method) named {@code key} of this object with
	the positional arguments {@code args} and the keyword arguments
	{@code kwargs} and return the result to UL4.</p>

	<p>This default implementation simply defers to
	{@link #getAttrUL4(EvaluationContext, String)} to get the object (which will
	probable return a {@link BoundMethod} or {@link GenericBoundMethod} object)
	and then call this bound method object.</p>

	<p>However this default implementation can be overwritten to implement the
	functionality of the method directly which skips creating a bound method
	object.</p>

	<p>However note that {@link #getAttrUL4(EvaluationContext, String)} must
	still return a bound method object for the method name for the case where
	the bound method isn't called directly.</p>

	@param context The evaluation context.
	@param key The name of the method to call.
	@param args Position arguments for the method call.
	@param kwargs Keyword arguments for the method call.
	@return the result of the method call.
	**/
	default Object callAttrUL4(EvaluationContext context, String key, List<Object> args, Map<String, Object> kwargs)
	{
		Object attribute;

		try
		{
			attribute = getAttrUL4(context, key);
		}
		catch (AttributeException exc)
		{
			if (exc.getObject() == this)
				attribute = new UndefinedAttribute(this, key);
			else
				// The {@code AttributeException} originated from another object
				throw exc;
		}
		return CallAST.call(context, attribute, args, kwargs);
	}

	/**
	<p>Render the attribute (i.e. method) named {@code key} of this object with
	the positional arguments {@code args} and the keyword arguments
	{@code kwargs}.</p>

	<p>This default implementation simply defers to
	{@link #getAttrUL4(EvaluationContext, String)} to get the object (which will
	probable return a {@link BoundMethod} or {@link GenericBoundMethod} object)
	and then render this bound method object.</p>

	<p>However this default implementation can be overwritten to implement the
	functionality of the method directly which skips creating a bound method
	object.</p>

	<p>However note that {@link #getAttrUL4(EvaluationContext, String)} must
	still return a bound method object for the method name for the case where
	the bound method isn't rendered directly.</p>

	@param context The evaluation context.
	@param key The name of the method to render.
	@param args Position arguments for the method call.
	@param kwargs Keyword arguments for the method call.
	**/
	default void renderAttrUL4(EvaluationContext context, String key, List<Object> args, Map<String, Object> kwargs)
	{
		Object attribute;

		try
		{
			attribute = getAttrUL4(context, key);
		}
		catch (AttributeException exc)
		{
			if (exc.getObject() == this)
				attribute = new UndefinedAttribute(this, key);
			else
				// The {@code AttributeException} originated from another object
				throw exc;
		}
		if (attribute instanceof UL4Render)
			((UL4Render)attribute).renderUL4(context, args, kwargs);
		else
			throw new NotRenderableException(attribute);
	}
}
