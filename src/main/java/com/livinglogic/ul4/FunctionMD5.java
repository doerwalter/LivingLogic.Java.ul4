/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

public class FunctionMD5 extends Function
{
	public String nameUL4()
	{
		return "md5";
	}

	private static final Signature signature = new Signature("string", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		return call(args.get(0));
	}

	public static Object call(Object obj)
	{
		if (obj instanceof String)
		{
			MessageDigest md;
			try
			{
				md = MessageDigest.getInstance("MD5");
			}
			catch (NoSuchAlgorithmException ex)
			{
				throw new RuntimeException(ex);
			}
			byte[] hash;
			try
			{
				hash = md.digest(((String)obj).getBytes("UTF-8"));
			}
			catch (UnsupportedEncodingException ex)
			{
				throw new RuntimeException(ex);
			}

			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < hash.length; ++i)
				buffer.append(Integer.toHexString((hash[i] & 0xFF) | 0x100).substring(1, 3));
			return buffer.toString();
		}
		throw new ArgumentTypeMismatchException("md5({!t}) not supported", obj);
	}
}
