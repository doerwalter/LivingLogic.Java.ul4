/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;

public class BoundStringMethodSplitlines extends BoundMethod<String>
{
	public BoundStringMethodSplitlines(String object)
	{
		super(object);
	}

	@Override
	public String getNameUL4()
	{
		return "splitlines";
	}

	private static final Signature signature = new Signature().addBoth("keepends", false);

	@Override
	public Signature getSignature()
	{
		return signature;
	}

	public static List<String> call(EvaluationContext context, String object)
	{
		return call(context, object, false);
	}

	private static int lookingAtLineEnd(String object, int pos)
	{
		char c = object.charAt(pos);
		if (c == '\n' || c == '\u000B' || c == '\u000C' || c == '\u001C' || c == '\u001D' || c == '\u001E' || c == '\u0085' || c == '\u2028' || c == '\u2029')
			return 1;
		else if (c == '\r')
		{
			if (pos == object.length()-1)
				return 1;
			else if (object.charAt(pos+1) == '\n')
				return 2;
			else
				return 1;
		}
		return 0;
	}

	public static List<String> call(EvaluationContext context, String object, boolean keepEnds)
	{
		List<String> result = new ArrayList<String>();
		int length = object.length();

		for (int pos = 0, startPos = 0;;)
		{
			if (pos >= length)
			{
				if (startPos != pos)
					result.add(object.substring(startPos));
				return result;
			}
			int lineEndLen = lookingAtLineEnd(object, pos);
			if (lineEndLen == 0)
				++pos;
			else
			{
				int endPos = pos + (keepEnds ? lineEndLen : 0);
				result.add(object.substring(startPos, endPos));
				pos += lineEndLen;
				startPos = pos;
			}
		}
	}

	@Override
	public Object evaluate(EvaluationContext context, BoundArguments args)
	{
		Object keepEnds = args.get(0);
		return call(context, object, Bool.call(context, keepEnds));
	}
}
