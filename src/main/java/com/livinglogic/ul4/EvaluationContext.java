/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

import com.livinglogic.utils.CloseableRegistry;
import com.livinglogic.utils.MapChain;
import com.livinglogic.utils.MapUtils;

/**
 * An {@code EvaluationContext} object is passed around calls to the node method
 * {@link AST#evaluate} and stores an output stream and a map containing the
 * currently defined variables as well as other globally available information.
 */
public class EvaluationContext implements AutoCloseable, CloseableRegistry
{
	/**
	 * The {@code Writer} object where output can be written via {@link #write}.
	 * May by {@code null}, in which case output will be ignored.
	 */
	protected Writer writer;

	/**
	 * The list of currently active indentation strings
	 */
	protected List<String> indents;

	/**
	 * A map containing the currently defined variables
	 */
	protected Map<String, Object> variables;

	/**
	 * The currently executing template object
	 */
	InterpretedTemplate template;

	/**
	 * A {@link com.livinglogic.utils.MapChain} object chaining all variables:
	 * The user defined ones from {@link #variables} and the map containing the
	 * global functions.
	 */
	protected MapChain<String, Object> allVariables;

	/**
	 * A list of cleanup tasks that have to be done, when the
	 * {@code EvaluationContext} is no longer used
	 */
	private LinkedList<AutoCloseable> closeables;

	/**
	 * The maximum number of milliseconds of runtime that are allowed
	 * using this {@code EvaluationContext} object. This can be used to limit
	 * the runtime of a template. If negative the runtime is unlimited.
	 */
	private long milliseconds = -1;
	private long startMilliseconds;

	/**
	 * Create a new {@code EvaluationContext} object.
	 */
	public EvaluationContext()
	{
		this(null, -1);
	}

	/**
	 * Create a new {@code EvaluationContext} object.
	 * @param writer The output stream where the template output will be written
	 */
	public EvaluationContext(Writer writer)
	{
		this(writer, -1);
	}

	/**
	 * Create a new {@code EvaluationContext} object.
	 * @param milliseconds The maximum number of milliseconds allowed for
	 *              templates using this {@code EvaluationContext}.
	 */
	public EvaluationContext(long milliseconds)
	{
		this(null, milliseconds);
	}

	/**
	 * Create a new {@code EvaluationContext} object.
	 * @param writer The output stream where the template output will be written
	 * @param milliseconds The maximum number of milliseconds allowed for
	 *              templates using this {@code EvaluationContext}.
	 */
	public EvaluationContext(Writer writer, long milliseconds)
	{
		this.writer = writer;
		this.indents = new LinkedList<String>();
		variables = new HashMap<String, Object>();
		template = null;
		allVariables = new MapChain<String, Object>(variables, functions);
		closeables = new LinkedList<AutoCloseable>();
		this.milliseconds = milliseconds;
		startMilliseconds = System.currentTimeMillis();
	}

	protected void tick()
	{
		if (milliseconds >= 0 && System.currentTimeMillis() > startMilliseconds + milliseconds)
		{
			throw new RuntimeExceededException();
		}
	}

	public void pushIndent(String indent)
	{
		indents.add(indent);
	}

	public void popIndent()
	{
		indents.remove(indents.size()-1);
	}

	/**
	 * Call this when the {@code EvaluationContext} is no longer required.
	 */
	public void close()
	{
		for (AutoCloseable closeable : closeables)
		{
			try
			{
				closeable.close();
			}
			catch (Exception ex)
			{
			}
		}
		closeables.clear();
	}

	/**
	 * Call this to register a new cleanup hook.
	 */
	public void registerCloseable(AutoCloseable closeable)
	{
		closeables.add(closeable);
	}

	/**
	 * Set the writer in {@link #writer} and return the previously defined one.
	 */
	public Writer setWriter(Writer writer)
	{
		Writer oldWriter = this.writer;
		this.writer = writer;
		return oldWriter;
	}

	/**
	 * Set the active template object and return the previously active one.
	 */
	public InterpretedTemplate setTemplate(InterpretedTemplate template)
	{
		InterpretedTemplate result = this.template;
		this.template = template;
		return result;
	}

	/**
	 * Return the currently active template object.
	 */
	public InterpretedTemplate getTemplate()
	{
		return template;
	}

	/**
	 * Return the map containing the variables local to the template/function.
	 */
	public Map<String, Object> getVariables()
	{
		return variables;
	}

	/**
	 * Set a new map containing the template variables and return the previous one.
	 */
	public Map<String, Object> setVariables(Map<String, Object> variables)
	{
		if (variables == null)
			variables = new HashMap<String, Object>();
		Map<String, Object> result = this.variables;
		this.variables = variables;
		allVariables.setFirst(variables);
		return result;
	}

