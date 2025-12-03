/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.utils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.livinglogic.ul4.AST;
import com.livinglogic.vsql.VSQLAST;


/**
Class that provides utilities for working with vSQL

@author W. Doerwald
**/

public class VSQLUtils
{
	/**
	<p>Return the part of the source at the start of `parent` up until the start of `firstChild`
	.</p>

	<p>For example in the expression `a + b` the source prefix (with the parent being
	the addition and the first child being `a`) is `null`, but in the expression `(a + b)`
	the source prefix is `"("`.</p>

	@return the source prefix of `null`.
	**/
	public static String getSourcePrefix(AST parent, AST firstChild)
	{
		int outerStart = parent.getStartPosStart();
		int innerStart = firstChild.getStartPosStart();
		return outerStart < innerStart ? parent.getFullSource().substring(outerStart, innerStart) : null;

	}

	/**
	<p>Return the part of the source after the end of `lastChild` up until the end of `parent`
	.</p>

	<p>For example in the expression `a + b` the source suffix (with the parent being
	the addition and the last child being `b`) is `null`, but in the expression `(a + b)`
	the source suffix is `")"`.</p>

	@return the source prefix of `null`.
	**/
	public static String getSourceSuffix(AST lastChild, AST parent)
	{
		int innerStop = lastChild.getStartPosStop();
		int outerStop = parent.getStartPosStop();
		return innerStop < outerStop ? parent.getFullSource().substring(innerStop, outerStop) : null;
	}

	/**
	<p>Return the part of the source between the end of `first` and the start of `second`.</p>

	<p>For example in the expression `a + b` the source infex (with the first being
	`a` and the second being `b` is `" + "`.</p>

	@return the source infix of `null`.
	**/
	public static String getSourceInfix(AST first, AST second)
	{
		int firstStop = first.getStartPosStop();
		int secondStart = second.getStartPosStart();
		return firstStop < secondStart ? first.getFullSource().substring(firstStop, secondStart) : null;
	}

	/**
	<p>Return the "type signature" of a list of `AST`s.</p>

	<p>I.e. return the types of the `AST` comma separated.</p>

	@return the type signature as a string.
	**/
	public static String getTypeSignature(List<VSQLAST> asts)
	{
		StringBuilder buffer = new StringBuilder();
		boolean first = true;

		for (VSQLAST ast : asts)
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(ast.getDataTypeString());
		}
		return buffer.toString();
	}
}
