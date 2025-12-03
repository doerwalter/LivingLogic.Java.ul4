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
import com.livinglogic.ul4.EnumValueException;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import static com.livinglogic.utils.StringUtils.formatMessage;


/**
A class to build an SQL query defined via vSQL expressions.

@author W. Doerwald
**/
public class VSQLQuery
{
	public enum Aggregate
	{
		GROUP
		{
			@Override
			public String toString()
			{
				return "group";
			}
		},
		COUNT
		{
			@Override
			public String toString()
			{
				return "count";
			}
		},
		MIN
		{
			@Override
			public String toString()
			{
				return "min";
			}
		},
		MAX
		{
			@Override
			public String toString()
			{
				return "max";
			}
		},
		SUM
		{
			@Override
			public String toString()
			{
				return "sum";
			}
		};

		public String toString()
		{
			return null;
		}

		public static Aggregate fromString(String value)
		{
			if (value == null)
				return null;
			switch (value)
			{
				case "group":
					return GROUP;
				case "count":
					return COUNT;
				case "min":
					return MIN;
				case "max":
					return MAX;
				case "sum":
					return SUM;
			}
			throw new EnumValueException("com.livinglogic.livingapps.vsql.VSQLQuery.Aggregate", value);
		}
	}
	public static abstract class Expr
	{
		/**
		Return the source that is used to identify the expresions.

		This is used to avoid adding this expression to the query multiple times.
		**/
		public abstract String getKey();
		public abstract String getSQLSource();
		public abstract String getSQLSourceWhere();
	}

	public static final class SQLExpr extends Expr
	{
		protected String expr;
		protected String comment;

		SQLExpr(String expr, String comment)
		{
			this.expr = expr;
			this.comment = comment;
		}

		public String getExpr()
		{
			return expr;
		}

		public String getComment()
		{
			return comment;
		}

		@Override
		public String getKey()
		{
			return expr;
		}

		@Override
		public String getSQLSource()
		{
			return addComment(expr, comment);
		}

		@Override
		public String getSQLSourceWhere()
		{
			return addComment(expr, comment);
		}
	}

	public final class VSQLExpr extends Expr
	{
		VSQLAST expr;
		String comment;

		VSQLExpr(String expr, String comment)
		{
			this.expr = compile(expr);
			this.comment = comment;
		}

		public VSQLAST getExpr()
		{
			return expr;
		}

		public void setExpr(VSQLAST expr)
		{
			this.expr = expr;
		}

		public String getComment()
		{
			return comment;
		}

		public String getSource()
		{
			return expr.getSource();
		}

		@Override
		public String getKey()
		{
			return expr.getSQLSource(VSQLQuery.this);
		}

		@Override
		public String getSQLSource()
		{
			return addComment(expr.getSQLSource(VSQLQuery.this), comment, expr.getSource());
		}

		public String getSQLSourceWhere()
		{
			VSQLAST finalExpr = expr;
			if (finalExpr.getDataType() != VSQLDataType.BOOL)
				finalExpr = VSQLFuncAST.make("bool", finalExpr);
			return addComment(finalExpr.getSQLSource(VSQLQuery.this) + " = 1", expr.getSource(), comment);
		}
	}

	public static class AliasExpr
	{
		protected Expr expr;
		protected String alias;

		AliasExpr(Expr expr, String alias)
		{
			this.expr = expr;
			this.alias = alias;
		}

		public Expr getExpr()
		{
			return expr;
		}

		public String getAlias()
		{
			return alias;
		}

		public String getKey()
		{
			String key = expr.getKey();
			if (alias != null)
				key += " " + alias;
			return key;
		}
	}

	public static class SelectExpr extends AliasExpr
	{
		SelectExpr(Expr expr, String alias)
		{
			super(expr, alias);
		}

		public String getSQLSource()
		{
			String sqlSource = expr.getSQLSource();

			if (alias != null)
			{
				sqlSource += " as ";
				sqlSource += alias;
			}

			return sqlSource;
		}
	}

