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
	 * The {@code Writer} object where output can be written via {@link #write}
	 */
	protected Writer writer;

	/**
	 * A map containing the currently defined variables
	 */
	protected Map<String, Object> variables;

	/**
	 * The currently executing template
	 *
	 */
	protected Template template;

	/**
	 * A map containing just the variable {@code self}, referencing the currently
	 * executing template
	 */
	protected Map<String, Template> templateVariables;

	/**
	 * A {@link com.livinglogic.utils.MapChain} object chaining all variables:
	 * The user defined ones from {@link #variables}, {@link #templateVariables}
	 * (referencing the current template) and the map containing the globally
	 * defined functions.
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
		this.template = null;
		this.templateVariables = makeMap("self", null);
		this.allVariables = new MapChain<String, Object>(variables, (Map)this.templateVariables);
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
	 * Set the currently executing template variable contained in {@link #templateVariables} and
	 * return the previously defined one.
	 */
	public Template setTemplate(Template template)
	{
		Template oldTemplate = this.template;
		this.template = template;
		templateVariables.put("self", template);
		return oldTemplate;
	}

	/**
	 * Get the template that is currently executing.
	 */
	public Template getTemplate()
	{
		return template;
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
	 * Set the map containing the template variables and return the previous map.
	 */
	public Map<String, Object> setVariables(Map<String, Object> variables)
	{
		Map<String, Object> oldVariables = this.variables;
		if (variables == null)
			variables = new HashMap<String, Object>();
		this.variables = variables;
		this.allVariables.setFirst(variables);
		return oldVariables;
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
