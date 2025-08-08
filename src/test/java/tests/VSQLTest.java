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
		query.selectVSQL("None", null);

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
		query.selectVSQL("False", null);
		query.selectVSQL("True", null);

		checkVSQL("select 0 /* False */, 1 /* True */ from dual", query);
	}

	@Test
	public void selectVSQL_int()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("-42", null);
		query.selectVSQL("0", null);
		query.selectVSQL("42", null);

		checkVSQL("select (-42) /* -42 */, 0 /* 0 */, 42 /* 42 */ from dual", query);
	}

	@Test
	public void selectVSQL_number()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("-42.5", null);
		query.selectVSQL("0.5", null);
		query.selectVSQL("42.5", null);

		checkVSQL("select (-42.5) /* -42.5 */, 0.5 /* 0.5 */, 42.5 /* 42.5 */ from dual", query);
	}

	@Test
	public void selectVSQL_str()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'foo'", null);
		query.selectVSQL("\"'\"", null);

		checkVSQL("select 'foo' /* 'foo' */, '''' /* \"'\" */ from dual", query);
	}

	// no test for the `CLOB` type since there are no literals

	@Test
	public void selectVSQL_color()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("#000", null);
		query.selectVSQL("#fff", null);
		query.selectVSQL("#0000", null);
		query.selectVSQL("#fff0", null);

		checkVSQL("select 255 /* #000 */, 4294967295 /* #fff */, 0 /* #0000 */, 4294967040 /* #fff0 */ from dual", query);
	}

	// no test for the `GEO` type since there are no literals

	@Test
	public void selectVSQL_date()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("@(2000-02-29)", null);

		checkVSQL("select to_date('2000-02-29', 'YYYY-MM-DD') /* @(2000-02-29) */ from dual", query);
	}

	@Test
	public void selectVSQL_datetime()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("@(2000-02-29T12:34:56)", null);

		checkVSQL("select to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS') /* @(2000-02-29T12:34:56) */ from dual", query);
	}

	// no test for the `DATEDELTA`, `DATETIMEDELTA`, and `MONTHDELTA` type since there are no literals

	// @Test
	public void selectVSQL_nulllist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[None, None, None]", null);

		checkVSQL("select 3 /* [None, None, None] */ from dual", query);
	}

	// @Test
	public void selectVSQL_intlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[17, None, 23]", null);

		checkVSQL("select integers(17, null, 23) /* [17, None, 23] */ from dual", query);
	}

	// @Test
	public void selectVSQL_numberlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[17.5, None, 23.5]", null);

		checkVSQL("select numbers(17.5, null, 23.5) /* [17.5, None, 23.5] */ from dual", query);
	}

	// @Test
	public void selectVSQL_strlist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("['gurk', None, 'hurz']", null);

		checkVSQL("select varchars('gurk', null, 'hurz') /* ['gurk', None, 'hurz'] */ from dual", query);
	}

	// no test for the `CLOBLIST` type since since we can't produce a `CLOB` literal

	@Test
	public void selectVSQL_datelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[@(2000-02-29), None, @(2024-09-14)]", null);

		checkVSQL("select dates(to_date('2000-02-29', 'YYYY-MM-DD'), null, to_date('2024-09-14', 'YYYY-MM-DD')) /* [@(2000-02-29), None, @(2024-09-14)] */ from dual", query);
	}

	// @Test
	public void selectVSQL_datetimelist()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("[@(2000-02-29T12:34:56), None, @(2024-09-14T12:34:56)]", null);

		checkVSQL("select dates(to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS'), null, to_date('2024-09-14 12:34:56', 'YYYY-MM-DD HH24:MI:SS')) /* [@(2000-02-29 12:34:56), None, @(2024-09-14 12:34:56)] */ from dual", query);
	}

	@Test
	public void selectVSQL_nullset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{None, None, None}", null);

		checkVSQL("select 1 /* {None, None, None} */ from dual", query);
	}

	@Test
	public void selectVSQL_nullset_empty()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{/}", null);

		checkVSQL("select 0 /* {/} */ from dual", query);
	}

	@Test
	public void selectVSQL_intset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{17, None, 23}", null);

		checkVSQL("select vsqlimpl_pkg.set_intlist(integers(17, null, 23)) /* {17, None, 23} */ from dual", query);
	}

	@Test
	public void selectVSQL_numberset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{17.5, None, 23.5}", null);

		checkVSQL("select vsqlimpl_pkg.set_numberlist(numbers(17.5, null, 23.5)) /* {17.5, None, 23.5} */ from dual", query);
	}

	@Test
	public void selectVSQL_strset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{'gurk', None, 'hurz'}", null);

		checkVSQL("select vsqlimpl_pkg.set_strlist(varchars('gurk', null, 'hurz')) /* {'gurk', None, 'hurz'} */ from dual", query);
	}

	// there is no `BLOBSET`

	@Test
	public void selectVSQL_dateset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{@(2000-02-29), None, @(2024-09-14)}", null);

		checkVSQL("select vsqlimpl_pkg.set_datetimelist(dates(to_date('2000-02-29', 'YYYY-MM-DD'), null, to_date('2024-09-14', 'YYYY-MM-DD'))) /* {@(2000-02-29), None, @(2024-09-14)} */ from dual", query);
	}

	@Test
	public void selectVSQL_datetimeset()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("{@(2000-02-29T12:34:56), None, @(2024-09-14T12:34:56)}", null);

		checkVSQL("select vsqlimpl_pkg.set_datetimelist(dates(to_date('2000-02-29 12:34:56', 'YYYY-MM-DD HH24:MI:SS'), null, to_date('2024-09-14 12:34:56', 'YYYY-MM-DD HH24:MI:SS'))) /* {@(2000-02-29T12:34:56), None, @(2024-09-14T12:34:56)} */ from dual", query);
	}

	@Test
	public void selectVSQL_not()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("not True", null);

		checkVSQL("select (case 1 when 1 then 0 else 1 end) /* not True */ from dual", query);
	}

	// NO test for `neg` since, we can't create a simple expression that uses it

	@Test
	public void selectVSQL_bitnot()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("~42", null);

		checkVSQL("select (-42 - 1) /* ~42 */ from dual", query);
	}

	@Test
	public void selectVSQL_add()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 + 23", null);

		checkVSQL("select (17 + 23) /* 17 + 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_sub()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 - 23", null);

		checkVSQL("select (17 - 23) /* 17 - 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_mul()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 * 23", null);

		checkVSQL("select (17 * 23) /* 17 * 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_truediv()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 / 23", null);

		checkVSQL("select (17 / 23) /* 17 / 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_floordiv()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 // 23", null);

		checkVSQL("select vsqlimpl_pkg.floordiv_int_int(17, 23) /* 17 // 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_mod()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 % 23", null);

		checkVSQL("select vsqlimpl_pkg.mod_int_int(17, 23) /* 17 % 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_eq()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' == 'b'", null);

		checkVSQL("select vsqlimpl_pkg.eq_str_str('a', 'b') /* 'a' == 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_ne()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' != 'b'", null);

		checkVSQL("select (1 - vsqlimpl_pkg.eq_str_str('a', 'b')) /* 'a' != 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_lt()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' < 'b'", null);

		checkVSQL("select (case when vsqlimpl_pkg.cmp_str_str('a', 'b') < 0 then 1 else 0 end) /* 'a' < 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_le()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' <= 'b'", null);

		checkVSQL("select (case when vsqlimpl_pkg.cmp_str_str('a', 'b') <= 0 then 1 else 0 end) /* 'a' <= 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_gt()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' > 'b'", null);

		checkVSQL("select (case when vsqlimpl_pkg.cmp_str_str('a', 'b') > 0 then 1 else 0 end) /* 'a' > 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_ge()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' >= 'b'", null);

		checkVSQL("select (case when vsqlimpl_pkg.cmp_str_str('a', 'b') >= 0 then 1 else 0 end) /* 'a' >= 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_in()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' in 'abc'", null);

		checkVSQL("select vsqlimpl_pkg.contains_str_str('a', 'abc') /* 'a' in 'abc' */ from dual", query);
	}

	@Test
	public void selectVSQL_notin()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' not in 'abc'", null);

		checkVSQL("select (1 - vsqlimpl_pkg.contains_str_str('a', 'abc')) /* 'a' not in 'abc' */ from dual", query);
	}

	@Test
	public void selectVSQL_is()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' is None", null);

		checkVSQL("select (case when 'a' is null then 1 else 0 end) /* 'a' is None */ from dual", query);
	}

	@Test
	public void selectVSQL_isnot()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' is not None", null);

		checkVSQL("select (case when 'a' is not null then 1 else 0 end) /* 'a' is not None */ from dual", query);
	}

	@Test
	public void selectVSQL_bitand()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 & 23", null);

		checkVSQL("select bitand(17, 23) /* 17 & 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_bitor()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 | 23", null);

		checkVSQL("select vsqlimpl_pkg.bitor_int(17, 23) /* 17 | 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_bitxor()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 ^ 23", null);

		checkVSQL("select vsqlimpl_pkg.bitxor_int(17, 23) /* 17 ^ 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_shiftleft()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 << 23", null);

		checkVSQL("select trunc(17 * power(2, 23)) /* 17 << 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_shiftright()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("17 >> 23", null);

		checkVSQL("select trunc(17 / power(2, 23)) /* 17 >> 23 */ from dual", query);
	}

	@Test
	public void selectVSQL_and()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' and 'b'", null);

		checkVSQL("select nvl2('a', 'b', 'a') /* 'a' and 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_or()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'a' or 'b'", null);

		checkVSQL("select nvl('a', 'b') /* 'a' or 'b' */ from dual", query);
	}

	@Test
	public void selectVSQL_item()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'abc'[1]", null);

		checkVSQL("select vsqlimpl_pkg.item_str('abc', 1) /* 'abc'[1] */ from dual", query);
	}

	@Test
	public void selectVSQL_slice()
	{
		VSQLQuery query = new VSQLQuery();
		query.selectVSQL("'abc'[1:-1]", null);

		checkVSQL("select vsqlimpl_pkg.slice_str('abc', 1, (-1)) /* 'abc'[1:-1] */ from dual", query);
	}
}
