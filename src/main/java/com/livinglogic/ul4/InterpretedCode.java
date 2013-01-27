/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.StringEscapeUtils;

import static com.livinglogic.utils.StringUtils.removeWhitespace;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.ul4on.Utils;

public abstract class InterpretedCode extends Block implements UL4Name
{
	/**
	 * The version number used in the UL4ON dump of the template/function.
	 */
	public static final String VERSION = "24";

	/**
	 * The name of the template/function (defaults to {@code null})
	 */
	public String name = null;

	/**
	 * Should whitespace be skipped when outputting text nodes?
	 * (i.e. linefeed and the whitespace after the linefeed will be skipped. Other spaces/tabs etc. will not be skipped)
	 */
	public boolean keepWhitespace = true;

	/**
	 * The start delimiter for tags (defaults to {@code "<?"})
	 */
	public String startdelim = "<?";

	/**
	 * The end delimiter for tags (defaults to {@code "?>"})
	 */
	public String enddelim = "?>";

	/**
	 * The template/function source (of the top-level template/function, i.e. subtemplates/subfunctions always get the full source).
	 */
	public String source = null;

	/**
	 * Creates an {@code InterpretedCode} object. The content will be filled later through a call to {@link #compile)
	 */
	public InterpretedCode(Location location, String source, String name, boolean keepWhitespace, String startdelim, String enddelim)
	{
		super(location);
		this.source = source;
		this.name = name;
		this.keepWhitespace = keepWhitespace;
		this.startdelim = startdelim;
		this.enddelim = enddelim;
	}

