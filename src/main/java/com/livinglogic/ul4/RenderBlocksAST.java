/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
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
	protected static class Type extends RenderAST.Type
	{
		@Override
		public String getNameUL4()
		{
			return "RenderBlocksAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.ul4.renderblocks";
		}

		@Override
		public String getDoc()
		{
			return "AST node for rendering a template and passing additional arguments via\nnested variable definitions, e.g.::";
		}

		@Override
		public RenderBlocksAST create(String id)
		{
			return new RenderBlocksAST(null, -1, -1, null);
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof RenderBlocksAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	protected List<AST> content;

	public RenderBlocksAST(Template template, int posStart, int posStop, AST obj)
	{
		super(template, posStart, posStop, obj);
		content = new LinkedList<AST>();
	}

	/**
	This is used to "convert" a {@link CallAST} that comes out of the parser into a {@code RenderBlockAST}
	**/
	public RenderBlocksAST(CallAST call)
	{
		super(call);
		content = new LinkedList<AST>();
	}

	@Override
	public String getType()
	{
		return "renderblocks";
	}

	@Override
	public String getBlockTag()
	{
		return "<?renderblocks?>";
	}

	public void toString(Formatter formatter)
	{
		formatter.write(getType());
		formatter.write(" ");
		super.toString(formatter);
		if (indent != null)
		{
			formatter.write(" with indent ");
			formatter.write(FunctionRepr.call(indent.getText()));
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
		String type = endtag.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("renderblocks"))
			throw new BlockException("<?renderblocks?> ended by <?end " + type + "?>");
		setStopPos(endtag.getStartPosStart(), endtag.getStartPosStop());
	}

	@Override
	public boolean handleLoopControl(String name)
	{
		throw new BlockException("<?" + name + "?> outside of <?for?>/<?while?> loop");
	}

	@Override
	protected void makeArguments(EvaluationContext context, List<Object> realArguments, Map<String, Object> realKeywordArguments)
	{
		super.makeArguments(context, realArguments, realKeywordArguments);

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
			if (realKeywordArguments.containsKey(key))
				throw new DuplicateArgumentException(obj, key);
		}
		realKeywordArguments.putAll(variables);
	}

	@Override
	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		super.dumpUL4ON(encoder);
		encoder.dump(stopPosStart);
		encoder.dump(stopPosStop);
		encoder.dump(content);
	}

	@Override
	public void loadUL4ON(Decoder decoder) throws IOException
	{
		super.loadUL4ON(decoder);
		setStopPos((int)decoder.load(), (int)decoder.load());
		content = (List<AST>)decoder.load();
	}

	protected static Set<String> attributes = makeExtendedSet(RenderAST.attributes, "stoppos", "stopline", "stopcol", "stopsource", "stopsourceprefix", "stopsourcesuffix", "content");

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
			case "stoppos":
				return getStopPos();
			case "stopline":
				return getStopLine();
			case "stopcol":
				return getStopCol();
			case "stopsource":
				return getStopSource();
			case "stopsourceprefix":
				return getStopSourcePrefix();
			case "stopsourcesuffix":
				return getStopSourceSuffix();
			case "content":
				return content;
			default:
				return super.getAttrUL4(context, key);
		}
	}
}
