package com.livinglogic.ul4;

import java.util.List;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class Compiler
{
	static private CompilerType compiler;

	static
	{
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("from ul4c import Compiler");
		PyObject compilerclass = interpreter.get("Compiler");
		PyObject compilerObj = compilerclass.__call__();
		compiler = (CompilerType)compilerObj.__tojava__(CompilerType.class);
	}

	public static InterpretedTemplate compile(String source)
	{
		return compile(source, "unnamed", "<?", "?>");
	}

	public static InterpretedTemplate compile(String source, String name)
	{
		return compile(source, name, "<?", "?>");
	}

	public static InterpretedTemplate compile(String source, String startdelim, String enddelim)
	{
		return compile(source, "unnamed", startdelim, enddelim);
	}

	public static InterpretedTemplate compile(String source, String name, String startdelim, String enddelim)
	{
		List<Location> tags = InterpretedTemplate.tokenizeTags(source, name, startdelim, enddelim);
		return compiler.compile(source, name, tags, startdelim, enddelim);
	}
}
