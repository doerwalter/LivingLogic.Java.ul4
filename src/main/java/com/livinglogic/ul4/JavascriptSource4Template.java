/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.lang.ObjectUtils;

public class JavascriptSource4Template
{
	private InterpretedTemplate template;

	public JavascriptSource4Template(InterpretedTemplate template)
	{
		this.template = template;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("ul4.Template.loads(");
		buffer.append(Utils.json(template.dumps()));
		buffer.append(")");
		return buffer.toString();
	}
}
