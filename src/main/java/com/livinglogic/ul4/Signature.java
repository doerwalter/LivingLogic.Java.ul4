/*
** Copyright 2009-2021 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
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

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	/**
	All parameters in the order they were specified in the constructor by name.
	**/
	protected LinkedHashMap<String, ParameterDescription> parametersByName;

	/**
	All parameters in the order they were specified in the constructor by position.
	**/
	protected ArrayList<ParameterDescription> parametersByPosition;

	/**
	The {@code *} parameter (which collects any additional positional arguments)
	or {@code null} is there's no such parameter;
	**/
	protected ParameterDescription varPositional;

	/**
	The {@code **} parameter (which collects any additional keyword arguments)
	or {@code null} is there's no such parameter;
	**/
	protected ParameterDescription varKeyword;

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
	Can be used as the default value for a parameter where the code must be able
	to determine whether a value has been specified or not.
	(Which only works from UL4, in Java you <b>can</b> of course pass
	{@code noValue}) explicitely).
	**/
	public static final Object noValue = new Object();

	/**
	Since fully configured signatures can be treated as immutable, we only
	need one parameter less signature, that can be shared by all users.
	**/
	public static final Signature noParameters = new Signature();

	public Signature()
	{
		parametersByName = new LinkedHashMap<String, ParameterDescription>();
		parametersByPosition = new ArrayList<ParameterDescription>();
		varPositional = null;
		varKeyword = null;
		countPositionalOnly = 0;
		countBoth = 0;
		countKeywordOnly = 0;
		countDefaults = 0;
	}

	public BoundArguments bind(UL4Name object, List<Object> args, Map<String, Object> kwargs)
	{
		return new BoundArguments(this, object, args, kwargs);
	}

	public BoundArguments bind(String name, List<Object> args, Map<String, Object> kwargs)
	{
		return new BoundArguments(this, name, args, kwargs);
	}

	ParameterDescription add(String name, ParameterDescription.Type type, Object defaultValue)
	{
		ParameterDescription param = new ParameterDescription(name, parametersByPosition.size() + (varPositional != null ? 1 : 0) + (varKeyword != null ? 1 : 0), type, defaultValue);
		if (!type.isVar())
		{
			parametersByName.put(name, param);
			parametersByPosition.add(param);
		}
		switch (type)
		{
			case POSITIONAL_OR_KEYWORD_REQUIRED:
				checkBoth(name);
				checkDefaults(name);
				++countBoth;
				break;
			case POSITIONAL_OR_KEYWORD_DEFAULT:
				checkBoth(name);
				++countBoth;
				++countDefaults;
				break;
			case POSITIONAL_ONLY_REQUIRED:
				checkPositionalOnly(name);
				checkDefaults(name);
				++countPositionalOnly;
				break;
			case POSITIONAL_ONLY_DEFAULT:
				checkPositionalOnly(name);
				++countPositionalOnly;
				++countDefaults;
				break;
			case KEYWORD_ONLY_REQUIRED:
				checkKeywordOnly(name);
				checkDefaults(name);
				++countKeywordOnly;
				break;
			case KEYWORD_ONLY_DEFAULT:
				checkKeywordOnly(name);
				++countKeywordOnly;
				++countDefaults;
				break;
			case VAR_POSITIONAL:
				checkVarPositional(name);
				varPositional = param;
				break;
			case VAR_KEYWORD:
				checkVarKeyword(name);
				varKeyword = param;
				break;
		}
		return param;
	}

	private void checkPositionalOnly(String name)
	{
		if (countBoth > 0)
			throw new SignatureException(Utils.formatMessage("positional only parameter {!r} must be before positional/keyword parameters", name));
		if (countKeywordOnly > 0)
			throw new SignatureException(Utils.formatMessage("positional only parameter {!r} must be before keyword only parameters", name));
		if (varPositional != null)
			throw new SignatureException(Utils.formatMessage("positional only parameter {!r} must be before * parameter", name));
		if (varKeyword != null)
			throw new SignatureException(Utils.formatMessage("positional only parameter {!r} must be before ** parameter", name));
	}

	private void checkBoth(String name)
	{
		if (countKeywordOnly > 0)
			throw new SignatureException(Utils.formatMessage("positional/keyword parameter {!r} must be before keyword only parameters", name));
		if (varPositional != null)
			throw new SignatureException(Utils.formatMessage("positional/keyword parameter {!r} must be before * parameter", name));
		if (varKeyword != null)
			throw new SignatureException(Utils.formatMessage("positional/keyword parameter {!r} must be before ** parameter", name));
	}

	private void checkKeywordOnly(String name)
	{
		if (varPositional != null)
			throw new SignatureException(Utils.formatMessage("keyword only parameter {!r} must be before * parameter", name));
		if (varKeyword != null)
			throw new SignatureException(Utils.formatMessage("keyword only parameter {!r} must be before ** parameter", name));
	}

	private void checkVarPositional(String name)
	{
		if (varPositional != null)
			throw new SignatureException(Utils.formatMessage("* parameter {!r} can only be specified once", name));
		if (varKeyword != null)
			throw new SignatureException(Utils.formatMessage("* parameter {!r} must be before ** parameter", name));
	}

	private void checkVarKeyword(String name)
	{
		if (varKeyword != null)
			throw new SignatureException(Utils.formatMessage("** parameter {!r} can only be specified once", name));
	}

	private void checkDefaults(String name)
	{
		if (countDefaults > 0)
			throw new SignatureException(Utils.formatMessage("parameter {!r} without default can't be after paramters with defaults", name));
	}

	public Signature addPositionalOnly(String name)
	{
		add(name, ParameterDescription.Type.POSITIONAL_ONLY_REQUIRED, null);
		return this;
	}

	public Signature addPositionalOnly(String name, Object defaultValue)
	{
		add(name, ParameterDescription.Type.POSITIONAL_ONLY_DEFAULT, defaultValue);
		return this;
	}

	public Signature addBoth(String name)
	{
		add(name, ParameterDescription.Type.POSITIONAL_OR_KEYWORD_REQUIRED, null);
		return this;
	}

	public Signature addBoth(String name, Object defaultValue)
	{
		add(name, ParameterDescription.Type.POSITIONAL_OR_KEYWORD_DEFAULT, defaultValue);
		return this;
	}

	public Signature addKeywordOnly(String name)
	{
		add(name, ParameterDescription.Type.KEYWORD_ONLY_REQUIRED, null);
		return this;
	}

	public Signature addKeywordOnly(String name, Object defaultValue)
	{
		add(name, ParameterDescription.Type.KEYWORD_ONLY_DEFAULT, defaultValue);
		return this;
	}

	public Signature addVarPositional(String name)
	{
		add(name, ParameterDescription.Type.VAR_POSITIONAL, null);
		return this;
	}

	public Signature addVarKeyword(String name)
	{
		add(name, ParameterDescription.Type.VAR_KEYWORD, null);
		return this;
	}

	public boolean hasVarPositional()
	{
		return varPositional != null;
	}

	public boolean hasVarKeyword()
	{
		return varKeyword != null;
	}

	public ParameterDescription getVarPositional()
	{
		return varPositional;
	}

	public ParameterDescription getVarKeyword()
	{
		return varKeyword;
	}

	public Map<String, ParameterDescription> getParametersByName()
	{
		return parametersByName;
	}

	public List<ParameterDescription> getParametersByPosition()
	{
		return parametersByPosition;
	}

	public ParameterDescription getParameterByName(String name)
	{
		return parametersByName.get(name);
	}

	public ParameterDescription getParameterByPosition(int position)
	{
		if (position < 0 || position >= parametersByPosition.size())
			return null;
		return parametersByPosition.get(position);
	}

	public Iterator<ParameterDescription> iterator()
	{
		return parametersByName.values().iterator();
	}

	public int count()
	{
		return parametersByName.size();
	}

	public int countPositionalOnly()
	{
		return countPositionalOnly;
	}

	public int countPositionalOrKeyword()
	{
		return countBoth;
	}

	public int countKeywordOnly()
	{
		return countKeywordOnly;
	}

	public List<Object> asUL4ONDump()
	{
		List<Object> dump = new ArrayList<Object>();
		for (ParameterDescription param : parametersByName.values())
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
			{
				name = (String)item;
				state = 1;
			}
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

	private void toStringParam(StringBuilder buffer, ParameterDescription param, ParameterDescription.Type type, ParameterDescription.Type lastType)
	{
		String sep = ParameterDescription.Type.separator(lastType, type);
		if (sep != null)
			buffer.append(sep);
		buffer.append(param);
	}

	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		ParameterDescription.Type lastType = null;
		ParameterDescription.Type type = null;

		buffer.append("(");

		for (ParameterDescription param : parametersByPosition)
		{
			type = param.getType();
			toStringParam(buffer, param, type, lastType);
			lastType = type;
		}
		if (varPositional != null)
		{
			type = varPositional.getType();
			toStringParam(buffer, varPositional, type, lastType);
			lastType = type;
		}
		if (varKeyword != null)
		{
			type = varKeyword.getType();
			toStringParam(buffer, varKeyword, type, lastType);
			lastType = type;
		}
		buffer.append(")");

		return buffer.toString();
	}
}
