/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.livinglogic.utils.Closeable;
import com.livinglogic.utils.CloseableRegistry;
import com.livinglogic.utils.MapChain;
import com.livinglogic.utils.MapUtils;

/**
 * An {@code EvaluationContext} object is passed around calls to the node method
 * {@link AST#evaluate} and stores an output stream and a map containing the
 * currently defined variables as well as other globally available information.
 */
public class EvaluationContext implements Closeable, CloseableRegistry
{
	/**
	 * The {@code Writer} object where output can be written via {@link #write}.
	 * May by {@code null}, in which case output will be ignored.
	 */
	protected Writer writer;

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
	private LinkedList<Closeable> closeables;

	/**
	 * The maximum number of milliseconds of runtime that are allowed
	 * using this {@code EvaluationContext} object. This can be use to limit
	 * the runtime of a template. If negative the runtime is unlimited.
	 */
	private long milliseconds = -1;
	private long startMilliseconds;
	/**
	 * Create a new {@code EvaluationContext} object. No variables will
	 * be available to the template code.
	 * @param writer The output stream where the template output will be written
	 */
	public EvaluationContext(Writer writer)
	{
		this(writer, null, -1);
	}

	/**
	 * Create a new {@code EvaluationContext} object
	 * @param writer The output stream where the template output will be written
	 * @param variables The template variables that will be available to the
	 *                  template code (or {@code null} for no variables)
	 */
	public EvaluationContext(Writer writer, Map<String, Object> variables)
	{
		this(writer, variables, -1);
	}
	/**
	 * Create a new {@code EvaluationContext} object
	 * @param writer The output stream where the template output will be written
	 * @param variables The template variables that will be available to the
	 *                  template code (or {@code null} for no variables)
	 * @param milliseconds The maximum number of milliseconds allowed for
	 *              templates using this {@code EvaluationContext}.
	 */
	public EvaluationContext(Writer writer, Map<String, Object> variables, long milliseconds)
	{
		this.writer = writer;
		if (variables == null)
			variables = new HashMap<String, Object>();
		this.variables = variables;
		this.template = null;
		this.allVariables = new MapChain<String, Object>(variables, functions);
		this.closeables = new LinkedList<Closeable>();
		this.milliseconds = milliseconds;
		this.startMilliseconds = System.currentTimeMillis();
	}

	protected void tick()
	{
		if (milliseconds >= 0 && System.currentTimeMillis() > startMilliseconds + milliseconds)
		{
			throw new RuntimeExceededException();
		}
	}
	/**
	 * Call this when the {@code EvaluationContext} is no longer required.
	 */
	public void close()
	{
		for (Closeable closeable : closeables)
		{
			try
			{
				closeable.close();
			}
			catch (Exception ex)
			{
			}
		}
	}

	/**
	 * Call this to register a new cleanup hook.
	 */
	public void registerCloseable(Closeable closeable)
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
	 * Return the map containing the all variables.
	 */
	public Map<String, Object> getAllVariables()
	{
		return allVariables;
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
	 * deferres non-existant keys to the previous one and return the previous one.
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
		if ("self".equals(key))
			throw new RuntimeException("can't assign to self");
		variables.put(key, value);
	}

	/**
	 * Return a template variable
	 * @param key The name of the variable
	 * @throws KeyException if the variable isn't defined
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
			"print", new FunctionPrint(),
			"printx", new FunctionPrintX(),
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
			"int", new FunctionInt(),
			"float", new FunctionFloat(),
			"bool", new FunctionBool(),
			"list", new FunctionList(),
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
