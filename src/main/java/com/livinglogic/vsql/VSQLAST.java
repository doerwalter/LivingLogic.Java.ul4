/*
** Copyright 2019-2025 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.vsql;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

import com.livinglogic.ul4.Template;
import com.livinglogic.ul4.UL4Instance;
import com.livinglogic.ul4.UL4Type;
import com.livinglogic.ul4.AbstractInstanceType;
import com.livinglogic.ul4on.UL4ONSerializable;
import com.livinglogic.ul4.UL4Repr;
import com.livinglogic.ul4.AttributeException;
import com.livinglogic.ul4.UndefinedAttribute;
import com.livinglogic.ul4.EvaluationContext;
import com.livinglogic.ul4on.ObjectFactory;
import com.livinglogic.ul4on.Utils;
import com.livinglogic.ul4.AST;
import com.livinglogic.ul4.ConstAST;
import com.livinglogic.ul4.NotAST;
import com.livinglogic.ul4.NegAST;
import com.livinglogic.ul4.BitNotAST;
import com.livinglogic.ul4.LTAST;
import com.livinglogic.ul4.LEAST;
import com.livinglogic.ul4.GTAST;
import com.livinglogic.ul4.GEAST;
import com.livinglogic.ul4.EQAST;
import com.livinglogic.ul4.NEAST;
import com.livinglogic.ul4.ContainsAST;
import com.livinglogic.ul4.IsAST;
import com.livinglogic.ul4.IsNotAST;
import com.livinglogic.ul4.AndAST;
import com.livinglogic.ul4.OrAST;
import com.livinglogic.ul4.NotContainsAST;
import com.livinglogic.ul4.AddAST;
import com.livinglogic.ul4.SubAST;
import com.livinglogic.ul4.MulAST;
import com.livinglogic.ul4.FloorDivAST;
import com.livinglogic.ul4.TrueDivAST;
import com.livinglogic.ul4.ModAST;
import com.livinglogic.ul4.ShiftLeftAST;
import com.livinglogic.ul4.ShiftRightAST;
import com.livinglogic.ul4.BitAndAST;
import com.livinglogic.ul4.BitOrAST;
import com.livinglogic.ul4.BitXOrAST;
import com.livinglogic.ul4.ItemAST;
import com.livinglogic.ul4.IfAST;
import com.livinglogic.ul4.SliceAST;
import com.livinglogic.ul4.ListAST;
import com.livinglogic.ul4.SetAST;
import com.livinglogic.ul4.PositionalArgumentAST;
import com.livinglogic.ul4.VarAST;
import com.livinglogic.ul4.AttrAST;
import com.livinglogic.ul4.CallAST;
import com.livinglogic.ul4.ReturnAST;


import static com.livinglogic.utils.MapUtils.makeMap;
import static com.livinglogic.utils.SetUtils.makeSet;

import static com.livinglogic.utils.StringUtils.formatMessage;

import static com.livinglogic.utils.VSQLUtils.getSourcePrefix;
import static com.livinglogic.utils.VSQLUtils.getSourceSuffix;
import static com.livinglogic.utils.VSQLUtils.getSourceInfix;


/**
Base class of all vSQL AST classes.

@author W. Doerwald
**/
public abstract class VSQLAST implements UL4Instance, UL4ONSerializable, UL4Repr
{
	/**
	UL4 type for the {@link VSQLAST} class.
	**/
	public static class Type extends AbstractInstanceType
	{
		@Override
		public String getModuleName()
		{
			return "vsql";
		}

		@Override
		public String getNameUL4()
		{
			return "VSQLAST";
		}

		@Override
		public String getUL4ONName()
		{
			return "de.livinglogic.vsql.vsqlast";
		}

