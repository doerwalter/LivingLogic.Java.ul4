/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import static com.livinglogic.utils.StringUtils.formatMessage;


/**
The vSQL operator for attribute access.

@author W. Doerwald
**/
public class VSQLAttrAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLAttrAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLAttrAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlattrast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an expression that gets or sets an attribute of an object.\n(e.g. ``x.y``).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLAttrAST;
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
	protected String name;

	public VSQLAttrAST(String sourcePrefix, VSQLAST obj, String sourceInfix, String name, String sourceSuffix)
	{
		super(sourcePrefix, obj, sourceInfix, name, sourceSuffix);
		this.obj = obj;
		this.name = name;
		validate();
	}

	public VSQLAttrAST(VSQLAST obj, String sourceInfix, String name, String sourceSuffix)
	{
		this(null, obj, sourceInfix, name, sourceSuffix);
	}

	public VSQLAttrAST(String sourcePrefix, VSQLAST obj, String sourceInfix, String name)
	{
		this(sourcePrefix, obj, sourceInfix, name, null);
	}

	public VSQLAttrAST(VSQLAST obj, String sourceInfix, String name)
	{
		this(null, obj, sourceInfix, name, null);
	}

	public static VSQLAttrAST make(VSQLAST obj, String name)
	{
		int prec = obj.getPrecedence();

		if (prec >= PRECEDENCE)
			return new VSQLAttrAST(obj, ".", name);
		else
			return new VSQLAttrAST("(", obj, ").", name);
	}

	@Override
	public String getDescription()
	{
		return "Attribute access expression";
	}

	public VSQLAST getObj()
	{
		return obj;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.ATTR;
	}

	@Override
	public String getTypeSignature()
	{
		return formatMessage("{}.{}", obj.getDataTypeString(), name);
	}

	@Override
	public VSQLDataType getDataType()
	{
		return dataType;
	}

	@Override
	public String getNodeValue()
	{
		return name;
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return List.of(obj);
	}

	@Override
	public VSQLRule getRule()
	{
		return rules.get(List.of(obj.getDataType(), name));
	}

	@Override
	public int getArity()
	{
		return 1;
	}

	private final static int PRECEDENCE = 19;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	public void validate()
	{
		if (obj.error != null)
		{
			error = VSQLError.SUBNODEERROR;
			dataType = null;
		}
		else
		{
			VSQLRule rule = getRule();
			if (rule == null)
			{
				error = VSQLError.SUBNODETYPES;
				dataType = null;
			}
			else
			{
				dataType = rule.getResultType();
				error = null;
			}
		}
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		encoder.dump(name);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (VSQLAST)decoder.load();
		name = (String)decoder.load();
	}

	private static Map<List<Object>, VSQLRule> rules = new HashMap<>();

	//BEGIN RULES (don't remove this comment)
	private static void addRulesPart1()
	{
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATE, "year"), List.of("extract(year from ", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "year"), List.of("extract(year from ", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATE, "month"), List.of("extract(month from ", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "month"), List.of("extract(month from ", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATE, "day"), List.of("extract(day from ", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "day"), List.of("extract(day from ", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "hour"), List.of("to_number(to_char(", 1, ", 'HH24'))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "minute"), List.of("to_number(to_char(", 1, ", 'MI'))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "second"), List.of("to_number(to_char(", 1, ", 'SS'))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATE, "weekday"), List.of("(to_char(", 1, ", 'D')-1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "weekday"), List.of("(to_char(", 1, ", 'D')-1)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATE, "yearday"), List.of("to_number(to_char(", 1, ", 'DDD'))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIME, "yearday"), List.of("to_number(to_char(", 1, ", 'DDD'))"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATEDELTA, "days"), List.of("trunc(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIMEDELTA, "days"), List.of("trunc(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.DATETIMEDELTA, "seconds"), List.of("trunc(mod(", 1, ", 1) * 86400 + 0.5)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.DATETIMEDELTA, "total_days"), List.of(1));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.DATETIMEDELTA, "total_hours"), List.of("(", 1, " * 24)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.DATETIMEDELTA, "total_minutes"), List.of("(", 1, " * 1440)"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.DATETIMEDELTA, "total_seconds"), List.of("(", 1, " * 86400)"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.COLOR, "r"), List.of("vsqlimpl_pkg.attr_color_r(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.COLOR, "g"), List.of("vsqlimpl_pkg.attr_color_g(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.COLOR, "b"), List.of("vsqlimpl_pkg.attr_color_b(", 1, ")"));
		addRule(rules, VSQLDataType.INT, List.of(VSQLDataType.COLOR, "a"), List.of("vsqlimpl_pkg.attr_color_a(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.GEO, "lat"), List.of("vsqlimpl_pkg.attr_geo_lat(", 1, ")"));
		addRule(rules, VSQLDataType.NUMBER, List.of(VSQLDataType.GEO, "long"), List.of("vsqlimpl_pkg.attr_geo_long(", 1, ")"));
		addRule(rules, VSQLDataType.STR, List.of(VSQLDataType.GEO, "info"), List.of("vsqlimpl_pkg.attr_geo_info(", 1, ")"));
	}

	static
	{
		addRulesPart1();
	}
	//END RULES (don't remove this comment)
}
