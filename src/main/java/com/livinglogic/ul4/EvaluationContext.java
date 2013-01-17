/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static com.livinglogic.utils.MapUtils.makeMap;
import com.livinglogic.utils.MapChain;

/**
 * An {@code EvaluationContext} object is passed around calls to the node method
 * {@link AST#evaluate} and stores an output stream and a map containing the
 * currently defined variables.
 */
public class EvaluationContext
{
	/**
	 * The {@code Writer} object where output can be written via {@link #write}.
	 * May by {@code null}, in which case outputs will be ignored.
	 */
	protected Writer writer;

	/**
	 * A map containing the currently defined variables
	 */
	protected Map<String, Object> variables;

	/**
	 * The stack of currently executing templates.
	 */
	protected Stack<Template> stack;

	/**
	 * A {@link com.livinglogic.utils.MapChain} object chaining all variables:
	 * The user defined ones from {@link #variables} and the one containing
	 * {@link #stack}
	 */
	protected MapChain<String, Object> allVariables;

	/**
	 * Create a new {@code EvaluationContext} object. (No variables) will
	 * be available to the template code.
	 * @param writer The output stream where the template output will be written
	 */
	public EvaluationContext(Writer writer)
	{
		this(writer, null);
	}

	/**
	 * Create a new {@code EvaluationContext} object
	 * @param writer The output stream where the template output will be written
	 * @param variables The template variables that will be available to the
	 *                  template code (or {@code null} for no variables)
	 */
	public EvaluationContext(Writer writer, Map<String, Object> variables)
	{
		this.writer = writer;
		if (variables == null)
			variables = new HashMap<String, Object>();
		this.variables = variables;
		this.stack = new Stack<Template>();
		this.allVariables = new MapChain<String, Object>(variables, makeMap("stack", stack));
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
	 * Push a template onto the stack.
	 */
	public void pushTemplate(Template template)
	{
		stack.push(template);
	}

	/**
	 * Remove the template from the top of the stack and return it.
	 */
	public Template popTemplate()
	{
		return stack.pop();
	}

	/**
	 * Return the template at the top of the stack
	 */
	public Template getTemplate()
	{
		return stack.peek();
	}

	/**
	 * Return the map containing the variables local to the template.
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
	 * Push a new map containing the template variables in front of the map chain
	 */
	public void pushVariables(Map<String, Object> variables)
	{
		if (variables == null)
			variables = new HashMap<String, Object>();
		this.variables = variables;
		this.allVariables = new MapChain<String, Object>(variables, allVariables);
	}

	/**
	 * Pop the frontmost map from the map chain and return it
	 */
	public Map<String, Object> popVariables()
	{
		Map<String, Object> first = allVariables.getFirst();
		Map<String, Object> second = allVariables.getSecond();
		if (second instanceof MapChain)
		{
			allVariables = (MapChain<String, Object>)second;
			variables = allVariables.getFirst();
			return first;
		}
		return null;
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
	public void write(String string) throws IOException
	{
		if (writer != null)
			writer.write(string);
	}

	/**
	 * Store a template variable in the variable map
	 * @param key The name of the variable
	 * @param value The value of the variable
	 */
	public void put(String key, Object value)
	{
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
	 * Return a template variable or a default value if the variable isn't defined
	 * @param key The name of the variable
	 * @param defaultValue Will be returned if the variable named {@code key} is
	 *                     not defined
	 */
	public Object get(String key, Object defaultValue)
	{
		Object result = allVariables.get(key);

		if ((result == null) && !allVariables.containsKey(key))
			return defaultValue;
		return result;
	}

	/**
	 * Delete a template variable
	 * @param key The name of the variable
	 */
	public void remove(String key)
	{
		variables.remove(key);
	}
}
