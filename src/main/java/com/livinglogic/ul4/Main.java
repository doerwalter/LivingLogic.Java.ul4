package com.livinglogic.ul4;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.math.BigInteger;
import java.text.DecimalFormat;


public class Main
{
	private static class Timer
	{
		private long startTime;

		public Timer()
		{
		}

		public void start()
		{
			startTime = System.nanoTime();
		}

		public long stop(String message)
		{
			long duration = System.nanoTime() - startTime;
			String pretty;
			if (duration >= 1000000000)
				pretty = new DecimalFormat("##0.0##").format(duration/1000000000.) + " s";
			else if (duration >= 1000000)
				pretty = new DecimalFormat("##0.0##").format(duration/1000000.) + " milli-s";
			else if (duration >= 1000)
				pretty = new DecimalFormat("##0.0##").format(duration/1000.) + " micro-s";
			else
				pretty = new DecimalFormat("##0.0##").format(duration) + "nano-s";
			System.out.println(message + " (" + pretty + ")");
			return duration;
		}
	}

	public static void main(String[] args) throws java.io.IOException
	{
		Timer timer = new Timer();
		timer.start();
		InterpretedTemplate tmpl = Compiler.compile("<?code langs = ['Python', 'Java', 'C']?><ul><?for l in langs?><li><?printx l?></li><?end for?></ul>");
		timer.stop("Compiled template to UL4 bytecode once");
		timer.start();
		tmpl = Compiler.compile("<?code langs = ['Python', 'Java', 'C']?><ul><?for l in langs?><li><?printx l?></li><?end for?></ul>");
		timer.stop("Compiled template to UL4 bytecode twice");

		System.out.println("Testing template:");
		System.out.println(tmpl.source);
		System.out.println();

		System.out.println("Byte code for template:");
		System.out.println(tmpl);
		// Not required System.out.println();

		Map vars = new HashMap<String, Object>();
		vars.put("t", tmpl);

		System.out.println("Interpreted run:");
		timer.start();
		String interpretedoutput = tmpl.renders(vars);
		long interpretedrendertime = timer.stop("Rendered template via InterpretedTemplate.renders() once");
		timer.start();
		interpretedoutput = tmpl.renders(vars);
		interpretedrendertime = timer.stop("Rendered template via InterpretedTemplate.renders() twice");
		System.out.println("output " + interpretedoutput);
		System.out.println();

		System.out.println("Compiled run:");
		timer.start();
		JSPTemplate compiledTmpl = tmpl.compileToJava();
		long compiledcompiletime = timer.stop("Compiled template to Java code once");
		timer.start();
		compiledTmpl = tmpl.compileToJava();
		compiledcompiletime = timer.stop("Compiled template to Java code twice");

		timer.start();
		String compiledoutput = compiledTmpl.renders(vars);
		long compiledrendertime = timer.stop("Rendered compiled template once");
		timer.start();
		compiledoutput = compiledTmpl.renders(vars);
		compiledrendertime = timer.stop("Rendered compiled template twice");
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
