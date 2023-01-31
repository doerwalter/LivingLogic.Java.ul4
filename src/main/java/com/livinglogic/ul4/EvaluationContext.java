/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
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
import java.util.Timer;

import com.livinglogic.utils.CloseableRegistry;
import com.livinglogic.utils.MapChain;
import com.livinglogic.utils.MapUtils;
import com.livinglogic.utils.InterruptTimerTask;

/**
An {@code EvaluationContext} object is passed around calls to the node method
{@link AST#evaluate} and stores an output stream and a map containing the
currently defined variables as well as other globally available information.
**/
public class EvaluationContext implements AutoCloseable, CloseableRegistry
{
	/**
	The {@code Writer} object where output can be written via {@link #write}.
	May by {@code null}, in which case output will be ignored.
	**/
	protected Writer writer;

	/**
	The list of currently active indentation strings
	**/
	protected List<String> indents;

	/**
	A map containing the global variables
	**/
	protected Map<String, Object> globalVariables;

	/**
	A map containing the currently defined variables
	**/
	protected Map<String, Object> variables;

	/**
	The currently executing template object
	**/
	Template template;

	/**
	A {@link com.livinglogic.utils.MapChain} object chaining all variables:
	The user defined ones from {@link #variables}, the global ones from
	{@link #globalVariables} and the map containing the global functions.
	**/
	protected MapChain<String, Object> allVariables;

	/**
	A list of cleanup tasks that have to be done, when the
	{@code EvaluationContext} is no longer used
	**/
	private LinkedList<AutoCloseable> closeables;

	/*
	A stack of currently active escaping functions
	**/
	protected List<StringEscape> escapes;

	/**
	The maximum number of milliseconds of runtime that are allowed
	using this {@code EvaluationContext} object. This can be used to limit
	the runtime of a template. If negative the runtime is unlimited.
	**/
	private long milliseconds = -1;

	/**
	The timer used to limit the maimum runtime
	**/
	private Timer timer;

	/**
	Create a new {@code EvaluationContext} object.
	**/
	public EvaluationContext()
	{
		this(null, -1, null);
	}

	/**
	Create a new {@code EvaluationContext} object.

	@param writer The output stream where the template output will be written
	**/
	public EvaluationContext(Writer writer)
	{
		this(writer, -1, null);
	}

	/**
	Create a new {@code EvaluationContext} object.

	@param milliseconds The maximum number of milliseconds allowed for
	                    templates using this {@code EvaluationContext}. If
	                    {@code milliseconds} is &lt; 0 there's no runtime limit.
	**/
	public EvaluationContext(long milliseconds)
	{
		this(null, milliseconds, null);
	}

	/**
	Create a new {@code EvaluationContext} object.
	@param writer The output stream where the template output will be written
	@param milliseconds The maximum number of milliseconds allowed for
	                    templates using this {@code EvaluationContext}. If
	                    {@code milliseconds} is &lt; 0 there's no runtime limit.
	**/
	public EvaluationContext(Writer writer, long milliseconds)
	{
		this(writer, milliseconds, null);
	}

	/**
	Create a new {@code EvaluationContext} object.
	@param globalVariables The global variables that should be available in
	                       the template and any called recursively.
	**/
	public EvaluationContext(Map<String, Object> globalVariables)
	{
		this(null, -1, globalVariables);
	}

	/**
	Create a new {@code EvaluationContext} object.
	@param writer The output stream where the template output will be written
	@param globalVariables The global variables that should be available in
	                       the template and any called recursively.
	**/
	public EvaluationContext(Writer writer, Map<String, Object> globalVariables)
	{
		this(writer, -1, globalVariables);
	}

	/**
	Create a new {@code EvaluationContext} object.
	@param milliseconds The maximum number of milliseconds allowed for
	                    templates using this {@code EvaluationContext}. If
	                    {@code milliseconds} is &lt; 0 there's no runtime limit.
	@param globalVariables The global variables that should be available in
	                       the template and any called recursively.
	**/
	public EvaluationContext(long milliseconds, Map<String, Object> globalVariables)
	{
		this(null, milliseconds, globalVariables);
	}

