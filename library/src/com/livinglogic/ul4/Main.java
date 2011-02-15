package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.math.BigInteger;


public class Main
{
	private static String pretty(long duration)
	{
		return (duration/1000) + "micros";
	}

	public static void main(String[] args) throws java.io.IOException
	{
		InterpretedTemplate tmpl = Compiler.compile("<?code langs = ['Python', 'Java', 'C']?><ul><?for l in langs?><li><?printx l?></li><?end for?></ul>");
		System.out.println("Testing template:");
		System.out.println(tmpl.source);
		System.out.println();

		System.out.println("Byte code for template:");
		System.out.println(tmpl);
		// Not required System.out.println();

		Map vars = new HashMap<String, Object>();
		vars.put("t", tmpl);

		System.out.println("Interpreted run:");
		long interpretedstart = System.nanoTime();
		String interpretedoutput = tmpl.renders(vars);
		long interpretedend = System.nanoTime();
		long interpretedrendertime = interpretedend - interpretedstart;
		System.out.println("rendered in " + pretty(interpretedrendertime));
		System.out.println("output " + interpretedoutput);
		System.out.println();

		System.out.println("Compiled run:");
		long compiledcompilestart = System.nanoTime();
		JSPTemplate compiledTmpl = tmpl.compileToJava();
		long compiledcompileend = System.nanoTime();
		long compiledcompiletime = compiledcompileend - compiledcompilestart;
		long compiledrenderstart = System.nanoTime();
		String compiledoutput = compiledTmpl.renders(vars);
		long compiledrenderend = System.nanoTime();
		long compiledrendertime = compiledrenderend - compiledrenderstart;
		System.out.println("compiled in " + pretty(compiledcompiletime));
		System.out.println("rendered in " + pretty(compiledrendertime));
		System.out.println("output " + compiledoutput);
		System.out.println();

		if (compiledoutput.equals(interpretedoutput))
			System.out.println("identical output");
		else
			System.out.println("outputs not identical!");

		if (interpretedrendertime > compiledrendertime)
		{
			double breakeven = (compiledcompiletime) / (interpretedrendertime - compiledrendertime);
			System.out.println("Break even: " + breakeven);
		}
		else
			System.out.println("No break even");
	}
}
