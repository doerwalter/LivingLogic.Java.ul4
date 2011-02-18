package com.livinglogic.ul4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.commons.lang.ObjectUtils;

public class JavascriptSource4Template
{
	private InterpretedTemplate template;
	private StringBuffer buffer;
	private int indent;

	public JavascriptSource4Template(InterpretedTemplate template)
	{
		this.template = template;
	}

	private void code(String code)
	{
		for (int i = 0; i < indent; ++i)
			buffer.append("\t");
		buffer.append(code);
		buffer.append("\n");
	}

	public String toString()
	{
		buffer = new StringBuffer();
		indent = 0;
		int varcounter = 0;
		Location lastLocation = null;

		code("ul4.Template.create(function(vars){");
		indent += 1;
		code("//@@@ BEGIN template source");
		code("//@@@ BEGIN template code");
		code("var out = [], r0 = null, r1 = null, r2 = null, r3 = null, r4 = null, r5 = null, r6 = null, r7 = null, r8 = null, r9 = null;");

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
					code("out.push(" + Utils.json(opcode.location.getCode()) + ");");
					break;
				case Opcode.OC_LOADSTR:
					code("r" + opcode.r1 + " = " + Utils.json(opcode.arg) + ";");
					break;
				case Opcode.OC_LOADINT:
					code("r" + opcode.r1 + " = " + opcode.arg + ";");
					break;
				case Opcode.OC_LOADFLOAT:
					code("r" + opcode.r1 + " = " + opcode.arg + ";");
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
					code("r" + opcode.r1 + " = " + Utils.json(Utils.isoparse(opcode.arg)) + ";");
					break;
				case Opcode.OC_LOADCOLOR:
					code("r" + opcode.r1 + " = " + Utils.json(Color.fromdump(opcode.arg)) + ";");
					break;
				case Opcode.OC_BUILDLIST:
					code("r" + opcode.r1 + " = [];");
					break;
				case Opcode.OC_BUILDDICT:
					code("r" + opcode.r1 + " = {};");
					break;
				case Opcode.OC_ADDLIST:
					code("r" + opcode.r1 + ".push(r" + opcode.r2 + ");");
					break;
				case Opcode.OC_ADDDICT:
					code("r" + opcode.r1 + "[r" + opcode.r2 + "] = r" + opcode.r3 + ";");
					break;
				case Opcode.OC_UPDATEDICT:
					code("for (var key in r" + opcode.r2 + ")");
					indent++;
					code("r" + opcode.r1 + "[key] = r" + opcode.r2 + "[key];");
					indent--;
					break;
				case Opcode.OC_LOADVAR:
					code("r" + opcode.r1 + " = ul4._op_getitem(vars, " + Utils.json(opcode.arg) + ");");
					break;
				case Opcode.OC_STOREVAR:
					code("vars[" + Utils.json(opcode.arg) + "] = r" + opcode.r1 + ";");
					break;
				case Opcode.OC_ADDVAR:
					code("vars[" + Utils.json(opcode.arg) + "] = ul4._op_add(vars[" + Utils.json(opcode.arg) + "], r" + opcode.r1 + ");");
					break;
				case Opcode.OC_SUBVAR:
					code("vars[" + Utils.json(opcode.arg) + "] = ul4._op_sub(vars[" + Utils.json(opcode.arg) + "], r" + opcode.r1 + ");");
					break;
				case Opcode.OC_MULVAR:
					code("vars[" + Utils.json(opcode.arg) + "] = ul4._op_mul(vars[" + Utils.json(opcode.arg) + "], r" + opcode.r1 + ");");
					break;
				case Opcode.OC_TRUEDIVVAR:
					code("vars[" + Utils.json(opcode.arg) + "] = ul4._op_truediv(vars[" + Utils.json(opcode.arg) + "], r" + opcode.r1 + ");");
					break;
				case Opcode.OC_FLOORDIVVAR:
					code("vars[" + Utils.json(opcode.arg) + "] = ul4._op_floordiv(vars[" + Utils.json(opcode.arg) + "], r" + opcode.r1 + ");");
					break;
				case Opcode.OC_MODVAR:
					code("vars[" + Utils.json(opcode.arg) + "] = ul4._op_mod(vars[" + Utils.json(opcode.arg) + "], r" + opcode.r1 + ");");
					break;
				case Opcode.OC_DELVAR:
					code("vars[" + Utils.json(opcode.arg) + "] = undefined;");
					break;
				case Opcode.OC_GETATTR:
					code("r" + opcode.r1 + " = ul4._op_getitem(r" + opcode.r2 + ", " + Utils.json(opcode.arg) + ");");
					break;
				case Opcode.OC_GETITEM:
					code("r" + opcode.r1 + " = ul4._op_getitem(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_GETSLICE12:
					code("r" + opcode.r1 + " = ul4._op_getslice(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
					break;
				case Opcode.OC_GETSLICE1:
					code("r" + opcode.r1 + " = ul4._op_getslice(r" + opcode.r2 + ", r" + opcode.r3 + ", null);");
					break;
				case Opcode.OC_GETSLICE2:
					code("r" + opcode.r1 + " = ul4._op_getslice(r" + opcode.r2 + ", null, r" + opcode.r3 + ");");
					break;
				case Opcode.OC_PRINT:
					code("out.push(ul4._fu_str(r" + opcode.r1 + "));");
					break;
				case Opcode.OC_PRINTX:
					code("out.push(ul4._fu_xmlescape(r" + opcode.r1 + "));");
					break;
				case Opcode.OC_FOR:
					varcounter++;
					code("for (var iter" + varcounter + " = ul4._iter(r" + opcode.r2 + ");;)");
					code("{");
					indent++;
					code("r" + opcode.r1 + " = iter" + varcounter + "();");
					code("if (r" + opcode.r1 + " === null)");
					indent++;
					code("break;");
					indent--;
					code("r" + opcode.r1 + " = r" + opcode.r1 + "[0];");
					break;
				case Opcode.OC_ENDFOR:
					indent--;
					code("}");
					break;
				case Opcode.OC_DEF:
					code("vars[" + Utils.json(opcode.arg) + "] = ul4.Template.create(function(vars){");
					indent++;
					code("var out = [], r0 = null, r1 = null, r2 = null, r3 = null, r4 = null, r5 = null, r6 = null, r7 = null, r8 = null, r9 = null;");
					break;
				case Opcode.OC_ENDDEF:
					code("return out;");
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
					code("r" + opcode.r1 + " = !ul4._fu_bool(r" + opcode.r2 + ");");
					break;
				case Opcode.OC_NEG:
					code("r" + opcode.r1 + " = ul4._op_neg(r" + opcode.r2 + ");");
					break;
				case Opcode.OC_CONTAINS:
					code("r" + opcode.r1 + " = ul4._op_contains(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_NOTCONTAINS:
					code("r" + opcode.r1 + " = !ul4._op_contains(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_EQ:
					code("r" + opcode.r1 + " = ul4._op_eq(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_NE:
					code("r" + opcode.r1 + " = !ul4._op_eq(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_LT:
					code("r" + opcode.r1 + " = ul4._op_lt(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_LE:
					code("r" + opcode.r1 + " = ul4._op_le(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_GT:
					code("r" + opcode.r1 + " = !ul4._op_le(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_GE:
					code("r" + opcode.r1 + " = !ul4._op_lt(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_ADD:
					code("r" + opcode.r1 + " = ul4._op_add(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_SUB:
					code("r" + opcode.r1 + " = ul4._op_sub(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_MUL:
					code("r" + opcode.r1 + " = ul4._op_mul(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_FLOORDIV:
					code("r" + opcode.r1 + " = ul4._op_floordiv(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_TRUEDIV:
					code("r" + opcode.r1 + " = ul4._op_truediv(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_MOD:
					code("r" + opcode.r1 + " = ul4._op_mod(r" + opcode.r2 + ", r" + opcode.r3 + ");");
					break;
				case Opcode.OC_AND:
					code("r" + opcode.r1 + " = ul4._fu_bool(r" + opcode.r3 + ") ? r" + opcode.r2 + " : r" + opcode.r3 + ";");
					break;
				case Opcode.OC_OR:
					code("r" + opcode.r1 + " = ul4._fu_bool(r" + opcode.r2 + ") ? r" + opcode.r2 + " : r" + opcode.r3 + ";");
					break;
				case Opcode.OC_CALLFUNC0:
					switch (opcode.argcode)
					{
						case Opcode.CF0_NOW:
							code("r" + opcode.r1 + " = new Date();");
							break;
						case Opcode.CF0_UTCNOW:
							code("r" + opcode.r1 + " = ul4._fu_utcnow();");
							break;
						case Opcode.CF0_RANDOM:
							code("r" + opcode.r1 + " = Math.random();");
							break;
						case Opcode.CF0_VARS:
							code("r" + opcode.r1 + " = vars;");
							break;
					}
					break;
				case Opcode.OC_CALLFUNC1:
					switch (opcode.argcode)
					{
						case Opcode.CF1_XMLESCAPE:
							code("r" + opcode.r1 + " = ul4._fu_xmlescape(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_CSV:
							code("r" + opcode.r1 + " = ul4._fu_csv(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_REPR:
							code("r" + opcode.r1 + " = ul4._fu_repr(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ENUMERATE:
							code("r" + opcode.r1 + " = ul4._fu_enumerate(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_CHR:
							code("r" + opcode.r1 + " = ul4._fu_chr(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ORD:
							code("r" + opcode.r1 + " = ul4._fu_ord(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_HEX:
							code("r" + opcode.r1 + " = ul4._fu_hex(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_OCT:
							code("r" + opcode.r1 + " = ul4._fu_oct(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_BIN:
							code("r" + opcode.r1 + " = ul4._fu_bin(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_SORTED:
							code("r" + opcode.r1 + " = ul4._fu_sorted(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_TYPE:
							code("r" + opcode.r1 + " = ul4._fu_type(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_JSON:
							code("r" + opcode.r1 + " = ul4._fu_json(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_REVERSED:
							code("r" + opcode.r1 + " = ul4._fu_reversed(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_RANDCHOICE:
							code("r" + opcode.r1 + " = ul4._fu_randchoice(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_STR:
							code("r" + opcode.r1 + " = ul4._fu_str(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_INT:
							code("r" + opcode.r1 + " = ul4._fu_int(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_FLOAT:
							code("r" + opcode.r1 + " = ul4._fu_float(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_BOOL:
							code("r" + opcode.r1 + " = ul4._fu_bool(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_LEN:
							code("r" + opcode.r1 + " = ul4._fu_len(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISSTR:
							code("r" + opcode.r1 + " = ul4._fu_isstr(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISINT:
							code("r" + opcode.r1 + " = ul4._fu_isint(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISFLOAT:
							code("r" + opcode.r1 + " = ul4._fu_isfloat(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISBOOL:
							code("r" + opcode.r1 + " = ul4._fu_isbool(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISDATE:
							code("r" + opcode.r1 + " = ul4._fu_isdate(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISLIST:
							code("r" + opcode.r1 + " = ul4._fu_islist(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISDICT:
							code("r" + opcode.r1 + " = ul4._fu_isdict(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISTEMPLATE:
							code("r" + opcode.r1 + " = ul4._fu_istemplate(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ISCOLOR:
							code("r" + opcode.r1 + " = ul4._fu_iscolor(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_ABS:
							code("r" + opcode.r1 + " = ul4._fu_abs(r" + opcode.r2 + ");");
							break;
						case Opcode.CF1_RANGE:
							code("r" + opcode.r1 + " = ul4._fu_range(0, r" + opcode.r2 + ", 1);");
							break;
						case Opcode.CF1_RANDRANGE:
							code("r" + opcode.r1 + " = ul4._fu_randrange(0, r" + opcode.r2 + ", 1);");
							break;
						case Opcode.CF1_ISNONE:
							code("r" + opcode.r1 + " = (r" + opcode.r2 + " === null);");
							break;
						case Opcode.CF1_GET:
							code("r" + opcode.r1 + " = ul4._me_get(vars, r" + opcode.r2 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLFUNC2:
					switch (opcode.argcode)
					{
						case Opcode.CF2_ZIP:
							code("r" + opcode.r1 + " = ul4._fu_zip(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CF2_INT:
							code("r" + opcode.r1 + " = ul4._fu_int(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CF2_RANGE:
							code("r" + opcode.r1 + " = ul4._fu_range(r" + opcode.r2 + ", r" + opcode.r3 + ", 1);");
							break;
						case Opcode.CF2_RANDRANGE:
							code("r" + opcode.r1 + " = ul4._fu_randrange(r" + opcode.r2 + ", r" + opcode.r3 + ", 1);");
							break;
						case Opcode.CF2_GET:
							code("r" + opcode.r1 + " = ul4._me_get(vars, r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLFUNC3:
					switch (opcode.argcode)
					{
						case Opcode.CF3_RANGE:
							code("r" + opcode.r1 + " = ul4._fu_range(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CF3_RANDRANGE:
							code("r" + opcode.r1 + " = ul4._fu_randrange(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CF3_ZIP:
							code("r" + opcode.r1 + " = ul4._fu_zip(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CF3_HLS:
							code("r" + opcode.r1 + " = ul4._fu_hls(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", 1.0);");
							break;
						case Opcode.CF3_HSV:
							code("r" + opcode.r1 + " = ul4._fu_hsv(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", 1.0);");
							break;
						case Opcode.CF3_RGB:
							code("r" + opcode.r1 + " = ul4._fu_rgb(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", 1.0);");
							break;
					}
					break;
				case Opcode.OC_CALLFUNC4:
					switch (opcode.argcode)
					{
						case Opcode.CF4_RGB:
							code("r" + opcode.r1 + " = ul4._fu_rgb(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
						case Opcode.CF4_HLS:
							code("r" + opcode.r1 + " = ul4._fu_hls(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
						case Opcode.CF4_HSV:
							code("r" + opcode.r1 + " = ul4._fu_hsv(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLMETH0:
					switch (opcode.argcode)
					{
						case Opcode.CM0_STRIP:
							code("r" + opcode.r1 + " = ul4._me_strip(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_LSTRIP:
							code("r" + opcode.r1 + " = ul4._me_lstrip(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_RSTRIP:
							code("r" + opcode.r1 + " = ul4._me_rstrip(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_UPPER:
							code("r" + opcode.r1 + " = ul4._me_upper(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_LOWER:
							code("r" + opcode.r1 + " = ul4._me_lower(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_CAPITALIZE:
							code("r" + opcode.r1 + " = ul4._me_capitalize(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_ITEMS:
							code("r" + opcode.r1 + " = ul4._me_items(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_ISOFORMAT:
							code("r" + opcode.r1 + " = ul4._me_isoformat(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_MIMEFORMAT:
							code("r" + opcode.r1 + " = ul4._me_mimeformat(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_DAY:
							code("r" + opcode.r1 + " = ul4._me_day(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_MONTH:
							code("r" + opcode.r1 + " = ul4._me_month(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_YEAR:
							code("r" + opcode.r1 + " = ul4._me_year(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_HOUR:
							code("r" + opcode.r1 + " = ul4._me_hour(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_MINUTE:
							code("r" + opcode.r1 + " = ul4._me_minute(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_SECOND:
							code("r" + opcode.r1 + " = ul4._me_second(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_MICROSECOND:
							code("r" + opcode.r1 + " = ul4._me_microsecond(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_WEEKDAY:
							code("r" + opcode.r1 + " = ul4._me_weekday(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_YEARDAY:
							code("r" + opcode.r1 + " = ul4._me_yearday(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_R:
							code("r" + opcode.r1 + " = ul4._me_r(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_G:
							code("r" + opcode.r1 + " = ul4._me_g(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_B:
							code("r" + opcode.r1 + " = ul4._me_b(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_A:
							code("r" + opcode.r1 + " = ul4._me_a(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_LUM:
							code("r" + opcode.r1 + " = ul4._me_lum(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_HLS:
							code("r" + opcode.r1 + " = ul4._me_hls(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_HLSA:
							code("r" + opcode.r1 + " = ul4._me_hlsa(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_HSV:
							code("r" + opcode.r1 + " = ul4._me_hsv(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_HSVA:
							code("r" + opcode.r1 + " = ul4._me_hsva(r" + opcode.r2 + ");");
							break;
						case Opcode.CM0_SPLIT:
							code("r" + opcode.r1 + " = ul4._me_split(r" + opcode.r2 + ", null, null);");
							break;
						case Opcode.CM0_RSPLIT:
							code("r" + opcode.r1 + " = ul4._me_rsplit(r" + opcode.r2 + ", null, null);");
							break;
						case Opcode.CM0_RENDER:
							code("r" + opcode.r1 + " = r" + opcode.r2 + ".renders({});");
							break;
					}
					break;
				case Opcode.OC_CALLMETH1:
					switch (opcode.argcode)
					{
						case Opcode.CM1_JOIN:
							code("r" + opcode.r1 + " = ul4._me_join(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_STRIP:
							code("r" + opcode.r1 + " = ul4._me_strip(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_LSTRIP:
							code("r" + opcode.r1 + " = ul4._me_lstrip(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_RSTRIP:
							code("r" + opcode.r1 + " = ul4._me_rstrip(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_STARTSWITH:
							code("r" + opcode.r1 + " = ul4._me_startswith(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_ENDSWITH:
							code("r" + opcode.r1 + " = ul4._me_endswith(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_FORMAT:
							code("r" + opcode.r1 + " = ul4._me_format(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_WITHLUM:
							code("r" + opcode.r1 + " = ul4._me_withlum(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_WITHA:
							code("r" + opcode.r1 + " = ul4._me_witha(r" + opcode.r2 + ", r" + opcode.r3 + ");");
							break;
						case Opcode.CM1_SPLIT:
							code("r" + opcode.r1 + " = ul4._me_split(r" + opcode.r2 + ", r" + opcode.r3 + ", null);");
							break;
						case Opcode.CM1_RSPLIT:
							code("r" + opcode.r1 + " = ul4._me_rsplit(r" + opcode.r2 + ", r" + opcode.r3 + ", null);");
							break;
						case Opcode.CM1_GET:
							code("r" + opcode.r1 + " = ul4._me_get(r" + opcode.r2 + ", r" + opcode.r3 + ", null);");
							break;
						case Opcode.CM1_FIND:
							code("r" + opcode.r1 + " = ul4._me_find(r" + opcode.r2 + ", r" + opcode.r3 + ", null, null);");
							break;
						case Opcode.CM1_RFIND:
							code("r" + opcode.r1 + " = ul4._me_rfind(r" + opcode.r2 + ", r" + opcode.r3 + ", null, null);");
							break;
					}
					break;
				case Opcode.OC_CALLMETH2:
					switch (opcode.argcode)
					{
						case Opcode.CM2_SPLIT:
							code("r" + opcode.r1 + " = ul4._me_split(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_RSPLIT:
							code("r" + opcode.r1 + " = ul4._me_rsplit(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_REPLACE:
							code("r" + opcode.r1 + " = ul4._me_replace(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_GET:
							code("r" + opcode.r1 + " = ul4._me_get(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ");");
							break;
						case Opcode.CM2_FIND:
							code("r" + opcode.r1 + " = ul4._me_find(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", null);");
							break;
						case Opcode.CM2_RFIND:
							code("r" + opcode.r1 + " = ul4._me_rfind(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", null);");
							break;
					}
					break;
				case Opcode.OC_CALLMETH3:
					switch (opcode.argcode)
					{
						case Opcode.CM3_FIND:
							code("r" + opcode.r1 + " = ul4._me_find(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
						case Opcode.CM3_RFIND:
							code("r" + opcode.r1 + " = ul4._me_rfind(r" + opcode.r2 + ", r" + opcode.r3 + ", r" + opcode.r4 + ", r" + opcode.r5 + ");");
							break;
					}
					break;
				case Opcode.OC_CALLMETHKW:
					switch (opcode.argcode)
					{
						case Opcode.CMKW_RENDER:
							code("r" + opcode.r1 + " = r" + opcode.r2 + ".renders(r" + opcode.r3 + ");");
							break;
					}
					break;
				case Opcode.OC_IF:
					code("if (ul4._fu_bool(r" + opcode.r1 + "))");
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
					code("out = out.concat(r" + opcode.r1 + ".render(r" + opcode.r2 + "));");
					break;
			}
		}
		code("return out;");
		code("//@@@ END template code");
		indent--;
		code("})");
		String result = buffer.toString();
		buffer = null;
		return result;
	}
}
