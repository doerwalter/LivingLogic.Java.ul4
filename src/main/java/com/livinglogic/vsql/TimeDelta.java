/*
** Copyright 2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Set;

import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.SourcePart;
import com.livinglogic.ul4.BoundMethod;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.UL4Attributes;
import com.livinglogic.ul4.UL4GetItemString;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4.UndefinedKey;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4.FunctionStr;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

public class TimeDelta extends Node
{
	protected Node days;
	protected Node seconds;
	protected Node microseconds;

	public TimeDelta(InterpretedTemplate template, SourcePart origin, Node days, Node seconds, Node microseconds)
	{
		super(template, origin);
		this.days = days;
		this.seconds = seconds;
		this.microseconds = microseconds;
	}

	protected SQLSnippet sqlOracle()
	{
		SQLSnippet snippetDays = days.sqlOracle();
		SQLSnippet snippetSeconds = seconds != null ? seconds.sqlOracle() : null;
		SQLSnippet snippetMicroseconds = microseconds != null ? microseconds.sqlOracle() : null;

		if (snippetMicroseconds != null)
		{
			if ((snippetDays.type == Type.BOOL || snippetDays.type == Type.INT) &&
			    (snippetSeconds.type == Type.BOOL || snippetSeconds.type == Type.INT) &&
			    (snippetMicroseconds.type == Type.BOOL || snippetMicroseconds.type == Type.INT))
				return new SQLSnippet(Type.TIMESTAMPDELTA, "(", snippetDays, "+", snippetSeconds, "/86400+", snippetMicroseconds, "/86400000000)");
		}
		else if (snippetSeconds != null)
		{
			if ((snippetDays.type == Type.BOOL || snippetDays.type == Type.INT) &&
			    (snippetSeconds.type == Type.BOOL || snippetSeconds.type == Type.INT))
				return new SQLSnippet(Type.TIMEDELTA, "(", snippetDays, "+", snippetSeconds, "/86400)");
		}
		else
		{
			if (snippetDays.type == Type.BOOL || snippetDays.type == Type.INT)
				return new SQLSnippet(Type.DAYDELTA, snippetDays);
		}
		complain(snippetDays, snippetSeconds, snippetMicroseconds);
		return null;
	}

	private void complain(SQLSnippet snippetDays, SQLSnippet snippetSeconds, SQLSnippet snippetMicroseconds)
	{
		throw error(
			"vsql.TimeDelta({}, {}, {}) not supported!",
			snippetDays.type,
			snippetSeconds != null ? snippetSeconds.type : null,
			snippetMicroseconds != null ? snippetMicroseconds.type : null
		);
	}

	public static class Function extends com.livinglogic.ul4.Function
	{
		public String nameUL4()
		{
			return "vsql.TimeDelta";
		}

		private static final Signature signature = new Signature("days", Signature.required, "seconds", null, "microseconds", null, "origin", null, "template", null);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return new TimeDelta((InterpretedTemplate)args.get(4), (SourcePart)args.get(3), (Node)args.get(0), (Node)args.get(1), (Node)args.get(2));
		}
	}

	protected static Set<String> attributes = makeExtendedSet(Node.attributes, "days", "seconds", "microseconds");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "days":
				return days;
			case "seconds":
				return seconds;
			case "microseconds":
				return microseconds;
			default:
				return super.getItemStringUL4(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" days=");
		formatter.visit(days);
		formatter.append(" seconds=");
		formatter.visit(seconds);
		formatter.append(" microseconds=");
		formatter.visit(microseconds);
		formatter.append(">");
	}
}