	/**
	 * Replace the map containing the template variables with a new map that
	 * defers non-existant keys to the previous one and return the previous one.
	 */
	public Map<String, Object> pushVariables(Map<String, Object> variables)
	{
		if (variables == null)
			variables = new HashMap<String, Object>();
		return setVariables(new MapChain<String, Object>(variables, getVariables()));
	}

	/**
	 * Return the {@code Writer} object where template output is written to.
	 */
	public Writer getWriter()
	{
		return writer;
	}

	/**
	 * Write output
	 */
	public void write(String string)
	{
		if (writer != null)
		{
			try
			{
				writer.write(string);
			}
			catch (IOException exc)
			{
				throw new RuntimeException(exc);
			}
		}
	}

	/**
	 * Store a template variable in the variable map
	 * @param key The name of the variable
	 * @param value The value of the variable
	 */
	public void set(String key, Object value)
	{
		variables.put(key, value);
	}

	/**
	 * Return a template variable
	 * @param key The name of the variable
	 */
	public Object get(String key)
	{
		Object result = allVariables.get(key);

		if ((result == null) && !allVariables.containsKey(key))
			return new UndefinedVariable(key);
		return result;
	}

	/**
	 * Delete a variable
	 * @param key The name of the variable
	 */
	public void remove(String key)
	{
		variables.remove(key);
	}

	private static Map<String, Object> functions = new HashMap<String, Object>();

	static
	{
		MapUtils.putMap(
			functions,
			"now", new FunctionNow(),
			"utcnow", new FunctionUTCNow(),
			"date", new FunctionDate(),
			"timedelta", new FunctionTimeDelta(),
			"monthdelta", new FunctionMonthDelta(),
			"random", new FunctionRandom(),
			"xmlescape", new FunctionXMLEscape(),
			"csv", new FunctionCSV(),
			"str", new FunctionStr(),
			"repr", new FunctionRepr(),
			"ascii", new FunctionASCII(),
			"int", new FunctionInt(),
			"float", new FunctionFloat(),
			"bool", new FunctionBool(),
			"list", new FunctionList(),
			"set", new FunctionSet(),
			"len", new FunctionLen(),
			"any", new FunctionAny(),
			"all", new FunctionAll(),
			"enumerate", new FunctionEnumerate(),
			"enumfl", new FunctionEnumFL(),
			"isfirstlast", new FunctionIsFirstLast(),
			"isfirst", new FunctionIsFirst(),
			"islast", new FunctionIsLast(),
			"isundefined", new FunctionIsUndefined(),
			"isdefined", new FunctionIsDefined(),
			"isnone", new FunctionIsNone(),
			"isstr", new FunctionIsStr(),
			"isint", new FunctionIsInt(),
			"isfloat", new FunctionIsFloat(),
			"isbool", new FunctionIsBool(),
			"isdate", new FunctionIsDate(),
			"islist", new FunctionIsList(),
			"isset", new FunctionIsSet(),
			"isdict", new FunctionIsDict(),
			"istemplate", new FunctionIsTemplate(),
			"isfunction", new FunctionIsFunction(),
			"iscolor", new FunctionIsColor(),
			"istimedelta", new FunctionIsTimeDelta(),
			"ismonthdelta", new FunctionIsMonthDelta(),
			"chr", new FunctionChr(),
			"ord", new FunctionOrd(),
			"hex", new FunctionHex(),
			"oct", new FunctionOct(),
			"bin", new FunctionBin(),
			"abs", new FunctionAbs(),
			"range", new FunctionRange(),
			"slice", new FunctionSlice(),
			"min", new FunctionMin(),
			"max", new FunctionMax(),
			"sum", new FunctionSum(),
			"first", new FunctionFirst(),
			"last", new FunctionLast(),
			"sorted", new FunctionSorted(),
			"type", new FunctionType(),
			"asjson", new FunctionAsJSON(),
			"fromjson", new FunctionFromJSON(),
			"asul4on", new FunctionAsUL4ON(),
			"fromul4on", new FunctionFromUL4ON(),
			"reversed", new FunctionReversed(),
			"randrange", new FunctionRandRange(),
			"randchoice", new FunctionRandChoice(),
			"format", new FunctionFormat(),
			"urlquote", new FunctionURLQuote(),
			"urlunquote", new FunctionURLUnquote(),
			"zip", new FunctionZip(),
			"rgb", new FunctionRGB(),
			"hls", new FunctionHLS(),
			"hsv", new FunctionHSV(),
			"round", new FunctionRound()
		);
	}
}
