/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Map;
import java.util.HashMap;

import com.livinglogic.utils.ObjectAsMap;
import com.livinglogic.utils.MapChain;

/**
 * Template closure
 *
 * @author W. Doerwald
 */

public class TemplateClosure extends ObjectAsMap implements Template, UL4Type
{
	private InterpretedTemplate template;
	private Map<String, Object> variables;

	public TemplateClosure(InterpretedTemplate template, Map<String, Object> variables)
	{
		this.template = template;
		this.variables = new HashMap<String, Object>(variables);
	}

	public InterpretedTemplate getTemplate()
	{
		return template;
	}

	public String getName()
	{
		return template.getName();
	}

	public void render(EvaluationContext context) throws java.io.IOException
	{
		context.pushVariables(new HashMap<String, Object>(variables));
		try
		{
			template.render(context);
		}
		finally
		{
			context.popVariables();
		}
	}

	// The following methods are here, because we can't extend both ObjectAsMap and CompiledTemplate,
	// so we extend ObjectAsMap and reimplement the methods of CompiledTemplate
	public void render(EvaluationContext context, Map<String, Object> variables) throws java.io.IOException
	{
		context.pushVariables(this.variables);

		try
		{
			context.pushVariables(variables);
			try
			{
				template.render(context);
			}
			finally
			{
				context.popVariables();
			}
		}
		finally
		{
			context.popVariables();
		}
	}

	public void render(Writer out, Map<String, Object> variables) throws java.io.IOException
	{
		render(new EvaluationContext(out, variables, template.getKeepWhitespace()));
	}

	public String renders(EvaluationContext context)
	{
		StringWriter out = new StringWriter();

		Writer oldWriter = context.setWriter(out);
		try
		{
			render(context);
		}
		catch (IOException ex)
		{
			// Can't happen with a StringWriter!
		}
		finally
		{
			context.setWriter(oldWriter);
		}
		return out.toString();
	}

	public String renders(EvaluationContext context, Map<String, Object> variables)
	{
		context.pushVariables(variables);
		try
		{
			return renders(context);
		}
		finally
		{
			context.popVariables();
		}
	}

	public String renders(Map<String, Object> variables)
	{
		return renders(new EvaluationContext(null, variables, template.getKeepWhitespace()));
	}

	public String typeUL4()
	{
		return "template";
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>();
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((TemplateClosure)object).getTemplate().getName();}});
			// The following attributes will only work if the template really is an InterpretedTemplate
			v.put("location", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)((TemplateClosure)object).getTemplate()).getLocation();}});
			v.put("endlocation", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)((TemplateClosure)object).getTemplate()).getEndLocation();}});
			v.put("content", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)((TemplateClosure)object).getTemplate()).getContent();}});
			v.put("startdelim", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)((TemplateClosure)object).getTemplate()).getStartDelim();}});
			v.put("enddelim", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)((TemplateClosure)object).getTemplate()).getEndDelim();}});
			v.put("source", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)((TemplateClosure)object).getTemplate()).getSource();}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
