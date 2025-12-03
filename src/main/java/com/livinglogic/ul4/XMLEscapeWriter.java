/*
** Copyright 2024-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.Writer;

public class XMLEscapeWriter extends TransformingFilterWriter
{
	protected XMLEscapeWriter(Writer out)
	{
		super(out);
	}

	@Override
	protected String transform(String str)
	{
		return FunctionXMLEscape.call(str);
	}
}