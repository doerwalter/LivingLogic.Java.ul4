/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import com.livinglogic.ul4on.Utils;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.utils.ObjectAsMap;

/**
 * The base class of all nodes in the abstract syntax tree.
 */
public abstract class AST extends ObjectAsMap implements UL4ONSerializable
{
	/**
	 * The source code location where this node appears in.
	 */
	protected Location location = null;

	/**
	 * Create a new {@code AST} object.
	 * @param location The source code location where this node appears in.
	 */
	public AST(Location location)
	{
		this.location = location;
	}

	/**
	 * Evaluate this node and return the resulting object.
	 *
	 * Evaluating the node might also have several side effects besides the
	 * method return value: It might write to the output stream that is stored
	 * in the {@code context} object (as the {@link Print} and {@link PrintX}
	 * nodes do) and it might modify the variables map stored in the context
	 * (like {@link StoreVar}, {@link DelVar}, {@link AddVar} etc. do)
	 * 
	 * @param context The context object in which this node has to be evaluated.
	 * @return The result of evaluating the node.
	 */
	abstract public Object evaluate(EvaluationContext context) throws IOException;

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
			if (ex.location != location && location != null)
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
	 * Return a unique name for this type of AST node.
	 */
	abstract public String getType();

	/**
	 * Return the source code location where this node appears in.
	 */
	public Location getLocation()
	{
		return location;
	}

	public String toString()
	{
		return toString(0);
	}

	/**
	 * This is an extension of the normal {@code toString} method: Returns
	 * nicely formatted sourcecode for this node formatted for indentation level
	 * {@code indent}.
	 * @param indent the indentation level
	 * @return The formatted sourcecode
	 */
	abstract public String toString(int indent);

	public String getUL4ONName()
	{
		return "de.livinglogic.ul4." + getType();
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(location);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		location = (Location)decoder.load();
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>();
			v.put("type", new ValueMaker(){public Object getValue(Object object){return ((AST)object).getType();}});
			v.put("location", new ValueMaker(){public Object getValue(Object object){return ((AST)object).getLocation();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
