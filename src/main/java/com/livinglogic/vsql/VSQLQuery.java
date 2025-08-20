/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import static com.livinglogic.utils.StringUtils.formatMessage;


/**
A class to build an SQL query defined via vSQL expressions.

@author W. Doerwald
**/
public class VSQLQuery
{
	private static final class Field
	{
		String comment;
		String alias;

		Field(String comment, String alias)
		{
			this.comment = comment;
			this.alias = alias;
		}
	}

	private static final class OrderBy
	{
		String sql;
		String comment;
		String ascDesc;
		String nulls;

		OrderBy(String sql, String comment, String ascDesc, String nulls)
		{
			this.sql = sql;
			this.comment = comment;
			this.ascDesc = ascDesc;
			this.nulls = nulls;
		}

		public String suffix()
		{
			if (ascDesc != null)
			{
				if (nulls != null)
				{
					return " " + ascDesc + " nulls " + nulls;
				}
				else
				{
					return " " + ascDesc;
				}
			}
			else
			{
				if (nulls != null)
				{
					return " nulls " + nulls;
				}
				else
				{
					return null;
				}
			}
		}
	}

	String comment;
	Map<String, VSQLField> vars;
	Map<String, Field> fields; // Keys are sql expression, values are comments/aliases
	Map<String, String> from;  // Keys are table expressions with aliass, values are comments
	Map<String, String> where; // Keys are sql conditions, values are comments
	List<OrderBy> orderBys;
	long offset = -1; // used for `fetch first ? rows only (-1 omits this)
	long limit = -1; // used for `offset ? rows` (-1 omits this)
	// Map identifier chains to table aliases
	// we need this so that when `a.b.c` and `a.b.d` appear as VQL expressions
	// in a query we do the join for `a.b` only once.
	Map<String, String> identifierAliases;

	public VSQLQuery(String comment, Map<String, VSQLField> vars)
	{
		this.comment = comment;
		this.vars = vars;
		fields = new LinkedHashMap<>();
		from = new LinkedHashMap<>();
		where = new LinkedHashMap<>();
		orderBys = new ArrayList<>();
		identifierAliases = new HashMap<>();
	}

	public VSQLQuery(String comment)
	{
		this(comment, new HashMap<>());
	}

	public VSQLQuery(Map<String, VSQLField> vars)
	{
		this(null, vars);
	}

	public VSQLQuery()
	{
		this(null, new HashMap<>());
	}

	String register(VSQLFieldRefAST fieldRef)
	{
		// Don't register broken expressions
		if (fieldRef.getError() != null)
			return null;
		VSQLFieldRefAST fieldRefParent = fieldRef.getParent();
		if (fieldRefParent == null)
		{
			// No need to register anything as this is a "global variable".
			// Also we don't need a table alias to access this field.
			return null;
		}

		String parentIdentifier = fieldRefParent.getFullIdentifier();
		String parentAlias = identifierAliases.get(parentIdentifier);
		if (parentAlias != null && identifierAliases.containsKey(parentIdentifier))
		{
			return parentAlias;
		}

		parentAlias = register(fieldRefParent);

		String newAlias = "t" + String.valueOf(from.size() + 1);
		VSQLField field = fieldRefParent.getField();
		String joinCondition = field.getJoinSQL();
		// Only add to `where` if the join condition is not empty
		if (joinCondition != null)
		{
			if (parentAlias != null)
			{
				joinCondition = joinCondition.replace("{m}", parentAlias);
			}
			joinCondition = joinCondition.replace("{d}", newAlias);
			where.put(joinCondition, fieldRefParent.getSource());
		}

		String tableSQL = field.getRefGroup().getTableSQL();

		if (tableSQL == null)
		{
			/*
			If this field is not part of a table (which can happen e.g. for
			the request parameters, which we get from function calls),
			we don't add the table aliases to the list of table aliases
			and we don't add a table to the "from" list.
			*/
			return null;
		}

		identifierAliases.put(parentIdentifier, newAlias);
		String fromSQL = tableSQL + " " + newAlias;
		from.put(fromSQL, fieldRefParent.getSource());
		return newAlias;
	}

