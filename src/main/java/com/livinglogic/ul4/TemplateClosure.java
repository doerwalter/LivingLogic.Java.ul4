/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.livinglogic.utils.MapChain;

/**
 * Template closure
 *
 * @author W. Doerwald
 */

public class TemplateClosure implements UL4CallWithContext, UL4Name, UL4Type, UL4Attributes
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

	public Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		BoundArguments arguments = new BoundArguments(template.signature, template, args, kwargs);
		Object result = null;
		try
		{
			result = call(context, arguments.byName());
		}
		finally
		{
			arguments.cleanup();
		}
		return result;
	}

	public Object call(EvaluationContext context, Map<String, Object> variables)
	{
		return template.call(context, new MapChain<String, Object>(variables, this.variables));
	}

	public void render(EvaluationContext context, Map<String, Object> variables)
	{
		template.render(context, null, new MapChain<String, Object>(variables, this.variables));
	}

	public String renders(EvaluationContext context, Map<String, Object> variables)
	{
		return template.renders(context, new MapChain<String, Object>(variables, this.variables));
	}

	public String typeUL4()
	{
		return "template";
	}

	private static class BoundMethodRender extends BoundMethodWithContext<TemplateClosure>
	{
		public BoundMethodRender(TemplateClosure object)
		{
			super(object);
		}

		public String nameUL4()
		{
			String name = object.nameUL4();
			return (name != null ? name : "template") + ".render";
		}

		public Signature getSignature()
		{
			return object.template.getSignature();
		}

		public Object evaluate(EvaluationContext context, BoundArguments arguments)
		{
			object.render(context, arguments.byName());
			return null;
		}
	}

	private static class BoundMethodRenderS extends BoundMethodWithContext<TemplateClosure>
	{
		public BoundMethodRenderS(TemplateClosure object)
		{
			super(object);
		}

		public String nameUL4()
		{
			String name = object.nameUL4();
			return (name != null ? name : "template") + ".renders";
		}

		public Signature getSignature()
		{
			return object.template.getSignature();
		}

		public Object evaluate(EvaluationContext context, BoundArguments arguments)
		{
			return object.renders(context, arguments.byName());
		}
	}

	protected static Set<String> attributes = InterpretedTemplate.attributes;

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("render".equals(key))
			return new BoundMethodRender(this);
		else if ("renders".equals(key))
			return new BoundMethodRenderS(this);
		else
			return template.getItemStringUL4(key);
	}
}
