/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
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
	 * A map containing the global variables
	 */
	protected Map<String, Object> globalVariables;

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
	 * The user defined ones from {@link #variables}, the global ones from
	 * {@link #globalVariables} and the map containing the global functions.
	 */
	protected MapChain<String, Object> allVariables;

	/**
	 * A list of cleanup tasks that have to be done, when the
	 * {@code EvaluationContext} is no longer used
	 */
	private LinkedList<AutoCloseable> closeables;

	/*
	 * A stack of currently active escaping functions
	 */
	protected List<StringEscape> escapes;

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
		this(writer, milliseconds, null);
	}

	/**
	 * Create a new {@code EvaluationContext} object.
	 * @param globalVariables The global variables that should be available in
	 *                        the template and any called recursively.
	 */
	public EvaluationContext(Map<String, Object> globalVariables)
	{
		this(null, -1, globalVariables);
	}

	/**
	 * Create a new {@code EvaluationContext} object.
	 * @param writer The output stream where the template output will be written
	 * @param globalVariables The global variables that should be available in
	 *                        the template and any called recursively.
	 */
	public EvaluationContext(Writer writer, Map<String, Object> globalVariables)
	{
		this(writer, -1, globalVariables);
	}

	/**
	 * Create a new {@code EvaluationContext} object.
	 * @param milliseconds The maximum number of milliseconds allowed for
	 *                     templates using this {@code EvaluationContext}.
	 * @param globalVariables The global variables that should be available in
	 *                        the template and any called recursively.
	 */
	public EvaluationContext(long milliseconds, Map<String, Object> globalVariables)
	{
		this(null, milliseconds, globalVariables);
	}

	/**
	 * Create a new {@code EvaluationContext} object.
	 * @param writer The output stream where the template output will be written
	 * @param milliseconds The maximum number of milliseconds allowed for
	 *                     templates using this {@code EvaluationContext}.
	 * @param globalVariables The global variables that should be available in
	 *                        the template and any called recursively.
	 */
	public EvaluationContext(Writer writer, long milliseconds, Map<String, Object> globalVariables)
	{
		this.writer = writer;
		this.indents = new LinkedList<String>();
		variables = new HashMap<String, Object>();
		if (globalVariables == null)
			globalVariables = new HashMap<String, Object>();
		this.globalVariables = globalVariables;
		template = null;
		allVariables = new MapChain<String, Object>(
			variables,
			new MapChain<String, Object>(globalVariables, functions)
		);
		closeables = new LinkedList<AutoCloseable>();
		escapes = new LinkedList<StringEscape>();
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
	 * Push a new escaping method onto the stack.
	 */
	public void pushEscape(StringEscape escape)
	{
		escapes.add(escape);
	}

	/**
	 * Pop the innermost escaping method from the stack.
	 */
	public void popEscape()
	{
		escapes.remove(escapes.size()-1);
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
	 * Return the map containing the global variables.
	 */
	public Map<String, Object> getGlobalVariables()
	{
		return globalVariables;
	}

	/**
	 * Set a new map containing the global variables and return the previous one.
	 */
	public Map<String, Object> setGlobalVariables(Map<String, Object> globalVariables)
	{
		if (globalVariables == null)
			globalVariables = new HashMap<String, Object>();
		Map<String, Object> result = this.globalVariables;
		this.globalVariables = globalVariables;
		((MapChain<String, Object>)allVariables.getSecond()).setFirst(globalVariables);
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
				for (StringEscape escape : escapes)
					string = escape.escape(string);
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

	/**
	 * Log a message on level <code>debug</code>
	 * Can be overwritten in subclasses. The default does nothing.
	 *
	 * @param mesage The log message.
	 */
	public void logDebug(String message)
	{
	}

	/**
	 * Log a message on level <code>info</code>
	 * Can be overwritten in subclasses. The default does nothing.
	 *
	 * @param mesage The log message.
	 */
	public void logInfo(String message)
	{
	}

	/**
	 * Log a message on level <code>notice</code>
	 * Can be overwritten in subclasses. The default does nothing.
	 *
	 * @param mesage The log message.
	 */
	public void logNotice(String message)
	{
	}

	/**
	 * Log a message on level <code>warning</code>
	 * Can be overwritten in subclasses. The default does nothing.
	 *
	 * @param mesage The log message.
	 */
	public void logWarning(String message)
	{
	}

	/**
	 * Log a message on level <code>error</code>
	 * Can be overwritten in subclasses. The default does nothing.
	 *
	 * @param mesage The log message.
	 */
	public void logError(String message)
	{
	}

	/**
	 * Log an exception (on level <code>exc</code>)
	 * Can be overwritten in subclasses. The default does nothing.
	 *
	 * @param exception The exception to log.
	 */
	public void logException(Throwable exception)
	{
	}

	private static Map<String, Object> functions = new HashMap<String, Object>();

	static
	{
		MapUtils.putMap(
			functions,
			"now", new FunctionNow(),
			"utcnow", new FunctionUTCNow(),
			"today", new FunctionToday(),
			"date", new FunctionDate(),
			"datetime", new FunctionDateTime(),
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
			"isdatetime", new FunctionIsDateTime(),
			"islist", new FunctionIsList(),
			"isset", new FunctionIsSet(),
			"isdict", new FunctionIsDict(),
			"istemplate", new FunctionIsTemplate(),
			"isfunction", new FunctionIsFunction(),
			"iscolor", new FunctionIsColor(),
			"istimedelta", new FunctionIsTimeDelta(),
			"ismonthdelta", new FunctionIsMonthDelta(),
			"isexception", new FunctionIsException(),
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
			"round", new FunctionRound(),
			"md5", new FunctionMD5(),
			"scrypt", new FunctionScrypt(),
			"getattr", new FunctionGetAttr(),
			"hasattr", new FunctionHasAttr(),
			"setattr", new FunctionSetAttr(),
			"dir", new FunctionDir()
		);
	}
}
