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

import com.livinglogic.ul4on.Decoder;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.ul4on.Utils;

public class InterpretedTemplate extends Block implements Template, UL4Type
{
	/**
	 * The version number used in the UL4ON dump of the template.
	 */
	public static final String VERSION = "22";

	/**
	 * The name of the template (defaults to {@code "unnamed"})
	 */
	public String name = null;

	/**
	 * The start delimiter for tags (defaults to {@code "<?"})
	 */
	public String startdelim = "<?";

	/**
	 * The end delimiter for tags (defaults to {@code "?>"})
	 */
	public String enddelim = "?>";

	/**
	 * The template source (of the top-level template, i.e. subtemplates always get the full source).
	 */
	public String source = null;

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

		List<Location> tags = InterpretedTemplate.tokenizeTags(source, startdelim, enddelim);

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
						innerBlock.finish(this, location);
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
				else if (type.equals("render"))
				{
					UL4Parser parser = getParser(location);
					innerBlock.append(new Render(location, parser.expression()));
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
			catch (TagException ex)
			{
				throw ex; // we have no info to add
			}
			catch (Exception ex)
			{
				throw new TagException(ex, location);
			}
		}
		if (stack.size() > 1) // the template itself is still on the stack
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

	public String getName()
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

	public String getStartDelim()
	{
		return startdelim;
	}

	public String getEndDelim()
	{
		return enddelim;
	}

	public String toString(int indent)
	{
		StringBuilder buffer = new StringBuilder();
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
	 */
	public static InterpretedTemplate loads(String data)
	{
		return (InterpretedTemplate)Utils.loads(data);
	}

	/**
	 * loads a template from a reader in the UL4ON serialization format.
	 * @param reader The Reader object from which to read the template.
	 * @return The template object.
	 * @throws IOException if reading from the stream fails
	 */
	public static InterpretedTemplate load(Reader reader) throws IOException
	{
		return (InterpretedTemplate)Utils.load(reader);
	}

	/**
	 * writes the Template object to a string in the UL4ON serialization format.
	 * @return The string containing the template in serialized form.
	 */
	public String dumps()
	{
		return Utils.dumps(this);
	}

	public Object evaluate(EvaluationContext context) throws IOException
	{
		context.put(name, new TemplateClosure(this, context.getAllVariables()));
		return null;
	}

	/**
	 * Renders the template.
	 * @param context   the EvaluationContext.
	 */
	public void render(EvaluationContext context) throws IOException
	{
		context.pushTemplate(this);
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
		catch (Exception ex)
		{
			if (location == null)
				throw new TemplateException(ex, this);
			else
				throw new TagException(ex, location);
		}
		finally
		{
			context.popTemplate();
		}
	}

	/**
	 * Renders the template using the passed in variables.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null.
	 */
	public void render(EvaluationContext context, Map<String, Object> variables) throws IOException
	{
		context.pushVariables(variables);
		try
		{
			render(context);
		}
		finally
		{
			context.popVariables();
		}
	}

	/**
	 * Renders the template to a java.io.Writer object.
	 * @param writer    the java.io.Writer object to which the output is written.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null.
	 */
	public void render(java.io.Writer writer, Map<String, Object> variables) throws IOException
	{
		render(new EvaluationContext(writer, variables));
	}

	/**
	 * Renders the template and returns the resulting string.
	 * @return The render output as a string.
	 */
	public String renders(EvaluationContext context)
	{
		StringWriter output = new StringWriter();
		Writer oldWriter = context.setWriter(output);
		try
		{
			render(context);
		}
		catch (IOException ex)
		{
			// can't happen
		}
		finally
		{
			context.setWriter(oldWriter);
		}
		return output.toString();
	}

	/**
	 * Renders the template using the passed in variables and returns the resulting string.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null
	 * @return The render output as a string.
	 */
	public String renders(EvaluationContext context, Map<String, Object> variables)
	{
		context.pushVariables(variables);
		try
		{
			return renders(context);
		}
		finally
		{
			context.popVariables();
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

	private static class RenderRunnable implements Runnable
	{
		protected InterpretedTemplate template;
		protected Writer writer;
		protected Map<String, Object> variables;
		
		public RenderRunnable(InterpretedTemplate template, Writer writer, Map<String, Object> variables)
		{
			this.template = template;
			this.writer = writer;
			this.variables = variables;
		}

		@Override
		public void run()
		{
			try
			{
				template.render(writer, variables);
				writer.close();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Renders the template and returns a Reader object from which the template
	 * output can be read.
	 * @param variables a map containing the top level variables that should be
	 *                  available to the template code. May be null
	 * @return The reader from which the template output can be read.
	 * @throws IOException 
	 */
	public Reader reader(Map<String, Object> variables) throws IOException
	{
		PipedReader reader = new PipedReader(10);
		PipedWriter writer = new PipedWriter(reader);
		new Thread(new RenderRunnable(this, writer, variables)).start();
		return reader;
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
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(printx|print|code|for|if|elif|else|end|break|continue|def|render|note)(\\s*(.*?)\\s*)?" + escapeREchars(enddelim), Pattern.DOTALL);
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

	public String getType()
	{
		return "template";
	}

	public void finish(InterpretedTemplate template, Location endlocation)
	{
		super.finish(template, endlocation);
		String type = endlocation.getCode().trim();
		if (type != null && type.length() != 0 && !type.equals("def"))
			throw new BlockException("def ended by end" + type);
		source = template.source;
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
		Utils.register("de.livinglogic.ul4.callfunc", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallFunc((Function)null); }});
		Utils.register("de.livinglogic.ul4.callmeth", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.CallMeth(null, (Method)null); }});
		Utils.register("de.livinglogic.ul4.render", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.Render(null, null); }});
		Utils.register("de.livinglogic.ul4.template", new ObjectFactory(){ public UL4ONSerializable create() { return new com.livinglogic.ul4.InterpretedTemplate((Location)null, null, null, null); }});
	}

	public void dumpUL4ON(Encoder encoder) throws IOException
	{
		encoder.dump(VERSION);
		encoder.dump(source);
		encoder.dump(name);
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
		startdelim = (String)decoder.load();
		enddelim = (String)decoder.load();
		super.loadUL4ON(decoder);
	}

	public String typeUL4()
	{
		return "template";
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
			valueMakers = v;
		}
		return valueMakers;
	}
}
