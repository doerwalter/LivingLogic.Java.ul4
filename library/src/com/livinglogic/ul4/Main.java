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
		Template tmpl = Compiler.compile("<?for i in [1,2,3,4]?><?for j in [1,2,3,4]?><?print j?>, <?if j>=i?><?break?><?end if?><?end for?><?if i>=3?><?break?><?end if?>\n<?end for?>");
		long start = new Date().getTime();
		String output = tmpl.renders();
		System.out.println("rendered " + (time()-start));
		System.out.println(output);
	}
}
