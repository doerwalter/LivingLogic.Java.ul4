/*
** Copyright 2021-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;


public class Str extends AbstractType
{
	public static final Str type = new Str();

	@Override
	public String getNameUL4()
	{
		return "str";
	}

	@Override
	public String getDoc()
	{
		return "A string";
	}

	private static final Signature signature = new Signature().addPositionalOnly("obj", "");

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	@Override
	public Object create(EvaluationContext context, BoundArguments arguments)
	{
		return call(context, arguments.get(0));
	}

	public static String call(EvaluationContext context, Object object)
	{
		UL4Type type = UL4Type.getType(object);
		return type.strInstance(context, object);
	}

	@Override
	public boolean instanceCheck(Object object)
	{
		return object instanceof String;
	}

	@Override
	public boolean boolInstance(EvaluationContext context, Object instance)
	{
		return !((String)instance).isEmpty();
	}

	@Override
	public Number intInstance(EvaluationContext context, Object instance)
	{
		try
		{
			return Integer.valueOf((String)instance);
		}
		catch (NumberFormatException ex1)
		{
			try
			{
				return Long.valueOf((String)instance);
			}
			catch (NumberFormatException ex2)
			{
				return new BigInteger((String)instance);
			}
		}
	}

	@Override
	public Number floatInstance(EvaluationContext context, Object instance)
	{
		return Double.valueOf((String)instance);
	}

	@Override
	public int lenInstance(EvaluationContext context, Object instance)
	{
		return ((String)instance).length();
	}

	@Override
	public String strInstance(EvaluationContext context, Object instance)
	{
		return (String)instance;
	}

	private static final Signature signatureSplit = new Signature().addBoth("sep", null).addBoth("maxsplit", null);
	private static final Signature signatureSplitLines = new Signature().addBoth("keepends", false);
	private static final Signature signatureStrip = new Signature().addPositionalOnly("chars", null);
	private static final Signature signatureStartsWith = new Signature().addPositionalOnly("prefix");
	private static final Signature signatureEndsWith = new Signature().addPositionalOnly("suffix");
	private static final Signature signatureReplace = new Signature().addPositionalOnly("old").addPositionalOnly("new").addPositionalOnly("count", -1);
	private static final Signature signatureCountFind = new Signature().addPositionalOnly("sub").addPositionalOnly("start", null).addPositionalOnly("end", null);
	private static final Signature signatureJoin = new Signature().addPositionalOnly("iterable");
	private static final BuiltinMethodDescriptor methodSplit = new BuiltinMethodDescriptor(type, "split", signatureSplit);
	private static final BuiltinMethodDescriptor methodRSplit = new BuiltinMethodDescriptor(type, "rsplit", signatureSplit);
	private static final BuiltinMethodDescriptor methodSplitLines = new BuiltinMethodDescriptor(type, "splitlines", signatureSplitLines);
	private static final BuiltinMethodDescriptor methodStrip = new BuiltinMethodDescriptor(type, "strip", signatureStrip);
	private static final BuiltinMethodDescriptor methodLStrip = new BuiltinMethodDescriptor(type, "lstrip", signatureStrip);
	private static final BuiltinMethodDescriptor methodRStrip = new BuiltinMethodDescriptor(type, "rstrip", signatureStrip);
	private static final BuiltinMethodDescriptor methodUpper = new BuiltinMethodDescriptor(type, "upper", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodLower = new BuiltinMethodDescriptor(type, "lower", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodCapitalize = new BuiltinMethodDescriptor(type, "capitalize", Signature.noParameters);
	private static final BuiltinMethodDescriptor methodStartsWith = new BuiltinMethodDescriptor(type, "startswith", signatureStartsWith);
	private static final BuiltinMethodDescriptor methodEndsWith = new BuiltinMethodDescriptor(type, "endswith", signatureEndsWith);
	private static final BuiltinMethodDescriptor methodReplace = new BuiltinMethodDescriptor(type, "replace", signatureReplace);
	private static final BuiltinMethodDescriptor methodCount = new BuiltinMethodDescriptor(type, "count", signatureCountFind);
	private static final BuiltinMethodDescriptor methodFind = new BuiltinMethodDescriptor(type, "find", signatureCountFind);
	private static final BuiltinMethodDescriptor methodRFind = new BuiltinMethodDescriptor(type, "rfind", signatureCountFind);
	private static final BuiltinMethodDescriptor methodJoin = new BuiltinMethodDescriptor(type, "join", signatureJoin);

	public static List<String> split(String instance)
	{
		return Utils.array2List(StringUtils.split(instance));
	}

	public static List<String> split(String instance, String separator)
	{
		if (separator == null)
			return split(instance);
		return Utils.array2List(StringUtils.splitByWholeSeparatorPreserveAllTokens(instance, separator));
	}

	public static List<String> split(String instance, String separator, int maxsplit)
	{
		if (separator == null)
			return Utils.array2List(StringUtils.splitByWholeSeparator(instance, null, maxsplit+1));
		return Utils.array2List(StringUtils.splitByWholeSeparatorPreserveAllTokens(instance, separator, maxsplit+1));
	}

	public static List<String> split(String instance, BoundArguments args)
	{
		Object separator = args.get(0);
		Object maxsplit = args.get(1);

		if (separator == null || separator instanceof String)
		{
			if (maxsplit == null)
				return split(instance, (String)separator);
			else
				return split(instance, (String)separator, Utils.toInt(maxsplit));
		}
		throw new ArgumentTypeMismatchException("{!t}.split({!t}, {!t}) not supported", instance, separator, maxsplit);
	}

	public static List<String> rsplit(String instance)
	{
		return Utils.array2List(StringUtils.split(instance));
	}

	public static List<String> rsplit(String instance, int maxsplit)
	{
		ArrayList<String> result = new ArrayList<String>();
		int start, end;
		start = end = instance.length() - 1;
		while (maxsplit-- > 0)
		{
			while (start >= 0 && Character.isWhitespace(instance.charAt(start)))
				--start;
			if (start < 0)
				break;
			end = start--;
			while (start >= 0 && !Character.isWhitespace(instance.charAt(start)))
				--start;
			if (start != end)
				result.add(0, instance.substring(start+1, end+1));
		}
		if (start >= 0)
		{
			while (start >= 0 && Character.isWhitespace(instance.charAt(start)))
				--start;
			if (start >= 0)
				result.add(0, instance.substring(0, start+1));
		}
		return result;
	}

	public static List<String> rsplit(String instance, Object separator)
	{
		if (separator == null)
			return rsplit(instance);
		else if (separator instanceof String)
			return rsplit(instance, (String)separator, 0x7fffffff);
		throw new ArgumentTypeMismatchException("{!t}.rsplit({!t}) not supported", instance, separator);
	}

	public static List<String> rsplit(String instance, String separator, int maxsplit)
	{
		if (separator.length() == 0)
			throw new UnsupportedOperationException("empty separator not supported");

		ArrayList<String> result = new ArrayList<String>();
		int start = instance.length(), end = start, seplen = separator.length();
		while (maxsplit-- > 0)
		{
			start = instance.lastIndexOf(separator, end-seplen);
			if (start < 0)
				break;
			result.add(0, instance.substring(start+seplen, end));
			end = start;
		}
		result.add(0, instance.substring(0, end));
		return result;
	}

	public static Object rsplit(String instance, BoundArguments args)
	{
		Object separator = args.get(0);
		Object maxsplit = args.get(1);

		if (separator == null)
		{
			if (maxsplit == null)
				return rsplit(instance);
			else
				return rsplit(instance, Utils.toInt(maxsplit));
		}
		else if (separator instanceof String)
		{
			if (maxsplit == null)
				return rsplit(instance, (String)separator);
			else
				return rsplit(instance, (String)separator, Utils.toInt(maxsplit));
		}
		throw new ArgumentTypeMismatchException("{!t}.rsplit({!t}, {!t}) not supported", instance, separator, maxsplit);
	}

	public static List<String> splitlines(String instance)
	{
		return splitlines(instance, false);
	}

	private static int lookingAtLineEnd(String instance, int pos)
	{
		char c = instance.charAt(pos);
		if (c == '\n' || c == '\u000B' || c == '\u000C' || c == '\u001C' || c == '\u001D' || c == '\u001E' || c == '\u0085' || c == '\u2028' || c == '\u2029')
			return 1;
		else if (c == '\r')
		{
			if (pos == instance.length()-1)
				return 1;
			else if (instance.charAt(pos+1) == '\n')
				return 2;
			else
				return 1;
		}
		return 0;
	}

	public static List<String> splitlines(String instance, boolean keepEnds)
	{
		List<String> result = new ArrayList<String>();
		int length = instance.length();

		for (int pos = 0, startPos = 0;;)
		{
			if (pos >= length)
			{
				if (startPos != pos)
					result.add(instance.substring(startPos));
				return result;
			}
			int lineEndLen = lookingAtLineEnd(instance, pos);
			if (lineEndLen == 0)
				++pos;
			else
			{
				int endPos = pos + (keepEnds ? lineEndLen : 0);
				result.add(instance.substring(startPos, endPos));
				pos += lineEndLen;
				startPos = pos;
			}
		}
	}

	public static Object splitlines(EvaluationContext context, String instance, BoundArguments args)
	{
		boolean keepEnds = Bool.call(context, args.get(0));
		return splitlines(instance, keepEnds);
	}

	public static String strip(String instance)
	{
		return StringUtils.strip(instance);
	}

	public static String strip(String instance, String chars)
	{
		return StringUtils.strip(instance, chars);
	}

	public static Object strip(String instance, BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg == null)
			return strip(instance);
		else if (arg instanceof String)
			return strip(instance, (String)arg);
		throw new ArgumentTypeMismatchException("{!t}.strip({!t}) not supported", instance, arg);
	}

	public static String lstrip(String instance)
	{
		return StringUtils.stripStart(instance, null);
	}

	public static String lstrip(String instance, String chars)
	{
		return StringUtils.stripStart(instance, chars);
	}

	public static Object lstrip(String instance, BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg == null)
			return lstrip(instance);
		else if (arg instanceof String)
			return lstrip(instance, (String)arg);
		throw new ArgumentTypeMismatchException("{!t}.lstrip({!t}) not supported", instance, arg);
	}

	public static String rstrip(String instance)
	{
		return StringUtils.stripEnd(instance, null);
	}

	public static String rstrip(String instance, String chars)
	{
		return StringUtils.stripEnd(instance, chars);
	}

	public static Object rstrip(String instance, BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg == null)
			return rstrip(instance);
		else if (arg instanceof String)
			return rstrip(instance, (String)arg);
		throw new ArgumentTypeMismatchException("{!t}.rstrip({!t}) not supported", instance, arg);
	}

	public static String lower(String instance)
	{
		return instance.toLowerCase();
	}

	public static String lower(String instance, BoundArguments args)
	{
		return lower(instance);
	}

	public static String upper(String instance)
	{
		return instance.toUpperCase();
	}

	public static String upper(String instance, BoundArguments args)
	{
		return upper(instance);
	}


	public static String capitalize(String instance)
	{
		return String.valueOf(Character.toTitleCase(instance.charAt(0))) + instance.substring(1).toLowerCase();
	}

	public static String capitalize(String instance, BoundArguments args)
	{
		return capitalize(instance);
	}

	public static boolean startswith(String instance, String prefix)
	{
		return instance.startsWith(prefix);
	}

	public static boolean startswith(String instance, Collection<String> prefixes)
	{
		for (String prefix : prefixes)
		{
			if (instance.startsWith(prefix))
				return true;
		}
		return false;
	}

	public static boolean startswith(String instance, String[] prefixes)
	{
		for (String prefix : prefixes)
		{
			if (instance.startsWith(prefix))
				return true;
		}
		return false;
	}

	public static boolean startswith(String instance, Map<String, ?> prefixes)
	{
		for (String prefix : prefixes.keySet())
		{
			if (instance.startsWith(prefix))
				return true;
		}
		return false;
	}

	public static boolean startswith(String instance, BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg instanceof String)
			return startswith(instance, (String)arg);
		else if (arg instanceof Collection)
			return startswith(instance, (Collection<String>)arg);
		else if (arg instanceof String[])
			return startswith(instance, (String[])arg);
		else if (arg instanceof Map)
			return startswith(instance, (Map<String, ?>)arg);
		throw new ArgumentTypeMismatchException("{!t}.startswith({!t}) not supported", instance, arg);
	}


	public static boolean endswith(String instance, String suffix)
	{
		return instance.endsWith(suffix);
	}

	public static boolean endswith(String instance, Collection<String> suffixes)
	{
		for (String suffix : suffixes)
		{
			if (instance.endsWith(suffix))
				return true;
		}
		return false;
	}

	public static boolean endswith(String instance, String[] suffixes)
	{
		for (String suffix : suffixes)
		{
			if (instance.endsWith(suffix))
				return true;
		}
		return false;
	}

	public static boolean endswith(String instance, Map<String, ?> suffixes)
	{
		for (String suffix : suffixes.keySet())
		{
			if (instance.endsWith(suffix))
				return true;
		}
		return false;
	}

	public static boolean endswith(String instance, BoundArguments args)
	{
		Object arg = args.get(0);

		if (arg instanceof String)
			return endswith(instance, (String)arg);
		else if (arg instanceof Collection)
			return endswith(instance, (Collection<String>)arg);
		else if (arg instanceof String[])
			return endswith(instance, (String[])arg);
		else if (arg instanceof Map)
			return endswith(instance, (Map<String, ?>)arg);
		throw new ArgumentTypeMismatchException("{!t}.endswith({!t}) not supported", instance, arg);
	}

	public static String replace(String instance, String search, String replace)
	{
		return instance.replace(search, replace);
	}

	public static String replace(String instance, String search, String replace, int count)
	{
		if (count == -1)
			return instance.replace(search, replace);
		return StringUtils.replace(instance, search, replace, count);
	}

	public static String replace(String instance, BoundArguments args)
	{
		String oldString = args.getString(0);
		String newString = args.getString(1);
		int count = args.getInt(2, -1);

		return replace(instance, oldString, newString, count);
	}

	public static int count(String instance, String sub)
	{
		return count(instance, sub, 0, instance.length());
	}

	public static int count(String instance, String sub, int start)
	{
		return count(instance, sub, start, instance.length());
	}

	public static int count(String instance, String sub, int start, int end)
	{
		int length = instance.length();
		if (start < 0)
			start += length;
		if (end < 0)
			end += length;

		if (sub.length() == 0)
		{
			if (end < 0 || start > length || start > end)
				return 0;
			int result = end - start + 1;
			if (result > length + 1)
				result = length + 1;
			return result;
		}

		start = Utils.getSliceStartPos(length, start);
		end = Utils.getSliceEndPos(length, end);

		int count = 0;
		int lastIndex = start;

		for (;;)
		{
			lastIndex = instance.indexOf(sub, lastIndex);
			if (lastIndex == -1)
				break;
			if (lastIndex + sub.length() > end)
				break;
			++count;
			lastIndex += sub.length();
		}
		return count;
	}

	public static int count(String instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.length());
		return count(instance, args.getString(0), startIndex, endIndex);
	}

	public static int find(String instance, String sub)
	{
		return instance.indexOf(sub);
	}

	public static int find(String instance, String sub, int start)
	{
		start = Utils.getSliceStartPos(instance.length(), start);
		return instance.indexOf(sub, start);
	}

	public static int find(String instance, String sub, int start, int end)
	{
		start = Utils.getSliceStartPos(instance.length(), start);
		end = Utils.getSliceEndPos(instance.length(), end);
		int result = instance.indexOf(sub, start);
		if (result + sub.length() > end)
			return -1;
		return result;
	}

	public static int find(String instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.length());
		return find(instance, args.getString(0), startIndex, endIndex);
	}

	public static int rfind(String instance, String sub)
	{
		return instance.lastIndexOf(sub);
	}

	public static int rfind(String instance, String sub, int start)
	{
		start = Utils.getSliceStartPos(instance.length(), start);
		int result = instance.lastIndexOf(sub);
		if (result < start)
			return -1;
		return result;
	}

	public static int rfind(String instance, String sub, int start, int end)
	{
		start = Utils.getSliceStartPos(instance.length(), start);
		end = Utils.getSliceStartPos(instance.length(), end);
		end -= sub.length();
		if (end < 0)
			return -1;
		int result = instance.lastIndexOf(sub, end);
		if (result < start)
			return -1;
		return result;
	}

	public static int rfind(String instance, BoundArguments args)
	{
		int startIndex = args.getInt(1, 0);
		int endIndex = args.getInt(2, instance.length());
		return rfind(instance, args.getString(0), startIndex, endIndex);
	}



	public static String join(String instance, Iterator iterator)
	{
		StringBuilder buffer = new StringBuilder();

		boolean first = true;
		while (iterator.hasNext())
		{
			if (!first)
				buffer.append(instance);
			buffer.append((String)iterator.next());
			first = false;
		}
		return buffer.toString();
	}

	public static String join(String instance, Object iterable)
	{
		return join(instance, Utils.iterator(iterable));
	}

	public static String join(String instance, BoundArguments args)
	{
		return join(instance, args.get(0));
	}

	protected static Set<String> attributes = Set.of(
		"split",
		"rsplit",
		"splitlines",
		"strip",
		"lstrip",
		"rstrip",
		"upper",
		"lower",
		"capitalize",
		"startswith",
		"endswith",
		"replace",
		"count",
		"find",
		"rfind",
		"join"
	);

	@Override
	public Set<String> dirInstance(EvaluationContext context, Object instance)
	{
		return attributes;
	}

	@Override
	public Object getAttr(EvaluationContext context, Object instance, String key)
	{
		String string = (String)instance;

		switch (key)
		{
			case "split":
				return methodSplit.bindMethod(string);
			case "rsplit":
				return methodRSplit.bindMethod(string);
			case "splitlines":
				return methodSplitLines.bindMethod(string);
			case "strip":
				return methodStrip.bindMethod(string);
			case "lstrip":
				return methodLStrip.bindMethod(string);
			case "rstrip":
				return methodRStrip.bindMethod(string);
			case "upper":
				return methodUpper.bindMethod(string);
			case "lower":
				return methodLower.bindMethod(string);
			case "capitalize":
				return methodCapitalize.bindMethod(string);
			case "startswith":
				return methodStartsWith.bindMethod(string);
			case "endswith":
				return methodEndsWith.bindMethod(string);
			case "replace":
				return methodReplace.bindMethod(string);
			case "count":
				return methodCount.bindMethod(string);
			case "find":
				return methodFind.bindMethod(string);
			case "rfind":
				return methodRFind.bindMethod(string);
			case "join":
				return methodJoin.bindMethod(string);
			default:
				return super.getAttr(context, instance, key);
		}
	}

	@Override
	public Object callAttr(EvaluationContext context, Object instance, String key, List<Object> args, Map<String, Object> kwargs)
	{
		String string = (String)instance;

		switch (key)
		{
			case "split":
				return split(string, methodSplit.bindArguments(args, kwargs));
			case "rsplit":
				return rsplit(string, methodRSplit.bindArguments(args, kwargs));
			case "splitlines":
				return splitlines(context, string, methodSplitLines.bindArguments(args, kwargs));
			case "strip":
				return strip(string, methodStrip.bindArguments(args, kwargs));
			case "lstrip":
				return lstrip(string, methodLStrip.bindArguments(args, kwargs));
			case "rstrip":
				return rstrip(string, methodRStrip.bindArguments(args, kwargs));
			case "upper":
				return upper(string, methodUpper.bindArguments(args, kwargs));
			case "lower":
				return lower(string, methodLower.bindArguments(args, kwargs));
			case "capitalize":
				return capitalize(string, methodCapitalize.bindArguments(args, kwargs));
			case "startswith":
				return startswith(string, methodStartsWith.bindArguments(args, kwargs));
			case "endswith":
				return endswith(string, methodEndsWith.bindArguments(args, kwargs));
			case "replace":
				return replace(string, methodReplace.bindArguments(args, kwargs));
			case "count":
				return count(string, methodCount.bindArguments(args, kwargs));
			case "find":
				return find(string, methodFind.bindArguments(args, kwargs));
			case "rfind":
				return rfind(string, methodRFind.bindArguments(args, kwargs));
			case "join":
				return join(string, methodJoin.bindArguments(args, kwargs));
			default:
				return super.callAttr(context, instance, key, args, kwargs);
		}
	}
}
