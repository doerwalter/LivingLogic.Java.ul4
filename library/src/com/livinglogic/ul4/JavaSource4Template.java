package com.livinglogic.ul4;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;

public class JavaSource4Template
{
	private InterpretedTemplate template;
	private StringBuffer buffer;
	private int initialIndent;
	private int indent;
	private String variables;

	public JavaSource4Template(InterpretedTemplate template)
	{
		this(template, 2, "variables");
	}

	public JavaSource4Template(InterpretedTemplate template, int indent)
	{
		this(template, indent, "variables");
	}

	public JavaSource4Template(InterpretedTemplate template, String variables)
	{
		this(template, 2, variables);
	}

	public JavaSource4Template(InterpretedTemplate template, int indent, String variables)
	{
		this.template = template;
		this.initialIndent = indent;
		this.variables = variables;
	}

	private void code(String code)
	{
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append(code);
		buffer.append("\n");
	}

	public String output(String expression)
	{
		return "out.write(" + expression + ");";
	}

	public String toString()
	{
		buffer = new StringBuffer();
		indent = initialIndent;
		int varcounter = 0;
		Location lastLocation = null;

		code("//@@@ BEGIN template source");
		// FIXME: Implement this
		code("//@@@ BEGIN template code");
		for (int i = 0; i < 10; ++i)
			code("Object r" + i + " = null;");

		int size = template.opcodes.size();

		for (int i = 0; i < size; ++i)
		{
			Opcode opcode = template.opcodes.get(i);
			if (opcode.location != lastLocation && opcode.name != Opcode.OC_TEXT)
			{
				lastLocation = opcode.location;
				String code = Utils.repr(lastLocation.getTag());
				code = code.substring(1, code.length()-1);
				code("// " + lastLocation + ": " + code);
			}

			switch (opcode.name)
			{
				case Opcode.OC_TEXT:
					code(output("\"" + StringEscapeUtils.escapeJava(opcode.location.getCode()) + "\""));
					break;
				case Opcode.OC_LOADSTR:
					code("r" + opcode.r1 + " = \"" + StringEscapeUtils.escapeJava(opcode.arg) + "\";");
					break;
				case Opcode.OC_LOADINT:
					code("r" + opcode.r1 + " = new Integer(" + opcode.arg + ");");
					break;
				case Opcode.OC_LOADFLOAT:
					code("r" + opcode.r1 + " = new Double(" + opcode.arg + ");");
					break;
				case Opcode.OC_LOADNONE:
					code("r" + opcode.r1 + " = null;");
					break;
				case Opcode.OC_LOADFALSE:
					code("r" + opcode.r1 + " = false;");
					break;
				case Opcode.OC_LOADTRUE:
					code("r" + opcode.r1 + " = true;");
					break;
				case Opcode.OC_LOADDATE:
					String year = opcode.arg.substring(0, 4);
					String month = StringUtils.stripStart(opcode.arg.substring(5, 7), "0");
					String day = StringUtils.stripStart(opcode.arg.substring(8, 10), "0");
					if (opcode.arg.length() > 11)
					{
						String hour = StringUtils.stripStart(opcode.arg.substring(11, 13), "0");
						String minute = StringUtils.stripStart(opcode.arg.substring(14, 16), "0");
						String second = StringUtils.stripStart(opcode.arg.substring(17, 19), "0");
						if (opcode.arg.length() > 20)
						{
							String microsecond = opcode.arg.substring(20);
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.makeDate(" + year + ", " + month + ", " + day + ", " + hour + ", " + minute + ", " + second + ", " + microsecond + ");");
						}
						else
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.makeDate(" + year + ", " + month + ", " + day + ", " + hour + ", " + minute + ", " + second + ");");
					}
					else
						code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.makeDate(" + year + ", " + month + ", " + day + ");");
					break;
				case Opcode.OC_LOADCOLOR:
				{
					Color color = Color.fromdump(opcode.arg);
					code("r" + opcode.r1 + " = new com.livinglogic.ul4.Color(" + color.getR() + ", " + color.getG() + ", " + color.getB() + ", " + color.getA() + ");");
					break;
				}
				case Opcode.OC_BUILDLIST:
					code("r" + opcode.r1 + " = new java.util.ArrayList();");
					break;
				case Opcode.OC_BUILDDICT:
					code("r" + opcode.r1 + " = new java.util.HashMap();");
					break;
				case Opcode.OC_ADDLIST:
					code("((java.util.List)r" + opcode.r1 + ").add(r" + opcode.r2 + ");");
					break;
				case Opcode.OC_ADDDICT:
					code("((java.util.Map)r" + opcode.r1 + ").put(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_UPDATEDICT:
					code("((java.util.Map)r" + opcode.r1 + ").putAll((java.util.Map)r" + opcode.r2 + ");");
					break;
				case Opcode.OC_LOADVAR:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getItem(" + variables + ", \"" + StringEscapeUtils.escapeJava(opcode.arg) + "\");");
					break;
				case Opcode.OC_STOREVAR:
					code(variables + ".put(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\", r" + opcode.r1 + ");");
					break;
				case Opcode.OC_ADDVAR:
					code(variables + ".put(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\", com.livinglogic.ul4.Utils.add(" + variables + ".get(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\"), r" + opcode.r1 + "));");
					break;
				case Opcode.OC_SUBVAR:
					code(variables + ".put(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\", com.livinglogic.ul4.Utils.sub(" + variables + ".get(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\"), r" + opcode.r1 + "));");
					break;
				case Opcode.OC_MULVAR:
					code(variables + ".put(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\", com.livinglogic.ul4.Utils.mul(" + variables + ".get(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\"), r" + opcode.r1 + "));");
					break;
				case Opcode.OC_TRUEDIVVAR:
					code(variables + ".put(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\", com.livinglogic.ul4.Utils.truediv(" + variables + ".get(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\"), r" + opcode.r1 + "));");
					break;
				case Opcode.OC_FLOORDIVVAR:
					code(variables + ".put(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\", com.livinglogic.ul4.Utils.floordiv(" + variables + ".get(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\"), r" + opcode.r1 + "));");
					break;
				case Opcode.OC_MODVAR:
					code(variables + ".put(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\", com.livinglogic.ul4.Utils.mod(" + variables + ".get(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\"), r" + opcode.r1 + "));");
					break;
				case Opcode.OC_DELVAR:
					code(variables +".remove(r" + opcode.r1 + ");");
					break;
				case Opcode.OC_GETATTR:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getItem(r" + opcode.r2 + ", \"" + StringEscapeUtils.escapeJava(opcode.arg) + "\");");
					break;
				case Opcode.OC_GETITEM:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getItem(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_GETSLICE12:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getSlice(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
					break;
				case Opcode.OC_GETSLICE1:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getSlice(r" + opcode.r2 + ", r" + opcode.r3 + ", null);");
					break;
				case Opcode.OC_GETSLICE2:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getSlice(r" + opcode.r2 + ", null, r" + opcode.r3 + ");");
					break;
				case Opcode.OC_PRINT:
					code(output("com.livinglogic.ul4.Utils.str(r" + opcode.r1 + ")"));
					break;
				case Opcode.OC_PRINTX:
					code(output("com.livinglogic.ul4.Utils.xmlescape(r" + opcode.r1 + ")"));
					break;
				case Opcode.OC_FOR:
					code("for (java.util.Iterator iterator" + varcounter + " = com.livinglogic.ul4.Utils.iterator(r" + opcode.r2 +"); iterator" + varcounter + ".hasNext();)");
					code("{");
					indent++;
					code("r" + opcode.r1 + " = iterator" + varcounter + ".next();");
					varcounter++;
					break;
				case Opcode.OC_ENDFOR:
					indent--;
					code("}");
					break;
				case Opcode.OC_DEF:
					code(variables +".put(\"" + StringEscapeUtils.escapeJava(opcode.arg) + "\", new com.livinglogic.ul4.JSPTemplate()");
					code("{");
					indent++;
					code("public void render(java.io.Writer out, java.util.Map<String, Object> variables) throws java.io.IOException");
					code("{");
					indent++;
					for (int j = 0; j < 10; ++j)
						code("Object r" + j + " = null;");
					break;
				case Opcode.OC_ENDDEF:
					indent--;
					code("}");
					indent--;
					code("});");
					break;
				case Opcode.OC_BREAK:
					code("break;");
					break;
				case Opcode.OC_CONTINUE:
					code("continue;");
					break;
				case Opcode.OC_NOT:
					code("r" + opcode.r1 + " = !com.livinglogic.ul4.Utils.getBool(r" + opcode.r2 + ");");
					break;
				case Opcode.OC_NEG:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.neg(r" + opcode.r2 + ");");
					break;
				case Opcode.OC_CONTAINS:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.contains(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_NOTCONTAINS:
					code("r" + opcode.r1 + " = !com.livinglogic.ul4.Utils.contains(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_EQ:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.eq(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_NE:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.ne(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_LT:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.lt(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_LE:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.le(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_GT:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.gt(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_GE:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.ge(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_ADD:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.add(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_SUB:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.sub(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_MUL:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.mul(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_FLOORDIV:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.floordiv(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_TRUEDIV:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.truediv(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_MOD:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.mod(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_AND:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getBool(r" + opcode.r3 + ") ? r" + opcode.r2 + " : r" + opcode.r3 + ";");
					break;
				case Opcode.OC_OR:
					code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getBool(r" + opcode.r2 + ") ? r" + opcode.r2 + " : r" + opcode.r3 + ";");
					break;
				case Opcode.OC_CALLFUNC0:
					switch (opcode.argcode)
					{
						case Opcode.CF0_NOW:
							code("r" + opcode.r1 + " = new java.util.Date();");
							break;
						case Opcode.CF0_UTCNOW:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.utcnow();");
							break;
						case Opcode.CF0_RANDOM:
							code("r" + opcode.r1 + " = Math.random();");
							break;
						case Opcode.CF0_VARS:
							code("r" + opcode.r1 + " = " + variables + ";");
							break;
					}
					break;
				case Opcode.OC_CALLFUNC1:
					switch (opcode.argcode)
					{
						case Opcode.CF1_XMLESCAPE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.xmlescape(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_CSV:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.csv(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_REPR:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.repr(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ENUMERATE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.enumerate(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_CHR:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.chr(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ORD:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.ord(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_HEX:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.hex(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_OCT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.oct(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_BIN:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.bin(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_SORTED:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.sorted(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_TYPE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.type(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_JSON:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.json(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_REVERSED:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.reversed(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_RANDCHOICE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.randchoice(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_STR:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.str(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_INT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.toInteger(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_FLOAT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.toFloat(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_BOOL:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.getFloat(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_LEN:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.length(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISSTR:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof String);");
							break;
						case Opcode.CF1_ISINT:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof Integer);");
							break;
						case Opcode.CF1_ISFLOAT:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof Double);");
							break;
						case Opcode.CF1_ISBOOL:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof Boolan);");
							break;
						case Opcode.CF1_ISDATE:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof java.util.Date);");
							break;
						case Opcode.CF1_ISLIST:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof java.util.List);");
							break;
						case Opcode.CF1_ISDICT:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof java.util.Map);");
							break;
						case Opcode.CF1_ISTEMPLATE:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof com.livinglogic.ul4.Template);");
							break;
						case Opcode.CF1_ISCOLOR:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " != null) && (r" + opcode.r2 + " instanceof com.livinglogic.ul4.Color);");
							break;
						case Opcode.CF1_ABS:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.abs(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_RANGE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.range(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_RANDRANGE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.randrange(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISNONE:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " == null);");
							break;
						case Opcode.CF1_GET:
							code("r" + opcode.r1 + " = " + variables + ".get(r" + opcode.r2 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLFUNC2:
					switch (opcode.argcode)
					{
						case Opcode.CF2_ZIP:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.zip(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CF2_INT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.toInteger(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CF2_RANGE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.range(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CF2_RANDRANGE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.randrange(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CF2_GET:
							code("r" + opcode.r1 + " = " + variables + ".containsKey(r" + opcode.r2 + ") ? " + variables + ".get(r" + opcode.r2 + ") : r" + opcode.r3 + ";");
							break;
					}
					break;
				case Opcode.OC_CALLFUNC3:
					switch (opcode.argcode)
					{
						case Opcode.CF3_RANGE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.range(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CF3_RANDRANGE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.randrange(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CF3_ZIP:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.zip(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CF3_HLS:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.hls(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CF3_HSV:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.hsv(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CF3_RGB:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rgb(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLFUNC4:
					switch (opcode.argcode)
					{
						case Opcode.CF4_RGB:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rgb(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
						case Opcode.CF4_HLS:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.hls(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
						case Opcode.CF4_HSV:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.hsv(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLMETH0:
					switch (opcode.argcode)
					{
						case Opcode.CM0_STRIP:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.strip(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_LSTRIP:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.lstrip(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_RSTRIP:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rstrip(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_UPPER:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.upper(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_LOWER:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.lower(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_CAPITALIZE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.capitalize(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_ITEMS:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.items(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_ISOFORMAT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.isoformat(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_MIMEFORMAT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.mimeformat(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_DAY:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.day(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_MONTH:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.month(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_YEAR:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.year(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_HOUR:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.hour(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_MINUTE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.minute(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_SECOND:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.second(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_MICROSECOND:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.microsecond(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_WEEKDAY:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.weekday(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_YEARDAY:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.yearday(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_R:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").getr();");
							break;
						case Opcode.CM0_G:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").getg();");
							break;
						case Opcode.CM0_B:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").getb();");
							break;
						case Opcode.CM0_A:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").geta();");
							break;
						case Opcode.CM0_LUM:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").lum();");
							break;
						case Opcode.CM0_HLS:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").hls();");
							break;
						case Opcode.CM0_HLSA:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").hlsa();");
							break;
						case Opcode.CM0_HSV:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").hsv();");
							break;
						case Opcode.CM0_HSVA:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Color)r" + opcode.r2 + ").hsva();");
							break;
						case Opcode.CM0_SPLIT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.split(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_RSPLIT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rsplit(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_RENDER:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Template)r" + opcode.r2 + ").renders(null);");
							break;
					}
					break;
				case Opcode.OC_CALLMETH1:
					switch (opcode.argcode)
					{
						case Opcode.CM1_JOIN:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.join(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_STRIP:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.strip(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_LSTRIP:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.lstrip(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_RSTRIP:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rstrip(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_STARTSWITH:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.startswith(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_ENDSWITH:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.endswith(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_FORMAT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.format(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_WITHLUM:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.withlum(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_WITHA:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.witha(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_SPLIT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.split(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_RSPLIT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rsplit(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_GET:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.get(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_FIND:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.find(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_RFIND:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rfind(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLMETH2:
					switch (opcode.argcode)
					{
						case Opcode.CM2_SPLIT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.split(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_RSPLIT:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rsplit(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_REPLACE:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.replace(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_GET:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.get(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_FIND:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.find(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_RFIND:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rfind(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLMETH3:
					switch (opcode.argcode)
					{
						case Opcode.CM3_FIND:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.find(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
						case Opcode.CM3_RFIND:
							code("r" + opcode.r1 + " = com.livinglogic.ul4.Utils.rfind(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLMETHKW:
					switch (opcode.argcode)
					{
						case Opcode.CMKW_RENDER:
							code("r" + opcode.r1 + " = ((com.livinglogic.ul4.Template)r" + opcode.r2 + ").renders((java.util.Map<String, Object>)r" + opcode.r3 + ");");
							break;
					}
					break;
				case Opcode.OC_IF:
					code("if (com.livinglogic.ul4.Utils.getBool(r" + opcode.r1 + "))");
					code("{");
					indent++;
					break;
				case Opcode.OC_ELSE:
					indent--;
					code("}");
					code("else");
					code("{");
					indent++;
					break;
				case Opcode.OC_ENDIF:
					indent--;
					code("}");
					break;
				case Opcode.OC_RENDER:
					code("((com.livinglogic.ul4.Template)r" + opcode.r1 + ").render(out, (java.util.Map<String, Object>)r" + opcode.r2 + ");");
					break;
			}
		}
		code("//@@@ END template code");
		String result = buffer.toString();
		buffer = null;
		return result;
	}
}
