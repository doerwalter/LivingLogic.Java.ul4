/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.livinglogic.ul4.UL4Type;


/**
A vSQL rule.

@author W. Doerwald
**/
public class VSQLRule
{
	protected VSQLDataType resultType;
	protected List<Object> signature; // strings (function/method/attribute name) or datatype (type of argument)
	protected List<Object> source; // strings (literal source) or integer (embed child source)

	public VSQLRule(VSQLDataType resultType, List<Object> signature, List<Object> source)
	{
		this.resultType = resultType;
		this.signature = signature;
		this.source = source;
	}


	public void makeSQLSource(StringBuilder buffer, VSQLQuery query, List<VSQLAST> children)
	{
		for (Object s : source)
		{
			if (s instanceof String string)
			{
				buffer.append(string);
			}
			else if (s instanceof Integer integer)
			{
				children.get(integer-1).makeSQLSource(buffer, query);
			}
		}
	}

	public VSQLDataType getResultType()
	{
		return resultType;
	}
}