	public static class AggregatedSelectExpr extends SelectExpr
	{
		protected Aggregate aggregate;

		AggregatedSelectExpr(Expr expr, String alias)
		{
			super(expr, alias);
			if (expr instanceof VSQLExpr vsqlExpr)
			{
				VSQLAST vsqlAST = vsqlExpr.getExpr();
				if (vsqlAST instanceof VSQLFuncAST vsqlFuncAST)
				{
					String name = vsqlFuncAST.getName();
					List<VSQLAST> args = vsqlFuncAST.getArgs();
					int argCount = args.size();
					if ("count".equals(name) && argCount == 0)
					{
						vsqlExpr.setExpr(null);
						this.aggregate = Aggregate.COUNT;
					}
					else if ("min".equals(name) && argCount == 1)
					{
						vsqlExpr.setExpr(args.get(0));
						this.aggregate = Aggregate.MIN;
					}
					else if ("max".equals(name) && argCount == 1)
					{
						vsqlExpr.setExpr(args.get(0));
						this.aggregate = Aggregate.MAX;
					}
					else if ("sum".equals(name) && argCount == 1)
					{
						vsqlExpr.setExpr(args.get(0));
						this.aggregate = Aggregate.SUM;
					}
					else if ("group".equals(name) && argCount == 1)
					{
						vsqlExpr.setExpr(args.get(0));
						this.aggregate = Aggregate.GROUP;
					}
					else
					{
						throw new VSQLAggregationException("Aggregation call is malformed.");
					}
				}
				else
				{
					throw new VSQLAggregationException("Aggregation call is malformed.");
				}
			}
		}

		public Aggregate getAggregate()
		{
			return aggregate;
		}

		public String getKey()
		{
			if (expr instanceof VSQLExpr vsqlExpr)
			{
				String key = expr.getKey();
				if (aggregate != null)
					key = aggregate.toString() + "(" + key + ")";
				if (alias != null)
					key += " " + alias;
				return key;
			}
			else
				return super.getKey();
		}

		public String getSQLSource()
		{
			String sqlSource;

			if (expr instanceof VSQLExpr vsqlExpr)
			{
				if (vsqlExpr.getExpr() == null)
					sqlSource = addComment("count(*)", vsqlExpr.getComment());
				else
				{
					sqlSource = vsqlExpr.getSQLSource();
					if (aggregate != Aggregate.GROUP)
					{
						sqlSource = aggregate.toString() + "(" + sqlSource + ")";
					}
				}
			}
			else
			{
				sqlSource = expr.getSQLSource();
			}

			if (alias != null)
			{
				sqlSource += " as ";
				sqlSource += alias;
			}

			return sqlSource;
		}
	}

	public static final class FromExpr extends AliasExpr
	{
		FromExpr(Expr expr, String alias)
		{
			super(expr, alias);
		}

		public String getSQLSource()
		{
			String sqlSource = expr.getSQLSource();

			if (alias != null)
			{
				sqlSource += " ";
				sqlSource += alias;
			}

			return sqlSource;
		}
	}

	public static final class WhereExpr
	{
		protected Expr expr;

		WhereExpr(Expr expr)
		{
			this.expr = expr;
		}

		public String getKey()
		{
			return expr.getKey();
		}

		public String getSQLSource()
		{
			return expr.getSQLSourceWhere();
		}
	}

	private static final class OrderByExpr
	{
		Expr expr;
		String ascDesc;
		String nulls;

		OrderByExpr(Expr expr, String ascDesc, String nulls)
		{
			this.expr = expr;
			this.ascDesc = ascDesc;
			this.nulls = nulls;
		}

		public String getKey()
		{
			String key = expr.getKey();
			if (ascDesc != null)
			{
				key += " " + ascDesc;
			}

			if (nulls != null)
			{
				key += " nulls " + nulls;
			}
			return key;
		}

		public String getSQLSource()
		{
			String sqlSource = expr.getSQLSource();

			if (ascDesc != null)
			{
				sqlSource += " " + ascDesc;
			}

			if (nulls != null)
			{
				sqlSource += " nulls " + nulls;
			}

			return sqlSource;
		}
	}

