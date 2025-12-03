/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.SeqItemASTBase;
import com.livinglogic.ul4.SeqItemAST;
import com.livinglogic.ul4.SetAST;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import static com.livinglogic.utils.StringUtils.formatMessage;

import static com.livinglogic.utils.VSQLUtils.getSourcePrefix;
import static com.livinglogic.utils.VSQLUtils.getSourceSuffix;
import static com.livinglogic.utils.VSQLUtils.getSourceInfix;


/**
A vSQL set "constant".

@author W. Doerwald
**/
public class VSQLSetAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLSetAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLSetAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlsetast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for creating a set object.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLSetAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected VSQLDataType dataType;

	protected List<VSQLAST> items;

	public VSQLSetAST(String source)
	{
		super(source);
		this.items = Collections.EMPTY_LIST;
		validate();
	}

	public VSQLSetAST(List<Object> content)
	{
		super(content.toArray());

		items = new ArrayList<>();

		for (Object item : content)
		{
			if (item instanceof VSQLAST)
				this.items.add((VSQLAST)item);
		}
		validate();
	}

	public static VSQLSetAST make(VSQLAST ... items)
	{
		if (items.length == 0)
			return new VSQLSetAST("{/}");

		ArrayList<Object> content = new ArrayList<>(2 * items.length + 1);
		content.add("{");
		boolean first = true;
		for (VSQLAST item : items)
		{
			if (first)
				first = false;
			else
				content.add(", ");
			content.add(item);
		}
		content.add("}");
		return new VSQLSetAST(content);
	}

	@Override
	public String getDescription()
	{
		return "Set expression";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		if (dataType == VSQLDataType.NULLSET)
			buffer.append(items.size() != 0 ? "1" : "0");
		else
		{
			if (dataType == VSQLDataType.INTSET)
				buffer.append("vsqlimpl_pkg.set_intlist(integers(");
			else if (dataType == VSQLDataType.NUMBERSET)
				buffer.append("vsqlimpl_pkg.set_numberlist(numbers(");
			else if (dataType == VSQLDataType.STRSET)
				buffer.append("vsqlimpl_pkg.set_strlist(varchars(");
			else
				buffer.append("vsqlimpl_pkg.set_datetimelist(dates(");
			boolean first = true;

			for (VSQLAST item : items)
			{
				if (first)
					first = false;
				else
					buffer.append(", ");
				item.makeSQLSource(buffer, query);
			}
			buffer.append("))");
		}
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.SET;
	}

	@Override
	public VSQLDataType getDataType()
	{
		return dataType;
	}

	@Override
	public String getTypeSignature()
	{
		StringBuilder buffer = new StringBuilder();

		boolean first = true;

		buffer.append("{");
		for (VSQLAST item : getChildren())
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(item.getDataTypeString());
		}
		buffer.append("}");
		return buffer.toString();
	}

	@Override
	public List<VSQLAST> getChildren()
	{
		return Collections.unmodifiableList(items);
	}

	@Override
	public int getArity()
	{
		return items.size();
	}

	private final static int PRECEDENCE = 20;

	@Override
	public int getPrecedence()
	{
		return PRECEDENCE;
	}

	@Override
	public void validate()
	{
		for (VSQLAST item : items)
		{
			if (item.error != null)
			{
				error = VSQLError.SUBNODEERROR;
				dataType = null;
				return;
			}
		}

		Set<VSQLDataType> itemTypes = new HashSet<>();

		for (VSQLAST item : items)
			itemTypes.add(item.getDataType());

		if (itemTypes.contains(VSQLDataType.NULL))
		{
			itemTypes.remove(VSQLDataType.NULL);
		}

		int itemTypeCount = itemTypes.size();

		if (itemTypeCount == 0)
		{
			dataType = VSQLDataType.NULLSET;
		}
		else if (itemTypeCount == 1)
		{
			for (VSQLAST item : items)
			{
				switch (item.getDataType())
				{
					case INT:
						dataType = VSQLDataType.INTSET;
						break;
					case NUMBER:
						dataType = VSQLDataType.NUMBERSET;
						break;
					case STR:
						dataType = VSQLDataType.STRSET;
						break;
					case DATE:
						dataType = VSQLDataType.DATESET;
						break;
					case DATETIME:
						dataType = VSQLDataType.DATETIMESET;
						break;
					default:
						dataType = null;
						break;
				}
				break;
			}
			error = dataType == null ? VSQLError.SETUNSUPPORTEDTYPES : null;
		}
		else
		{
			error = VSQLError.SETMIXEDTYPES;
			dataType = null;
		}
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(items);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		items = (List<VSQLAST>)decoder.load();
	}
}
