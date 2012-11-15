/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

/**
 * An AST node that has a location (i.e. is the top level AST in a tag)
 */
public abstract class LocationAST extends AST
{
	/**
	 * The source code location where this node appears in.
	 */
	protected Location location = null;

	/**
	 * Create a new {@code AST} object.
	 * @param location The source code location where this node appears in.
	 */
	public LocationAST(Location location)
	{
		super();
		this.location = location;
	}

	/**
	 * {@code decoratedEvaluate} wraps a call to {@link evaluate} with exception
	 * handling. {@link evaluate} should not be called directly. Instead
	 * {@code decoratedEvaluate} should be used. When an exception bubbles up
	 * the call stack, {@code decoratedEvaluate} creates a exception chain
	 * containing information about the location of the exception.
	 */
	public Object decoratedEvaluate(EvaluationContext context) throws IOException
	{
		try
		{
			return evaluate(context);
		}
		catch (BreakException ex)
		{
			throw ex;
		}
		catch (ContinueException ex)
		{
			throw ex;
		}
		catch (LocationException ex)
		{
			if (ex.location != location)
				throw new LocationException(ex, location);
			else
				throw ex;
		}
		catch (Exception ex)
		{
			throw new LocationException(ex, location);
		}
	}

	/**
	 * Return the source code location where this node appears in.
	 */
	public Location getLocation()
	{
		return location;
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(location);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		location = (Location)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("location", new ValueMaker(){public Object getValue(Object object){return ((LocationAST)object).getLocation();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
