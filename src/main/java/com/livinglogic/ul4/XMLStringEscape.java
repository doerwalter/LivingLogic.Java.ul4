/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

public class XMLStringEscape implements StringEscape
{
	public String escape(String value)
	{
		return FunctionXMLEscape.call(value);
	}

	public static XMLStringEscape function = new XMLStringEscape();
}
