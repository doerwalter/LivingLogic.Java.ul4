/*
** Copyright 2009-2018 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static com.livinglogic.utils.SetUtils.makeExtendedSet;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import static java.util.Arrays.asList;
import java.io.IOException;

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;

public class RenderBlocksAST extends RenderAST implements BlockLike
{
	protected Tag endtag;
	protected List<AST> content;

	public RenderBlocksAST(Tag tag, Slice pos, AST obj)
	{
		super(tag, pos, obj);
		endtag = null;
		content = new LinkedList<AST>();
	}

	/**
	 * This is used to "convert" a {@link CallAST} that comes out of the parser into a {@code RenderBlockAST}
	 */
	public RenderBlocksAST(CallAST call)
	{
		super(call);
		endtag = null;
		content = new LinkedList<AST>();
	}

	@Override
	public String getType()
	{
		return "renderblocks";
	}

	public void toString(Formatter formatter)
	{
		formatter.write(getType());
		formatter.write(" ");
		super.toString(formatter);
		if (indent != null)
		{
			formatter.write(" with indent ");
			formatter.write(FunctionRepr.call(indent.getCodeText()));
		}
		formatter.indent();
		BlockAST.blockToString(formatter, content);
		formatter.dedent();
	}

	@Override
	public IndentAST popTrailingIndent()
	{
		if (content.size() > 0)
		{
			AST lastItem = content.get(content.size()-1);
			if (lastItem instanceof IndentAST)
			{
				content.remove(content.size()-1);
				return (IndentAST)lastItem;
			}
		}
		return null;
	}

	@Override
	public void append(AST item)
	{
		content.add(item);
	}

	@Override
	public void finish(Tag endtag)
	{
		String type = endtag.getCodeText().trim();
		if (type != null && type.length() != 0 && !type.equals("renderblocks"))
			throw new BlockException("renderblocks ended by end" + type);
		this.endtag = endtag;
	}

	@Override
	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for/while loop");
	}

	@Override
	public void call(EvaluationContext context, UL4RenderWithContext obj, List<Object> args, Map<String, Object> kwargs)
	{
		Map<String, Object> variables = new LinkedHashMap<String, Object>();
		Map<String, Object> oldVariables = context.pushVariables(variables);

		try
		{
			for (AST item : content)
				item.decoratedEvaluate(context);
		}
		finally
		{
			context.setVariables(oldVariables);
		}

		for (String key : variables.keySet())
		{
			if (kwargs.containsKey(key))
				throw new DuplicateArgumentException(obj, key);
		}
		kwargs.putAll(variables);
		super.call(context, obj, args, kwargs);
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(endtag);
		encoder.dump(content);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		endtag = (Tag)decoder.load();
		content = (List<AST>)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(RenderAST.attributes, "endtag", "content");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getAttrUL4(String key)
	{
		switch (key)
		{
			case "endtag":
				return endtag;
			case "content":
				return content;
			default:
				return super.getAttrUL4(key);
		}
	}
}
