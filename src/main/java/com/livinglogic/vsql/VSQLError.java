/*
** Copyright 2019-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import static com.livinglogic.utils.StringUtils.formatMessage;

import com.livinglogic.ul4.EnumValueException;
import static com.livinglogic.utils.StringUtils.lowerCaseFirstLetter;

public enum VSQLError
{
	/* Subnodes have failures */
	SUBNODEERROR
	{
		@Override
		public String toString()
		{
			return "subnodeerror";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Subexpressions of {} {!`} have errors", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* Unknown node type (not any of the defined node types */
	NODETYPE
	{
		@Override
		public String toString()
		{
			return "nodetype";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Unknown expression type {!t}", ast);
		}
	},
	/* Node does not have the proper number of children */
	ARITY
	{
		@Override
		public String toString()
		{
			return "arity";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} uses unsupported number of operands: {}", ast.getDescription(), ast.getSource(), ast.getArity());
		}
	},
	/* Subnodes have a combination of types that are not supported by the node */
	SUBNODETYPES
	{
		@Override
		public String toString()
		{
			return "subnodetypes";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} uses unsupported types of operands: {}", ast.getDescription(), ast.getSource(), ast.getTypeSignature());
		}

		@Override
		public void throwError(VSQLAST ast)
		{
			throw new VSQLUnsupportedOperationException(getErrorMessage(ast));
		}

	},
	/* The node references an unknown field */
	FIELD
	{
		@Override
		public String toString()
		{
			return "field";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Field unknown in {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}

		@Override
		public void throwError(VSQLAST ast)
		{
			throw new VSQLFieldUnknownException(getErrorMessage(ast));
		}
	},
	/* The node's value is {@code null} or malformed */
	CONST_BOOL
	{
		@Override
		public String toString()
		{
			return "const_bool";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Malformed {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* The node's value is {@code null} or malformed */
	CONST_INT
	{
		@Override
		public String toString()
		{
			return "const_int";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Malformed {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* The node's value is {@code null} or malformed */
	CONST_NUMBER
	{
		@Override
		public String toString()
		{
			return "const_number";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Malformed {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* The node's value is {@code null} or malformed */
	CONST_DATE
	{
		@Override
		public String toString()
		{
			return "const_date";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Malformed {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* The node's value is {@code null} or malformed */
	CONST_DATETIME
	{
		@Override
		public String toString()
		{
			return "const_datetime";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Malformed {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* The node's value is {@code null} or malformed */
	CONST_TIMESTAMP
	{
		@Override
		public String toString()
		{
			return "const_timestamp";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Malformed {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* The node's value is {@code null} or malformed */
	CONST_COLOR
	{
		@Override
		public String toString()
		{
			return "const_color";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Malformed {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* Attribute/Function/method is unknown */
	NAME
	{
		@Override
		public String toString()
		{
			return "name";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Unknown attribute/function/method name in {} {!`}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource());
		}
	},
	/* List is empty or only has literal {@code None}`s as items, so the type can't be determined */
	LISTTYPEUNKNOWN
	{
		@Override
		public String toString()
		{
			return "listtypeunknown";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("List type can't be determined in {} {!`}: {}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource(), ast.getTypeSignature());
		}
	},
	/* List items have incompatible types, so the type can't be determined */
	LISTMIXEDTYPES
	{
		@Override
		public String toString()
		{
			return "listmixedtypes";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("List contains incompatible types in {} {!`}: {}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource(), ast.getTypeSignature());
		}
	},
	/* List items have unsupported types, so the type can't be determined */
	LISTUNSUPPORTEDTYPES
	{
		@Override
		public String toString()
		{
			return "listunsupportedtypes";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("List contains unsupported types in {} {!`}: {}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource(), ast.getTypeSignature());
		}
	},
	/* Set is empty or only has literal {@code None}s as items, so the type can't be determined */
	SETTYPEUNKNOWN
	{
		@Override
		public String toString()
		{
			return "settypeunknown";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Set type can't be determined in {} {!`}: {}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource(), ast.getTypeSignature());
		}
	},
	/* Set items have incompatible types, so the type can't be determined */
	SETMIXEDTYPES
	{
		@Override
		public String toString()
		{
			return "setmixedtypes";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Set contains incomaptible types in {} {!`}: {}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource(), ast.getTypeSignature());
		}
	},
	/* Set items have unsupported types, so the type can't be determined */
	SETUNSUPPORTEDTYPES
	{
		@Override
		public String toString()
		{
			return "setunsupportedtypes";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("Set contains unsupported types in {} {!`}: {}", lowerCaseFirstLetter(ast.getDescription()), ast.getSource(), ast.getTypeSignature());
		}
	},
	/* The datatype of the node should be {@code null} but isn't */
	DATATYPE_NULL
	{
		@Override
		public String toString()
		{
			return "datatype_null";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `NULL` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code bool} but isn't */
	DATATYPE_BOOL
	{
		@Override
		public String toString()
		{
			return "datatype_bool";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `BOOL` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code int} but isn't */
	DATATYPE_INT
	{
		@Override
		public String toString()
		{
			return "datatype_int";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `INT` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code number} but isn't */
	DATATYPE_NUMBER
	{
		@Override
		public String toString()
		{
			return "datatype_number";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `NUMBER` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code str} but isn't */
	DATATYPE_STR
	{
		@Override
		public String toString()
		{
			return "datatype_str";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `STR` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code clob} but isn't */
	DATATYPE_CLOB
	{
		@Override
		public String toString()
		{
			return "datatype_clob";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `CLOB` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code color} but isn't */
	DATATYPE_COLOR
	{
		@Override
		public String toString()
		{
			return "datatype_color";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `COLOR` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code geo} but isn't */
	DATATYPE_GEO
	{
		@Override
		public String toString()
		{
			return "datatype_geo";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `GEO` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code date} but isn't */
	DATATYPE_DATE
	{
		@Override
		public String toString()
		{
			return "datatype_date";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `DATE` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code datetime} but isn't */
	DATATYPE_DATETIME
	{
		@Override
		public String toString()
		{
			return "datatype_datetime";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `DATETIME` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code datedelta} but isn't */
	DATATYPE_DATEDELTA
	{
		@Override
		public String toString()
		{
			return "datatype_datedelta";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `DATEDELTA` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code datetimedelta} but isn't */
	DATATYPE_DATETIMEDELTA
	{
		@Override
		public String toString()
		{
			return "datatype_datetimedelta";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `DATETIMEDELTA` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code monthdelta} but isn't */
	DATATYPE_MONTHDELTA
	{
		@Override
		public String toString()
		{
			return "datatype_monthdelta";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `MONTHDELTA` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code intlist} but isn't */
	DATATYPE_INTLIST
	{
		@Override
		public String toString()
		{
			return "datatype_intlist";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `INTLIST` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code numberlist} but isn't */
	DATATYPE_NUMBERLIST
	{
		@Override
		public String toString()
		{
			return "datatype_numberlist";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `NUMBERLIST` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code strlist} but isn't */
	DATATYPE_STRLIST
	{
		@Override
		public String toString()
		{
			return "datatype_strlist";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `STRLIST` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code cloblist} but isn't */
	DATATYPE_CLOBLIST
	{
		@Override
		public String toString()
		{
			return "datatype_cloblist";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `CLOBLIST` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code datelist} but isn't */
	DATATYPE_DATELIST
	{
		@Override
		public String toString()
		{
			return "datatype_datelist";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `DATELIST` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code datetimelist} but isn't */
	DATATYPE_DATETIMELIST
	{
		@Override
		public String toString()
		{
			return "datatype_datetimelist";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `DATETIMELIST` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code intset} but isn't */
	DATATYPE_INTSET
	{
		@Override
		public String toString()
		{
			return "datatype_intset";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `INTSET` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code numberset} but isn't */
	DATATYPE_NUMBERSET
	{
		@Override
		public String toString()
		{
			return "datatype_numberset";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `NUMBERSET` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code strset} but isn't */
	DATATYPE_STRSET
	{
		@Override
		public String toString()
		{
			return "datatype_strset";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `STRSET` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code dateset} but isn't */
	DATATYPE_DATESET
	{
		@Override
		public String toString()
		{
			return "datatype_dateset";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `DATESET` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	},
	/* The datatype of the node should be {@code datetimeset} but isn't */
	DATATYPE_DATETIMESET
	{
		@Override
		public String toString()
		{
			return "datatype_datetimeset";
		}

		@Override
		public String getErrorMessage(VSQLAST ast)
		{
			return formatMessage("{} {!`} should be of type `DATETIMESET` but is {!`}", ast.getDescription(), ast.getSource(), ast.getDataTypeString());
		}
	};

	public String toString()
	{
		return null;
	}

	public String getErrorMessage(VSQLAST ast)
	{
		return formatMessage("{} {!`} is invalid", ast.getDescription(), ast.getSource());
	}

	public void throwError(VSQLAST ast)
	{
		throw new RuntimeException(getErrorMessage(ast));
	}

	public static VSQLError fromString(String value)
	{
		if (value == null)
			return null;
		switch (value)
		{
			case "subnodeerror":
				return SUBNODEERROR;
			case "nodetype":
				return NODETYPE;
			case "arity":
				return ARITY;
			case "subnodetypes":
				return SUBNODETYPES;
			case "field":
				return FIELD;
			case "const_bool":
				return CONST_BOOL;
			case "const_int":
				return CONST_INT;
			case "const_number":
				return CONST_NUMBER;
			case "const_date":
				return CONST_DATE;
			case "const_datetime":
				return CONST_DATETIME;
			case "const_timestamp":
				return CONST_TIMESTAMP;
			case "const_color":
				return CONST_COLOR;
			case "name":
				return NAME;
			case "listtypeunknown":
				return LISTTYPEUNKNOWN;
			case "listmixedtypes":
				return LISTMIXEDTYPES;
			case "listunsupportedtypes":
				return LISTUNSUPPORTEDTYPES;
			case "settypeunknown":
				return SETTYPEUNKNOWN;
			case "setmixedtypes":
				return SETMIXEDTYPES;
			case "setunsupportedtypes":
				return SETUNSUPPORTEDTYPES;
			case "datatype_null":
				return DATATYPE_NULL;
			case "datatype_bool":
				return DATATYPE_BOOL;
			case "datatype_int":
				return DATATYPE_INT;
			case "datatype_number":
				return DATATYPE_NUMBER;
			case "datatype_str":
				return DATATYPE_STR;
			case "datatype_clob":
				return DATATYPE_CLOB;
			case "datatype_color":
				return DATATYPE_COLOR;
			case "datatype_date":
				return DATATYPE_DATE;
			case "datatype_datetime":
				return DATATYPE_DATETIME;
			case "datatype_datedelta":
				return DATATYPE_DATEDELTA;
			case "datatype_datetimedelta":
				return DATATYPE_DATETIMEDELTA;
			case "datatype_monthdelta":
				return DATATYPE_MONTHDELTA;
			case "datatype_intlist":
				return DATATYPE_INTLIST;
			case "datatype_numberlist":
				return DATATYPE_NUMBERLIST;
			case "datatype_strlist":
				return DATATYPE_STRLIST;
			case "datatype_cloblist":
				return DATATYPE_CLOBLIST;
			case "datatype_datelist":
				return DATATYPE_DATELIST;
			case "datatype_datetimelist":
				return DATATYPE_DATETIMELIST;
			case "datatype_intset":
				return DATATYPE_INTSET;
			case "datatype_numberset":
				return DATATYPE_NUMBERSET;
			case "datatype_strset":
				return DATATYPE_STRSET;
			case "datatype_dateset":
				return DATATYPE_DATESET;
			case "datatype_datetimeset":
				return DATATYPE_DATETIMESET;
		}
		throw new EnumValueException("com.livinglogic.livingapps.vsql.VSQLError", value);
	}
};
