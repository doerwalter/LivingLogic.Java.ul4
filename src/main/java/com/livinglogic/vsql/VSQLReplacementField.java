/*
** Copyright 2026 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Map;
import java.util.LinkedHashMap;

/**
<p>A variable of a vSQL expression that stands for another vSQL expression
(a "replacement variable").</p>

<p>Every reference to such a variable is replaced by the compiled replacement
expression, so an expression that has been written for one variable can be
compiled for another one (see {@link VSQLAST#fromsource(String, Map, Map)}).
The replacement expression may only reference real variables (i.e.
{@link VSQLField} objects that aren't replacement variables).</p>
**/
public class VSQLReplacementField extends VSQLField
{
	/**
	The source code of the replacement expression.
	**/
	protected String source;

	public VSQLReplacementField(String identifier, String source, VSQLDataType dataType)
	{
		super(identifier, dataType, null, null, null);
		this.source = source;
	}

	/**
	Return the source code of the replacement expression.
	**/
	public String getSource()
	{
		return source;
	}

	/**
	<p>Compile the replacement expression for one reference to this variable.</p>

	<p>{@code sourcePrefix} and {@code sourceSuffix} are the parts of the source
	around the variable reference (e.g. parentheses); they are retained in the
	source of the resulting expression. The replacement expression is enclosed
	in parentheses unless it is a simple variable or attribute reference, so the
	precedence of the operators around the variable is preserved.</p>
	**/
	public VSQLAST makeAST(Map<String, VSQLField> vars, String sourcePrefix, String sourceSuffix)
	{
		// Compile against the real variables only, so that a replacement
		// expression can't reference replacement variables (which would
		// recurse endlessly for a variable that references itself).
		Map<String, VSQLField> fields = new LinkedHashMap<>();
		for (Map.Entry<String, VSQLField> entry : vars.entrySet())
		{
			if (!(entry.getValue() instanceof VSQLReplacementField))
				fields.put(entry.getKey(), entry.getValue());
		}

		VSQLAST ast = VSQLAST.fromsource(source, fields);
		boolean simple = ast instanceof VSQLFieldRefAST || ast instanceof VSQLAttrAST;
		ast.wrapSource(simple ? sourcePrefix : sourcePrefix + "(", simple ? sourceSuffix : ")" + sourceSuffix);
		return ast;
	}
}