	public String registerVSQL(String identifier)
	{
		VSQLField field = vars.get(identifier);
		if (field == null)
			throw new VSQLFieldUnknownException(formatMessage("Field {!r} unknown!", identifier));

		String newAlias = "t" + String.valueOf(from.size() + 1);
		String joinCondition = field.getJoinSQL();
		// Only add to `where` if the join condition is not empty
		if (joinCondition != null)
		{
			joinCondition = joinCondition.replace("{d}", newAlias);
			where.put(joinCondition, field.getIdentifier());
		}

		String tableSQL = field.getRefGroup().getTableSQL();

		if (tableSQL == null)
		{
			/*
			If this field is not part of a table (which can happen e.g. for
			the request parameters, which we get from function calls),
			we don't add the table aliases to the list of table aliases
			and we don't add a table to the "from" list.
			*/
			return null;
		}

		identifierAliases.put(field.getIdentifier(), newAlias);
		String fromSQL = tableSQL + " " + newAlias;
		from.put(fromSQL, field.getIdentifier());
		return newAlias;
	}

	VSQLAST compile(String source)
	{
		VSQLAST expression = VSQLAST.fromsource(source, vars);
		for (VSQLFieldRefAST fieldRefAST : expression.getFieldRefs())
		{
			register(fieldRefAST);
		}
		return expression;
	}

	public VSQLQuery selectSQL(String sqlSource, String comment, String alias)
	{
		Field field = fields.get(sqlSource);
		if (field == null)
		{
			fields.put(sqlSource, new Field(comment, alias));
		}
		return this;
	}

	public VSQLQuery selectVSQL(String source, String alias)
	{
		VSQLAST expression = compile(source);
		// FIXME: Check validity
		String sqlSource = expression.getSQLSource(this);
		String comment = expression.getSource();
		return selectSQL(sqlSource, comment, alias);
	}

	public VSQLQuery selectVSQL(String source)
	{
		return selectVSQL(source, null);
	}

	public VSQLQuery fromSQL(String tablename, String alias, String comment)
	{
		from.put(tablename + " " + alias, comment);
		return this;
	}

	public VSQLQuery whereSQL(String sqlSource, String comment)
	{
		String value = where.get(sqlSource);
		if (value == null && !where.containsKey(sqlSource))
		{
			where.put(sqlSource, comment);
		}
		return this;
	}

	public VSQLQuery whereVSQL(String source)
	{
		VSQLAST expression = compile(source);
		// FIXME: Check validity and type
		String sqlSource = expression.getSQLSource(this) + " = 1";
		String comment = expression.getSource();

		return whereSQL(sqlSource, comment);
	}

	public VSQLQuery orderBySQL(String sqlSource, String comment, String ascDesc, String nulls)
	{
		orderBys.add(new OrderBy(sqlSource, comment, ascDesc, nulls));
		return this;
	}

	public VSQLQuery orderByVSQL(String source, String ascDesc, String nulls)
	{
		VSQLAST expression = compile(source);
		// FIXME: Check validity
		String sqlSource = expression.getSQLSource(this);
		String comment = expression.getSource();
		return orderBySQL(sqlSource, comment, ascDesc, nulls);
	}

	public VSQLQuery orderByVSQL(String source)
	{
		String nulls = null;
		String ascDesc = null;

		while (true)
		{
			boolean found = false;
			if (nulls == null && source.endsWith(" nulls first"))
			{
				nulls = "first";
				source = source.substring(0, source.length() - 12).trim();
				found = true;
			}
			else if (nulls == null && source.endsWith(" nulls last"))
			{
				nulls = "last";
				source = source.substring(0, source.length() - 11).trim();
				found = true;
			}
			else if (ascDesc == null && source.endsWith(" asc"))
			{
				ascDesc = "asc";
				source = source.substring(0, source.length() - 4).trim();
				found = true;
			}
			else if (ascDesc == null && source.endsWith(" desc"))
			{
				ascDesc = "desc";
				source = source.substring(0, source.length() - 5).trim();
				found = true;
			}
			if (!found)
				break;
		}

		return orderByVSQL(source, ascDesc, nulls);
	}

