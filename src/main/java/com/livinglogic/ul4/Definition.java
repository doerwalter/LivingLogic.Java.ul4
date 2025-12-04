/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Set;
import java.io.IOException;


/**
Stores the name and signature of a defined function or template.
**/
public class Definition
{
	protected String name;
	protected SignatureAST signature;

	public Definition(String name, SignatureAST signature)
	{
		this.name = name;
		this.signature = signature;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public SignatureAST getSignature()
	{
		return signature;
	}

	public void setSignature(SignatureAST signature)
	{
		this.signature = signature;
	}
}
