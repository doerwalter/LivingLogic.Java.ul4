package com.livinglogic.pull;

public interface CompilerType
{
	public Template compile(String source, String startdelim, String enddelim);
}