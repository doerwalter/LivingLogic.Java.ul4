/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Collections;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.VarAST;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import static com.livinglogic.utils.StringUtils.formatMessage;


/**
A vSQL reference to a database field.

@author W. Doerwald
**/
public class VSQLFieldRefAST extends VSQLAST implements UL4Repr
{
	/**
	UL4 type for the {@link VSQLFieldRefAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLFieldRefAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlfieldrefast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for an SQL field.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLFieldRefAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	<p>The identifier of the innermost attribute reference.</p>

	<p>Note that this might be different from {@code field.getIdentifier()},
	if {@code field.getIdentifier()} is {@code "*"}.
	**/
	protected String identifier;

	protected VSQLFieldRefAST parent;

	/**
	The final field that's referenced by this object.
	**/
	protected VSQLField field;

	public VSQLFieldRefAST(String sourcePrefix, VSQLFieldRefAST parent, String sourceInfix, String identifier, String sourceSuffix, VSQLField field)
	{
		super(sourcePrefix, parent, sourceInfix, identifier, sourceSuffix);
		this.identifier = identifier;
		this.parent = parent;
		this.field = field;
		validate();
	}

	public static VSQLFieldRefAST make(VSQLFieldRefAST parent, VSQLField field)
	{
		return new VSQLFieldRefAST(null, parent, parent != null ? "." : null, field.getIdentifier(), null, field);
	}

	public static VSQLFieldRefAST make(VSQLFieldRefAST parent, String identifier)
	{
		VSQLField resultField = null;

		VSQLField parentField = parent.getField();
		if (parentField != null)
		{
			VSQLGroup group = parentField.getRefGroup();
			if (group != null)
				resultField = group.getField(identifier);
		}
		return new VSQLFieldRefAST(null, parent, ".", identifier, null, resultField);
	}

	public static VSQLFieldRefAST make(Map<String, VSQLField> variables, String ... parts)
	{
		if (parts.length == 0)
			throw new IllegalArgumentException("VSQLFieldRefAST.make() requires at least one identifier");

		VSQLFieldRefAST parent = null;
		VSQLField field = null;
		for (String part : parts)
		{
			if (parent == null)
			{
				field = variables.get(part);
				parent = new VSQLFieldRefAST(null, parent, null, part, null, field);
			}
			else
			{
				VSQLGroup refGroup = parent.field != null ? parent.field.getRefGroup() : null;
				field = refGroup != null ? refGroup.getField(part) : null;
				parent = new VSQLFieldRefAST(null, parent, ".", part, null, field);
			}
		}
		return parent;
	}

	@Override
	public String getDescription()
	{
		return "Field reference";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		String alias = query.register(this).getAlias();
		// FIXME: Handle `params.` (or don't?)
		String source = getSource();
		String fieldSQL = field.getFieldSQL();
		if (alias != null)
		{
			fieldSQL = fieldSQL.replace("{a}", alias);
		}
		buffer.append(fieldSQL + " /* " + source + " */");
	}

	@Override
	public int getSourceLength()
	{
		int length = 0;
		if (parent != null)
			length += parent.getSourceLength();
		length += super.getSourceLength();
		return length;
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.FIELD;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return field != null ? field.getDataType() : null;
	}

	@Override
	public String getNodeValue()
	{
		if (parent == null)
			return identifier;
		else
			return parent.getNodeValue() + "." + identifier;
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return Collections.EMPTY_LIST;
	}

	@Override
	public int getArity()
	{
		return 0;
	}

	private final static int PRECEDENCE = 19;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	protected void addFieldRefs(List<VSQLFieldRefAST> fieldRefs)
	{
		fieldRefs.add(this);
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public String getFullIdentifier()
	{
		if (parent == null)
		{
			return identifier;
		}
		else
		{
			return parent.getFullIdentifier() + "." + identifier;
		}
	}

	public VSQLFieldRefAST getParent()
	{
		return parent;
	}

	public VSQLField getField()
	{
		return field;
	}

	@Override
	public void validate()
	{
		error = field != null ? null : VSQLError.FIELD;
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(parent);
		encoder.dump(field);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		parent = (VSQLFieldRefAST)decoder.load();
		field = (VSQLField)decoder.load();
	}
}
