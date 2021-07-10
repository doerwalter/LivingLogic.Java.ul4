/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
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
Template closure

@author W. Doerwald
**/

public class TemplateClosure implements UL4Instance, UL4Call, UL4Render, UL4Name, UL4Dir, UL4Repr
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getModuleName()
		{
			return "ul4";
		}

		@Override
		public String getNameUL4()
		{
			return "TemplateClosure";
		}

		@Override
		public String getDoc()
		{
			return "A locally defined UL4 template";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof TemplateClosure;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private Template template;
	private Map<String, Object> variables;
	private Signature signature;

	public TemplateClosure(Template template, EvaluationContext context)
	{
		this.template = template;
		this.variables = context.getVariables();
		signature = template.signatureAST != null ? template.signatureAST.evaluate(context) : null;
	}

	public Template getTemplate()
	{
		return template;
	}

	public String getNameUL4()
	{
		return template.getNameUL4();
	}

	public String getFullNameUL4()
	{
		return template.getFullNameUL4();
	}

	public void renderUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		// We can clean up here, as a "render" call can't pass anything to the outside world
		try (BoundArguments arguments = new BoundArguments(signature, template, args, kwargs))
		{
			render(context, arguments.byName());
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

	protected static Set<String> attributes = Template.attributes;

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "renders":
				return new GenericBoundMethod<TemplateClosure>(this, "renders");
			case "render":
				return new GenericBoundMethod<TemplateClosure>(this, "render");
			case "signature":
				return signature;
			default:
				return template.getAttrUL4(context, key);
		}
	}

	@Override
	public Object callAttrUL4(EvaluationContext context, String key, List<Object> args, Map<String, Object> kwargs)
	{
		switch (key)
		{
			case "renders":
				try (BoundArguments boundArgs = new BoundArguments(signature, this, args, kwargs))
				{
					return renders(context, boundArgs.byName());
				}
			case "render":
				try (BoundArguments boundArgs = new BoundArguments(signature, this, args, kwargs))
				{
					render(context, boundArgs.byName());
					return null;
				}
			default:
				return UL4Instance.super.callAttrUL4(context, key, args, kwargs);
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