	protected void compile() throws RecognitionException
	{
		if (source == null)
			return;

		List<Location> tags = tokenizeTags(source, startdelim, enddelim);

		// Stack of currently active blocks
		Stack<Block> stack = new Stack<Block>();
		stack.push(this);

		// Stack of currently active code objects
		Stack<InterpretedCode> codeStack = new Stack<InterpretedCode>();
		codeStack.push(this);

		for (Location location : tags)
		{
			try
			{
				Block innerBlock = stack.peek();
				String type = location.getType();
				// FIXME: use a switch in Java 7
				if (type == null)
				{
					if (codeStack.peek() instanceof InterpretedTemplate)
						innerBlock.append(new Text(location));
				}
				else if (type.equals("print"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new Print(location, parser.expression()));
				}
				else if (type.equals("printx"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new PrintX(location, parser.expression()));
				}
				else if (type.equals("code"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(parser.stmt());
				}
				else if (type.equals("if"))
				{
					UL4Parser parser = getParser(location);
					ConditionalBlockBlock node = new ConditionalBlockBlock(location, new If(location, parser.expression()));
					innerBlock.append(node);
					stack.push(node);
				}
				else if (type.equals("elif"))
				{
					if (innerBlock instanceof ConditionalBlockBlock)
					{
						UL4Parser parser = getParser(location);
						((ConditionalBlockBlock)innerBlock).startNewBlock(new ElIf(location, parser.expression()));
					}
					else
						throw new BlockException("elif doesn't match any if");
				}
				else if (type.equals("else"))
				{
					if (innerBlock instanceof ConditionalBlockBlock)
					{
						((ConditionalBlockBlock)innerBlock).startNewBlock(new Else(location));
					}
					else
						throw new BlockException("else doesn't match any if");
				}
				else if (type.equals("end"))
				{
					if (stack.size() > 1)
					{
						innerBlock.finish(location);
						if (stack.pop() instanceof InterpretedCode)
							codeStack.pop();
					}
					else
						throw new BlockException("not in any block");
				}
				else if (type.equals("for"))
				{
					UL4Parser parser = getParser(location);
					Block node = parser.for_();
					innerBlock.append(node);
					stack.push(node);
				}
				else if (type.equals("break"))
				{
					for (int i = stack.size()-1; i >= 0; --i)
					{
						if (stack.get(i).handleLoopControl("break"))
							break;
					}
					innerBlock.append(new Break(location));
				}
				else if (type.equals("continue"))
				{
					for (int i = stack.size()-1; i >= 0; --i)
					{
						if (stack.get(i).handleLoopControl("continue"))
							break;
					}
					innerBlock.append(new Continue(location));
				}
				else if (type.equals("render"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new Render(location, parser.expression()));
				}
				else if (type.equals("return"))
				{
					if (codeStack.peek() instanceof InterpretedTemplate)
						throw new BlockException("return in template");
					UL4Parser parser = getParser(location);
					innerBlock.append(new Return(location, parser.expression()));
				}
				else if (type.equals("template"))
				{
					// Copy over all the attributes, however passing a {@link Location} will prevent compilation
					InterpretedTemplate subtemplate = new InterpretedTemplate(location, source, location.getCode(), keepWhitespace, startdelim, enddelim);
					innerBlock.append(subtemplate);
					stack.push(subtemplate);
					codeStack.push(subtemplate);
				}
				else if (type.equals("function"))
				{
					// Copy over all the attributes, however passing a {@link Location} will prevent compilation
					InterpretedFunction subfunction = new InterpretedFunction(location, source, location.getCode(), keepWhitespace, startdelim, enddelim);
					innerBlock.append(subfunction);
					stack.push(subfunction);
					codeStack.push(subfunction);
				}
				else
				{
					// Can't happen
					throw new RuntimeException("unknown tag " + type);
				}
			}
			catch (TagException ex)
			{
				throw ex; // we have no info to add
			}
			catch (Exception ex)
			{
				throw new TagException(ex, location);
			}
		}
		if (stack.size() > 1) // the template/function itself is still on the stack
		{
			Block innerBlock = stack.peek();
			throw new TagException(new BlockException(innerBlock.getType() + " block unclosed"), innerBlock.getLocation());
		}
	}

	private UL4Parser getParser(Location location)
	{
		return getParser(location, location.getCode());
	}

	private UL4Parser getParser(Location location, String source)
	{
		ANTLRStringStream input = new ANTLRStringStream(source);
		UL4Lexer lexer = new UL4Lexer(location, input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		UL4Parser parser = new UL4Parser(location, tokens);
		return parser;
	}

	public String nameUL4()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSource()
	{
		return source;
	}

	public boolean getKeepWhitespace()
	{
		return keepWhitespace;
	}

	public String getStartDelim()
	{
		return startdelim;
	}

	public String getEndDelim()
	{
		return enddelim;
	}

	public String toString(InterpretedCode code, int indent)
	{
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append(this.getType() + " " + (name != null ? name : "unnamed") + "(**vars)\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(code, indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}

	public String toString()
	{
		return toString(this, 0);
	}

	/**
	 * writes the {@code InterpretedCode} object to a string in the UL4ON serialization format.
	 * @return The string containing the template/function in serialized form.
	 */
	public String dumps()
	{
		return Utils.dumps(this);
	}

	public String formatText(String text)
	{
		return keepWhitespace ? text : removeWhitespace(text);
	}

	/**
	 * Split the template source into tags and literal text.
	 * @param source The sourcecode of the template
	 * @param startdelim The start delimiter for template tags (usually {@code "<?"})
	 * @param enddelim The end delimiter for template tags (usually {@code "?>"})
	 * @return A list of {@link Location} objects
	 */
	public static List<Location> tokenizeTags(String source, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(printx|print|code|for|if|elif|else|end|break|continue|template|function|return|render|note)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
		LinkedList<Location> tags = new LinkedList<Location>();
		if (source != null)
		{
			Matcher matcher = tagPattern.matcher(source);
			int pos = 0;

			int start;
			int end;
			while (matcher.find())
			{
				start = matcher.start();
				end = start + matcher.group().length();
				if (pos != start)
					tags.add(new Location(source, null, pos, start, pos, start));
				int codestart = matcher.start(3);
				int codeend = codestart + matcher.group(3).length();
				String type = matcher.group(1);
				if (!type.equals("note"))
					tags.add(new Location(source, matcher.group(1), start, end, codestart, codeend));
				pos = end;
			}
			end = source.length();
			if (pos != end)
				tags.add(new Location(source, null, pos, end, pos, end));
		}
		return tags;
	}

	private static String escapeREchars(String input)
	{
		int len = input.length();

		StringBuilder buffer = new StringBuilder(len);

		for (int i = 0; i < len; ++i)
		{
			char c = input.charAt(i);
			if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9')))
				buffer.append('\\');
			buffer.append(c);
		}
		return buffer.toString();
	}

	public void finish(Location endlocation)
	{
		super.finish(endlocation);
		String type = endlocation.getCode().trim();
		String myType = getType();
		if (type != null && type.length() != 0 && !type.equals(myType))
			throw new BlockException(myType + " ended by end" + type);
	}

	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for loop");
	}

	static
	{
		Utils.register("de.livinglogic.ul4.location", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Location(null, null, -1, -1, -1, -1); }});
		Utils.register("de.livinglogic.ul4.text", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Text(null); }});
		Utils.register("de.livinglogic.ul4.const", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Const(null); }});
		Utils.register("de.livinglogic.ul4.list", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.List(); }});
		Utils.register("de.livinglogic.ul4.listcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ListComprehension(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.dict", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Dict(); }});
		Utils.register("de.livinglogic.ul4.dictcomp", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.DictComprehension(null, null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.genexpr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GeneratorExpression(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.var", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Var(null); }});
		Utils.register("de.livinglogic.ul4.ieie", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ConditionalBlockBlock(null); }});
		Utils.register("de.livinglogic.ul4.if", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.If(null, null); }});
		Utils.register("de.livinglogic.ul4.elif", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ElIf(null, null); }});
		Utils.register("de.livinglogic.ul4.else", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Else(null); }});
		Utils.register("de.livinglogic.ul4.for", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.For(null, null, null); }});
		Utils.register("de.livinglogic.ul4.break", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Break(null); }});
		Utils.register("de.livinglogic.ul4.continue", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Continue(null); }});
		Utils.register("de.livinglogic.ul4.getattr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetAttr(null, null); }});
		Utils.register("de.livinglogic.ul4.getslice", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetSlice(null, null, null); }});
		Utils.register("de.livinglogic.ul4.not", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Not(null); }});
		Utils.register("de.livinglogic.ul4.neg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Neg(null); }});
		Utils.register("de.livinglogic.ul4.print", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Print(null, null); }});
		Utils.register("de.livinglogic.ul4.printx", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.PrintX(null, null); }});
		Utils.register("de.livinglogic.ul4.return", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Return(null, null); }});
		Utils.register("de.livinglogic.ul4.getitem", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetItem(null, null); }});
		Utils.register("de.livinglogic.ul4.eq", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.EQ(null, null); }});
		Utils.register("de.livinglogic.ul4.ne", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NE(null, null); }});
		Utils.register("de.livinglogic.ul4.lt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LT(null, null); }});
		Utils.register("de.livinglogic.ul4.le", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LE(null, null); }});
		Utils.register("de.livinglogic.ul4.gt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GT(null, null); }});
		Utils.register("de.livinglogic.ul4.ge", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GE(null, null); }});
		Utils.register("de.livinglogic.ul4.contains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Contains(null, null); }});
		Utils.register("de.livinglogic.ul4.notcontains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NotContains(null, null); }});
		Utils.register("de.livinglogic.ul4.add", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Add(null, null); }});
		Utils.register("de.livinglogic.ul4.sub", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Sub(null, null); }});
		Utils.register("de.livinglogic.ul4.mul", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Mul(null, null); }});
		Utils.register("de.livinglogic.ul4.floordiv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDiv(null, null); }});
		Utils.register("de.livinglogic.ul4.truediv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDiv(null, null); }});
		Utils.register("de.livinglogic.ul4.or", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Or(null, null); }});
		Utils.register("de.livinglogic.ul4.and", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.And(null, null); }});
		Utils.register("de.livinglogic.ul4.mod", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Mod(null, null); }});
		Utils.register("de.livinglogic.ul4.storevar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.StoreVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.addvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AddVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.subvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SubVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.mulvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.MulVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.floordivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDivVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.truedivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDivVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.modvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ModVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.callfunc", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallFunc(null); }});
		Utils.register("de.livinglogic.ul4.callmeth", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallMeth(null, (Method)null); }});
		Utils.register("de.livinglogic.ul4.render", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Render(null, null); }});
		Utils.register("de.livinglogic.ul4.template", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.InterpretedTemplate(null, null, null, false, null, null); }});
		Utils.register("de.livinglogic.ul4.function", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.InterpretedFunction(null, null, null, false, null, null); }});
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(VERSION);
		encoder.dump(source);
		encoder.dump(name);
		encoder.dump(keepWhitespace);
		encoder.dump(startdelim);
		encoder.dump(enddelim);
		super.dumpUL4ON(encoder);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		String version = (String)decoder.load();
		if (!VERSION.equals(version))
		{
			throw new RuntimeException("Invalid version, expected " + VERSION + ", got " + version);
		}
		source = (String)decoder.load();
		name = (String)decoder.load();
		keepWhitespace = (Boolean)decoder.load();
		startdelim = (String)decoder.load();
		enddelim = (String)decoder.load();
		super.loadUL4ON(decoder);
	}

	private static Map<String, ValueMaker> valueMakers = null;

	public Map<String, ValueMaker> getValueMakers()
	{
		if (valueMakers == null)
		{
			HashMap<String, ValueMaker> v = new HashMap<String, ValueMaker>(super.getValueMakers());
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((InterpretedCode)object).name;}});
			v.put("keepws", new ValueMaker(){public Object getValue(Object object){return ((InterpretedCode)object).keepWhitespace;}});
			v.put("startdelim", new ValueMaker(){public Object getValue(Object object){return ((InterpretedCode)object).startdelim;}});
			v.put("enddelim", new ValueMaker(){public Object getValue(Object object){return ((InterpretedCode)object).enddelim;}});
			v.put("source", new ValueMaker(){public Object getValue(Object object){return ((InterpretedCode)object).source;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
