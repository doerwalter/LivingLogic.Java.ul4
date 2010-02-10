package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;


public class Main
{
	public static void main(String[] args)
	{
		Template tmpl = Compiler.compile("<?print len('gurk')?>, <?print len([1,2,3])?>, <?print len({1: 'eins', 2: 'zwei', 3: 'drei'})?>");
		System.out.println(tmpl);

		Map vars = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
