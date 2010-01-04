package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.lang.Byte;
import java.math.BigInteger;


public class Main
{
	public static void main(String[] args)
	{
		Long c = new Long(42);
		Template tmpl = Compiler.compile("<?print abs(t)?>");
		System.out.println(tmpl);
		Map vars = new HashMap<String, Object>();
		vars.put("t", new BigInteger("-42"));
		long start = System.currentTimeMillis();
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
