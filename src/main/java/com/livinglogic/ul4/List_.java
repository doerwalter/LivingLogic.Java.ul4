/*
** Copyright 2021-2023 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.Collections;
import static java.util.Arrays.asList;


public class List_ extends AbstractType
{
	public static final UL4Type type = new List_();

	@Override
	public String getNameUL4()
	{
		return "list";
	}

	@Override
	public String getDoc()
	{
		return "A ordered collection of objects.";
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof List || object instanceof Object[];
	}

	private static final Signature signature = new Signature().addPositionalOnly("iterable", Collections.EMPTY_LIST);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(EvaluationContext context, BoundArguments args)
	{
		return call(context, args.get(0));
	}

	public static ArrayList call(EvaluationContext context, String obj)
	{
		ArrayList result;
		int length = obj.length();
		result = new ArrayList(obj.length());
		for (int i = 0; i < length; i++)
		{
			result.add(String.valueOf(obj.charAt(i)));
		}
		return result;
	}

	public static ArrayList call(EvaluationContext context, Collection obj)
	{
		return new ArrayList(obj);
	}

	public static ArrayList call(EvaluationContext context, Object[] obj)
	{
		return new ArrayList(asList(obj));
	}

	public static ArrayList call(EvaluationContext context, Map obj)
	{
		return new ArrayList(obj.keySet());
	}

	public static ArrayList call(EvaluationContext context, Iterable obj)
	{
		return call(context, obj.iterator());
	}

	public static ArrayList call(EvaluationContext context, Iterator obj)
	{
		ArrayList retVal = new ArrayList();
		while (obj.hasNext())
			retVal.add(obj.next());
		return retVal;
	}

	public static ArrayList call(EvaluationContext context, Object obj)
	{
		if (obj instanceof String)
			return call(context, (String)obj);
		else if (obj instanceof Collection)
			return call(context, (Collection)obj);
		else if (obj instanceof Object[])
			return call(context, (Object[])obj);
		else if (obj instanceof Map)
			return call(context, (Map)obj);
		else if (obj instanceof Iterable)
			return call(context, (Iterable)obj);
		else if (obj instanceof Iterator)
			return call(context, (Iterator)obj);
		throw new ArgumentTypeMismatchException("list({!t}) not supported", obj);
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		if (instance instanceof List)
			return !((List)instance).isEmpty();
		else
			return ((Object[])instance).length != 0;
	}

	@Override
	public int lenInstance(EvaluationContext context, Object instance)
	{
		if (instance instanceof List)
			return ((List)instance).size();
		else
			return ((Object[])instance).length;
	}

	private static final Signature signatureAppend = new Signature().addVarPositional("items");
	private static final Signature signatureInsert = new Signature().addPositionalOnly("pos").addVarPositional("items");
	private static final Signature signaturePop = new Signature().addPositionalOnly("pos", -1);
	private static final Signature signatureCount = new Signature().addPositionalOnly("sub").addPositionalOnly("start", null).addPositionalOnly("end", null);
	private static final Signature signatureFind = new Signature().addPositionalOnly("sub").addPositionalOnly("start", null).addPositionalOnly("end", null);
	private static final BuiltinMethodDescriptor methodItems = new BuiltinMethodDescriptor(type, "items", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodAppend = new BuiltinMethodDescriptor(type, "append", signatureAppend);
	private static final BuiltinMethodDescriptor methodInsert = new BuiltinMethodDescriptor(type, "insert", signatureInsert);
	private static final BuiltinMethodDescriptor methodPop = new BuiltinMethodDescriptor(type, "pop", signaturePop);
	private static final BuiltinMethodDescriptor methodCount = new BuiltinMethodDescriptor(type, "count", signatureCount);
	private static final BuiltinMethodDescriptor methodFind = new BuiltinMethodDescriptor(type, "find", signatureFind);
	private static final BuiltinMethodDescriptor methodRFind = new BuiltinMethodDescriptor(type, "rfind", signatureFind);

	public static Object append(EvaluationContext context, Object[] instance, BoundArguments args)
	{
		throw new ArgumentTypeMismatchException("{!t}.append(...) not supported!", instance);
	}

	public static void append(EvaluationContext context, List instance, List<Object> items)
	{
		instance.addAll(items);
	}

	public static Object append(EvaluationContext context, List instance, BoundArguments args)
	{
		append(context, instance, (List<Object>)args.get(0));
		return null;
	}

	public static Object append(EvaluationContext context, Object instance, BoundArguments args)
	{
		if (instance instanceof List)
			return append(context, (List)instance, args);
		else
			return append(context, (Object[])instance, args);
	}

	public static Object insert(EvaluationContext context, Object[] instance, BoundArguments args)
	{
		throw new ArgumentTypeMismatchException("{!t}.insert(...) not supported!", instance);
	}

	public static void insert(EvaluationContext context, List instance, int pos, List<Object> items)
	{
		if (pos < 0)
			pos += instance.size();
		instance.addAll(pos, items);
	}

	public static Object insert(EvaluationContext context, List instance, BoundArguments args)
	{
		insert(context, instance, args.getInt(0), (List<Object>)args.get(1));
		return null;
	}

	public static Object insert(EvaluationContext context, Object instance, BoundArguments args)
	{
		if (instance instanceof List)
			return insert(context, (List)instance, args);
		else
			return insert(context, (Object[])instance, args);
	}

	public static Object pop(EvaluationContext context, Object[] instance, BoundArguments args)
	{
		throw new ArgumentTypeMismatchException("{!t}.pop(...) not supported!", instance);
	}

	public static Object pop(EvaluationContext context, List instance, int pos)
	{
		if (pos < 0)
			pos += instance.size();
		return instance.remove(pos);
	}

	public static Object pop(EvaluationContext context, List instance, BoundArguments args)
	{
		return pop(context, instance, args.getInt(0));
	}

	public static Object pop(EvaluationContext context, Object instance, BoundArguments args)
	{
		if (instance instanceof List)
			return pop(context, (List)instance, args);
		else
			return pop(context, (Object[])instance, args);
	}

	public static int count(EvaluationContext context, Object[] instance, Object sub)
	{
		return count(context, instance, sub, 0, instance.length);
	}

	public static int count(EvaluationContext context, Object[] instance, Object sub, int start)
	{
		return count(context, instance, sub, start, instance.length);
	}

	public static int count(EvaluationContext context, Object[] instance, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(instance.length, start);
		end = Utils.getSliceEndPos(instance.length, end);

		int count = 0;
		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(context, instance[i], sub))
				++count;
		}
		return count;
	}

	public static Object count(EvaluationContext context, Object[] instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.length);

		return count(context, instance, args.get(0), startIndex, endIndex);
	}

	public static int count(EvaluationContext context, List instance, Object sub)
	{
		return count(context, instance, sub, 0, instance.size());
	}

	public static int count(EvaluationContext context, List instance, Object sub, int start)
	{
		return count(context, instance, sub, start, instance.size());
	}

	public static int count(EvaluationContext context, List instance, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(instance.size(), start);
		end = Utils.getSliceEndPos(instance.size(), end);

		int count = 0;
		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(context, instance.get(i), sub))
				++count;
		}
		return count;
	}

	public static Object count(EvaluationContext context, List instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.size());

		return count(context, instance, args.get(0), startIndex, endIndex);
	}

	public static Object count(EvaluationContext context, Object instance, BoundArguments args)
	{
		if (instance instanceof List)
			return count(context, (List)instance, args);
		else
			return count(context, (Object[])instance, args);
	}

	public static int find(EvaluationContext context, Object[] instance, Object sub)
	{
		int start = 0;
		int end = instance.length;

		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(context, instance[i], sub))
				return i;
		}
		return -1;
	}

	public static int find(EvaluationContext context, Object[] instance, Object sub, int start)
	{
		start = Utils.getSliceStartPos(instance.length, start);
		int end = instance.length;

		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(context, instance[i], sub))
				return i;
		}
		return -1;
	}

	public static int find(EvaluationContext context, Object[] instance, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(instance.length, start);
		end = Utils.getSliceEndPos(instance.length, end);

		for (int i = start; i < end; ++i)
		{
			if (EQAST.call(context, instance[i], sub))
				return i;
		}
		return -1;
	}

	public static Object find(EvaluationContext context, Object[] instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.length);

		return find(context, instance, args.get(0), startIndex, endIndex);
	}

	public static int find(EvaluationContext context, List instance, Object sub)
	{
		return instance.indexOf(sub);
	}

	public static int find(EvaluationContext context, List instance, Object sub, int start)
	{
		start = Utils.getSliceStartPos(instance.size(), start);
		if (start != 0)
			instance = instance.subList(start, instance.size());
		int pos = instance.indexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	public static int find(EvaluationContext context, List instance, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(instance.size(), start);
		end = Utils.getSliceEndPos(instance.size(), end);
		if (start != 0 || end != instance.size())
			instance = instance.subList(start, end);
		int pos = instance.indexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	public static Object find(EvaluationContext context, List instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.size());

		return find(context, instance, args.get(0), startIndex, endIndex);
	}

	public static Object find(EvaluationContext context, Object instance, BoundArguments args)
	{
		if (instance instanceof List)
			return find(context, (List)instance, args);
		else
			return find(context, (Object[])instance, args);
	}

	public static int rfind(EvaluationContext context, Object[] instance, Object sub)
	{
		int start = 0;
		int end = instance.length;

		for (int i = end-1; i >= start; --i)
		{
			if (EQAST.call(context, instance[i], sub))
				return i;
		}
		return -1;
	}

	public static int rfind(EvaluationContext context, Object[] instance, Object sub, int start)
	{
		start = Utils.getSliceStartPos(instance.length, start);
		int end = instance.length;

		for (int i = end-1; i >= start; --i)
		{
			if (EQAST.call(context, instance[i], sub))
				return i;
		}
		return -1;
	}

	public static int rfind(EvaluationContext context, Object[] instance, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(instance.length, start);
		end = Utils.getSliceEndPos(instance.length, end);

		for (int i = end-1; i >= start; --i)
		{
			if (EQAST.call(context, instance[i], sub))
				return i;
		}
		return -1;
	}

	public static Object rfind(EvaluationContext context, Object[] instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.length);

		return rfind(context, instance, args.get(0), startIndex, endIndex);
	}

	public static int rfind(EvaluationContext context, List instance, Object sub)
	{
		return instance.lastIndexOf(sub);
	}

	public static int rfind(EvaluationContext context, List instance, Object sub, int start)
	{
		start = Utils.getSliceStartPos(instance.size(), start);
		int result = instance.lastIndexOf(sub);
		if (result < start)
			return -1;
		return result;
	}

	public static int rfind(EvaluationContext context, List instance, Object sub, int start, int end)
	{
		start = Utils.getSliceStartPos(instance.size(), start);
		end = Utils.getSliceEndPos(instance.size(), end);
		instance = instance.subList(start, end);
		int pos = instance.lastIndexOf(sub);
		if (pos != -1)
			pos += start;
		return pos;
	}

	public static Object rfind(EvaluationContext context, List instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.size());

		return rfind(context, instance, args.get(0), startIndex, endIndex);
	}

	public static Object rfind(EvaluationContext context, Object instance, BoundArguments args)
	{
		if (instance instanceof List)
			return rfind(context, (List)instance, args);
		else
			return rfind(context, (Object[])instance, args);
	}

	protected static Set<String> attributes = Set.of("append", "insert", "pop", "count", "find", "rfind");

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return attributes;
	}

	public Object getAttr(EvaluationContext context, Object instance, String key)
	{
		switch (key)
		{
			case "append":
				return methodAppend.bindMethod(instance);
			case "insert":
				return methodInsert.bindMethod(instance);
			case "pop":
				return methodPop.bindMethod(instance);
			case "count":
				return methodCount.bindMethod(instance);
			case "find":
				return methodFind.bindMethod(instance);
			case "rfind":
				return methodRFind.bindMethod(instance);
			default:
				return super.getAttr(context, instance, key);
		}
	}

	public Object callAttr(EvaluationContext context, Object instance, String key, List<Object> args, Map<String, Object> kwargs)
	{
		switch (key)
		{
			case "append":
				return append(context, instance, methodAppend.bindArguments(args, kwargs));
			case "insert":
				return insert(context, instance, methodInsert.bindArguments(args, kwargs));
			case "pop":
				return pop(context, instance, methodPop.bindArguments(args, kwargs));
			case "count":
				return count(context, instance, methodCount.bindArguments(args, kwargs));
			case "find":
				return find(context, instance, methodFind.bindArguments(args, kwargs));
			case "rfind":
				return rfind(context, instance, methodRFind.bindArguments(args, kwargs));
			default:
				return super.callAttr(context, instance, key, args, kwargs);
		}
	}
}
