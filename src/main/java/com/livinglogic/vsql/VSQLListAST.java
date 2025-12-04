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
import com.livinglogic.ul4.ListAST;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import static com.livinglogic.utils.StringUtils.formatMessage;

import static com.livinglogic.utils.VSQLUtils.getSourcePrefix;
import static com.livinglogic.utils.VSQLUtils.getSourceSuffix;
import static com.livinglogic.utils.VSQLUtils.getSourceInfix;


/**
A vSQL list "constant".

@author W. Doerwald
**/
public class VSQLListAST extends VSQLAST
{
	/**
	UL4 type for the {@link VSQLListAST} class.
	**/
	protected static class Type extends VSQLAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "VSQLListAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqllistast";
		}

		@Override
		public String getDoc()
		{
			return "AST node for creating a list object.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLListAST;
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

	public VSQLListAST(String source)
	{
		super(source);
		this.items = Collections.EMPTY_LIST;
		validate();
	}

	public VSQLListAST(List<Object> content)
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

	public static VSQLListAST make(VSQLAST ... items)
	{
		ArrayList<Object> content = new ArrayList<>(2 * items.length + 1);
		content.add("[");
		boolean first = true;
		for (VSQLAST item : items)
		{
			if (first)
				first = false;
			else
				content.add(", ");
			content.add(item);
		}
		content.add("]");
		return new VSQLListAST(content);
	}

	@Override
	public String getDescription()
	{
		return "List expression";
	}

	@Override
	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		if (dataType == VSQLDataType.NULLLIST)
			buffer.append(items.size());
		else
		{
			if (dataType == VSQLDataType.INTLIST)
				buffer.append("integers(");
			else if (dataType == VSQLDataType.NUMBERLIST)
				buffer.append("numbers(");
			else if (dataType == VSQLDataType.STRLIST)
				buffer.append("varchars(");
			else if (dataType == VSQLDataType.CLOBLIST)
				buffer.append("clobs(");
			else
				buffer.append("dates(");
			boolean first = true;

			for (VSQLAST item : items)
			{
				if (first)
					first = false;
				else
					buffer.append(", ");
				item.makeSQLSource(buffer, query);
			}
			buffer.append(")");
		}
	}

	@Override
	public NodeType getNodeType()
	{
		return NodeType.LIST;
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

		buffer.append("[");
		for (VSQLAST item : getChildren())
		{
			if (first)
				first = false;
			else
				buffer.append(", ");
			buffer.append(item.getDataTypeString());
		}
		buffer.append("]");
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
			dataType = VSQLDataType.NULLLIST;
		}
		else if (itemTypeCount == 1)
		{
			for (VSQLAST item : items)
			{
				switch (item.getDataType())
				{
					case INT:
						dataType = VSQLDataType.INTLIST;
						break;
					case NUMBER:
						dataType = VSQLDataType.NUMBERLIST;
						break;
					case STR:
						dataType = VSQLDataType.STRLIST;
						break;
					case CLOB:
						dataType = VSQLDataType.CLOBLIST;
						break;
					case DATE:
						dataType = VSQLDataType.DATELIST;
						break;
					case DATETIME:
						dataType = VSQLDataType.DATETIMELIST;
						break;
					default:
						dataType = null;
						break;
				}
				break;
			}
			error = dataType == null ? VSQLError.LISTUNSUPPORTEDTYPES : null;
		}
		else
		{
			error = VSQLError.LISTMIXEDTYPES;
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
