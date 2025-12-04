/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.*;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.UL4ONSerializable;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeSet;


/**
A definition of a group of database fields (i.e. an app or table) in vSQL.

@author W. Doerwald
**/
public class VSQLGroup implements UL4Instance, UL4ONSerializable, UL4Repr
{
	/**
	UL4 type for the {@link VSQLGroup} class.
	**/
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getModuleName()
		{
			return "vsql";
		}

		@Override
		public String getNameUL4()
		{
			return "VSQLGroup";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.group";
		}

		@Override
		public String getDoc()
		{
			return "A definition of a group of database fields (i.e. an app or table) in vSQL.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLGroup;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected String tableSQL;
	protected Map<String, VSQLField> fields;

	public VSQLGroup(String tableSQL)
	{
		this.tableSQL = tableSQL;
		this.fields = new LinkedHashMap<String, VSQLField>();
	}

	@Override
	public String getUL4ONName()
	{
		return getTypeUL4().getUL4ONName();
	}

	public String getTableSQL()
	{
		return tableSQL;
	}

	public Map<String, VSQLField> getFields()
	{
		return fields;
	}

	public VSQLField getField(String identifier)
	{
		VSQLField field = fields.get(identifier);
		if (field == null)
			field = fields.get("*");
		return field;
	}

	public VSQLGroup addField(String identifier, VSQLDataType dataType, String fieldSQL)
	{
		return addField(new VSQLField(identifier, dataType, fieldSQL));
	}

	public VSQLGroup addField(String identifier, VSQLDataType dataType, String fieldSQL, String joinSQL, VSQLGroup targetGroup)
	{
		return addField(new VSQLField(identifier, dataType, fieldSQL, joinSQL, targetGroup));
	}

	public VSQLGroup addField(VSQLField field)
	{
		fields.put(field.getIdentifier(), field);
		return this;
	}

	/** {@code attributes} contains the names of the attributes available to UL4 */
	protected static Set<String> attributes = makeSet(
		"tablesql",
		"fields"
	);

	/** {@inheritDoc} */
	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	/** {@inheritDoc} */
	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		VSQLDataType dataType;
		switch (key)
		{
			case "tablesql":
				return tableSQL;
			case "fields":
				return Collections.unmodifiableMap(fields);
			default:
				return new UndefinedAttribute(this, key);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" tablesql=");
		formatter.visit(tableSQL);
		formatter.append(">");
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(tableSQL);
		encoder.dump(fields);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		tableSQL = (String)decoder.load();
		fields = (Map<String, VSQLField>)decoder.load();
	}
}
