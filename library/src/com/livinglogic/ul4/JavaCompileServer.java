package com.livinglogic.ul4;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;


public class JavaCompileServer
{
	protected void start() throws Exception
	{
		ServerSocket socket = new ServerSocket(60000);
		for (;;)
		{
			Socket remote = socket.accept();
			JavaCompileHandler handler = new JavaCompileHandler(remote);
			handler.start();
		}
	}

	public static void main(String args[]) throws Exception
	{
		JavaCompileServer server = new JavaCompileServer();
		server.start();
	}
}

class JavaCompileHandler extends Thread
{
	private Socket socket;

	public JavaCompileHandler(Socket socket)
	{
		this.socket = socket;
	}

	public void run()
	{
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
			OutputStream out = socket.getOutputStream();
			StringBuffer inputBuffer = new StringBuffer();
			inputBuffer.append("\tpublic String execute()\n");
			inputBuffer.append("\t{\n");
			for (;;)
			{
				String input = in.readLine();
				if (input == null || input.equals("@@@"))
					break;
				inputBuffer.append(input);
				inputBuffer.append("\n");
			}
			inputBuffer.append("\t}\n");
			System.out.println("INPUT: " + Utils.repr(inputBuffer.toString()));
			Executor executor = (Executor)Utils.compileToJava(inputBuffer.toString(), null, "com.livinglogic.ul4.Executor");
			String output;
			String status;
			try
			{
				Thread.sleep(2000);
				output = executor.execute();
				status = "OK\n";
			}
			catch (Exception ex)
			{
				output = ex.toString();
				status = "ERROR\n";
			}

			System.out.print("STATUS: " + status);
			System.out.println("OUTPUT: " + Utils.repr(output));
			out.write(status.getBytes("UTF-8"));
			out.flush();
			out.write(output.getBytes("UTF-8"));
			out.flush();
			out.write("\n@@@\n".getBytes("UTF-8"));
			out.flush();
			in.close();
			out.close();
		}
		catch (Exception ex)
		{
		}
		finally
		{
			try
			{
				socket.close();
			}
			catch (Exception ex)
			{
			}
		}
	}
}