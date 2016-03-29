package tests;

import static com.livinglogic.ul4on.Utils.dumps;
import static com.livinglogic.utils.MapUtils.makeMap;
import static com.livinglogic.utils.SetUtils.makeSet;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.livinglogic.ul4.Function;
import com.livinglogic.ul4.Signature;
import com.livinglogic.ul4.BoundArguments;
import com.livinglogic.ul4.InterpretedTemplate;
import com.livinglogic.ul4.SourcePart;
import com.livinglogic.ul4.SourceException;

import com.livinglogic.vsql.Type;
import com.livinglogic.vsql.Node;
import com.livinglogic.vsql.Module;
import com.livinglogic.vsql.Field;


@RunWith(CauseTestRunner.class)
public class VSQLTest
{
	static Module module = new Module();

	private static class FunctionCompile extends Function
	{
		public String nameUL4()
		{
			return "compile";
		}

		private static final Signature signature = new Signature("source", Signature.required, "name", null, "signature", null);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			try
			{
				return new InterpretedTemplate((String)args.get(0), (String)args.get(1), InterpretedTemplate.Whitespace.keep, null, null, (String)args.get(2));
			}
			catch (RecognitionException ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}

	static Function compile = new FunctionCompile();

	private static class FunctionError extends Function
	{
		public String nameUL4()
		{
			return "error";
		}

		private static final Signature signature = new Signature("message", Signature.required, "ast", null, "template", null);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			throw Node.error((InterpretedTemplate)args.get(2), (SourcePart)args.get(1), (String)args.get(0));
		}
	}

	static Function error = new FunctionError();

	String convert = 
		"<?def convert(template, ast)?>" +
			"<?if ast.type == 'const'?>" +
				"<?return vsql.const(ast.value, ast, template)?>" +
			"<?elif ast.type == 'add'?>" +
				"<?return vsql.add(convert(template, ast.obj1), convert(template, ast.obj2), ast, template)?>" +
			"<?elif ast.type == 'mul'?>" +
				"<?return vsql.mul(convert(template, ast.obj1), convert(template, ast.obj2), ast, template)?>" +
			"<?elif ast.type == 'attr'?>" +
				"<?if ast.obj.type == 'var' and ast.obj.name == 'f'?>" +
					"<?if ast.attrname in fields?>" +
						"<?return vsql.field(fields[ast.attrname], ast, template)?>" +
					"<?else?>" +
						"<?code error('no field named ' + repr(ast.attrname), ast, template)?>" +
					"<?end if?>" +
				"<?else?>" +
					"<?code error('unknown attribute access ' + repr(ast), ast, template)?>" +
				"<?end if?>" +
			"<?else?>" +
				"<?code error('unknown node type ' + ast.type, ast, template)?>" +
			"<?end if?>" +
		"<?end def?>" +
		"<?code template = compile('<' + '?return ' + source + '?' + '>', 'expression')?>" +
		"<?print convert(template, template.content[1].obj).sql('oracle')?>"
	;

	public static HashMap<String, Field> fields;

	static
	{
		fields = new HashMap<String, Field>();
		fields.put("vorname", new Field("vorname", Type.STR, "dat_char1"));
		fields.put("nachname", new Field("nachname", Type.STR, "dat_char2"));
		fields.put("id", new Field("id", Type.INT, "dat_int1"));
	}

	public void check(String expected, String source)
	{
		tests.UL4Test.checkTemplateOutput(expected, convert, "source", source, "compile", compile, "error", error, "fields", fields, "vsql", module);
	}

	@Test
	public void constants()
	{
		check("1", "True");
		check("42", "42");
		check("42.5", "42.5");
		check("'fo''o'", "'fo\\'o'");
		check("to_date('2016-01-01', 'YYYY-MM-DD')", "@(2016-01-01)");
		check("to_date('2016-01-01 12:34:56', 'YYYY-MM-DD HH24:MI:SS')", "@(2016-01-01T12:34:56)");
		check("to_timestamp('2016-01-01 12:34:56.789000', 'YYYY-MM-DD HH24:MI:SS.FF6')", "@(2016-01-01T12:34:56.789000)");
	}

	@Test
	public void lower()
	{
		tests.UL4Test.checkTemplateOutput("lower('FOO')", "<?print vsql.lower(vsql.const('FOO')).sql('oracle')?>", "vsql", module);
	}

	@CauseTest(expectedCause=RuntimeException.class)
	public void lower_fail()
	{
		tests.UL4Test.checkTemplateOutput(null, "<?print vsql.lower(vsql.const(42)).sql('oracle')?>", "vsql", module);
	}

	@Test
	public void upper()
	{
		tests.UL4Test.checkTemplateOutput("upper('FOO')", "<?print vsql.upper(vsql.const('FOO')).sql('oracle')?>", "vsql", module);
	}

	@CauseTest(expectedCause=RuntimeException.class)
	public void upper_fail()
	{
		tests.UL4Test.checkTemplateOutput(null, "<?print vsql.upper(vsql.const(42)).sql('oracle')?>", "vsql", module);
	}

	@Test
	public void add()
	{
		tests.UL4Test.checkTemplateOutput("(1+42)", "<?print vsql.add(vsql.const(True), vsql.const(42)).sql('oracle')?>", "vsql", module);
		tests.UL4Test.checkTemplateOutput("(17+23)", "<?print vsql.add(vsql.const(17), vsql.const(23)).sql('oracle')?>", "vsql", module);
		tests.UL4Test.checkTemplateOutput("('foo'||'bar')", "<?print vsql.add(vsql.const('foo'), vsql.const('bar')).sql('oracle')?>", "vsql", module);
	}

	@CauseTest(expectedCause=RuntimeException.class)
	public void add_fail()
	{
		tests.UL4Test.checkTemplateOutput(null, "<?print vsql.add(vsql.const('foo'), vsql.const(42)).sql('oracle')?>", "vsql", module);
	}

	@Test
	public void fields()
	{
		check("((dat_char1||' ')||dat_char2)", "f.vorname + ' ' + f.nachname");
		check("(2*dat_int1)", "2 * f.id");
	}
}