		@Override
		public String getDoc()
		{
			return "Base class for all vSQL syntax tree nodes.";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof VSQLAST;
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

	public enum NodeType
	{
		FIELD
		{
			@Override
			public String toString()
			{
				return "field";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		CONST_NONE
		{
			@Override
			public String toString()
			{
				return "const_none";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		CONST_BOOL
		{
			@Override
			public String toString()
			{
				return "const_bool";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		CONST_INT
		{
			@Override
			public String toString()
			{
				return "const_int";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		CONST_NUMBER
		{
			@Override
			public String toString()
			{
				return "const_number";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		CONST_STR
		{
			@Override
			public String toString()
			{
				return "const_str";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		CONST_DATE
		{
			@Override
			public String toString()
			{
				return "const_date";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		CONST_DATETIME
		{
			@Override
			public String toString()
			{
				return "const_datetime";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		CONST_COLOR
		{
			@Override
			public String toString()
			{
				return "const_color";
			}

			@Override
			public int arity()
			{
				return 0;
			}
		},
		LIST
		{
			@Override
			public String toString()
			{
				return "list";
			}

			@Override
			public int arity()
			{
				return -1;
			}
		},
		SET
		{
			@Override
			public String toString()
			{
				return "set";
			}

			@Override
			public int arity()
			{
				return -1;
			}
		},
		CMP_EQ
		{
			@Override
			public String toString()
			{
				return "cmp_eq";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		CMP_NE
		{
			@Override
			public String toString()
			{
				return "cmp_ne";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		CMP_LT
		{
			@Override
			public String toString()
			{
				return "cmp_lt";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		CMP_LE
		{
			@Override
			public String toString()
			{
				return "cmp_le";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		CMP_GT
		{
			@Override
			public String toString()
			{
				return "cmp_gt";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		CMP_GE
		{
			@Override
			public String toString()
			{
				return "cmp_ge";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_ADD
		{
			@Override
			public String toString()
			{
				return "binop_add";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_MUL
		{
			@Override
			public String toString()
			{
				return "binop_mul";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_SUB
		{
			@Override
			public String toString()
			{
				return "binop_sub";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_FLOORDIV
		{
			@Override
			public String toString()
			{
				return "binop_floordiv";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_TRUEDIV
		{
			@Override
			public String toString()
			{
				return "binop_truediv";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_MOD
		{
			@Override
			public String toString()
			{
				return "binop_mod";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_AND
		{
			@Override
			public String toString()
			{
				return "binop_and";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_OR
		{
			@Override
			public String toString()
			{
				return "binop_or";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_CONTAINS
		{
			@Override
			public String toString()
			{
				return "binop_contains";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_NOTCONTAINS
		{
			@Override
			public String toString()
			{
				return "binop_notcontains";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_IS
		{
			@Override
			public String toString()
			{
				return "binop_is";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_ISNOT
		{
			@Override
			public String toString()
			{
				return "binop_isnot";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_ITEM
		{
			@Override
			public String toString()
			{
				return "binop_item";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_SHIFTLEFT
		{
			@Override
			public String toString()
			{
				return "binop_shiftleft";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_SHIFTRIGHT
		{
			@Override
			public String toString()
			{
				return "binop_shiftright";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_BITAND
		{
			@Override
			public String toString()
			{
				return "binop_bitand";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_BITOR
		{
			@Override
			public String toString()
			{
				return "binop_bitor";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		BINOP_BITXOR
		{
			@Override
			public String toString()
			{
				return "binop_bitxor";
			}

			@Override
			public int arity()
			{
				return 2;
			}
		},
		TERNOP_IF
		{
			@Override
			public String toString()
			{
				return "ternop_if";
			}

			@Override
			public int arity()
			{
				return 3;
			}
		},
		TERNOP_SLICE
		{
			@Override
			public String toString()
			{
				return "ternop_slice";
			}

			@Override
			public int arity()
			{
				return 3;
			}
		},
		UNOP_NOT
		{
			@Override
			public String toString()
			{
				return "unop_not";
			}

			@Override
			public int arity()
			{
				return 1;
			}
		},
		UNOP_NEG
		{
			@Override
			public String toString()
			{
				return "unop_neg";
			}

			@Override
			public int arity()
			{
				return 1;
			}
		},
		UNOP_BITNOT
		{
			@Override
			public String toString()
			{
				return "unop_bitnot";
			}

			@Override
			public int arity()
			{
				return 1;
			}
		},
		ATTR
		{
			@Override
			public String toString()
			{
				return "attr";
			}

			@Override
			public int arity()
			{
				return 1;
			}
		},
		FUNC
		{
			@Override
			public String toString()
			{
				return "func";
			}

			@Override
			public int arity()
			{
				return -1;
			}
		},
		METH
		{
			@Override
			public String toString()
			{
				return "meth";
			}

			@Override
			public int arity()
			{
				return -1;
			}
		};

		public String toString()
		{
			return null;
		}

		public abstract int arity();
	};

	/**
	If {@code null} this node is valid, otherwise it contains the type of the
	error.
	**/
	protected VSQLError error;

	protected List<Object> content;

	protected VSQLAST(Object ... content)
	{
		this.content = new ArrayList<>();

		for (Object item : content)
		{
			if (item instanceof String || item instanceof VSQLAST)
				this.content.add(item);
			else if (item != null)
				throw new IllegalArgumentException(formatMessage("VSQLAST(...) doesn't support an argument of type {!t}", item));
		}
	}

	public static VSQLAST fromsource(String source, Map<String, VSQLField> vars)
	{
		Template template = new Template("<?return " + source + "?>", "vSQL", null, Template.Whitespace.keep);
		List<AST> content = template.getContent();
		ReturnAST returnExpr = (ReturnAST)content.get(content.size() - 1);
		return returnExpr.getObj().asVSQL(vars);
	}

	@Override
	public String toString()
	{
		return getSource();
	}

	/**
	Return a human readable description of this operation for error messages
	**/
	abstract public String getDescription();

	public VSQLRule getSignature()
	{
		return null;
	}

	public List<Object> getContent()
	{
		return content;
	}

	public String getSource()
	{
		StringBuilder buffer = new StringBuilder();
		makeSource(buffer);
		return buffer.toString();
	}

	protected void makeSource(StringBuilder buffer)
	{
		for (Object item : content)
		{
			if (item instanceof String)
				buffer.append((String)item);
			else
				((VSQLAST)item).makeSource(buffer);
		}
	}

	public int getSourceLength()
	{
		int length = 0;

		for (Object item : content)
		{
			if (item instanceof String)
				length += ((String)item).length();
			else
				length += ((VSQLAST)item).getSourceLength();
		}
		return length;
	}

	public String getSQLSource(VSQLQuery query)
	{
		if (error != null)
		{
			VSQLAST rootErrorAST = getErrorAST();

			rootErrorAST.getError().throwError(rootErrorAST);
		}

		StringBuilder buffer = new StringBuilder();
		makeSQLSource(buffer, query);
		return buffer.toString();
	}

	protected void makeSQLSource(StringBuilder buffer, VSQLQuery query)
	{
		VSQLRule rule = getRule();
		if (rule == null)
		{
			throw new RuntimeException(getSource());
		}
		List<VSQLAST> children = getChildren();
		rule.makeSQLSource(buffer, query, children);
	}

	public abstract NodeType getNodeType();

	public abstract VSQLDataType getDataType();

	public String getDataTypeString()
	{
		VSQLDataType dataType = getDataType();
		return dataType == null ? "?" : dataType.toString().toUpperCase();
	}

	public String getTypeSignature()
	{
		return null;
	}

	public VSQLError getError()
	{
		return error;
	}

	public String getNodeValue()
	{
		return null;
	}

	public abstract List<VSQLAST> getChildren();

	public VSQLRule getRule()
	{
		return null;
	}

	/**
	<p>Return the arity (i.e. the number of operands for the operation).</p>

	<p>Returns -1 if the arity isn't fixed (e.g. for a function call etc.).</p>
	**/
	public int getArity()
	{
		return getChildren().size();
	}

	public abstract int getPrecedence();

	public List<VSQLFieldRefAST> getFieldRefs()
	{
		List<VSQLFieldRefAST> result = new ArrayList<>();

		addFieldRefs(result);

		return result;
	}

	protected void addFieldRefs(List<VSQLFieldRefAST> fieldRefs)
	{
		for (VSQLAST child : getChildren())
		{
			child.addFieldRefs(fieldRefs);
		}
	}

	public VSQLAST getErrorAST()
	{
		if (error == null)
		{
			return null;
		}
		else if (error == VSQLError.SUBNODEERROR)
		{
			for (VSQLAST childAST : getChildren())
			{
				VSQLAST errorAST = childAST.getErrorAST();
				if (errorAST != null)
				{
					return errorAST;
				}
			}
		}
		// Either `this` has to real (non-`SUBNODEERROR`) error (which is normal)
		// or we had a `SUBNODEERROR`, but no child AST ahd any (which shouldn't happen)
		return this;
	}

	public String getErrorMessage()
	{
		if (error == null)
		{
			return null;
		}
		else
		{
			return error.getErrorMessage(this);
		}
	}

	/**
	Validate this node.

	If any of the child nodes are invalid or this node itself is invalid,
	{@link #getError()} will return the appropriate error, and
	{@link #getDataType()} will return {@code null}.

	If the node is valid {@link #getError()} will return {@code null} and
	{@link #getDataType()} will return the datatype of the result.
	**/
	public abstract void validate();

	public Object asJSON()
	{
		Map<String, Object> json = makeMap("nodetype", getNodeType().toString());
		VSQLDataType dataType = getDataType();
		if (dataType != null)
			json.put("datatype", dataType.toString());
		String value = getNodeValue();
		if (value != null)
			json.put("value", value);
		List<VSQLAST> children = getChildren();
		int childCount = children.size();
		if (childCount != 0)
		{
			List<Object> childNodes = new ArrayList<>(childCount);

			for (VSQLAST child : children)
			{
				childNodes.add(child.asJSON());
			}
			json.put("children", childNodes);
		}
		return json;
	}

	/** {@code attributes} contains the names of the attributes available to UL4 */
	protected static Set<String> attributes = makeSet(
		"nodetype",
		"datatype",
		"error",
		"value",
		"children",
		"content"
	);

	/** {@inheritDoc} */
	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	/** {@inheritDoc} */
	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		VSQLDataType dataType;
		switch (key)
		{
			case "nodetype":
				return getNodeType().toString();
			case "datatype":
				dataType = getDataType();
				return dataType != null ? dataType.toString() : null;
			case "error":
				return error != null ? error.toString() : null;
			case "value":
				return getNodeValue();
			case "children":
				return getChildren();
			case "content":
				return getContent();
			default:
				return new UndefinedAttribute(this, key);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		formatter.append(getClass().getName());
		formatter.append(" source=");
		visitForRepr(formatter);
		formatter.append(">");
	}

	private void visitForRepr(UL4Repr.Formatter formatter)
	{
		formatter.append("<");
		boolean first = true;
		for (Object item : content)
		{
			if (item == null || (item instanceof String itemString && itemString.isEmpty()))
			{
				continue;
			}
			if (first)
			{
				first = false;
			}
			else
			{
				formatter.append(", ");
			}
			if (item instanceof String)
			{
				formatter.visit(item);
			}
			else if (item instanceof VSQLAST itemAST)
			{
				itemAST.visitForRepr(formatter);
			}
		}
		formatter.append(">");
	}
	@Override
	public String getUL4ONName()
	{
		return getTypeUL4().getUL4ONName();
	}

	protected static void addRule(Map rules, VSQLDataType resultType, List<Object> signature, List<Object> source)
	{
		rules.put(signature, new VSQLRule(resultType, signature, source));
	}

	// @Override
	// public void dumpUL4ON(Encoder encoder) throws IOException
	// {
	// 	encoder.dump(source);
	// 	encoder.dump(pos);
	// }

	// @Override
	// public void loadUL4ON(Decoder decoder) throws IOException
	// {
	// 	source = (String)decoder.load();
	// 	pos = (Slice)decoder.load();
	// }

	static public void register4UL4ON()
	{
		// register definition classes
		Utils.register(VSQLField.type);
		Utils.register(VSQLGroup.type);

		// register AST classes
		// Utils.register("de.livinglogic.vsql.none", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new NoneAST(null, null); }});
		// Utils.register("de.livinglogic.vsql.bool", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new BoolAST(null, null, false); }});
		// Utils.register("de.livinglogic.vsql.int", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new IntAST(null, null, 0); }});
		// Utils.register("de.livinglogic.vsql.number", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new NumberAST(null, null, 0.0); }});
		// Utils.register("de.livinglogic.vsql.str", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new StrAST(null, null, null); }});
		// Utils.register("de.livinglogic.vsql.color", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new ColorAST(null, null, null); }});
		// Utils.register("de.livinglogic.vsql.date", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new DateAST(null, null, null); }});
		// Utils.register("de.livinglogic.vsql.datetime", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new DateTimeAST(null, null, null); }});
		// Utils.register("de.livinglogic.vsql.list", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new ListAST(null, null); }});
		// Utils.register("de.livinglogic.vsql.fieldref", new ObjectFactory(){ public UL4ONSerializable create(String id) { return new FieldRefAST(null, null, null, null); }});
	}
}
