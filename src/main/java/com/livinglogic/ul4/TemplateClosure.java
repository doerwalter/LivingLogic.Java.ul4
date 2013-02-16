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

public class TemplateClosure extends ObjectAsMap implements Template, UL4CallableWithContext, UL4Name, UL4Type
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

	public String nameUL4()
	{
		return template.nameUL4();
	}

	public String formatText(String text)
	{
		return template.formatText(text);
	}

	public void render(EvaluationContext context) throws java.io.IOException
	{
		Map<String, Object> oldVariables = context.setVariables(variables);
		try
		{
			template.render(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	public void render(EvaluationContext context, Map<String, Object> variables) throws java.io.IOException
	{
		Map<String, Object> oldVariables = context.setVariables(new MapChain<String, Object>(variables, this.variables));
		try
		{
			template.render(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	public void render(Writer writer, Map<String, Object> variables) throws java.io.IOException
	{
		render(new EvaluationContext(writer, variables));
	}

	public String renders(EvaluationContext context)
	{
		StringWriter writer = new StringWriter();

		Writer oldWriter = context.setWriter(writer);
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
		return writer.toString();
	}

	public String renders(EvaluationContext context, Map<String, Object> variables)
	{
		StringWriter writer = new StringWriter();

		Writer oldWriter = context.setWriter(writer);
		try
		{
			render(context, variables);
		}
		catch (IOException ex)
		{
			// Can't happen with a StringWriter!
		}
		finally
		{
			context.setWriter(oldWriter);
		}
		return writer.toString();
	}

	public String renders(Map<String, Object> variables)
	{
		return renders(new EvaluationContext(null, variables));
	}


	public Object callUL4(EvaluationContext context, Object[] args, Map<String, Object> kwargs)
	{
		if (args.length > 0)
			throw new PositionalArgumentsNotSupportedException(nameUL4());
		return call(context, kwargs);
	}

	public Object call(EvaluationContext context)
	{
		Map<String, Object> oldVariables = context.setVariables(variables);
		try
		{
			return template.call(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}
	}

	public Object call(EvaluationContext context, Map<String, Object> variables)
	{
		return template.call(context, new MapChain<String, Object>(variables, this.variables));
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
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((TemplateClosure)object).getTemplate().nameUL4();}});
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
