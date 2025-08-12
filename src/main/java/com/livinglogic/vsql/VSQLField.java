/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import com.livinglogic.ul4.UL4Instance;
import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.AbstractInstanceType;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.ul4.UndefinedAttribute;
import com.livinglogic.ul4.ArgumentTypeMismatchException;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import java.io.IOException;
import java.util.Set;

import static com.livinglogic.utils.SetUtils.makeSet;


/**
A definition of a database field in vSQL.

@author W. Doerwald
**/
public class VSQLField implements UL4Instance, UL4ONSerializable, UL4Repr
{
	/**
	UL4 type for the {@link VSQLField} class.
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
			return "VSQLField";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.field";
		}

		@Override
		public String getDoc()
		{
			return "A definition of a database field in vSQL.";
		}

		@Override
		public VSQLField create(String id)
		{
			return new VSQLField(null, null, null);
		}

		private static final Signature signature = new Signature().addBoth("identifier").addBoth("datatype").addBoth("field_sql").addBoth("join_sql", null).addBoth("ref_group", null);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public VSQLField create(EvaluationContext context, BoundArguments args)
		{
			String identifier = args.getString(0);
			String dataType = args.getString(1);
			String fieldSQL = args.getString(2);
			String joinSQL = args.getString(3, null);
			Object refGroup = args.get(4);

			if (
				(joinSQL != null && refGroup instanceof VSQLGroup) ||
				(joinSQL == null && refGroup == null)
			)
			{
				return new VSQLField(
					identifier,
					VSQLDataType.fromString(dataType),
					fieldSQL,
					joinSQL,
					(VSQLGroup)refGroup
				);
			}
			throw new ArgumentTypeMismatchException("vsql.VSQLField({!t}, {!t}, {!t}, {!t}, {!t}) not supported", identifier, dataType, fieldSQL, joinSQL, refGroup);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLField;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected String identifier;
	protected VSQLDataType dataType;
	protected String fieldSQL;
	protected String joinSQL;
	protected VSQLGroup refGroup;

	public VSQLField(String identifier, VSQLDataType dataType, String fieldSQL, String joinSQL, VSQLGroup refGroup)
	{
		this.identifier = identifier;
		this.dataType = dataType;
		this.fieldSQL = fieldSQL;
		this.joinSQL = joinSQL;
		this.refGroup = refGroup;
	}

	public VSQLField(String identifier, VSQLDataType dataType, String fieldSQL)
	{
		this(identifier, dataType, fieldSQL, null, null);
	}

	@Override
	public String toString()
	{
		return repr();
	}

	@Override
	public String getUL4ONName()
	{
		return getTypeUL4().getUL4ONName();
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public VSQLDataType getDataType()
	{
		return dataType;
	}

	public String getFieldSQL()
	{
		return fieldSQL;
	}

	public String getJoinSQL()
	{
		return joinSQL;
	}

	public VSQLGroup getRefGroup()
	{
		return refGroup;
	}

	/** {@code attributes} contains the names of the attributes available to UL4 */
	protected static Set<String> attributes = makeSet(
		"identifier",
		"datatype",
		"fieldsql",
		"joinsql",
		"refgroup"
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
			case "identifier":
				return identifier;
			case "datatype":
				dataType = getDataType();
				return dataType != null ? dataType.toString() : null;
			case "fieldsql":
				return fieldSQL;
			case "joinsql":
				return joinSQL;
			case "refgroup":
				return refGroup;
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
		formatter.append(" identifier=");
		formatter.visit(identifier);
		if (dataType != null)
		{
			formatter.append(" datatype=");
			formatter.visit(dataType.toString());
		}
		formatter.append(" fieldsql=");
		formatter.visit(fieldSQL);
		formatter.append(">");
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(identifier);
		encoder.dump(dataType.toString());
		encoder.dump(fieldSQL);
		encoder.dump(joinSQL);
		encoder.dump(refGroup);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		identifier = (String)decoder.load();
		dataType = VSQLDataType.fromString((String)decoder.load());
		fieldSQL = (String)decoder.load();
		joinSQL = (String)decoder.load();
		refGroup = (VSQLGroup)decoder.load();
	}
}
