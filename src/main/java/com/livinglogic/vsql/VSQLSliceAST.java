/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.ItemAST;
import com.livinglogic.ul4.SliceAST;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import com.livinglogic.utils.VSQLUtils;


/**
The ternary vSQL slice operator, i.e. {a[b:c]}.

@author W. Doerwald
**/
public class VSQLSliceAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLSliceAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLSliceAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlsliceast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for a slice expression (e.g. ``x[y:z]``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLSliceAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected VSQLDataType dataType;

	protected VSQLAST obj;
	protected VSQLAST index1;
	protected VSQLAST index2;

	public VSQLSliceAST(String sourcePrefix, VSQLAST obj, String sourceInfix1, VSQLAST index1, String sourceInfix2, VSQLAST index2, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceInfix1, index1, sourceInfix2, index2, sourceSuffix);
		this.obj = obj;
		this.index1 = index1;
		this.index2 = index2;
		validate();
	}

	public VSQLSliceAST(String sourcePrefix, VSQLAST obj, String sourceInfix1, VSQLAST index1, String sourceInfix2, VSQLAST index2)
	{
		this(sourcePrefix, obj, sourceInfix1, index1, sourceInfix2, index2, null);
	}

	public VSQLSliceAST(VSQLAST obj, String sourceInfix1, VSQLAST index1, String sourceInfix2, VSQLAST index2, String sourceSuffix)
	{
		this(null, obj, sourceInfix1, index1, sourceInfix2, index2, sourceSuffix);
	}

	public VSQLSliceAST(VSQLAST obj, String sourceInfix1, VSQLAST index1, String sourceInfix2, VSQLAST index2)
	{
		this(null, obj, sourceInfix1, index1, sourceInfix2, index2, null);
	}

	public static VSQLSliceAST make(VSQLAST obj, VSQLAST index1, VSQLAST index2)
	{
		int prec = obj.getPrecedence();
		if (prec >= PRECEDENCE)
			// When an index is missing, add "silent" {@code VSQLNoneAST}
			return new VSQLSliceAST(obj, "[", index1 != null ? index1 : new VSQLNoneAST(""), ":", index2 != null ? index2 : new VSQLNoneAST(""), "]");
		else
			return new VSQLSliceAST("(", obj, ")[", index1 != null ? index1 : new VSQLNoneAST(""), ":", index2 != null ? index2 : new VSQLNoneAST(""), "]");
	}

	@Override
	public String getDescription()
	{
		return "Slice expression";
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.TERNOP_SLICE;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return dataType;
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return List.of(obj, index1, index2);
	}

	@Override
	public VSQLRule getRule()
	{
		return rules.get(List.of(obj.getDataType(), index1.getDataType(), index2.getDataType()));
	}

	@Override
	public int getArity()
	{
		return 3;
	}

	private final static int PRECEDENCE = 16;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	public void validate()
	{
		obj.validate();
		index1.validate();
		index2.validate();

		if (obj.error != null || index1.error != null || index2.error != null)
		{
			error = VSQLError.SUBNODEERROR;
			dataType = null;
		}
		else
		{
			dataType = getRule().getResultType();
			error = (dataType == null) ? VSQLError.SUBNODETYPES : null;
		}
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(index1);
		encoder.dump(index2);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (VSQLAST)decoder.load();
		index1 = (VSQLAST)decoder.load();
		index2 = (VSQLAST)decoder.load();
	}

	protected Map<List<VSQLDataType>, VSQLRule> getRules()
	{
		return rules;
	}

	private static Map<List<VSQLDataType>, VSQLRule> rules = new HashMap<>();

	//BEGIN RULES (don't remove this comment)
	private static void addRulesPart1()
	{
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.STR, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_str(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOB, List.of(VSQLDataType.CLOB, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_clob(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.INTLIST, List.of(VSQLDataType.INTLIST, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_intlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NUMBERLIST, List.of(VSQLDataType.NUMBERLIST, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_numberlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.STRLIST, List.of(VSQLDataType.STRLIST, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_strlist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.CLOBLIST, List.of(VSQLDataType.CLOBLIST, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_cloblist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATELIST, List.of(VSQLDataType.DATELIST, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.DATETIMELIST, List.of(VSQLDataType.DATETIMELIST, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_datetimelist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.NULL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.BOOL, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.BOOL, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.BOOL, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INT, VSQLDataType.NULL), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INT, VSQLDataType.BOOL), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
		addRule(rules, VSQLDataType.NULLLIST, List.of(VSQLDataType.NULLLIST, VSQLDataType.INT, VSQLDataType.INT), List.of("vsqlimpl_pkg.slice_nulllist(", 1, ", ", 2, ", ", 3, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
