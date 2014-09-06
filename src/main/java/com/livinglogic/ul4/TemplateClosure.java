/*
** Copyright 2009-2014 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

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
		// The template (i.e. the closure) itself should be visible in the parent variables
		this.variables.put(template.nameUL4(), this);
	}

	public InterpretedTemplate getTemplate()
	{
		return template;
	}

	public String nameUL4()
	{
		return template.nameUL4();
	}

	public Object callUL4(EvaluationContext context, Object[] args, Map<String, Object> kwargs)
	{
		if (args.length > 0)
			throw new PositionalArgumentsNotSupportedException(nameUL4());
		return call(context, kwargs);
	}

	public Object call(EvaluationContext context, Map<String, Object> variables)
	{
		return template.call(context, new MapChain<String, Object>(variables, this.variables));
	}

	public void render(EvaluationContext context, Map<String, Object> variables)
	{
		template.render(context, new MapChain<String, Object>(variables, this.variables));
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
			return "template.render";
		}

		private static final Signature signature = new Signature("kwargs", Signature.remainingKeywordArguments);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(EvaluationContext context, Object[] args)
		{
			object.render(context, (Map<String, Object>)args[0]);
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
			return "template.renders";
		}

		private static final Signature signature = new Signature("kwargs", Signature.remainingKeywordArguments);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(EvaluationContext context, Object[] args)
		{
			return object.renders(context, (Map<String, Object>)args[0]);
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
