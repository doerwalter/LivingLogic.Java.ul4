/*
** Copyright 2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;

import static com.livinglogic.utils.SetUtils.makeSet;


public class Dict extends AbstractType
{
	public static final UL4Type type = new Dict();

	@Override
	public String getNameUL4()
	{
		return "dict";
	}

	@Override
	public String getDoc()
	{
		return "An object that maps keys to values.";
	}

	private static final Signature signature = new Signature().addVarPositional("args").addVarKeyword("kwargs");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(EvaluationContext context, BoundArguments arguments)
	{
		List<Object> args = (List<Object>)arguments.get(0);
		Map<String, Object> kwargs = (Map<String, Object>)arguments.get(1);

		Map<String, Object> result = new LinkedHashMap<String, Object>();

		update(context, result, args, kwargs);
		return result;
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof Map;
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		return !((Map)instance).isEmpty();
	}

	@Override
	public int lenInstance(EvaluationContext context, Object instance)
	{
		return ((Map)instance).size();
	}

	private static final Signature signatureGet = new Signature().addPositionalOnly("key").addPositionalOnly("default", null);
	private static final Signature signatureUpdate = new Signature().addVarPositional("others").addVarKeyword("kwargs");
	private static final Signature signaturePop = new Signature().addPositionalOnly("key").addPositionalOnly("default", Signature.noValue);
	private static final BuiltinMethodDescriptor methodItems = new BuiltinMethodDescriptor(type, "items", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodValues = new BuiltinMethodDescriptor(type, "values", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodGet = new BuiltinMethodDescriptor(type, "get", signatureGet);
	private static final BuiltinMethodDescriptor methodUpdate = new BuiltinMethodDescriptor(type, "update", signatureUpdate);
	private static final BuiltinMethodDescriptor methodClear = new BuiltinMethodDescriptor(type, "clear", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodPop = new BuiltinMethodDescriptor(type, "pop", signaturePop);

	public static Iterator<Vector> items(EvaluationContext context, Map instance)
	{
		return new MapItemIterator(instance);
	}

	public static Iterator<Vector> items(EvaluationContext context, Map instance, BoundArguments args)
	{
		return items(context, instance);
	}

	private static class MapItemIterator implements Iterator<Vector>
	{
		Iterator iterator;

		public MapItemIterator(Map map)
		{
			iterator = map.entrySet().iterator();
		}

		@Override
		public boolean hasNext()
		{
			return iterator.hasNext();
		}

		@Override
		public Vector next()
		{
			Vector retVal = new Vector(2);
			Map.Entry entry = (Map.Entry)iterator.next();
			retVal.add(entry.getKey());
			retVal.add(entry.getValue());
			return retVal;
		}

		@Override
		public void remove()
		{
			iterator.remove();
		}
	}

	public static Collection<Object> values(EvaluationContext context, Map instance)
	{
		return instance.values();
	}

	public Collection<Object> values(EvaluationContext context, Map instance, BoundArguments args)
	{
		return values(context, instance);
	}

	public static Object get(EvaluationContext context, Map instance, Object key, Object defaultValue)
	{
		Object result = instance.get(key);
		if (result == null && !instance.containsKey(key))
			return defaultValue;
		return result;
	}

	public static Object get(EvaluationContext context, Map instance, BoundArguments args)
	{
		return get(context, instance, args.get(0), args.get(1));
	}

	public static void update(EvaluationContext context, Map instance, List<Object> others, Map<String, Object> kwargs)
	{
		String exceptionMessage = "positional arguments for update() method must be dicts or lists of (key, value) pairs";
		for (Object other : others)
		{
			if (other instanceof Map)
				instance.putAll((Map)other);
			else if (other instanceof List)
			{
				for (Object item : (List)other)
				{
					if (item instanceof List && ((List)item).size()==2)
						instance.put(((List)item).get(0), ((List)item).get(1));
					else
						throw new ArgumentException(exceptionMessage);
				}
			}
			else
				throw new ArgumentException(exceptionMessage);
		}
		instance.putAll(kwargs);
	}

	public static Object update(EvaluationContext context, Map instance, BoundArguments args)
	{
		update(context, instance, (List<Object>)args.get(0), (Map<String, Object>)args.get(1));
		return null;
	}

	public static void clear(Map instance)
	{
		instance.clear();
	}

	public static Object clear(EvaluationContext context, Map instance, BoundArguments args)
	{
		clear(instance);
		return null;
	}

	public static Object pop(EvaluationContext context, Map instance, Object key)
	{
		Object value = instance.get(key);
		if (value == null && !instance.containsKey(key))
			throw new KeyException(key);
		instance.remove(key);
		return value;
	}

	public static Object pop(EvaluationContext context, Map instance, Object key, Object defaultValue)
	{
		Object value = instance.get(key);
		if (value == null && !instance.containsKey(key))
			return defaultValue;
		instance.remove(key);
		return value;
	}

	public static Object pop(EvaluationContext context, Map instance, BoundArguments args)
	{
		if (args.get(1) == Signature.noValue)
			return pop(context, instance, args.get(0));
		else
			return pop(context, instance, args.get(0), args.get(1));
	}

	protected static Set<String> attributes = makeSet("items", "values", "get", "update", "clear", "pop");

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object instance, String key)
	{
		// If {@code instance} implements {@link UL4GetAttr} prefer that
		if (instance instanceof UL4GetAttr)
			return ((UL4GetAttr)instance).getAttrUL4(context, key);

		switch (key)
		{
			case "items":
				return methodItems.bindMethod(instance);
			case "values":
				return methodValues.bindMethod(instance);
			case "get":
				return methodGet.bindMethod(instance);
			case "update":
				return methodUpdate.bindMethod(instance);
			case "clear":
				return methodClear.bindMethod(instance);
			case "pop":
				return methodPop.bindMethod(instance);
			default:
				Map map = (Map)instance;
				Object result = map.get(key);

				if ((result == null) && !map.containsKey(key))
					return super.getAttr(context, map, key);
				return result;
		}
	}

	@Override
	public Object callAttr(EvaluationContext context, Object instance, String key, List<Object> args, Map<String, Object> kwargs)
	{
		// If {@code instance} implements {@link UL4GetAttr} prefer that
		if (instance instanceof UL4GetAttr)
			return ((UL4GetAttr)instance).callAttrUL4(context, key, args, kwargs);

		Map map = (Map)instance;

		switch (key)
		{
			case "items":
				return items(context, map, methodItems.bindArguments(args, kwargs));
			case "values":
				return values(context, map, methodValues.bindArguments(args, kwargs));
			case "get":
				return get(context, map, methodGet.bindArguments(args, kwargs));
			case "update":
				return update(context, map, methodUpdate.bindArguments(args, kwargs));
			case "clear":
				return clear(context, map, methodClear.bindArguments(args, kwargs));
			case "pop":
				return pop(context, map, methodPop.bindArguments(args, kwargs));
			default:
				return super.callAttr(context, map, key, args, kwargs);
		}
	}

	@Override
	public void setAttr(EvaluationContext context, Object object, String key, Object value)
	{
		// If {@code object} implements {@link UL4SetAttr} prefer that
		if (object instanceof UL4SetAttr)
			((UL4SetAttr)object).setAttrUL4(context, key, value);
		else
			((Map)object).put(key, value);
	}
}
