/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.EnumValueException;
import com.livinglogic.ul4.Utils;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

import static com.livinglogic.utils.StringUtils.formatMessage;


/**
A class to build an SQL query defined via vSQL expressions.

{@code VSQLQuery} itself is abstract: which database the query will be
executed in (and with that which SQL dialect will be used) is determined
by the class, so use one of the subclasses {@link OracleVSQLQuery} or
{@link PostgresVSQLQuery} instead.

@author W. Doerwald
**/
public abstract class VSQLQuery
{
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
			this(expr, comment, null);
		}

		VSQLExpr(String expr, String comment, Map<String, String> replacements)
		{
			this.expr = compile(expr, replacements);
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

		public VSQLQuery getQuery()
		{
			return VSQLQuery.this;
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
			return addComment(getConditionSQLSource(finalExpr.getSQLSource(VSQLQuery.this)), expr.getSource(), comment);
		}

		public Object conform(Object value)
		{
			if (expr == null) // this was a count
				return VSQLDataType.INT.conform(value);
			else
			{
				VSQLDataType dataType = expr.getDataType();
				if (dataType != null)
					value = dataType.conform(value);
				return value;
			}
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
		protected VSQLAggregate aggregate;

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
						setExprASTAndAggregate(null, VSQLAggregate.COUNT);
					}
					else if ("min".equals(name) && argCount == 1)
					{
						setExprASTAndAggregate(args.get(0), VSQLAggregate.MIN);
					}
					else if ("max".equals(name) && argCount == 1)
					{
						setExprASTAndAggregate(args.get(0), VSQLAggregate.MAX);
					}
					else if ("sum".equals(name) && argCount == 1)
					{
						setExprASTAndAggregate(args.get(0), VSQLAggregate.SUM);
					}
					else if ("group".equals(name) && argCount == 1)
					{
						setExprASTAndAggregate(args.get(0), VSQLAggregate.GROUP);
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

		private void setExprASTAndAggregate(VSQLAST ast, VSQLAggregate aggregate)
		{
			if (ast != null && !ast.getDataType().getAllowedAggregates().contains(aggregate))
			{
				throw new VSQLAggregationException(formatMessage("Values of type {!`} can not be aggragted with function {!`}", ast.getDataTypeString().toUpperCase(), aggregate.toString()));
			}
			((VSQLExpr)expr).setExpr(ast);
			this.aggregate = aggregate;
		}

		public VSQLAggregate getAggregate()
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
					if (aggregate != VSQLAggregate.GROUP)
					{
						if (aggregate == VSQLAggregate.SUM)
							sqlSource = vsqlExpr.getQuery().getSumOperandSQLSource(sqlSource, vsqlExpr.getExpr().getDataType());
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

	protected VSQLQuery(String comment, Map<String, VSQLField> vars)
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

	protected VSQLQuery(String comment)
	{
		this(comment, new HashMap<>());
	}

	protected VSQLQuery(Map<String, VSQLField> vars)
	{
		this(null, vars);
	}

	protected VSQLQuery()
	{
		this(null, new HashMap<>());
	}

	/**
	Prefix and suffix for the SQL source of list and set constants.
	**/
	public record SeqSQL(String prefix, String suffix)
	{
	}

	/**
	Return the SQL source template of the rule {@code rule} for the SQL
	dialect of this query.

	This uses double dispatch to select the SQL dialect: {@link OracleVSQLQuery}
	returns the Oracle version of the source template (via
	{@link VSQLRule#getOracleSource}) and {@link PostgresVSQLQuery} returns the
	Postgres version (via {@link VSQLRule#getPostgresSource}).
	**/
	protected abstract List<Object> getRuleSource(VSQLRule rule);

	/**
	Return the SQL source for the {@code BOOL} constant {@code value}.
	**/
	public abstract String getBoolSQLSource(boolean value);

	/**
	Return the SQL source for the {@code DATE} constant {@code value}.
	**/
	public abstract String getDateSQLSource(LocalDate value);

	/**
	Return the SQL source for the {@code DATETIME} constant {@code value}.
	**/
	public abstract String getDateTimeSQLSource(LocalDateTime value);

	/**
	Return prefix and suffix for the SQL source of a list or set constant
	of type {@code dataType}.
	**/
	public abstract SeqSQL getSeqSQL(VSQLDataType dataType);

	/**
	Turn the SQL source {@code sql} of a {@code BOOL} expression into a
	real condition that can be used in the "where" clause.
	**/
	protected abstract String getConditionSQLSource(String sql);

	/**
	Return the SQL source for the operand of a {@code sum()} aggregation
	(Postgres can't sum its native {@code boolean} type, so a vSQL
	{@code BOOL} value has to be converted to a number first).
	**/
	protected abstract String getSumOperandSQLSource(String sql, VSQLDataType dataType);

	/**
	Return the "from" clause used when the query selects from no tables
	(or {@code null} if no "from" clause is required).
	**/
	protected abstract String getSQLSourceNoFrom(int indentLevel);

	/**
	Return the offset/limit clauses at the end of the query (or {@code null}
	if neither {@code offset} nor {@code limit} is set).
	**/
	protected abstract String getSQLSourceOffsetLimit(int indentLevel);

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

		return registerTable(fieldRefParent);
	}

	/**
	<p>Register the table that the field reference {@code fieldRef} references
	(i.e. the table containing the fields of {@code fieldRef}).</p>

	<p>The table (and its join condition) is added to the {@code from} and
	{@code where} clauses (together with all the tables required to join it),
	unless that has been done before. Returns the {@link FromExpr} of the table
	(or {@code null} if the field isn't part of a table).</p>
	**/
	FromExpr registerTable(VSQLFieldRefAST fieldRef)
	{
		String identifier = fieldRef.getFullIdentifier();
		FromExpr fromExpr = identifier2Expr.get(identifier);
		if (fromExpr != null)
		{
			return fromExpr;
		}

		FromExpr parentExpr = register(fieldRef);

		String newAlias = "t" + String.valueOf(from.size() + 1);
		VSQLField field = fieldRef.getField();
		String joinCondition = field.getJoinSQL();
		// Only add to `where` if the join condition is not empty
		if (joinCondition != null)
		{
			if (parentExpr != null)
			{
				joinCondition = joinCondition.replace("{m}", parentExpr.getAlias());
			}
			joinCondition = joinCondition.replace("{d}", newAlias);
			where.put(joinCondition, new WhereExpr(new SQLExpr(joinCondition, fieldRef.getSource())));
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

		fromExpr = new FromExpr(new SQLExpr(tableSQL, fieldRef.getSource()), newAlias);
		identifier2Expr.put(identifier, fromExpr);
		from.add(fromExpr);
		return fromExpr;
	}

	/**
	<p>Register the field {@code identifier} as a table to select from and
	return its {@link FromExpr} (which provides the alias of the table).</p>

	<p>{@code identifier} is either the name of one of the variables passed to
	the constructor or an attribute path starting with such a variable (e.g.
	{@code "b.author"}); it must reference a table.</p>

	<p>This makes sure that the table is added to the {@code from} list, even if
	it is never referenced by a vSQL expression. Since the {@link FromExpr} is
	returned, the table can be used in SQL expressions (e.g. via
	{@link #whereSQL(String, String)}) too. Registering a table that has been
	joined before (explicitly or by a vSQL expression) returns the
	{@link FromExpr} of the existing join.</p>
	**/
	public FromExpr fromVSQL(String identifier)
	{
		VSQLAST ast = VSQLAST.fromsource(identifier, vars);
		if (!(ast instanceof VSQLFieldRefAST fieldRef))
			throw new IllegalArgumentException(formatMessage("{!r} is not a field reference!", identifier));
		if (fieldRef.getField() == null)
			throw new VSQLFieldUnknownException(formatMessage("Field {!r} unknown!", identifier));
		if (fieldRef.getField().getRefGroup() == null)
			throw new IllegalArgumentException(formatMessage("Field {!r} doesn't reference a table!", identifier));
		return registerTable(fieldRef);
	}

	VSQLAST compile(String source)
	{
		return compile(source, null);
	}

	/**
	<p>Compile the vSQL expression {@code source} and register all field
	references in it.</p>

	<p>{@code replacements} are additional variables for this expression only,
	given as vSQL expressions that reference the variables of the query (see
	{@link VSQLAST#fromsource(String, Map, Map)}). Such a variable replaces a
	variable of the query with the same name for this expression. May be
	{@code null}.</p>
	**/
	VSQLAST compile(String source, Map<String, String> replacements)
	{
		VSQLAST expression = VSQLAST.fromsource(source, vars, replacements);
		for (VSQLFieldRefAST fieldRefAST : expression.getFieldRefs())
		{
			register(fieldRefAST);
		}
		return expression;
	}

	/**
	Add the SQL expression `expr` to the list of expression to select.

	`comment` can be used to give the column a comment in the select list.

	`alias` can be used to give the expression a column alias.

	Note that that adds `expr` directly as "raw" SQL. To add a vSQL
	expression use {@link #selectVSQL(String)} instead.
	**/
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
		return selectVSQL(expr, comment, alias, null);
	}

	/**
	<p>Add the vSQL expression {@code expr} to the list of expression to select.</p>

	<p>{@code replacements} are additional variables for {@code expr} only,
	given as vSQL expressions that reference the variables of the query (see
	{@link #whereVSQL(String, String, Map)} for an example).</p>
	**/
	public SelectExpr selectVSQL(String expr, String comment, String alias, Map<String, String> replacements)
	{
		if (aggregatedFields.size() > 0)
			throw new VSQLMixedAggregationException();

		SelectExpr selectExpr = new SelectExpr(new VSQLExpr(expr, comment, replacements), alias);
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
		return aggregateVSQL(expr, comment, alias, null);
	}

	/**
	<p>Add the aggregating vSQL expression {@code expr} to the list of
	expression to select.</p>

	<p>{@code replacements} are additional variables for {@code expr} only,
	given as vSQL expressions that reference the variables of the query (see
	{@link #whereVSQL(String, String, Map)} for an example).</p>
	**/
	public AggregatedSelectExpr aggregateVSQL(String expr, String comment, String alias, Map<String, String> replacements)
	{
		if (fields.size() > 0)
			throw new VSQLMixedAggregationException();

		VSQLExpr vsqlExpr = new VSQLExpr(expr, comment, replacements);
		AggregatedSelectExpr aggregatedSelectExpr = new AggregatedSelectExpr(vsqlExpr, alias);
		aggregatedFields.add(aggregatedSelectExpr);
		VSQLAggregate aggregate = aggregatedSelectExpr.getAggregate();

		if (aggregate == VSQLAggregate.GROUP)
		{
			String key = aggregatedSelectExpr.getKey();
			if (!groupBys.containsKey(key))
			{
				// create a copy of our initial `VSQLExpr`
				groupBys.put(key, new GroupByExpr(new VSQLExpr(vsqlExpr.getSource(), vsqlExpr.getComment())));
			}
		}
		return aggregatedSelectExpr;
	}

	public AggregatedSelectExpr aggregateVSQL(String expr)
	{
		return aggregateVSQL(expr, null, null);
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
		return whereVSQL(expr, comment, null);
	}

	/**
	<p>Add the vSQL condition {@code expr} to the {@code where} clause.</p>

	<p>{@code replacements} are additional variables for {@code expr} only,
	given as vSQL expressions that reference the variables of the query. Every
	reference to such a variable in {@code expr} is replaced by that expression
	(see {@link VSQLAST#fromsource(String, Map, Map)}). This makes it possible
	to apply a condition that has been written for one variable to another one.
	For example a condition on a person {@code p} can be applied to the author
	{@code b.author} of a book {@code b} like this:</p>

	<pre>
	VSQLQuery query = new OracleVSQLQuery("Example query", Map.of("b", book));
	query.whereVSQL("p.lastname == 'Einstein'", null, Map.of("p", "b.author"));
	</pre>

	<p>which is equivalent to:</p>

	<pre>
	query.whereVSQL("b.author.lastname == 'Einstein'");
	</pre>

	<p>A replacement variable replaces a variable of the query with the same
	name for this condition, and it isn't available to other expressions.</p>
	**/
	public WhereExpr whereVSQL(String expr, String comment, Map<String, String> replacements)
	{
		WhereExpr newWhereExpr = new WhereExpr(new VSQLExpr(expr, comment, replacements));
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
		return orderByVSQL(expr, comment, ascDesc, nulls, null);
	}

	/**
	<p>Add the "order by" vSQL expression {@code expr} to this query.</p>

	<p>{@code replacements} are additional variables for {@code expr} only,
	given as vSQL expressions that reference the variables of the query (see
	{@link #whereVSQL(String, String, Map)} for an example).</p>
	**/
	public OrderByExpr orderByVSQL(String expr, String comment, String ascDesc, String nulls, Map<String, String> replacements)
	{
		OrderByExpr orderByExpr = new OrderByExpr(new VSQLExpr(expr, comment, replacements), ascDesc, nulls);
		orderBys.add(orderByExpr);
		return orderByExpr;
	}

	public OrderByExpr orderByVSQL(String expr, String comment)
	{
		return orderByVSQL(expr, comment, (Map<String, String>)null);
	}

	/**
	<p>Add the "order by" vSQL expression {@code expr} to this query.</p>

	<p>{@code expr} may be followed by {@code asc} or {@code desc}, optionally
	followed by {@code nulls first} or {@code nulls last}.</p>

	<p>{@code replacements} are additional variables for {@code expr} only,
	given as vSQL expressions that reference the variables of the query (see
	{@link #whereVSQL(String, String, Map)} for an example).</p>
	**/
	public OrderByExpr orderByVSQL(String expr, String comment, Map<String, String> replacements)
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

		return orderByVSQL(expr, comment, ascDesc, nulls, replacements);
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
		return groupByVSQL(expr, comment, null);
	}

	/**
	<p>Add the grouping vSQL expression {@code expr} to the list of expression
	to group by.</p>

	<p>{@code replacements} are additional variables for {@code expr} only,
	given as vSQL expressions that reference the variables of the query (see
	{@link #whereVSQL(String, String, Map)} for an example).</p>
	**/
	public GroupByExpr groupByVSQL(String expr, String comment, Map<String, String> replacements)
	{
		if (fields.size() > 0)
			throw new VSQLMixedAggregationException();

		GroupByExpr newGroupByExpr = new GroupByExpr(new VSQLExpr(expr, comment, replacements));
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
		if (from.size() > 0)
		{
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
			buffer.append("\n");
		}
		else
		{
			String noFrom = getSQLSourceNoFrom(indentLevel);
			if (noFrom != null)
			{
				buffer.append(noFrom);
			}
		}

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

		// Output offset/limit clauses
		String offsetLimit = getSQLSourceOffsetLimit(indentLevel);
		if (offsetLimit != null)
		{
			buffer.append(offsetLimit);
		}

		return buffer.toString();
	}

	public String getSQLSource()
	{
		return getSQLSource(0);
	}
}
