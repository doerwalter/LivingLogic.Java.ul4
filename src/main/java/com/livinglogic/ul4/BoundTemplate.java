/*
** Copyright 2009-2023 by LivingLogic AG, Bayreuth/Germany
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
import static com.livinglogic.utils.SetUtils.makeExtendedSet;

/**
A template bound as an attribute of an object.

Calling/rendering the bound template will pass the object as the first argument.

@author W. Doerwald
**/

public class BoundTemplate implements UL4Instance, UL4Call, UL4Render, UL4Name, UL4Dir, UL4Repr
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
			return "BundTemplate";
		}

		@Override
		public String getDoc()
		{
			return "An UL4 template bound to an object";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof BoundTemplate;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	private Object self;
	private Template template;

	public BoundTemplate(Object self, Template template)
	{
		this.self = self;
		this.template = template;
	}

	public Object getSelf()
	{
		return self;
	}

	public Template getTemplate()
	{
		return template;
	}

	@Override
	public String getNameUL4()
	{
		return template.getNameUL4();
	}

	@Override
	public String getFullNameUL4()
	{
		return template.getFullNameUL4();
	}

	public void renderUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		// We can clean up here, as a "render" call can't pass anything to the outside world
		BoundArguments arguments = new BoundArguments(template.signature, template, self, args, kwargs);
		render(context, arguments.byName());
	}

	public Object callUL4(EvaluationContext context, List<Object> args, Map<String, Object> kwargs)
	{
		BoundArguments arguments = new BoundArguments(template.signature, template, self, args, kwargs);
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
		return template.callBound(context, variables);
	}

	private void render(EvaluationContext context, Map<String, Object> variables)
	{
		template.renderBound(context, null, variables);
	}

	private String renders(EvaluationContext context, Map<String, Object> variables)
	{
		Writer writer = new StringWriter();
		template.renderBound(context, writer, variables);
		return writer.toString();
	}

	protected static Set<String> attributes = makeExtendedSet(
		Template.attributes,
		"self",
		"template"
	);

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
				return new GenericBoundMethod<BoundTemplate>(this, "renders");
			case "render":
				return new GenericBoundMethod<BoundTemplate>(this, "render");
			case "self":
				return self;
			case "template":
				return template;
			case "signature":
				return template.signature;
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
				BoundArguments boundRenderSArgs = new BoundArguments(template.signature, this, self, args, kwargs);
				return renders(context, boundRenderSArgs.byName());
			case "render":
				BoundArguments boundRenderArgs = new BoundArguments(template.signature, this, self, args, kwargs);
				render(context, boundRenderArgs.byName());
				return null;
			default:
				return UL4Instance.super.callAttrUL4(context, key, args, kwargs);
		}
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" self=");
		formatter.visit(self);
		formatter.append(" template=");
		formatter.visit(template);
		formatter.append(">");
	}
}
