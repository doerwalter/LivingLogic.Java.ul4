package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.lang.Byte;
import java.math.BigInteger;
import java.math.BigDecimal;


public class Main
{
	public static Object dings(int i, int value)
	{
		switch (i)
		{
			case 0:
				return true;
			case 1:
				return new Byte((byte)value);
			case 2:
				return new Short((short)value);
			case 3:
				return new Integer(value);
			case 4:
				return new Float(value);
			case 5:
				return new Double(value);
			case 6:
				return new BigInteger(Integer.toString(value));
			case 7:
				return new BigDecimal(Integer.toString(value));
			default:
				return null;
		}
	}
	public static void main(String[] args)
	{
		Long c = new Long(42);
		Template tmpl = Compiler.compile("<?print a?>(<?print type(a)?>) + <?print b?>(<?print type(b)?>) = <?print a + b?>(<?print type(a+b)?>)");
		System.out.println(tmpl);
		for (int i = 0; i <= 7; ++i)
		{
			for (int j = 0; j <= 7; ++j)
			{
				Map vars = new HashMap<String, Object>();
				vars.put("a", dings(i, 17));
				vars.put("b", dings(j, 23));
				long start = System.currentTimeMillis();
				String output = tmpl.renders(vars);
				// System.out.println("rendered " + (System.currentTimeMillis()-start));
				System.out.println(output);
			}
		}
	}
}
