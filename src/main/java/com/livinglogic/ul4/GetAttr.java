/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class GetAttr extends AST
{
	protected AST obj;
	protected String attrname;

	public GetAttr(Location location, int start, int end, AST obj, String attrname)
	{
		super(location, start, end);
		this.obj = obj;
		this.attrname = attrname;
	}

	public String getType()
	{
		return "getattr";
	}

	public Object evaluate(EvaluationContext context)
	{
		return call(obj.decoratedEvaluate(context), attrname);
	}

	public static Object call(UL4GetItem obj, String attrname)
	{
		return obj.getItemUL4(attrname);
	}

	public static Object call(UL4Attributes obj, String attrname)
	{
		if ("items".equals(attrname))
			return new BoundUL4AttributesMethodItems(obj);
		else if ("values".equals(attrname))
			return new BoundUL4AttributesMethodValues(obj);
		else if ("get".equals(attrname))
			return new BoundUL4AttributesMethodGet(obj);

		return obj.getItemStringUL4(attrname);
	}

	public static Object call(UL4GetItemString obj, String attrname)
	{
		return obj.getItemStringUL4(attrname);
	}

	public static Object call(Map obj, String attrname)
	{
		if ("items".equals(attrname))
			return new BoundDictMethodItems(obj);
		else if ("values".equals(attrname))
			return new BoundDictMethodValues(obj);
		else if ("get".equals(attrname))
			return new BoundDictMethodGet(obj);
		else if ("update".equals(attrname))
			return new BoundDictMethodUpdate(obj);

		Object result = obj.get(attrname);

		if ((result == null) && !obj.containsKey(attrname))
			return new UndefinedKey(attrname);
		return result;
	}

	public static Object call(List obj, String attrname)
	{
		if ("append".equals(attrname))
			return new BoundListMethodAppend(obj);
		else if ("insert".equals(attrname))
			return new BoundListMethodInsert(obj);
		else if ("pop".equals(attrname))
			return new BoundListMethodPop(obj);
		else if ("find".equals(attrname))
			return new BoundListMethodFind(obj);
		else if ("rfind".equals(attrname))
			return new BoundListMethodRFind(obj);
		else
			return new UndefinedKey(attrname);
	}

	public static Object call(String obj, String attrname)
	{
		if ("split".equals(attrname))
			return new BoundStringMethodSplit(obj);
		else if ("rsplit".equals(attrname))
			return new BoundStringMethodRSplit(obj);
		else if ("strip".equals(attrname))
			return new BoundStringMethodStrip(obj);
		else if ("lstrip".equals(attrname))
			return new BoundStringMethodLStrip(obj);
		else if ("rstrip".equals(attrname))
			return new BoundStringMethodRStrip(obj);
		else if ("upper".equals(attrname))
			return new BoundStringMethodUpper(obj);
		else if ("lower".equals(attrname))
			return new BoundStringMethodLower(obj);
		else if ("capitalize".equals(attrname))
			return new BoundStringMethodCapitalize(obj);
		else if ("startswith".equals(attrname))
			return new BoundStringMethodStartsWith(obj);
		else if ("endswith".equals(attrname))
			return new BoundStringMethodEndsWith(obj);
		else if ("replace".equals(attrname))
			return new BoundStringMethodReplace(obj);
		else if ("find".equals(attrname))
			return new BoundStringMethodFind(obj);
		else if ("rfind".equals(attrname))
			return new BoundStringMethodRFind(obj);
		else if ("join".equals(attrname))
			return new BoundStringMethodJoin(obj);
		else
			return new UndefinedKey(attrname);
	}

	public static Object call(Date obj, String attrname)
	{
		if ("year".equals(attrname))
			return new BoundDateMethodYear(obj);
		else if ("month".equals(attrname))
			return new BoundDateMethodMonth(obj);
		else if ("day".equals(attrname))
			return new BoundDateMethodDay(obj);
		else if ("hour".equals(attrname))
			return new BoundDateMethodHour(obj);
		else if ("minute".equals(attrname))
			return new BoundDateMethodMinute(obj);
		else if ("second".equals(attrname))
			return new BoundDateMethodSecond(obj);
		else if ("microsecond".equals(attrname))
			return new BoundDateMethodMicrosecond(obj);
		else if ("weekday".equals(attrname))
			return new BoundDateMethodWeekday(obj);
		else if ("yearday".equals(attrname))
			return new BoundDateMethodYearday(obj);
		else if ("week".equals(attrname))
			return new BoundDateMethodWeek(obj);
		else if ("isoformat".equals(attrname))
			return new BoundDateMethodISOFormat(obj);
		else if ("mimeformat".equals(attrname))
			return new BoundDateMethodMIMEFormat(obj);
		else
			return new UndefinedKey(attrname);
	}

	public static Object call(Object obj, String attrname)
	{
		if (obj instanceof UL4Attributes) // test this before UL4GetItemString
			return call((UL4Attributes)obj, attrname);
		else if (obj instanceof UL4GetItemString)
			return call((UL4GetItemString)obj, attrname);
		else if (obj instanceof UL4GetItem)
			return call((UL4GetItem)obj, attrname);
		else if (obj instanceof Map)
			return call((Map)obj, attrname);
		else if (obj instanceof List)
			return call((List)obj, attrname);
		else if (obj instanceof String)
			return call((String)obj, attrname);
		else if (obj instanceof Date)
			return call((Date)obj, attrname);
		throw new ArgumentTypeMismatchException("{}[{}]", obj, attrname);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(attrname);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		attrname = (String)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(AST.attributes, "obj", "attrname");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("obj".equals(key))
			return obj;
		else if ("attrname".equals(key))
			return attrname;
		else
			return super.getItemStringUL4(key);
	}
}

		// MapUtils.putMap(
		// 	builtinMethods,
		// 	"split", new MethodSplit(),
		// 	"rsplit", new MethodRSplit(),
		// 	"strip", new MethodStrip(),
		// 	"lstrip", new MethodLStrip(),
		// 	"rstrip", new MethodRStrip(),
		// 	"upper", new MethodUpper(),
		// 	"lower", new MethodLower(),
		// 	"capitalize", new MethodCapitalize(),
		// 	"items", new MethodItems(),
		// 	"values", new MethodValues(),
		// 	"isoformat", new MethodISOFormat(),
		// 	"mimeformat", new MethodMIMEFormat(),
		// 	"day", new MethodDay(),
		// 	"month", new MethodMonth(),
		// 	"year", new MethodYear(),
		// 	"hour", new MethodHour(),
		// 	"minute", new MethodMinute(),
		// 	"second", new MethodSecond(),
		// 	"microsecond", new MethodMicrosecond(),
		// 	"week", new MethodWeek(),
		// 	"weekday", new MethodWeekday(),
		// 	"yearday", new MethodYearday(),
		// 	"startswith", new MethodStartsWith(),
		// 	"endswith", new MethodEndsWith(),
		// 	"find", new MethodFind(),
		// 	"rfind", new MethodRFind(),
		// 	"get", new MethodGet(),
		// 	"join", new MethodJoin(),
		// 	"replace", new MethodReplace(),
		// 	"append", new MethodAppend(),
		// 	"insert", new MethodInsert(),
		// 	"pop", new MethodPop(),
		// 	"update", new MethodUpdate()
		// );
