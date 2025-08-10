package tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.livinglogic.vsql.VSQLQuery;

@RunWith(CauseTestRunner.class)
public class VSQLTest
{
	void checkVSQL(String expected, VSQLQuery query)
	{

		assertEquals(
			expected.replaceAll("\\s+", " ").trim(),
			query.getSQLSource().replaceAll("\\s+", " ").trim()
		);
	}

	@Test
	public void basic()
	{
		VSQLQuery query = new VSQLQuery("comment");

		checkVSQL("/* comment */ select 42 from dual", query);
	}

	@Test
	public void selectSQL()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectSQL("42", null, null);

		checkVSQL("select 42 from dual", query);
	}

	@Test
	public void selectSQL_with_comment()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectSQL("42", "/*bad*/", null);

		checkVSQL("select 42 /* / *bad* / */ from dual", query);
	}

	@Test
	public void selectSQL_with_alias()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectSQL("42", null, "answer");

		checkVSQL("select 42 as answer from dual", query);
	}

	@Test
	public void selectSQL_with_comment_and_alias()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectSQL("42", "/*bad*/", "answer");

		checkVSQL("select 42 /* / *bad* / */ as answer from dual", query);
	}

	@Test
	public void selectVSQL_null()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("None");

		checkVSQL("select null /* None */ from dual", query);
	}

	@Test
	public void selectVSQL_null_with_alias()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("None", "nix");

		checkVSQL("select null /* None */ as nix from dual", query);
	}

	@Test
	public void selectVSQL_bool()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("False");
		query.selectVSQL("True");

		checkVSQL("select 0 /* False */, 1 /* True */ from dual", query);
	}

	@Test
	public void selectVSQL_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("-42");
		query.selectVSQL("0");
		query.selectVSQL("42");

		checkVSQL("select (-42) /* -42 */, 0 /* 0 */, 42 /* 42 */ from dual", query);
	}

	@Test
	public void selectVSQL_number()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("-42.5");
		query.selectVSQL("0.5");
		query.selectVSQL("42.5");

		checkVSQL("select (-42.5) /* -42.5 */, 0.5 /* 0.5 */, 42.5 /* 42.5 */ from dual", query);
	}

	@Test
	public void selectVSQL_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'foo'");
		query.selectVSQL("\"'\"");

		checkVSQL("select 'foo' /* 'foo' */, '''' /* \"'\" */ from dual", query);
	}

	// no test for the `CLOB` type since there are no literals

	@Test
	public void selectVSQL_color()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("#000");
		query.selectVSQL("#fff");
		query.selectVSQL("#0000");
		query.selectVSQL("#fff0");

		checkVSQL("select 255 /* #000 */, 4294967295 /* #fff */, 0 /* #0000 */, 4294967040 /* #fff0 */ from dual", query);
	}

	// no test for the `GEO` type since there are no literals

	@Test
	public void selectVSQL_date()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("@(2000-02-29)");

		checkVSQL("select to_date('2000-02-29', 'YYYY-MM-DD') /* @(2000-02-29) */ from dual", query);
	}

	@Test
	public void selectVSQL_datetime()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("@(2000-02-29T12:34:56)");

		checkVSQL("select to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS') /* @(2000-02-29T12:34:56) */ from dual", query);
	}

	// no test for the `DATEDELTA`, `DATETIMEDELTA`, and `MONTHDELTA` type since there are no literals

	// @Test
	public void selectVSQL_nulllist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[None, None, None]");

		checkVSQL("select 3 /* [None, None, None] */ from dual", query);
	}

	// @Test
	public void selectVSQL_intlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[17, None, 23]");

		checkVSQL("select integers(17, null, 23) /* [17, None, 23] */ from dual", query);
	}

	// @Test
	public void selectVSQL_numberlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[17.5, None, 23.5]");

		checkVSQL("select numbers(17.5, null, 23.5) /* [17.5, None, 23.5] */ from dual", query);
	}

	// @Test
	public void selectVSQL_strlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("['gurk', None, 'hurz']");

		checkVSQL("select varchars('gurk', null, 'hurz') /* ['gurk', None, 'hurz'] */ from dual", query);
	}

	// no test for the `CLOBLIST` type since since we can't produce a `CLOB` literal

	@Test
	public void selectVSQL_datelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[@(2000-02-29), None, @(2024-09-14)]");

		checkVSQL("select dates(to_date('2000-02-29', 'YYYY-MM-DD'), null, to_date('2024-09-14', 'YYYY-MM-DD')) /* [@(2000-02-29), None, @(2024-09-14)] */ from dual", query);
	}

	// @Test
	public void selectVSQL_datetimelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[@(2000-02-29T12:34:56), None, @(2024-09-14T12:34:56)]");

		checkVSQL("select dates(to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS'), null, to_date('2024-09-14 12:34:56', 'YYYY-MM-DD HH24:MI:SS')) /* [@(2000-02-29 12:34:56), None, @(2024-09-14 12:34:56)] */ from dual", query);
	}

	@Test
	public void selectVSQL_nullset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{None, None, None}");

		checkVSQL("select 1 /* {None, None, None} */ from dual", query);
	}

	@Test
	public void selectVSQL_nullset_empty()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{/}");

		checkVSQL("select 0 /* {/} */ from dual", query);
	}

	@Test
	public void selectVSQL_intset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{17, None, 23}");

		checkVSQL("select vsqlimpl_pkg.set_intlist(integers(17, null, 23)) /* {17, None, 23} */ from dual", query);
	}

	@Test
	public void selectVSQL_numberset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{17.5, None, 23.5}");

		checkVSQL("select vsqlimpl_pkg.set_numberlist(numbers(17.5, null, 23.5)) /* {17.5, None, 23.5} */ from dual", query);
	}

	@Test
	public void selectVSQL_strset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{'gurk', None, 'hurz'}");

		checkVSQL("select vsqlimpl_pkg.set_strlist(varchars('gurk', null, 'hurz')) /* {'gurk', None, 'hurz'} */ from dual", query);
	}

	// there is no `BLOBSET`

	@Test
	public void selectVSQL_dateset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{@(2000-02-29), None, @(2024-09-14)}");

		checkVSQL("select vsqlimpl_pkg.set_datetimelist(dates(to_date('2000-02-29', 'YYYY-MM-DD'), null, to_date('2024-09-14', 'YYYY-MM-DD'))) /* {@(2000-02-29), None, @(2024-09-14)} */ from dual", query);
	}

	@Test
	public void selectVSQL_datetimeset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{@(2000-02-29T12:34:56), None, @(2024-09-14T12:34:56)}");

		checkVSQL("select vsqlimpl_pkg.set_datetimelist(dates(to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS'), null, to_date('2024-09-14 12:34:56', 'YYYY-MM-DD HH24:MI:SS'))) /* {@(2000-02-29T12:34:56), None, @(2024-09-14T12:34:56)} */ from dual", query);
	}

	@Test
	public void selectVSQL_not()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("not True");

		checkVSQL("select (case 1 when 1 then 0 else 1 end) /* not True */ from dual", query);
	}

	// NO test for `neg` since, we can't create a simple expression that uses it

	@Test
	public void selectVSQL_bitnot()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("~42");

		checkVSQL("select (-42 - 1) /* ~42 */ from dual", query);
	}

	@Test
	public void selectVSQL_add()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 + 23");

		checkVSQL("select (17 + 23) /* 17 + 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_sub()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 - 23");

		checkVSQL("select (17 - 23) /* 17 - 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_mul()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 * 23");

		checkVSQL("select (17 * 23) /* 17 * 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_truediv()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 / 23");

		checkVSQL("select (17 / 23) /* 17 / 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_floordiv()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 // 23");

		checkVSQL("select vsqlimpl_pkg.floordiv_int_int(17, 23) /* 17 // 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_mod()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 % 23");

		checkVSQL("select vsqlimpl_pkg.mod_int_int(17, 23) /* 17 % 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_eq()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' == 'b'");

		checkVSQL("select vsqlimpl_pkg.eq_str_str('a', 'b') /* 'a' == 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_ne()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' != 'b'");

		checkVSQL("select (1 - vsqlimpl_pkg.eq_str_str('a', 'b')) /* 'a' != 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_lt()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' < 'b'");

		checkVSQL("select (case when vsqlimpl_pkg.cmp_str_str('a', 'b') < 0 then 1 else 0 end) /* 'a' < 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_le()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' <= 'b'");

		checkVSQL("select (case when vsqlimpl_pkg.cmp_str_str('a', 'b') <= 0 then 1 else 0 end) /* 'a' <= 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_gt()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' > 'b'");

		checkVSQL("select (case when vsqlimpl_pkg.cmp_str_str('a', 'b') > 0 then 1 else 0 end) /* 'a' > 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_ge()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' >= 'b'");

		checkVSQL("select (case when vsqlimpl_pkg.cmp_str_str('a', 'b') >= 0 then 1 else 0 end) /* 'a' >= 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_in()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' in 'abc'");

		checkVSQL("select vsqlimpl_pkg.contains_str_str('a', 'abc') /* 'a' in 'abc' */ from dual", query);
	}

	@Test
	public void selectVSQL_notin()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' not in 'abc'");

		checkVSQL("select (1 - vsqlimpl_pkg.contains_str_str('a', 'abc')) /* 'a' not in 'abc' */ from dual", query);
	}

	@Test
	public void selectVSQL_is()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' is None");

		checkVSQL("select (case when 'a' is null then 1 else 0 end) /* 'a' is None */ from dual", query);
	}

	@Test
	public void selectVSQL_isnot()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' is not None");

		checkVSQL("select (case when 'a' is not null then 1 else 0 end) /* 'a' is not None */ from dual", query);
	}

	@Test
	public void selectVSQL_bitand()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 & 23");

		checkVSQL("select bitand(17, 23) /* 17 & 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_bitor()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 | 23");

		checkVSQL("select vsqlimpl_pkg.bitor_int(17, 23) /* 17 | 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_bitxor()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 ^ 23");

		checkVSQL("select vsqlimpl_pkg.bitxor_int(17, 23) /* 17 ^ 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_shiftleft()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 << 23");

		checkVSQL("select trunc(17 * power(2, 23)) /* 17 << 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_shiftright()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 >> 23");

		checkVSQL("select trunc(17 / power(2, 23)) /* 17 >> 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_and()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' and 'b'");

		checkVSQL("select nvl2('a', 'b', 'a') /* 'a' and 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_or()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' or 'b'");

		checkVSQL("select nvl('a', 'b') /* 'a' or 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_item()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'abc'[1]");

		checkVSQL("select vsqlimpl_pkg.item_str('abc', 1) /* 'abc'[1] */ from dual", query);
	}

	@Test
	public void selectVSQL_slice()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'abc'[1:-1]");

		checkVSQL("select vsqlimpl_pkg.slice_str('abc', 1, (-1)) /* 'abc'[1:-1] */ from dual", query);
	}

	@Test
	public void selectVSQL_if()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' if 'b' else 'c'");

		checkVSQL("select (case when 'b' is not null then 'a' else 'c' end) /* 'a' if 'b' else 'c' */ from dual", query);
	}

	@Test
	public void selectVSQL_func_abs()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("abs(42)");

		checkVSQL("select abs(42) /* abs(42) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_today()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("today()");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_now()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("now()");

		checkVSQL("select sysdate /* now() */ from dual", query);
	}

	@Test
	public void selectVSQL_func_bool()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("bool()");

		checkVSQL("select 0 /* bool() */ from dual", query);
	}

	@Test
	public void selectVSQL_func_bool_none()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("bool(None)");

		checkVSQL("select 0 /* bool(None) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_bool_bool()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("bool(True)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_bool_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("bool(42)");

		checkVSQL("select (case when nvl(42, 0) = 0 then 0 else 1 end) /* bool(42) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_bool_date()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("bool(@(2000-02-29))");

		checkVSQL("select case when to_date('2000-02-29', 'YYYY-MM-DD') is null then 0 else 1 end) /* bool(@(2000-02-29)) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("int()");

		checkVSQL("select 0 /* int() */ from dual", query);
	}

	@Test
	public void selectVSQL_func_int_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("int(False)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_int_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("int('42')");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_float()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("float()");

		checkVSQL("select 0.0 /* float() */ from dual", query);
	}

	@Test
	public void selectVSQL_func_float_float()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("float(42.5)");

		checkVSQL("select 42.5 /* float(42.5) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_float_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("float('42.5')");

		checkVSQL("select vsqlimpl_pkg.float_str('42.5') /* float('42.5') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_geo_int_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("geo(49, 11)");

		checkVSQL("select vsqlimpl_pkg.geo_number_number_str(49, 11, null) /* geo(49, 11) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_geo_int_int_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("geo(49, 11, 'Here')");

		checkVSQL("select vsqlimpl_pkg.geo_number_number_str(49, 11, 'Here') /* geo(49, 11, 'Here') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str()");

		checkVSQL("select null /* str() */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_none()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str(None)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_str_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str('gurk')");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_str_bool()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str(True)");

		checkVSQL("select (case 1 when 0 then 'False' when null then 'None' else 'True' end) /* str(True) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str(42)");

		checkVSQL("select to_char(42) /* str(42) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_number()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str(42.5)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_str_geo()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str(geo(49, 11))");

		checkVSQL("vsqlimpl_pkg.repr_geo(vsqlimpl_pkg.geo_number_number_str(49, 11, null)) /* str(geo(49, 11)) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_date()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str(@(2000-02-29))");

		checkVSQL("select to_char(to_date('2000-02-29', 'YYYY-MM-DD'), 'YYYY-MM-DD') /* str(@(2000-02-29)) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_datetime()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str(@(2000-02-29T12:34:56))");

		checkVSQL("select to_char(to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') /* str(@(2000-02-29T12:34:56)) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_nulllist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str([])");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_str_datelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str([@(2000-02-29)])");

		checkVSQL("select vsqlimpl_pkg.repr_datelist(dates(to_date('2000-02-29', 'YYYY-MM-DD'))) /* str([@(2000-02-29)]) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_intlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str([17, 23])");

		checkVSQL("select vsqlimpl_pkg.repr_intlist(integers(17, 23)) /* str([17, 23]) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_nullset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str({/})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_str_intset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str({17, 23})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_str_numberset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str({17.5, 23.5})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_str_strset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str({'gurk', 'hurz'})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_str_dateset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str({@(2000-02-29)})");

		checkVSQL("select vsqlimpl_pkg.repr_dateset(vsqlimpl_pkg.set_datetimelist(dates(to_date('2000-02-29', 'YYYY-MM-DD')))) /* str({@(2000-02-29)}) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_datetimeset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str(@(2000-02-29T12:34:56))");

		checkVSQL("select to_char(to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') /* str(@(2000-02-29T12:34:56)) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_str_any()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("str({ANY})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_repr_none()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr(None)");

		checkVSQL("select 'None' /* repr(None) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_bool()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr(True)");

		checkVSQL("select (case 1 when 0 then 'False' when null then 'None' else 'True' end) /* repr(True) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_clob()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr(CLOB_CLOBLIST)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_repr_date()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr(@(2000-02-29))");

		checkVSQL("select vsqlimpl_pkg.repr_date(to_date('2000-02-29', 'YYYY-MM-DD')) /* repr(@(2000-02-29)) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_datelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr([@(2000-02-29)])");

		checkVSQL("select vsqlimpl_pkg.repr_datelist(dates(to_date('2000-02-29', 'YYYY-MM-DD'))) /* repr([@(2000-02-29)]) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_nullset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr({/})");

		checkVSQL("select vsqlimpl_pkg.repr_nullset(0) /* repr({/}) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_intset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr({17, 23})");

		checkVSQL("select vsqlimpl_pkg.repr_intset(vsqlimpl_pkg.set_intlist(integers(17, 23))) /* repr({17, 23}) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_numberset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr({17.5, 23.5})");

		checkVSQL("select vsqlimpl_pkg.repr_numberset(vsqlimpl_pkg.set_numberlist(numbers(17.5, 23.5))) /* repr({17.5, 23.5}) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_strset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr({'gurk'})");

		checkVSQL("select vsqlimpl_pkg.repr_strset(vsqlimpl_pkg.set_strlist(varchars('gurk'))) /* repr({'gurk'}) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_dateset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr({@(2000-02-29)})");

		checkVSQL("select vsqlimpl_pkg.repr_dateset(vsqlimpl_pkg.set_datetimelist(dates(to_date('2000-02-29', 'YYYY-MM-DD')))) /* repr({@(2000-02-29)}) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_datetimeset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr(@(2000-02-29T12:34:56))");

		checkVSQL("select vsqlimpl_pkg.repr_datetime(to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS')) /* repr(@(2000-02-29T12:34:56)) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_repr_any()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("repr({ANY})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_date_int_int_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("date(2000, 2, 29)");

		checkVSQL("select vsqlimpl_pkg.date_int(2000, 2, 29) /* date(2000, 2, 29) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_date_datetime()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("date(@(2000-02-29T12:34:56))");

		checkVSQL("select trunc(to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS')) /* date(@(2000-02-29T12:34:56)) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_datetime_int3()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("datetime(2000, 2, 29)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_datetime_int4()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("datetime(2000, 2, 29, 12)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_datetime_int5()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("datetime(2000, 2, 29, 12, 34)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_datetime_int6()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("datetime(2000, 2, 29, 12, 34)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_datetime_date()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("select to_date('2000-02-29', 'YYYY-MM-DD') /* datetime(@(2000-02-29)) */ from dual");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_datetime_date_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("datetime(@(2000-02-29), 12)");

		checkVSQL("select (to_date('2000-02-29', 'YYYY-MM-DD') + 12/24) /* datetime(@(2000-02-29), 12) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_datetime_date_int2()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("datetime(@(2000-02-29), 12, 34)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_datetime_date_int3()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("datetime(@(2000-02-29), 12, 34, 56)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_len_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("len('gurk')");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_len_nulllist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("len([])");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_len_list()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("len(['gurk'])");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_len_nullset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("len({/})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_len_set()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("len({42})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_timedelta()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("timedelta()");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_timedelta_int1()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("timedelta(1)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_timedelta_int2()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("timedelta(1, 12)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_monthdelta()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("monthdelta()");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_monthdelta_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("monthdelta(2)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_years()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("years(2)");

		checkVSQL("select (12 * 2) /* years(2) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_months()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("months(2)");

		checkVSQL("select 2 /* months(2) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_weeks()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("weeks(2)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_days()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("days(2)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_hours()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("hours(12)");

		checkVSQL("select (12 / 24) /* hours(12) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_minutes()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("minutes(34)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_seconds()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("seconds(56)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_md5()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("md5('gurk')");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_random()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("random()");

		checkVSQL("select dbms_random.value /* random() */ from dual", query);
	}

	@Test
	public void selectVSQL_func_randrange()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("randrange(0, 10)");

		checkVSQL("select floor(dbms_random.value(0, 10)) /* randrange(0, 10) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_seq()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("seq()");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_rgb_int3()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("rgb(0.2, 0.4, 0.6)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_rgb_int4()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("rgb(0.2, 0.4, 0.6, 0.8)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_list_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("list('gurk')");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_list_list()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("list([17, 23])");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_list_nullset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("list({/})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_list_intset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("list({17, 23})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_list_numberset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("list({17.5, 23.5})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_list_strset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("list({'gurk'})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_list_dateset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("list({@(2000-02-29)})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_list_datetimeset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("list({@(2000-02-29T12:34:56)})");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_set_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("set('gurk')");

		checkVSQL("select vsqlimpl_pkg.set_str('gurk') /* set('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_set_set()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("set({17})");

		checkVSQL("select vsqlimpl_pkg.set_intlist(integers(17)) /* set({17}) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_set_nulllist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("set([])");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_set_intlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("set([17, 23])");

		checkVSQL("select vsqlimpl_pkg.set_intlist(integers(17, 23)) /* set([17, 23]) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_set_numberlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("set([17.5, 23.5])");

		checkVSQL("select vsqlimpl_pkg.set_numberlist(numbers(17.5, 23.5)) /* set([17.5, 23.5]) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_set_strlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("set(['gurk'])");

		checkVSQL("select vsqlimpl_pkg.set_strlist(varchars('gurk')) /* set(['gurk']) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_set_datelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("set([@(2000-02-29)])");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_set_datetimelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("set([@(2000-02-29T12:34:56)])");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_dist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("dist(geo(49, 11), geo(0, 0))");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_abs_bool()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("abs(True)");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_abs_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("abs(42)");

		checkVSQL("select abs(42) /* abs(42) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_abs_number()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("abs(42.5)");

		checkVSQL("select abs(42.5) /* abs(42.5) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_cos()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("cos(3)");

		checkVSQL("select cos(3) /* cos(3) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_sin()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("sin(3)");

		checkVSQL("select sin(3) /* sin(3) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_tan()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("tan(3)");

		checkVSQL("select tan(3) /* tan(3) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_sqrt()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("sqrt(42)");

		checkVSQL("select sqrt(case when 42 >= 0 then 42 else null end) /* sqrt(42) */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_id()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_id()");

		checkVSQL("select livingapi_pkg.reqid /* request_id() */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_method()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_method()");

		checkVSQL("select livingapi_pkg.reqmethod /* request_method() */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_url()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_url()");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_request_header_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_header_str('Content-Type')");

		checkVSQL("select livingapi_pkg.reqheader_str('Content-Type') /* request_header_str('Content-Type') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_header_strlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_header_strlist('Content-Type')");

		checkVSQL("select livingapi_pkg.reqheader_str('Content-Type') /* request_header_strlist('Content-Type') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_cookie()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_cookie('gurk')");

		checkVSQL("select livingapi_pkg.reqcookie_str('gurk') /* request_cookie('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_str('gurk')");

		checkVSQL("select livingapi_ppgest_param_str('gurk') /* request_param_str('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_strlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_strlist('gurk')");

		checkVSQL("select livingapi_ppg.reqparam_strlist('gurk') /* request_param_strlist('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_int('gurk')");

		checkVSQL("select livingapi_ppgest_param_int('gurk') /* request_param_int('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_intlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_intlist('gurk')");

		checkVSQL("select livingapi_ppg.reqparam_intlist('gurk') /* request_param_intlist('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_float()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_float('gurk')");

		checkVSQL("select livingapi_ppg.rt_param_float('gurk') /* request_param_float('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_floatlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_floatlist('gurk')");

		checkVSQL("select livingapi_ppg.requeram_floatlist('gurk') /* request_param_floatlist('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_date()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_date('gurk')");

		checkVSQL("select livingapi_ppg.st_param_date('gurk') /* request_param_date('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_datelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_datelist('gurk')");

		checkVSQL("select livingapi_ppg.requaram_datelist('gurk') /* request_param_datelist('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_datetime()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_datetime('gurk')");

		checkVSQL("select livingapi_ppg.requaram_datetime('gurk') /* request_param_datetime('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_request_param_datetimelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("request_param_datetimelist('gurk')");

		checkVSQL("select livingapi_ppg.request__datetimelist('gurk') /* request_param_datetimelist('gurk') */ from dual", query);
	}

	@Test
	public void selectVSQL_func_search()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("search()");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_lang()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("lang()");

		checkVSQL("", query);
	}

	@Test
	public void selectVSQL_func_mode()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("mode()");

		checkVSQL("", query);
	}


}
