/*
** Copyright 2009-2015 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import static java.util.Arrays.asList;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class CallAST extends CodeAST
{
	protected AST obj;
	protected List<Argument> arguments = new LinkedList<Argument>();

	public CallAST(Tag tag, int start, int end, AST obj)
	{
		super(tag, start, end);
		this.obj = obj;
	}

	public String getType()
	{
		return "call";
	}

	public void appendArgument(AST arg)
	{
		arguments.add(new Argument(arg));
	}

	public void appendKeywordArgument(String name, AST arg)
	{
		arguments.add(new KeywordArgument(name, arg));
	}

	public void appendRemainingArguments(AST arg)
	{
		arguments.add(new RemainingArguments(arg));
	}

	public void appendRemainingKeywordArguments(AST arg)
	{
		arguments.add(new RemainingKeywordArguments(arg));
	}

	@Override
	public Object decoratedEvaluate(EvaluationContext context)
	{
		// Overwrite with a version that rewrap ASTException and TagException too.
		try
		{
			context.tick();
			return evaluate(context);
		}
		catch (BreakException ex)
		{
			throw ex;
		}
		catch (ContinueException ex)
		{
			throw ex;
		}
		catch (ReturnException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new ASTException(ex, context.getTemplate(), this);
		}
	}

	public Object evaluate(EvaluationContext context)
	{
		Object realObject = obj.decoratedEvaluate(context);

		List<Object> realArguments = new ArrayList<Object>();
		Map<String, Object> realKeywordArguments = new HashMap<String, Object>();

		for (Argument argument : arguments)
			argument.addToCallArguments(context, realObject, realArguments, realKeywordArguments);

		return call(context, realObject, realArguments, realKeywordArguments);
	}

	public Object call(UL4Call obj, List<Object> args, Map<String, Object> kwargs)
	{
		return obj.callUL4(args, kwargs);
	}

	public Object call(EvaluationContext context, UL4CallWithContext obj, List<Object> args, Map<String, Object> kwargs)
	{
		return obj.callUL4(context, args, kwargs);
	}

	public Object call(EvaluationContext context, Object obj, List<Object> args, Map<String, Object> kwargs)
	{
		if (obj instanceof UL4Call)
			return call((UL4Call)obj, args, kwargs);
		else if (obj instanceof UL4CallWithContext)
			return call(context, (UL4CallWithContext)obj, args, kwargs);
		throw new NotCallableException(obj);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(obj);
		List argumentList = new LinkedList();
		for (Argument arg : arguments)
			argumentList.add(asList(arg.getName(), arg.getArg()));
		encoder.dump(argumentList);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		obj = (AST)decoder.load();
		List<List> argumentList = (List<List>)decoder.load();
		for (List namearg : argumentList)
		{
			String name = (String)namearg.get(0);
			AST arg = (AST)namearg.get(1);
			if (name == null)
				appendArgument(arg);
			else if (name.equals("*"))
				appendRemainingArguments(arg);
			else if (name.equals("**"))
				appendRemainingKeywordArguments(arg);
			else
				appendKeywordArgument(name, arg);
		}
	}

	protected static Set<String> attributes = makeExtendedSet(CodeAST.attributes, "obj", "args");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		if ("obj".equals(key))
			return obj;
		else if ("args".equals(key))
			return arguments;
		else
			return super.getItemStringUL4(key);
	}
}
