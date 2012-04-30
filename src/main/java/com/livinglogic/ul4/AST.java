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

public abstract class AST extends ObjectAsMap implements UL4ONSerializable
{
	protected Location location = null;

	public AST(Location location)
	{
		this.location = location;
	}

	abstract public Object evaluate(EvaluationContext context) throws IOException;

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

	abstract public String getType();

	public Location getLocation()
	{
		return location;
	}

	public String toString()
	{
		return toString(0);
	}

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
