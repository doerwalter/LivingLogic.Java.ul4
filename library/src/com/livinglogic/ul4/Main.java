package com.livinglogic.ul4;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Main
{
	public static long time()
	{
		return new Date().getTime();
	}

	public static void main(String[] args)
	{
		Template tmpl = Compiler.compile("<?for o in [None, True, False, 42, 4.2, 'fo\\'\"\\r\\n', now(), [0, 1, None], {1: 'eins', 2: 'zwei', 3: 'drei'}]?><?print csvescape(o)?>\n<?end for?>");
		long start = new Date().getTime();
		String output = tmpl.renders();
		System.out.println("rendered " + (time()-start));
		System.out.println(output);
	}
}