	public static final class GroupByExpr
	{
		Expr expr;

		GroupByExpr(Expr expr)
		{
			this.expr = expr;
		}

		public String getKey()
		{
			return expr.getKey();
		}

		public String getSQLSource()
		{
			return expr.getSQLSource();
		}
	}

	protected String comment;
	protected Map<String, VSQLField> vars;
	protected List<SelectExpr> fields; // SQL and vSQL expression to be selected
	protected List<AggregatedSelectExpr> aggregatedFields; // SQL and vSQL expression to be selected
	protected List<FromExpr> from; // tables (and otehr stuff) to select from
	protected Map<String, WhereExpr> where; // maps SQL source to filter expressions
	protected List<OrderByExpr> orderBys;
	protected Map<String, GroupByExpr> groupBys;
	protected long offset = -1; // used for `fetch first ? rows only (-1 omits this)
	protected long limit = -1; // used for `offset ? rows` (-1 omits this)
	// Map identifier chains to from expressions
	// we need this so that when `a.b.c` and `a.b.d` appear as VQL expressions
	// in a query we do the join for `a.b` only once.
	protected Map<String, FromExpr> identifier2Expr;

	public VSQLQuery(String comment, Map<String, VSQLField> vars)
	{
		this.comment = comment;
		this.vars = vars;
		fields = new ArrayList<>();
		aggregatedFields = new ArrayList<>();
		from = new ArrayList<>();
		where = new LinkedHashMap<>();
		orderBys = new ArrayList<>();
		groupBys = new LinkedHashMap<>();
		identifier2Expr = new HashMap<>();
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

	FromExpr register(VSQLFieldRefAST fieldRef)
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
		FromExpr parentExpr = identifier2Expr.get(parentIdentifier);
		if (parentExpr != null)
		{
			return parentExpr;
		}

		parentExpr = register(fieldRefParent);

		String newAlias = "t" + String.valueOf(from.size() + 1);
		VSQLField field = fieldRefParent.getField();
		String joinCondition = field.getJoinSQL();
		// Only add to `where` if the join condition is not empty
		if (joinCondition != null)
		{
			if (parentExpr != null)
			{
				joinCondition = joinCondition.replace("{m}", parentExpr.getAlias());
			}
			joinCondition = joinCondition.replace("{d}", newAlias);
			where.put(joinCondition, new WhereExpr(new SQLExpr(joinCondition, fieldRefParent.getSource())));
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

		FromExpr fromExpr = new FromExpr(new SQLExpr(tableSQL, fieldRefParent.getSource()), newAlias);
		identifier2Expr.put(parentIdentifier, fromExpr);
		from.add(fromExpr);
		return fromExpr;
	}

	public FromExpr fromVSQL(String identifier)
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
			where.put(joinCondition, new WhereExpr(new SQLExpr(joinCondition, field.getIdentifier())));
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

		FromExpr fromExpr = new FromExpr(new SQLExpr(tableSQL, field.getIdentifier()), newAlias);
		identifier2Expr.put(field.getIdentifier(), fromExpr);
		from.add(fromExpr);
		return fromExpr;
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

	public SelectExpr selectSQL(String expr, String comment, String alias)
	{
		if (aggregatedFields.size() > 0)
			throw new VSQLMixedAggregationException();

		SelectExpr selectExpr = new SelectExpr(new SQLExpr(expr, comment), alias);
		fields.add(selectExpr);
		return selectExpr;
	}

	public SelectExpr selectVSQL(String expr, String comment, String alias)
	{
		if (aggregatedFields.size() > 0)
			throw new VSQLMixedAggregationException();

		SelectExpr selectExpr = new SelectExpr(new VSQLExpr(expr, comment), alias);
		fields.add(selectExpr);
		return selectExpr;
	}

	public SelectExpr selectVSQL(String expr)
	{
		return selectVSQL(expr, null, null);
	}

	public AggregatedSelectExpr aggregateSQL(String expr, String comment, String alias)
	{
		if (fields.size() > 0)
			throw new VSQLMixedAggregationException();

		AggregatedSelectExpr aggregatedSelectExpr = new AggregatedSelectExpr(new SQLExpr(expr, comment), alias);
		aggregatedFields.add(aggregatedSelectExpr);
		return aggregatedSelectExpr;
	}

	public AggregatedSelectExpr aggregateSQL(String expr)
	{
		return aggregateSQL(expr, null, null);
	}

	public AggregatedSelectExpr aggregateVSQL(String expr, String comment, String alias)
	{
		if (fields.size() > 0)
			throw new VSQLMixedAggregationException();

		AggregatedSelectExpr aggregatedSelectExpr = new AggregatedSelectExpr(new VSQLExpr(expr, comment), alias);
		aggregatedFields.add(aggregatedSelectExpr);
		Aggregate aggregate = aggregatedSelectExpr.getAggregate();

		if (aggregate == Aggregate.GROUP)
		{
			String key = aggregatedSelectExpr.getKey();
			if (!groupBys.containsKey(key))
			{
				// We know that our expression is a `VSQLExpr`, otherweise `aggregate` would be `null`.
				VSQLExpr vsqlExpr = (VSQLExpr)aggregatedSelectExpr.getExpr();
				groupBys.put(key, new GroupByExpr(new VSQLExpr(vsqlExpr.getSource(), vsqlExpr.getComment())));
			}
		}
		return aggregatedSelectExpr;
	}

	public FromExpr fromSQL(String tablename, String comment, String alias)
	{
		FromExpr fromExpr = new FromExpr(new SQLExpr(tablename, comment), alias);
		from.add(fromExpr);
		return fromExpr;
	}

	public WhereExpr whereSQL(String expr, String comment)
	{
		WhereExpr newWhereExpr = new WhereExpr(new SQLExpr(expr, comment));
		String key = newWhereExpr.getKey();
		WhereExpr oldWhereExpr = where.get(key);
		if (oldWhereExpr != null)
			return oldWhereExpr;
		where.put(key, newWhereExpr);
		return newWhereExpr;
	}

	public WhereExpr whereVSQL(String expr, String comment)
	{
		WhereExpr newWhereExpr = new WhereExpr(new VSQLExpr(expr, comment));
		String key = newWhereExpr.getKey();
		WhereExpr oldWhereExpr = where.get(key);
		if (oldWhereExpr != null)
			return oldWhereExpr;
		where.put(key, newWhereExpr);
		return newWhereExpr;
	}

	public WhereExpr whereVSQL(String expr)
	{
		return whereVSQL(expr, null);
	}

	public OrderByExpr orderBySQL(String expr, String comment, String ascDesc, String nulls)
	{
		OrderByExpr orderByExpr = new OrderByExpr(new SQLExpr(expr, comment), ascDesc, nulls);
		orderBys.add(orderByExpr);
		return orderByExpr;
	}

	public OrderByExpr orderByVSQL(String expr, String comment, String ascDesc, String nulls)
	{
		OrderByExpr orderByExpr = new OrderByExpr(new VSQLExpr(expr, comment), ascDesc, nulls);
		orderBys.add(orderByExpr);
		return orderByExpr;
	}

	public OrderByExpr orderByVSQL(String expr, String comment)
	{
		String nulls = null;
		String ascDesc = null;

		while (true)
		{
			boolean found = false;
			if (nulls == null && expr.endsWith(" nulls first"))
			{
				nulls = "first";
				expr = expr.substring(0, expr.length() - 12).trim();
				found = true;
			}
			else if (nulls == null && expr.endsWith(" nulls last"))
			{
				nulls = "last";
				expr = expr.substring(0, expr.length() - 11).trim();
				found = true;
			}
			else if (ascDesc == null && expr.endsWith(" asc"))
			{
				ascDesc = "asc";
				expr = expr.substring(0, expr.length() - 4).trim();
				found = true;
			}
			else if (ascDesc == null && expr.endsWith(" desc"))
			{
				ascDesc = "desc";
				expr = expr.substring(0, expr.length() - 5).trim();
				found = true;
			}
			if (!found)
				break;
		}

		return orderByVSQL(expr, comment, ascDesc, nulls);
	}

	public OrderByExpr orderByVSQL(String expr)
	{
		return orderByVSQL(expr, null);
	}

	public GroupByExpr groupBySQL(String expr, String comment)
	{
		if (fields.size() > 0)
			throw new VSQLMixedAggregationException();

		GroupByExpr newGroupByExpr = new GroupByExpr(new SQLExpr(expr, comment));
		String key = newGroupByExpr.getKey();
		GroupByExpr oldGroupByExpr = groupBys.get(key);
		if (oldGroupByExpr != null)
			return oldGroupByExpr;
		groupBys.put(key, newGroupByExpr);
		return newGroupByExpr;
	}

	public GroupByExpr groupBySQL(String expr)
	{
		return groupBySQL(expr, null);
	}

	public GroupByExpr groupByVSQL(String expr, String comment)
	{
		if (fields.size() > 0)
			throw new VSQLMixedAggregationException();

		GroupByExpr newGroupByExpr = new GroupByExpr(new VSQLExpr(expr, comment));
		String key = newGroupByExpr.getKey();
		GroupByExpr oldGroupByExpr = groupBys.get(key);
		if (oldGroupByExpr != null)
			return oldGroupByExpr;
		groupBys.put(key, newGroupByExpr);
		return newGroupByExpr;
	}

	public void limit(long limit)
	{
		this.limit = limit;
	}

	public void noLimit()
	{
		limit = -1;
	}

	public void offset(long offset)
	{
		this.offset = offset;
	}

	public void noOffset()
	{
		offset = -1;
	}

	private static String makeComment(String comment)
	{
		return "/* " + comment.replace("/*", "/ *").replace("*/", "* /") + " */";
	}

	private static String addComment(String expression, String comment)
	{
		if (comment == null)
			return expression;
		comment = makeComment(comment);
		if (!expression.endsWith(comment))
			return expression + " " + comment;
		else
			return expression;
	}

	private static String addComment(String expression, String comment1, String comment2)
	{
		if (comment1 == null)
			return addComment(expression, comment2);
		else if (comment2 == null)
			return addComment(expression, comment1);

		String testComment1 = makeComment(comment1);
		String testComment2 = makeComment(comment2);

		if (expression.endsWith(testComment1))
			return expression + " " + testComment2;
		else
		{
			if (expression.endsWith(testComment2))
				return expression + " " + testComment1;
			else
			{
				return expression + " " + makeComment(comment1 + ": " + comment2);
			}
		}
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
		for (SelectExpr selectExpr : fields)
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
			buffer.append(selectExpr.getSQLSource());
		}
		for (AggregatedSelectExpr aggregatedSelectExpr : aggregatedFields)
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
			buffer.append(aggregatedSelectExpr.getSQLSource());
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
		for (FromExpr fromExpr : from)
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
			buffer.append(fromExpr.getSQLSource());
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
			for (WhereExpr whereExpr : where.values())
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
				String sql = whereExpr.getSQLSource();
				if (where.size() > 1)
					sql = "(" + sql + ")";
				buffer.append(sql);
			}
			buffer.append("\n");
		}

		// Output "order by"
		if (orderBys.size() > 0)
		{
			indent(buffer, indentLevel);
			buffer.append("order by\n");
			first = true;
			for (OrderByExpr orderByExpr : orderBys)
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
				buffer.append(orderByExpr.getSQLSource());
			}
			buffer.append("\n");
		}

		// Output "group by"
		if (groupBys.size() > 0)
		{
			indent(buffer, indentLevel);
			buffer.append("group by\n");
			first = true;
			for (GroupByExpr groupByExpr : groupBys.values())
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
				buffer.append(groupByExpr.getSQLSource());
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