	public VSQLQuery limit(long limit)
	{
		this.limit = limit;
		return this;
	}

	public VSQLQuery noLimit()
	{
		return limit(-1);
	}

	public VSQLQuery offset(long offset)
	{
		this.offset = offset;
		return this;
	}

	public VSQLQuery noOffset()
	{
		return offset(-1);
	}

	private static String makeComment(String comment)
	{
		return "/* " + comment.replace("/*", "/ *").replace("*/", "* /") + " */";
	}

	private void indent(StringBuilder buffer, int indentLevel)
	{
		for (int i = 0; i < indentLevel; ++i)
			buffer.append("\t");
	}

	private void output(StringBuilder buffer, String sql, String comment, String alias, String suffix)
	{
		buffer.append(sql);
		if (comment != null)
		{
			comment = makeComment(comment);

			if (!sql.endsWith(comment))
			{
				buffer.append(" " + comment);
			}
		}

		if (alias != null)
		{
			buffer.append(" as ").append(alias);
		}

		if (suffix != null)
		{
			buffer.append(suffix);
		}
	}

	public String getSQLSource(int indentLevel)
	{
		StringBuilder buffer = new StringBuilder();

		boolean first = true;

		// Output comment
		if (comment != null)
		{
			indent(buffer, indentLevel);
			buffer.append(makeComment(comment) + "\n");
		}

		// Output "select" field list
		indent(buffer, indentLevel);
		buffer.append("select\n");
		first = true;
		for (Map.Entry<String, Field> selectEntry : fields.entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				buffer.append(",\n");
			}
			indent(buffer, indentLevel+1);
			output(buffer, selectEntry.getKey(), selectEntry.getValue().comment, selectEntry.getValue().alias, null);
		}
		if (first)
		{
			indent(buffer, indentLevel+1);
			buffer.append("42");
		}
		buffer.append("\n");

		// Output "from"
		indent(buffer, indentLevel);
		buffer.append("from\n");
		first = true;
		for (Map.Entry<String, String> fromEntry : from.entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				buffer.append(",\n");
			}
			indent(buffer, indentLevel+1);
			output(buffer, fromEntry.getKey(), fromEntry.getValue(), null, null);
		}
		if (first)
		{
			indent(buffer, indentLevel+1);
			buffer.append("dual");
		}
		buffer.append("\n");

		// Output "where"
		if (where.size() > 0)
		{
			indent(buffer, indentLevel);
			buffer.append("where\n");
			first = true;
			for (Map.Entry<String, String> whereEntry : where.entrySet())
			{
				if (first)
				{
					first = false;
				}
				else
				{
					buffer.append(" and\n");
				}
				indent(buffer, indentLevel+1);
				output(buffer, whereEntry.getKey(), whereEntry.getValue(), null, null);
			}
			buffer.append("\n");
		}

		// Output "order by"
		if (orderBys.size() > 0)
		{
			indent(buffer, indentLevel);
			buffer.append("order by\n");
			first = true;
			for (OrderBy orderBy : orderBys)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					buffer.append(",\n");
				}
				indent(buffer, indentLevel+1);
				output(buffer, orderBy.sql, orderBy.comment, null, orderBy.suffix());
			}
			buffer.append("\n");
		}

		// Output "offset ? rows"
		if (offset >= 0)
		{
			indent(buffer, indentLevel);
			buffer.append("offset ").append(offset).append(" rows\n");
		}

		// Output "fetch next ? rows only"
		if (limit >= 0)
		{
			indent(buffer, indentLevel);
			buffer.append("fetch next ").append(limit).append(" rows only\n");
		}

		return buffer.toString();
	}

	public String getSQLSource()
	{
		return getSQLSource(0);
	}
}
