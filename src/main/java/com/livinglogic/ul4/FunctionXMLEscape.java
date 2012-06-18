/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class FunctionXMLEscape implements Function
{
	public static String call(Object obj)
	{
		if (obj == null)
			return "";

		String str = FunctionStr.call(obj);
		int length = str.length();
		StringBuffer sb = new StringBuffer((int)(1.2 * length));
		for (int offset = 0; offset < length; offset++)
		{
			char c = str.charAt(offset);
			switch (c)
			{
				case '<':
					sb.append("&lt;");
					break;
				case '>':
					sb.append("&gt;");
					break;
				case '&':
					sb.append("&amp;");
					break;
				case '\'':
					sb.append("&#39;");
					break;
				case '"':
					sb.append("&quot;");
					break;
				case '\t':
					sb.append(c);
					break;
				case '\n':
					sb.append(c);
					break;
				case '\r':
					sb.append(c);
					break;
				case '\u0085':
					sb.append(c);
					break;
				default:
					if ((('\u0020' <= c) && (c <= '\u007e')) || ('\u00A0' <= c))
						sb.append(c);
					else
						sb.append("&#").append((int)c).append(';');
					break;
			}
		}
		return sb.toString();
	}

	public Object evaluate(EvaluationContext context, Object... args)
	{
		if (args.length == 1)
			return call(args[0]);
		throw new ArgumentCountMismatchException("function", "xmlescape", args.length, 1);
	}

	public String getName()
	{
		return "xmlescape";
	}
}
