package com.livinglogic.ul4;

import java.util.Properties;
import java.util.List;

import org.python.core.PyObject;
import org.python.core.PyString;
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
		return compile(source, "<?", "?>");
	}

	public static InterpretedTemplate compile(String source, String startdelim, String enddelim)
	{
		List tags = InterpretedTemplate.tokenizeTags(source, startdelim, enddelim);
		return compiler.compile(source, tags, startdelim, enddelim);
	}
}
