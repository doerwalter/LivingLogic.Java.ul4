/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.Writer;
import java.io.StringWriter;

import com.livinglogic.utils.MapChain;

/**
 * Template closure
 *
 * @author W. Doerwald
 */

public class TemplateClosure implements UL4CallWithContext, UL4RenderWithContext, UL4Name, UL4Type, UL4GetItemString, UL4Attributes, UL4Repr
{
	private InterpretedTemplate template;
	private Map<String, Object> variables;
	private Signature signature;

	public TemplateClosure(InterpretedTemplate template, EvaluationContext context)
	{
		this.template = template;
		this.variables = context.getVariables();
		signature = template.signatureAST != null ? template.signatureAST.evaluate(context) : null;
	}

	public InterpretedTemplate getTemplate()
	{
		return template;
	}

	public String nameUL4()
	{
		return template.nameUL4();
	}

	public void renderUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		BoundArguments arguments = new BoundArguments(signature, template, args, kwargs);
		try
		{
			render(context, arguments.byName());
		}
		finally
		{
			// We can clean up here, as a "render" call can't pass anything to the outside world
			arguments.close();
		}
	}

	public Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		BoundArguments arguments = new BoundArguments(signature, template, args, kwargs);
		Object result = null;
		try
		{
			result = call(context, arguments.byName());
		}
		finally
		{
			// no cleanup here, as the result might be a closure that still needs the local variables
		}
		return result;
	}

	private Object call(EvaluationContext context, Map<String, Object> variables)
	{
		return template.callBound(context, new MapChain<String, Object>(variables, this.variables));
	}

	private void render(EvaluationContext context, Map<String, Object> variables)
	{
		template.renderBound(context, null, new MapChain<String, Object>(variables, this.variables));
	}

	private String renders(EvaluationContext context, Map<String, Object> variables)
	{
		Writer writer = new StringWriter();
		template.renderBound(context, writer, new MapChain<String, Object>(variables, this.variables));
		return writer.toString();
	}

	public String typeUL4()
	{
		return "template";
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
			return object.signature;
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
		switch (key)
		{
			case "renders":
				return new BoundMethodRenderS(this);
			default:
				return template.getItemStringUL4(key);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" for ");
		formatter.visit(template);
		formatter.append(">");
	}
}
