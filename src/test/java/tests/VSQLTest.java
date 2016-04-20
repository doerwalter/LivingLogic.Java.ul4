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
		"<?def convert(ast, template)?>" +
			"<?if ast.type == 'const'?>" +
				"<?return vsql.const(ast.value, ast, template)?>" +
			"<?elif ast.type == 'eq'?>" +
				"<?return vsql.Eq(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'ne'?>" +
				"<?return vsql.NE(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'add'?>" +
				"<?return vsql.add(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'mul'?>" +
				"<?return vsql.mul(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'sub'?>" +
				"<?return vsql.sub(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'truediv'?>" +
				"<?return vsql.truediv(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'floordiv'?>" +
				"<?return vsql.floordiv(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'lt'?>" +
				"<?return vsql.Lt(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'le'?>" +
				"<?return vsql.LE(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'gt'?>" +
				"<?return vsql.Gt(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'ge'?>" +
				"<?return vsql.GE(convert(ast.obj1, template), convert(ast.obj2, template), ast, template)?>" +
			"<?elif ast.type == 'if'?>" +
				"<?return vsql.ifelse(convert(ast.objif, template), convert(ast.objcond, template), convert(ast.objelse, template), ast, template)?>" +
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
			"<?elif ast.type == 'call'?>" +
				"<?if ast.obj.type == 'attr' and ast.obj.attrname == 'lower' and not ast.args?>" +
					"<?return vsql.lower(convert(ast.obj.obj, template), ast, template)?>" +
				"<?elif ast.obj.type == 'attr' and ast.obj.attrname == 'upper' and not ast.args?>" +
					"<?return vsql.upper(convert(ast.obj.obj, template), ast, template)?>" +
				"<?elif ast.obj.type == 'var' and ast.obj.name == 'str' and len(ast.args) == 1 and ast.args[0].type == 'posarg'?>" +
					"<?return vsql.str(convert(ast.args[0].value, template), ast, template)?>" +
				"<?elif ast.obj.type == 'var' and ast.obj.name == 'monthdelta' and len(ast.args) == 1 and ast.args[0].type == 'posarg'?>" +
					"<?return vsql.MonthDelta(convert(ast.args[0].value, template), ast, template)?>" +
				"<?elif ast.obj.type == 'var' and ast.obj.name == 'timedelta' and len(ast.args) >= 1 and len(ast.args) <= 3 and all(arg.type == 'posarg' for arg in ast.args)?>" +
					"<?return vsql.TimeDelta(" +
						"convert(ast.args[0].value, template)," +
						"convert(ast.args[1].value, template) if len(ast.args) >= 2 else None," +
						"convert(ast.args[2].value, template) if len(ast.args) >= 3 else None," +
						"ast," +
						"template," +
					")?>" +
				"<?elif ast.obj.type == 'var' and ast.obj.name == 'rightnow' and not ast.args?>" +
					"<?return vsql.rightnow(ast, template)?>" +
				"<?elif ast.obj.type == 'var' and ast.obj.name == 'now' and not ast.args?>" +
					"<?return vsql.now(ast, template)?>" +
				"<?elif ast.obj.type == 'var' and ast.obj.name == 'today' and not ast.args?>" +
					"<?return vsql.today(ast, template)?>" +
				"<?else?>" +
					"<?code error('unknown call ' + repr(ast), ast, template)?>" +
				"<?end if?>" +
			"<?else?>" +
				"<?code error('unknown node type ' + ast.type, ast, template)?>" +
			"<?end if?>" +
		"<?end def?>" +
		"<?code template = compile('<' + '?return ' + source + '?' + '>', 'expression')?>" +
		"<?print convert(template.content[1].obj, template).sql('oracle')?>"
	;

	public static HashMap<String, Field> fields;

	static
	{
		fields = new HashMap<String, Field>();
		fields.put("firstname", new Field("firstname", Type.STR, "dat_firstname"));
		fields.put("lastname", new Field("lastname", Type.STR, "dat_lastname"));
		fields.put("id", new Field("id", Type.INT, "dat_id"));
		fields.put("active", new Field("active", Type.BOOL, "dat_active"));
		fields.put("weight", new Field("age", Type.NUMBER, "dat_weight"));
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
	public void monthdelta()
	{
		check("1", "monthdelta(1)");
		check("add_months(sysdate, 1)", "now() + monthdelta(1)");
	}

	@Test
	public void timedelta()
	{
		check("1", "timedelta(1)");
		check("(0+1/86400)", "timedelta(0, 1)");
	}

	@Test
	public void lower()
	{
		check("lower('FOO')", "'FOO'.lower()");
	}

	@CauseTest(expectedCause=RuntimeException.class)
	public void lower_fail()
	{
		check(null, "42.lower()");
	}

	@Test
	public void upper()
	{
		check("upper('foo')", "'foo'.upper()");
	}

	@CauseTest(expectedCause=RuntimeException.class)
	public void upper_fail()
	{
		check(null, "42.upper()");
	}

	@Test
	public void add()
	{
		check("(1+dat_id)", "True + f.id");
		check("(dat_active+dat_id)", "f.active + f.id");
		check("(dat_id+42)", "f.id + 42");
		check("(dat_id+42.5)", "f.id + 42.5");
		check("((dat_firstname||' ')||dat_lastname)", "f.firstname + ' ' + f.lastname");
		check("(trunc(sysdate)+1)", "today() + timedelta(1)");
		check("(trunc(sysdate)+(0+1/86400))", "today() + timedelta(0, 1)");
		check("(cast(trunc(sysdate) as timestamp)+(0+0/86400+1/86400000000))", "today() + timedelta(0, 0, 1)");
		check("(sysdate+1)", "now() + timedelta(1)");
		check("(sysdate+(0+1/86400))", "now() + timedelta(0, 1)");
		check("(cast(sysdate as timestamp)+(0+0/86400+1/86400000000))", "now() + timedelta(0, 0, 1)");
		check("(systimestamp+1)", "rightnow() + timedelta(1)");
		check("(systimestamp+(0+1/86400))", "rightnow() + timedelta(0, 1)");
		check("(systimestamp+(0+0/86400+1/86400000000))", "rightnow() + timedelta(0, 0, 1)");
	}

	@Test
	public void sub()
	{
		check("(1-dat_id)", "True - f.id");
		check("(dat_active-dat_id)", "f.active - f.id");
		check("(dat_id-42)", "f.id - 42");
		check("(dat_id-42.5)", "f.id - 42.5");
		check("(trunc(sysdate)-1)", "today() - timedelta(1)");
		check("(trunc(sysdate)-(0+1/86400))", "today() - timedelta(0, 1)");
		check("(cast(trunc(sysdate) as timestamp)-(0+0/86400+1/86400000000))", "today() - timedelta(0, 0, 1)");
		check("(sysdate-1)", "now() - timedelta(1)");
		check("(sysdate-(0+1/86400))", "now() - timedelta(0, 1)");
		check("(cast(sysdate as timestamp)-(0+0/86400+1/86400000000))", "now() - timedelta(0, 0, 1)");
		check("(systimestamp-1)", "rightnow() - timedelta(1)");
		check("(systimestamp-(0+1/86400))", "rightnow() - timedelta(0, 1)");
		check("(systimestamp-(0+0/86400+1/86400000000))", "rightnow() - timedelta(0, 0, 1)");
	}

	@Test
	public void mul()
	{
		check("(1*dat_id)", "True * f.id");
		check("(dat_active*dat_id)", "f.active * f.id");
		check("ul4_pkg.mul_int_str(2, dat_firstname)", "2 * f.firstname");
	}

	@Test
	public void truediv()
	{
		check("ul4_pkg.truediv_bool_bool(dat_active, 1)", "f.active / True");
		check("ul4_pkg.truediv_bool_int(dat_active, 42)", "f.active / 42");
		check("ul4_pkg.truediv_bool_number(dat_active, 42.5)", "f.active / 42.5");
		check("ul4_pkg.truediv_int_bool(dat_id, 1)", "f.id / True");
		check("ul4_pkg.truediv_int_int(dat_id, 42)", "f.id / 42");
		check("ul4_pkg.truediv_int_number(dat_id, 42.5)", "f.id / 42.5");
		check("ul4_pkg.truediv_number_bool(dat_weight, 1)", "f.weight / True");
		check("ul4_pkg.truediv_number_int(dat_weight, 42)", "f.weight / 42");
		check("ul4_pkg.truediv_number_number(dat_weight, 42.5)", "f.weight / 42.5");
	}

	@Test
	public void floordiv()
	{
		check("ul4_pkg.floordiv_bool_bool(dat_active, 1)", "f.active // True");
		check("ul4_pkg.floordiv_bool_int(dat_active, 42)", "f.active // 42");
		check("ul4_pkg.floordiv_bool_number(dat_active, 42.5)", "f.active // 42.5");
		check("ul4_pkg.floordiv_int_bool(dat_id, 1)", "f.id // True");
		check("ul4_pkg.floordiv_int_int(dat_id, 42)", "f.id // 42");
		check("ul4_pkg.floordiv_int_number(dat_id, 42.5)", "f.id // 42.5");
		check("ul4_pkg.floordiv_number_bool(dat_weight, 1)", "f.weight // True");
		check("ul4_pkg.floordiv_number_int(dat_weight, 42)", "f.weight // 42");
		check("ul4_pkg.floordiv_number_number(dat_weight, 42.5)", "f.weight // 42.5");
	}

	@Test
	public void ifelse()
	{
		check("case when ul4_pkg.bool_bool(dat_active) then dat_firstname else 'Inactive' end", "f.firstname if f.active else 'Inactive'");
	}

	@Test
	public void str()
	{
		check("case 1 when null then null when 0 then 'False' else 'True' end", "str(True)");
		check("to_char(42)", "str(42)");
		check("to_char(42.5)", "str(42.5)");
		check("ul4_pkg.str_date(to_date('2016-01-01', 'YYYY-MM-DD'))", "str(@(2016-01-01))");
		check("ul4_pkg.str_datetime(to_date('2016-01-01 12:34:56', 'YYYY-MM-DD HH24:MI:SS'))", "str(@(2016-01-01T12:34:56))");
		check("ul4_pkg.str_timestamp(to_timestamp('2016-01-01 12:34:56.789000', 'YYYY-MM-DD HH24:MI:SS.FF6'))", "str(@(2016-01-01T12:34:56.789000))");
		check("'foo'", "str('foo')");
	}

	@Test
	public void eq()
	{
		check("case when dat_active is null and dat_firstname is null then 1 else 0 end", "f.active == f.firstname");
		check("ul4_pkg.eq_bool_int(dat_active, dat_id)", "f.active == f.id");
		check("ul4_pkg.eq_str_str(dat_firstname, dat_lastname)", "f.firstname == f.lastname");
	}

	@Test
	public void ne()
	{
		check("case when dat_active is null and dat_firstname is null then 0 else 1 end", "f.active != f.firstname");
		check("ul4_pkg.ne_bool_int(dat_active, dat_id)", "f.active != f.id");
		check("ul4_pkg.ne_str_str(dat_firstname, dat_lastname)", "f.firstname != f.lastname");
	}

	@Test
	public void lt()
	{
		check("ul4_pkg.lt_bool_bool(dat_active, 1)", "f.active < True");
		check("ul4_pkg.lt_bool_int(dat_active, 42)", "f.active < 42");
		check("ul4_pkg.lt_bool_number(dat_active, 42.5)", "f.active < 42.5");
		check("ul4_pkg.lt_int_bool(dat_id, 1)", "f.id < True");
		check("ul4_pkg.lt_int_int(dat_id, 42)", "f.id < 42");
		check("ul4_pkg.lt_int_number(dat_id, 42.5)", "f.id < 42.5");
		check("ul4_pkg.lt_number_bool(dat_weight, 1)", "f.weight < True");
		check("ul4_pkg.lt_number_int(dat_weight, 42)", "f.weight < 42");
		check("ul4_pkg.lt_number_number(dat_weight, 42.5)", "f.weight < 42.5");
		check("ul4_pkg.lt_str_str(dat_firstname, dat_lastname)", "f.firstname < f.lastname");
	}

	@Test
	public void le()
	{
		check("ul4_pkg.le_bool_bool(dat_active, 1)", "f.active <= True");
		check("ul4_pkg.le_bool_int(dat_active, 42)", "f.active <= 42");
		check("ul4_pkg.le_bool_number(dat_active, 42.5)", "f.active <= 42.5");
		check("ul4_pkg.le_int_bool(dat_id, 1)", "f.id <= True");
		check("ul4_pkg.le_int_int(dat_id, 42)", "f.id <= 42");
		check("ul4_pkg.le_int_number(dat_id, 42.5)", "f.id <= 42.5");
		check("ul4_pkg.le_number_bool(dat_weight, 1)", "f.weight <= True");
		check("ul4_pkg.le_number_int(dat_weight, 42)", "f.weight <= 42");
		check("ul4_pkg.le_number_number(dat_weight, 42.5)", "f.weight <= 42.5");
		check("ul4_pkg.le_str_str(dat_firstname, dat_lastname)", "f.firstname <= f.lastname");
	}

	@Test
	public void gt()
	{
		check("ul4_pkg.gt_bool_bool(dat_active, 1)", "f.active > True");
		check("ul4_pkg.gt_bool_int(dat_active, 42)", "f.active > 42");
		check("ul4_pkg.gt_bool_number(dat_active, 42.5)", "f.active > 42.5");
		check("ul4_pkg.gt_int_bool(dat_id, 1)", "f.id > True");
		check("ul4_pkg.gt_int_int(dat_id, 42)", "f.id > 42");
		check("ul4_pkg.gt_int_number(dat_id, 42.5)", "f.id > 42.5");
		check("ul4_pkg.gt_number_bool(dat_weight, 1)", "f.weight > True");
		check("ul4_pkg.gt_number_int(dat_weight, 42)", "f.weight > 42");
		check("ul4_pkg.gt_number_number(dat_weight, 42.5)", "f.weight > 42.5");
		check("ul4_pkg.gt_str_str(dat_firstname, dat_lastname)", "f.firstname > f.lastname");
	}

	@Test
	public void ge()
	{
		check("ul4_pkg.ge_bool_bool(dat_active, 1)", "f.active >= True");
		check("ul4_pkg.ge_bool_int(dat_active, 42)", "f.active >= 42");
		check("ul4_pkg.ge_bool_number(dat_active, 42.5)", "f.active >= 42.5");
		check("ul4_pkg.ge_int_bool(dat_id, 1)", "f.id >= True");
		check("ul4_pkg.ge_int_int(dat_id, 42)", "f.id >= 42");
		check("ul4_pkg.ge_int_number(dat_id, 42.5)", "f.id >= 42.5");
		check("ul4_pkg.ge_number_bool(dat_weight, 1)", "f.weight >= True");
		check("ul4_pkg.ge_number_int(dat_weight, 42)", "f.weight >= 42");
		check("ul4_pkg.ge_number_number(dat_weight, 42.5)", "f.weight >= 42.5");
		check("ul4_pkg.ge_str_str(dat_firstname, dat_lastname)", "f.firstname >= f.lastname");
	}

	@Test
	public void rightnow()
	{
		check("systimestamp", "rightnow()");
	}

	@Test
	public void now()
	{
		check("sysdate", "now()");
	}

	@Test
	public void today()
	{
		check("trunc(sysdate)", "today()");
	}

	@CauseTest(expectedCause=RuntimeException.class)
	public void add_fail()
	{
		tests.UL4Test.checkTemplateOutput(null, "<?print vsql.add(vsql.const('foo'), vsql.const(42)).sql('oracle')?>", "vsql", module);
	}

	@Test
	public void fields()
	{
		check("((dat_firstname||' ')||dat_lastname)", "f.firstname + ' ' + f.lastname");
		check("(2*dat_id)", "2 * f.id");
	}
}
