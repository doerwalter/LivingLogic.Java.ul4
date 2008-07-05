package com.livinglogic.ul4;

import java.util.Date;
import java.util.HashMap;

public class Main
{
	public static long time()
	{
		return new Date().getTime();
	}

	public static void main(String[] args)
	{
		Template tmpl1 = Compiler.compile("<?print key?> (<?print value?>)\n");
		Template tmpl2 = Compiler.compile("<?for (key, value) in {'eins': 1, 'zwei': 2, 'drei': 3,}.items()?><?render item(key=key, value=value)?><?end for?>");
		long start = new Date().getTime();
		HashMap templates = new HashMap();
		templates.put("item", tmpl1);
		String output = tmpl2.renders(null, templates);
		System.out.println("rendered " + (time()-start));
		System.out.println(output);
	}
}
