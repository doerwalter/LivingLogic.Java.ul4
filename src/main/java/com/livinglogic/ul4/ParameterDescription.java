/*
** Copyright 2009-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;


/**
An {@code ParameterDescription} object is used by {@link Signature} objects
to store information about one particular parameter.
**/
public class ParameterDescription implements UL4Repr
{
	/**
	The name of the parameter.
	**/
	protected String name;

	/**
	The position of the parameter in the signature.
	**/
	protected int position;

	public enum Type
	{
		/**
		The parameter can only be specified via position and must have an argument in the call.
		**/
		POSITIONAL_ONLY_REQUIRED
		{
			@Override
			public String getUL4ONString()
			{
				return "p";
			}

			@Override
			public boolean isPositional()
			{
				return true;
			}
		},

		/**
		The parameter can only be specified via position and is optional. The parameter has a default value.
		**/
		POSITIONAL_ONLY_DEFAULT
		{
			@Override
			public String getUL4ONString()
			{
				return "p=";
			}

			@Override
			public boolean isPositional()
			{
				return true;
			}

			@Override
			public boolean hasDefault()
			{
				return true;
			}
		},

		/**
		The parameter can be specified via position or keyword and must have an argument in the call.
		**/
		POSITIONAL_OR_KEYWORD_REQUIRED
		{
			@Override
			public String getUL4ONString()
			{
				return "pk";
			}

			@Override
			public boolean isPositional()
			{
				return true;
			}

			@Override
			public boolean isKeyword()
			{
				return true;
			}
		},

		/**
		The parameter can be specified via position or keyword and is optional. The parameter has a default value.
		**/
		POSITIONAL_OR_KEYWORD_DEFAULT
		{
			@Override
			public String getUL4ONString()
			{
				return "pk=";
			}

			@Override
			public boolean isPositional()
			{
				return true;
			}

			@Override
			public boolean isKeyword()
			{
				return true;
			}

			@Override
			public boolean hasDefault()
			{
				return true;
			}
		},

		/**
		The parameter can only be specified via keyword and must have an argument in the call.
		**/
		KEYWORD_ONLY_REQUIRED
		{
			@Override
			public String getUL4ONString()
			{
				return "k";
			}

			@Override
			public boolean isKeyword()
			{
				return true;
			}
		},

		/**
		The parameter can only be specified via keyword and is optional. The parameter has a default value.
		**/
		KEYWORD_ONLY_DEFAULT
		{
			@Override
			public String getUL4ONString()
			{
				return "k=";
			}

			@Override
			public boolean isKeyword()
			{
				return true;
			}

			@Override
			public boolean hasDefault()
			{
				return true;
			}
		},

		/**
		The parameter collects all additional positional arguments in a list.
		**/
		VAR_POSITIONAL
		{
			@Override
			public String getUL4ONString()
			{
				return "*";
			}

			@Override
			public boolean isVar()
			{
				return true;
			}
		},

		/**
		The parameter collects all additional keyword arguments in a map.
		**/
		VAR_KEYWORD
		{
			@Override
			public String getUL4ONString()
			{
				return "**";
			}

			@Override
			public boolean isVar()
			{
				return true;
			}
		};

		public String getUL4ONString()
		{
			return null;
		}

		public boolean hasDefault()
		{
			return false;
		}

		public boolean isPositional()
		{
			return false;
		}

		public boolean isKeyword()
		{
			return false;
		}

		public boolean isVar()
		{
			return false;
		}

		public static Type fromUL4ONString(String value)
		{
			switch (value)
			{
				case "pk":
					return POSITIONAL_OR_KEYWORD_REQUIRED;
				case "pk=":
					return POSITIONAL_OR_KEYWORD_DEFAULT;
				case "p":
					return POSITIONAL_ONLY_REQUIRED;
				case "p=":
					return POSITIONAL_ONLY_DEFAULT;
				case "k":
					return KEYWORD_ONLY_REQUIRED;
				case "k=":
					return KEYWORD_ONLY_DEFAULT;
				case "*":
					return VAR_POSITIONAL;
				case "**":
					return VAR_KEYWORD;
				default:
					throw new EnumValueException("com.livinglogic.ul4.ParameterDescription.Type", value);
			}
		}

		public static String separator(Type oldType, Type newType)
		{
			if (oldType == null)
			{
				switch (newType)
				{
					case KEYWORD_ONLY_REQUIRED:
					case KEYWORD_ONLY_DEFAULT:
						return "*, ";
					default:
						return null;
				}
			}
			else
			{
				switch (oldType)
				{
					case POSITIONAL_OR_KEYWORD_REQUIRED:
					case POSITIONAL_OR_KEYWORD_DEFAULT:
						switch (newType)
						{
							case KEYWORD_ONLY_REQUIRED:
							case KEYWORD_ONLY_DEFAULT:
								return ", *, ";
							default:
								return ", ";
						}
					case POSITIONAL_ONLY_REQUIRED:
					case POSITIONAL_ONLY_DEFAULT:
						switch (newType)
						{
							case POSITIONAL_OR_KEYWORD_REQUIRED:
							case POSITIONAL_OR_KEYWORD_DEFAULT:
								return ", /, ";
							case KEYWORD_ONLY_REQUIRED:
							case KEYWORD_ONLY_DEFAULT:
								return ", /, *, ";
							default:
								return ", ";
						}
					case KEYWORD_ONLY_REQUIRED:
					case KEYWORD_ONLY_DEFAULT:
					case VAR_POSITIONAL:
					case VAR_KEYWORD:
						return ", ";
					default:
						return null; // Can't happen
				}
			}
		}
	}

	/**
	The type of the parameter
	**/
	protected Type type;

	/**
	The default value of the parameter (if the parameter has a default).
	**/
	protected Object defaultValue;

	public ParameterDescription(String name, int position, Type type, Object defaultValue)
	{
		this.name = name;
		this.position = position;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public String getName()
	{
		return name;
	}

	public int getPosition()
	{
		return position;
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public Type getType()
	{
		return type;
	}

	public boolean isPositional()
	{
		return type.isPositional();
	}

	public boolean isKeyword()
	{
		return type.isKeyword();
	}

	public boolean hasDefault()
	{
		return type.hasDefault();
	}

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" ");
		formatter.append(toString());
		formatter.append(" ");
		if (isPositional())
		{
			if (isKeyword())
				formatter.append("positional/keyword");
			else
				formatter.append("positional-only");
		}
		else
		{
			if (isKeyword())
				formatter.append("keyword-only");
			else
				formatter.append("?");
		}
		formatter.append(">");
	}

	public String toString()
	{
		switch (type)
		{
			case POSITIONAL_OR_KEYWORD_REQUIRED:
			case POSITIONAL_ONLY_REQUIRED:
			case KEYWORD_ONLY_REQUIRED:
				return name;
			case POSITIONAL_OR_KEYWORD_DEFAULT:
			case POSITIONAL_ONLY_DEFAULT:
			case KEYWORD_ONLY_DEFAULT:
				return name + "=" + FunctionRepr.call(defaultValue);
			case VAR_POSITIONAL:
				return "*" + name;
			case VAR_KEYWORD:
				return "**" + name;
			default:
				return null;
		}
	}
}
