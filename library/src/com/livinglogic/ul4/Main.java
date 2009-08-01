package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;


public class Main
{
	public static void main(String[] args)
	{
		Long c = new Long(42);
		Template tmpl = Compiler.compile("<?def p?><?if islist(o)?>[<?for (i,c) in enumerate(o)?><?if i?>, <?end if?><?render p(o=c, p=p)?><?end for?>]<?elif isdict(o)?>{<?for (i, kv) in enumerate(o.items())?><?if i?>, <?end if?><?render p(o=kv[0], p=p)?>: <?render p(o=kv[1], p=p)?><?end for?>}<?else?><?print repr(o)?><?end if?><?end def?><?code x = {'x': 42, 'y': 23, 'z': 17, 'templates': [{'id': 10, 'identifier': 'kunde', 'controls': [{'id': 10, 'identifier': 'name', 'type': 'string'}, {'id': 20, 'identifier': 'vorname', 'type': 'string'}]}, {'id': 20, 'identifier': 'rechnung', 'controls': [{'id': 10, 'identifier': 'betrag', 'type': 'number'}, {'id': 20, 'identifier': 'datum', 'type': 'date'}]}]}?><?render p(o=x, p=p)?>");
		// System.out.println(tmpl);
		long start = System.currentTimeMillis();
		Map vars = new HashMap<String, Object>();
		vars.put("t", "123");
		String output = tmpl.renders(vars);
		System.out.println("rendered " + (System.currentTimeMillis()-start));
		System.out.println(output);
	}
}