	/**
	Create a new {@code EvaluationContext} object.

	{@code milliseconds} can be used to limit the runtime of the template using
	this {@code EvaluationContext}. In reality this means that the code
	instantiating the {@code EvaluationContext} has the specified amount of time
	before a timer thread will interupt the thread that created the
	{@code EvaluationContext}. This timer will be cancel when the
	{@code EvaluationContext} gets closed before the timer fires.

	@param writer The output stream where the template output will be written
	@param milliseconds The maximum number of milliseconds allowed for
	                    templates using this {@code EvaluationContext}. If
	                    {@code milliseconds} is &lt; 0 there's no runtime limit.
	@param globalVariables The global variables that should be available in
	                       the template and any called recursively.
	**/
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
			new MapChain<String, Object>(globalVariables, builtins)
		);
		closeables = new LinkedList<AutoCloseable>();
		escapes = new LinkedList<StringEscape>();
		this.milliseconds = milliseconds;
		if (milliseconds >= 0)
		{
			timer = new Timer("Runtime monitor for UL4 template", true);
			InterruptTimerTask interruptTimerTask = new InterruptTimerTask(Thread.currentThread());
			timer.schedule(interruptTimerTask, milliseconds);
		}
		else
			timer = null;
	}

	protected void tick()
	{
		if (Thread.interrupted())
			throw new RuntimeException(new InterruptedException("Maximum runtime of " + milliseconds + " ms exceeded"));
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
	Call this when the {@code EvaluationContext} is no longer required.
	**/
	@Override
	public void close()
	{
		if (timer != null)
			timer.cancel();
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
	Call this to register a new cleanup hook.
	**/
	public void registerCloseable(AutoCloseable closeable)
	{
		closeables.add(closeable);
	}

	/**
	Push a new escaping method onto the stack.
	**/
	public void pushEscape(StringEscape escape)
	{
		escapes.add(escape);
	}

	/**
	Pop the innermost escaping method from the stack.
	**/
	public void popEscape()
	{
		escapes.remove(escapes.size()-1);
	}

	/**
	Set the writer in {@link #writer} and return the previously defined one.
	**/
	public Writer setWriter(Writer writer)
	{
		Writer oldWriter = this.writer;
		this.writer = writer;
		return oldWriter;
	}

	/**
	Set the active template object and return the previously active one.
	**/
	public Template setTemplate(Template template)
	{
		Template result = this.template;
		this.template = template;
		return result;
	}

	/**
	Return the currently active template object.
	**/
	public Template getTemplate()
	{
		return template;
	}

	/**
	Return the map containing the variables local to the template/function.
	**/
	public Map<String, Object> getVariables()
	{
		return variables;
	}

	/**
	Set a new map containing the template variables and return the previous one.
	**/
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
	Return the map containing the global variables.
	**/
	public Map<String, Object> getGlobalVariables()
	{
		return globalVariables;
	}

	/**
	Set a new map containing the global variables and return the previous one.
	**/
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
	Replace the map containing the template variables with a new map that
	defers non-existant keys to the previous one and return the previous one.
	**/
	public Map<String, Object> pushVariables(Map<String, Object> variables)
	{
		if (variables == null)
			variables = new HashMap<String, Object>();
		return setVariables(new MapChain<String, Object>(variables, getVariables()));
	}

	/**
	Return the {@code Writer} object where template output is written to.
	**/
	public Writer getWriter()
	{
		return writer;
	}

	/**
	Write output
	**/
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
	Store a template variable in the variable map
	@param key The name of the variable
	@param value The value of the variable
	**/
	public void set(String key, Object value)
	{
		variables.put(key, value);
	}

	/**
	Return a template variable
	@param key The name of the variable
	**/
	public Object get(String key)
	{
		Object result = allVariables.get(key);

		if ((result == null) && !allVariables.containsKey(key))
			return new UndefinedVariable(key);
		return result;
	}

	/**
	Delete a variable
	@param key The name of the variable
	**/
	public void remove(String key)
	{
		variables.remove(key);
	}

	/**
	Log a message on level <code>debug</code>
	Can be overwritten in subclasses. The default does nothing.

	@param message The log message.
	**/
	public void logDebug(String message)
	{
	}

	/**
	Log an exception on level <code>debug</code>
	Can be overwritten in subclasses. The default does nothing.

	@param exception The exception to log.
	**/
	public void logDebug(Throwable exception)
	{
	}

	/**
	Log a message on level <code>info</code>
	Can be overwritten in subclasses. The default does nothing.

	@param message The log message.
	**/
	public void logInfo(String message)
	{
	}

	/**
	Log an exception on level <code>info</code>
	Can be overwritten in subclasses. The default does nothing.

	@param exception The exception to log.
	**/
	public void logInfo(Throwable exception)
	{
	}

	/**
	Log a message on level <code>notice</code>
	Can be overwritten in subclasses. The default does nothing.

	@param message The log message.
	**/
	public void logNotice(String message)
	{
	}

	/**
	Log an exception on level <code>notice</code>
	Can be overwritten in subclasses. The default does nothing.

	@param exception The exception to log.
	**/
	public void logNotice(Throwable exception)
	{
	}

	/**
	Log a message on level <code>warning</code>
	Can be overwritten in subclasses. The default does nothing.

	@param message The log message.
	**/
	public void logWarning(String message)
	{
	}

	/**
	Log an exception on level <code>warning</code>
	Can be overwritten in subclasses. The default does nothing.

	@param exception The exception to log.
	**/
	public void logWarning(Throwable exception)
	{
	}

	/**
	Log a message on level <code>error</code>
	Can be overwritten in subclasses. The default does nothing.

	@param message The log message.
	**/
	public void logError(String message)
	{
	}

	/**
	Log an exception on level <code>error</code>
	Can be overwritten in subclasses. The default does nothing.

	@param exception The exception to log.
	**/
	public void logError(Throwable exception)
	{
	}

	private static Map<String, Object> builtins = new HashMap<String, Object>();

	static
	{
		MapUtils.putMap(
			builtins,
			"now", FunctionNow.function,
			"utcnow", FunctionUTCNow.function,
			"today", FunctionToday.function,
			"date", Date_.type,
			"datetime", DateTime.type,
			"timedelta", TimeDelta.type,
			"monthdelta", MonthDelta.type,
			"random", FunctionRandom.function,
			"xmlescape", FunctionXMLEscape.function,
			"csv", FunctionCSV.function,
			"str", Str.type,
			"repr", FunctionRepr.function,
			"ascii", FunctionASCII.function,
			"int", Int.type,
			"float", Float_.type,
			"bool", Bool.type,
			"list", List_.type,
			"set", Set_.type,
			"dict", Dict.type,
			"function", Function.type,
			"len", FunctionLen.function,
			"any", FunctionAny.function,
			"all", FunctionAll.function,
			"enumerate", FunctionEnumerate.function,
			"enumfl", FunctionEnumFL.function,
			"isfirstlast", FunctionIsFirstLast.function,
			"isfirst", FunctionIsFirst.function,
			"islast", FunctionIsLast.function,
			"isundefined", FunctionIsUndefined.function,
			"isdefined", FunctionIsDefined.function,
			"isnone", FunctionIsNone.function,
			"isstr", FunctionIsStr.function,
			"isint", FunctionIsInt.function,
			"isfloat", FunctionIsFloat.function,
			"isbool", FunctionIsBool.function,
			"isdate", FunctionIsDate.function,
			"isdatetime", FunctionIsDateTime.function,
			"islist", FunctionIsList.function,
			"isset", FunctionIsSet.function,
			"isdict", FunctionIsDict.function,
			"istemplate", FunctionIsTemplate.function,
			"isfunction", FunctionIsFunction.function,
			"iscolor", FunctionIsColor.function,
			"istimedelta", FunctionIsTimeDelta.function,
			"ismonthdelta", FunctionIsMonthDelta.function,
			"isexception", FunctionIsException.function,
			"isinstance", FunctionIsInstance.function,
			"chr", FunctionChr.function,
			"ord", FunctionOrd.function,
			"hex", FunctionHex.function,
			"oct", FunctionOct.function,
			"bin", FunctionBin.function,
			"abs", FunctionAbs.function,
			"range", FunctionRange.function,
			"slice", FunctionSlice.function,
			"min", FunctionMin.function,
			"max", FunctionMax.function,
			"sum", FunctionSum.function,
			"first", FunctionFirst.function,
			"last", FunctionLast.function,
			"sorted", FunctionSorted.function,
			"type", FunctionType.function,
			"asjson", FunctionAsJSON.function,
			"fromjson", FunctionFromJSON.function,
			"asul4on", FunctionAsUL4ON.function,
			"fromul4on", FunctionFromUL4ON.function,
			"reversed", FunctionReversed.function,
			"randrange", FunctionRandRange.function,
			"randchoice", FunctionRandChoice.function,
			"format", FunctionFormat.function,
			"urlquote", FunctionURLQuote.function,
			"urlunquote", FunctionURLUnquote.function,
			"zip", FunctionZip.function,
			"rgb", FunctionRGB.function,
			"hls", FunctionHLS.function,
			"hsv", FunctionHSV.function,
			"round", FunctionRound.function,
			"floor", FunctionFloor.function,
			"ceil", FunctionCeil.function,
			"md5", FunctionMD5.function,
			"scrypt", FunctionScrypt.function,
			"getattr", FunctionGetAttr.function,
			"hasattr", FunctionHasAttr.function,
			"setattr", FunctionSetAttr.function,
			"dir", FunctionDir.function,
			"ul4", ModuleUL4.module,
			"ul4on", ModuleUL4ON.module,
			"math", ModuleMath.module,
			"operator", ModuleOperator.module,
			"color", ModuleColor.module
		);
	}
}
