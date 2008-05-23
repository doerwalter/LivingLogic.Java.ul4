import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.util.Date;

public class CompilerFactory
{
	public CompilerFactory()
	{
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("import sys");
		interpreter.exec("sys.path.append('.')");
		interpreter.exec("from l4c import Compiler");
		PyObject compilerclass = interpreter.get("Compiler");
		PyObject compilerObj = compilerclass.__call__();
		compiler = (L4CompilerType)compilerObj.__tojava__(L4CompilerType.class);
	}

	public String compile(String source)
	{
		System.out.println(new Date().getTime());
		String result = (compiler).compile(source);
		System.out.println(new Date().getTime());
		return result;
	}

	private L4CompilerType compiler;
}