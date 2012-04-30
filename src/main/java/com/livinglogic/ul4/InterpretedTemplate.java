/*
** Copyright 2009-2012 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.antlr.runtime.*;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import com.livinglogic.utils.ObjectAsMap;
import com.livinglogic.ul4on.Utils;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.UL4ONSerializable;

public class InterpretedTemplate extends Block implements Template
{
	/**
	 * The header used in the compiled format of the template.
	 */
	public static final String HEADER = "ul4";

	/**
	 * The version number used in the compiled format of the template.
	 */
	public static final String VERSION = "17";

	/**
	 * The name of the template (defaults to <code>unnamed</code>)
	 */
	public String name = null;

	/**
	 * The start delimiter for tags (defaults to <code>&lt;?</code>)
	 */
	public String startdelim = "<?";

	/**
	 * The end delimiter for tags (defaults to <code>?&gt;</code>)
	 */
	public String enddelim = "?>";

	/**
	 * The template source (of the top-level template, i.e. subtemplates always get the full source).
	 */
	public String source = null;

	/**
	 * The locale to be used when formatting int, float or date objects.
	 */
	private Locale defaultLocale = Locale.ENGLISH;

	/**
	 * Creates an empty template object. Must be filled in later (use for creating subtemplates)
	 */
	public InterpretedTemplate(Location location, String name, String startdelim, String enddelim)
	{
		super(location);
		this.source = null;
		this.name = name;
		this.startdelim = startdelim;
		this.enddelim = enddelim;
	}

	public InterpretedTemplate(String source) throws RecognitionException
	{
		this(source, null, "<?", "?>");
	}

	public InterpretedTemplate(String source, String name) throws RecognitionException
	{
		this(source, name, "<?", "?>");
	}

	public InterpretedTemplate(String source, String startdelim, String enddelim) throws RecognitionException
	{
		this(source, null, startdelim, enddelim);
	}

	public InterpretedTemplate(String source, String name, String startdelim, String enddelim) throws RecognitionException
	{
		super((Location)null);
		this.source = source;
		this.name = name;
		this.startdelim = startdelim;
		this.enddelim = enddelim;

		if (source == null)
			return;

		List<Location> tags = InterpretedTemplate.tokenizeTags(source, name, startdelim, enddelim);

		Stack<Block> stack = new Stack<Block>();

		stack.push(this); // Stack of currently active blocks

		for (Location location : tags)
		{
			try
			{
				Block innerBlock = stack.peek();
				String type = location.getType();
				// FIXME: use a switch in Java 7
				if (type == null)
				{
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
						innerBlock.finish(this, stack.peek().getLocation(), location);
						stack.pop();
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
				else if (type.equals("def"))
				{
					// Copy over the attributes that we know now, the source is set once the <?end?> tag is encountered
					InterpretedTemplate subtemplate = new InterpretedTemplate(location, location.getCode(), startdelim, enddelim);
					innerBlock.append(subtemplate);
					stack.push(subtemplate);
				}
				else
				{
					// Can't happen
					throw new RuntimeException("unknown tag " + type);
				}
			}
			catch (LocationException ex)
			{
				throw ex; // we have no info to add
			}
			catch (Exception ex)
			{
				throw new LocationException(ex, location);
			}
		}
		if (stack.size() > 1) // the template itself is still on the stack
		{
			Block innerBlock = stack.peek();
			throw new LocationException(new BlockException(innerBlock.getType() + " block unclosed"), innerBlock.getLocation());
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String toString(int indent)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("def " + (name != null ? name : "unnamed") + "(**vars)\n");
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("{\n");
		++indent;
		for (AST item : content)
			buffer.append(item.toString(indent));
		--indent;
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append("}\n");
		return buffer.toString();
	}

	/**
	 * loads a template from a string in the UL4ON serialization format.
	 * @param data The template in serialized form.
	 * @return The template object.
	 * @throws IOException if reading from the stream fails
	 */
	public static InterpretedTemplate loads(String data)
	{
		return (InterpretedTemplate)Utils.loads(data);
	}

	/**
	 * writes the Template object to a string in the UL4ON serialization format.
	 * @return The string containing the template in serialized form.
	 */
	public String dumps()
	{
		return Utils.dumps(this);
	}

	// public Reader reader(Map<String, Object> variables)
	// {
	// 	return new IteratorReader(new Renderer(variables));
	// }

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(name, this);
		return null;
	}

	/**
	 * Renders the template to a java.io.Writer object.
	 * @param writer    the java.io.Writer object to which the output is written.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null.
	 */
	public void render(java.io.Writer writer, Map<String, Object> variables) throws IOException
	{
		if (variables == null)
			variables = new HashMap<String, Object>();
		EvaluationContext context = new EvaluationContext(writer, variables, defaultLocale);
		try
		{
			super.evaluate(context);
		}
		catch (BreakException ex)
		{
			throw ex;
		}
		catch (ContinueException ex)
		{
			throw ex;
		}
		catch (LocationException ex)
		{
			if (ex.location != location && location != null)
				throw new LocationException(ex, location);
			else
				throw ex;
		}
		catch (Exception ex)
		{
			throw new LocationException(ex, location);
		}
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null
	 * @return The render output as a string.
	 */
	public String renders(Map<String, Object> variables)
	{
		StringWriter output = new StringWriter();
		try
		{
			render(output, variables);
		}
		catch (IOException ex)
		{
			// can't happen
		}
		return output.toString();
	}

	public static List<Location> tokenizeTags(String source, String name, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(printx|print|code|for|if|elif|else|end|break|continue|def|note)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
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

		StringBuffer output = new StringBuffer(len);

		for (int i = 0; i < len; ++i)
		{
			char c = input.charAt(i);
			if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9')))
				output.append('\\');
			output.append(c);
		}
		return output.toString();
	}

	public JSPTemplate compileToJava() throws java.io.IOException
	{
		StringBuffer source = new StringBuffer();
		source.append("\tpublic String getName()\n");
		source.append("\t{\n");
		source.append("\t\treturn \"" + StringEscapeUtils.escapeJava(name) + "\";\n");
		source.append("\t}\n");
		source.append("\n");
		source.append("\tpublic void render(java.io.Writer out, java.util.Map<String, Object> variables) throws java.io.IOException\n");
		source.append("\t{\n");
		source.append(javaSource());
		source.append("\t}\n");

		Class clazz = com.livinglogic.ul4.Utils.compileToJava(source.toString(), "com.livinglogic.ul4.JSPTemplate", null);
		try
		{
			return (JSPTemplate)clazz.newInstance();
		}
		catch (InstantiationException ex)
		{
			// Can't happen
			throw new RuntimeException(ex);
		}
		catch (IllegalAccessException ex)
		{
			// Can't happen
			throw new RuntimeException(ex);
		}
	}

	public String getType()
	{
		return "template";
	}

	public void finish(InterpretedTemplate template, Location startLocation, Location endLocation)
	{
		String type = endLocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("def"))
			throw new BlockException("def ended by end" + type);
		source = template.source.substring(startLocation.starttag, endLocation.endtag);
	}

	public boolean handleLoopControl(String name)
	{
		throw new BlockException(name + " outside of for loop");
	}

	public String javaSource()
	{
		return new JavaSource4Template(this).toString();
	}

	public String javascriptSource()
	{
		return new JavascriptSource4Template(this).toString();
	}

	static
	{
		Utils.register("de.livinglogic.ul4.location", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Location(null, null, -1, -1, -1, -1); }});
		Utils.register("de.livinglogic.ul4.text", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Text(null); }});
		Utils.register("de.livinglogic.ul4.none", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LoadNone(null); }});
		Utils.register("de.livinglogic.ul4.true", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LoadTrue(null); }});
		Utils.register("de.livinglogic.ul4.false", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LoadFalse(null); }});
		Utils.register("de.livinglogic.ul4.int", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LoadInt(null, 0); }});
		Utils.register("de.livinglogic.ul4.float", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LoadFloat(null, 0.0); }});
		Utils.register("de.livinglogic.ul4.str", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LoadStr(null, null); }});
		Utils.register("de.livinglogic.ul4.date", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LoadDate(null, null); }});
		Utils.register("de.livinglogic.ul4.color", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LoadColor(null, null); }});
		Utils.register("de.livinglogic.ul4.list", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.List(null); }});
		Utils.register("de.livinglogic.ul4.dict", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Dict(null); }});
		Utils.register("de.livinglogic.ul4.loadvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Name(null, null); }});
		Utils.register("de.livinglogic.ul4.ieie", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ConditionalBlockBlock(null); }});
		Utils.register("de.livinglogic.ul4.if", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.If(null, null); }});
		Utils.register("de.livinglogic.ul4.elif", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ElIf(null, null); }});
		Utils.register("de.livinglogic.ul4.else", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Else(null); }});
		Utils.register("de.livinglogic.ul4.for", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ForNormal(null, null, null); }});
		Utils.register("de.livinglogic.ul4.foru", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ForUnpack(null, null); }});
		Utils.register("de.livinglogic.ul4.break", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Break(null); }});
		Utils.register("de.livinglogic.ul4.continue", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Continue(null); }});
		Utils.register("de.livinglogic.ul4.getattr", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetAttr(null, null, null); }});
		Utils.register("de.livinglogic.ul4.getslice", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetSlice(null, null, null, null); }});
		Utils.register("de.livinglogic.ul4.not", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Not(null, null); }});
		Utils.register("de.livinglogic.ul4.neg", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Neg(null, null); }});
		Utils.register("de.livinglogic.ul4.print", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Print(null, null); }});
		Utils.register("de.livinglogic.ul4.printx", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.PrintX(null, null); }});
		Utils.register("de.livinglogic.ul4.getitem", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GetItem(null, null, null); }});
		Utils.register("de.livinglogic.ul4.eq", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.EQ(null, null, null); }});
		Utils.register("de.livinglogic.ul4.ne", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NE(null, null, null); }});
		Utils.register("de.livinglogic.ul4.lt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LT(null, null, null); }});
		Utils.register("de.livinglogic.ul4.le", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.LE(null, null, null); }});
		Utils.register("de.livinglogic.ul4.gt", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GT(null, null, null); }});
		Utils.register("de.livinglogic.ul4.ge", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.GE(null, null, null); }});
		Utils.register("de.livinglogic.ul4.contains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Contains(null, null, null); }});
		Utils.register("de.livinglogic.ul4.notcontains", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.NotContains(null, null, null); }});
		Utils.register("de.livinglogic.ul4.add", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Add(null, null, null); }});
		Utils.register("de.livinglogic.ul4.sub", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Sub(null, null, null); }});
		Utils.register("de.livinglogic.ul4.mul", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Mul(null, null, null); }});
		Utils.register("de.livinglogic.ul4.floordiv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDiv(null, null, null); }});
		Utils.register("de.livinglogic.ul4.truediv", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDiv(null, null, null); }});
		Utils.register("de.livinglogic.ul4.or", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Or(null, null, null); }});
		Utils.register("de.livinglogic.ul4.and", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.And(null, null, null); }});
		Utils.register("de.livinglogic.ul4.mod", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Mod(null, null, null); }});
		Utils.register("de.livinglogic.ul4.storevar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.StoreVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.addvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.AddVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.subvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.SubVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.mulvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.MulVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.floordivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.FloorDivVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.truedivvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.TrueDivVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.modvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.ModVar(null, null, null); }});
		Utils.register("de.livinglogic.ul4.delvar", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.DelVar(null, null); }});
		Utils.register("de.livinglogic.ul4.callfunc", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallFunc(null, (Function)null); }});
		Utils.register("de.livinglogic.ul4.callmeth", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallMeth(null, null, (Method)null); }});
		Utils.register("de.livinglogic.ul4.callmethkw", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallMethKeywords(null, null, (KeywordMethod)null); }});
		// Utils.register("de.livinglogic.ul4.render", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Render(null); }});
		Utils.register("de.livinglogic.ul4.template", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.InterpretedTemplate((Location)null, null, null, null); }});
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(HEADER);
		encoder.dump(VERSION);
		encoder.dump(source);
		encoder.dump(name);
		encoder.dump(startdelim);
		encoder.dump(enddelim);
		super.dumpUL4ON(encoder);
	}

	public void loadUL4ON(Decoder decoder) throws IOException
	{
		String header = (String)decoder.load();
		if (!header.equals(HEADER))
			throw new RuntimeException("Invalid header, expected " + HEADER + ", got " + header);
		String version = (String)decoder.load();
		if (!VERSION.equals(version))
		{
			throw new RuntimeException("Invalid version, expected " + VERSION + ", got " + version);
		}
		source = (String)decoder.load();
		name = (String)decoder.load();
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
			v.put("name", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).name;}});
			v.put("startdelim", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).startdelim;}});
			v.put("enddelim", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).enddelim;}});
			v.put("source", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).source;}});
			// v.put("opcodes", new ValueMaker(){public Object getValue(Object object){return ((InterpretedTemplate)object).opcodes;}});
			valueMakers = v;
		}
		return valueMakers;
	}
}
