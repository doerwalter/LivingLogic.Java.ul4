/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collection;


public class Signature implements UL4Instance, UL4Repr, Iterable<ParameterDescription>
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getNameUL4()
		{
			return "Signature";
		}

		@Override
		public String getDoc()
		{
			return "The signature of a function or callable.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Signature;
		}
	}

	public static final UL4Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	All parameters in the order they were specified in the constructor.
	**/
	protected LinkedHashMap<String, ParameterDescription> parameters;

	/**
	The number of parameters that can ony be passed by position.
	**/
	protected int countPositionalOnly;

	/**
	The number of parameters can be passed both positionally and via keyword.
	**/
	protected int countBoth;

	/**
	The number of parameters can be only be passed via keyword.
	**/
	protected int countKeywordOnly;

	/**
	The number of parameters that have defaults.
	**/
	protected int countDefaults;

	/**
	Does the signature have a {@code *} parameter (which collections any
	additional positional argument)?
	**/
	protected boolean hasVarPositional;

	/**
	Does the signature have a {@code **} parameter (which collections any
	additional keyword argument)?
	**/
	protected boolean hasVarKeyword;

	protected List<String> parameterNames;

	/**
	Marker object that specifies that this parameter is required.
	**/
	public static Object required = new Object();

	/**
	Marker object that specifies that this parameter collects any additional
	positional argument.
	**/
	public static Object remainingParameters = new Object();

	/**
	Marker object that specifies that this parameter collects any additional
	keyword argument.
	**/
	public static Object remainingKeywordParameters = new Object();

	public Signature()
	{
		parameters = new LinkedHashMap<String, ParameterDescription>();
		countPositionalOnly = 0;
		countBoth = 0;
		countKeywordOnly = 0;
		countDefaults = 0;
		hasVarPositional = false;
		hasVarKeyword = false;
		parameterNames = null;
	}

	/*
		String parameterName = null;
		for (int i = 0; i < args.length; ++i)
		{
			if (i%2 == 0)
				parameterName = (String)args[i];
			else
			{
				if (args[i] == required)
					add(parameterName, ParameterDescription.Type.REQUIRED, null);
				else if (args[i] == remainingParameters)
					add(parameterName, ParameterDescription.Type.VAR_POSITIONAL, null);
				else if (args[i] == remainingKeywordParameters)
					add(parameterName, ParameterDescription.Type.VAR_KEYWORD, null);
				else
					add(parameterName, ParameterDescription.Type.DEFAULT, args[i]);
			}
		}
	*/

	void add(String name, ParameterDescription.Type type, Object defaultValue)
	{
		ParameterDescription param = new ParameterDescription(name, size(), type, defaultValue);
		parameters.put(name, param);
		switch (param.getType())
		{
			case POSITIONAL_OR_KEYWORD_REQUIRED:
				++countPositionalOnly;
				break;
			case POSITIONAL_OR_KEYWORD_DEFAULT:
				++countBoth;
				++countDefaults;
				break;
			case POSITIONAL_ONLY_REQUIRED:
				++countBoth;
				break;
			case POSITIONAL_ONLY_DEFAULT:
				++countBoth;
				++countDefaults;
				break;
			case KEYWORD_ONLY_REQUIRED:
				++countKeywordOnly;
				break;
			case KEYWORD_ONLY_DEFAULT:
				++countKeywordOnly;
				++countDefaults;
				break;
			case VAR_POSITIONAL:
				hasVarPositional = true;
				break;
			case VAR_KEYWORD:
				hasVarKeyword = true;
				break;
		}
	}

	private void checkPositionalOnly(String name)
	{
		if (countBoth > 0)
			throw new SignatureException(Utils.formatMessage("positional only parameter {!r} must be before positional/keyword parameters", name));
		if (countKeywordOnly > 0)
			throw new SignatureException(Utils.formatMessage("positional only parameter {!r} must be before keyword only parameters", name));
		if (hasVarPositional)
			throw new SignatureException(Utils.formatMessage("positional only parameter {!r} must be before * parameter", name));
		if (hasVarKeyword)
			throw new SignatureException(Utils.formatMessage("positional only parameter {!r} must be before ** parameter", name));
	}

	private void checkBoth(String name)
	{
		if (countKeywordOnly > 0)
			throw new SignatureException(Utils.formatMessage("positional/keyword parameter {!r} must be before keyword only parameters", name));
		if (hasVarPositional)
			throw new SignatureException(Utils.formatMessage("positional/keyword parameter {!r} must be before * parameter", name));
		if (hasVarKeyword)
			throw new SignatureException(Utils.formatMessage("positional/keyword parameter {!r} must be before ** parameter", name));
	}

	private void checkKeywordOnly(String name)
	{
		if (hasVarPositional)
			throw new SignatureException(Utils.formatMessage("keyword only parameter {!r} must be before * parameter", name));
		if (hasVarKeyword)
			throw new SignatureException(Utils.formatMessage("keyword only parameter {!r} must be before ** parameter", name));
	}

	private void checkVarPositional(String name)
	{
		if (hasVarPositional)
			throw new SignatureException(Utils.formatMessage("* parameter {!r} can only be specified once", name));
		if (hasVarKeyword)
			throw new SignatureException(Utils.formatMessage("* parameter {!r} must be before ** parameter", name));
	}

	private void checkVarKeyword(String name)
	{
		if (hasVarKeyword)
			throw new SignatureException(Utils.formatMessage("** parameter {!r} can only be specified once", name));
	}

	private void checkDefaults(String name)
	{
		if (countDefaults > 0)
			throw new SignatureException(Utils.formatMessage("parameter {!r} without default can't be after paramters with defaults", name));
	}

	public Signature addPositionalOnly(String name)
	{
		checkPositionalOnly(name);
		checkDefaults(name);
		parameters.put(name, new ParameterDescription(name, size(), ParameterDescription.Type.POSITIONAL_ONLY_REQUIRED, null));
		++countPositionalOnly;
		return this;
	}

	public Signature addPositionalOnly(String name, Object defaultValue)
	{
		checkPositionalOnly(name);
		parameters.put(name, new ParameterDescription(name, size(), ParameterDescription.Type.POSITIONAL_ONLY_DEFAULT, defaultValue));
		++countPositionalOnly;
		++countDefaults;
		return this;
	}

	public Signature addBoth(String name)
	{
		checkBoth(name);
		checkDefaults(name);
		parameters.put(name, new ParameterDescription(name, size(), ParameterDescription.Type.POSITIONAL_OR_KEYWORD_REQUIRED, null));
		++countBoth;
		return this;
	}

	public Signature addBoth(String name, Object defaultValue)
	{
		checkBoth(name);
		parameters.put(name, new ParameterDescription(name, size(), ParameterDescription.Type.POSITIONAL_OR_KEYWORD_DEFAULT, defaultValue));
		++countBoth;
		++countDefaults;
		return this;
	}

	public Signature addKeywordOnly(String name)
	{
		checkKeywordOnly(name);
		checkDefaults(name);
		parameters.put(name, new ParameterDescription(name, size(), ParameterDescription.Type.KEYWORD_ONLY_REQUIRED, null));
		++countKeywordOnly;
		return this;
	}

	public Signature addKeywordOnly(String name, Object defaultValue)
	{
		checkKeywordOnly(name);
		parameters.put(name, new ParameterDescription(name, size(), ParameterDescription.Type.KEYWORD_ONLY_DEFAULT, defaultValue));
		++countKeywordOnly;
		++countDefaults;
		return this;
	}

	public Signature addVarPositional(String name)
	{
		checkVarPositional(name);
		parameters.put(name, new ParameterDescription(name, size(), ParameterDescription.Type.VAR_POSITIONAL, null));
		hasVarPositional = true;
		return this;
	}

	public Signature addVarKeyword(String name)
	{
		checkVarKeyword(name);
		parameters.put(name, new ParameterDescription(name, size(), ParameterDescription.Type.VAR_KEYWORD, null));
		hasVarKeyword = true;
		return this;
	}

	public boolean hasVarPositional()
	{
		return hasVarPositional;
	}

	public boolean hasVarKeyword()
	{
		return hasVarKeyword;
	}

	public Collection<ParameterDescription> getParameters()
	{
		return parameters.values();
	}

	public Iterator<ParameterDescription> iterator()
	{
		return parameters.values().iterator();
	}

	public int size()
	{
		return parameters.size();
	}

	public boolean containsParameterNamed(String argName)
	{
		ParameterDescription description = parameters.get(argName);
		if (description == null)
			return false;
		return !description.getType().isVar();
	}

	public List<Object> asUL4ONDump()
	{
		List<Object> dump = new ArrayList<Object>();
		for (ParameterDescription param : parameters.values())
		{
			dump.add(param.getName());
			ParameterDescription.Type type = param.getType();
			dump.add(type.getUL4ONString());
			if (type.hasDefault())
				dump.add(param.getDefaultValue());
		}
		return dump;
	}

	public static Signature fromUL4ONDump(List<Object> dump)
	{
		Signature result = new Signature();

		int state = 0;
		String name = null;
		ParameterDescription.Type type = null;
		for (Object item : dump)
		{
			if (state == 0)
				name = (String)item;
			else if (state == 1)
			{
				type = ParameterDescription.Type.fromUL4ONString((String)item);
				if (type.hasDefault())
					state = 2;
				else
				{
					result.add(name, type, null);
					state = 0;
				}
			}
			else
			{
				result.add(name, type, item);
				state = 0;
			}
		}
		return result;
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" ");
		formatter.append(toString());
		formatter.append(">");
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		ParameterDescription.Type lastType = null;
		ParameterDescription.Type type = null;

		buffer.append("(");

		for (ParameterDescription paramDesc : parameters.values())
		{
			type = paramDesc.getType();
			String sep = ParameterDescription.Type.separator(lastType, type);
			if (sep != null)
				buffer.append(sep);
			buffer.append(paramDesc);
			lastType = type;
		}
		buffer.append(")");

		return buffer.toString();
	}
}
