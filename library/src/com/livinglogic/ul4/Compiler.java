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
		Properties props = new Properties();
		props.setProperty("python.path", "C:\\jython\\Lib;C:\\ul4");
		PythonInterpreter.initialize(System.getProperties(), props, new String[] {""});
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("from ul4c import Compiler");
		PyObject compilerclass = interpreter.get("Compiler");
		PyObject compilerObj = compilerclass.__call__();
		compiler = (CompilerType)compilerObj.__tojava__(CompilerType.class);
	}

	public static Template compile(String source)
	{
		return compile(source, "<?", "?>");
	}

	public static Template compile(String source, String startdelim, String enddelim)
	{
		List tags = Template.tokenizeTags(source, startdelim, enddelim);
		return compiler.compile(source, tags, startdelim, enddelim);
	}
}