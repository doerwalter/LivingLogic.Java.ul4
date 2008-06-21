package com.livinglogic.ull;

import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.LinkedList;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class Compiler
{
	static private CompilerType compiler;

	static
	{
		Properties props = new Properties();
		props.setProperty("python.path", "C:\\jython\\Lib;C:\\ull");
		PythonInterpreter.initialize(System.getProperties(), props, new String[] {""});
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("from ullc import Compiler");
		PyObject compilerclass = interpreter.get("Compiler");
		PyObject compilerObj = compilerclass.__call__();
		compiler = (CompilerType)compilerObj.__tojava__(CompilerType.class);
	}

	public static List tokenize(String source, String startdelim, String enddelim)
	{
		Pattern tagPattern = Pattern.compile(escapeREchars(startdelim) + "(print|code|for|if|elif|else|end|render)(\\s*((.|\\n)*?)\\s*)?" + escapeREchars(enddelim));
		LinkedList tags = new LinkedList();
		Matcher matcher = tagPattern.matcher(source);
		int pos = 0;

		int start;
		int end;
		while (matcher.find())
		{
			start = matcher.start();
			end = start + matcher.group().length();
			if (pos != start)
				tags.add(new Location(source, null, pos, start, pos, start));
			int codestart = matcher.start(3);
			int codeend = codestart + matcher.group(3).length();
			tags.add(new Location(source, matcher.group(1), start, end, codestart, codeend));
			pos = end;
		}
		end = source.length();
		if (pos != end)
			tags.add(new Location(source, null, pos, end, pos, end));
		return tags;
	}

	private static String escapeREchars(String input)
	{
		int len = input.length();

		StringBuffer output = new StringBuffer(len);

		for (int i = 0; i < len; ++i)
		{
			char c = input.charAt(i);
			if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9')))
				output.append('\\');
			output.append(c);
		}
		return output.toString();
	}

	public static Template compile(String source)
	{
		return compile(source, "<?", "?>");
	}

	public static Template compile(String source, String startdelim, String enddelim)
	{
		List tags = tokenize(source, startdelim, enddelim);
		return compiler.compile(source, tags, startdelim, enddelim);
	}
}