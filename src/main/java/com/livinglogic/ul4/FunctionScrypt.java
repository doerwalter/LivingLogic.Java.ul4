/*
** Copyright 2009-2019 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import com.lambdaworks.crypto.SCrypt;

public class FunctionScrypt extends Function
{
	public String nameUL4()
	{
		return "scrypt";
	}

	private static final Signature signature = new Signature("string", Signature.required, "salt", Signature.required);

	public Signature getSignature()
	{
		return signature;
	}

	public Object evaluate(BoundArguments args)
	{
		Object arg0 = args.get(0);
		Object arg1 = args.get(1);

		if (arg0 instanceof String && arg1 instanceof String)
			return call((String)arg0, (String)arg1);
		throw new ArgumentTypeMismatchException("script({!t}, {!t}) not supported", arg0, arg1);
	}

	public static Object call(String string, String salt)
	{
		byte[] stringBytes = string.getBytes(StandardCharsets.UTF_8);
		byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
		byte[] hash;

		try
		{
			hash = SCrypt.scrypt(stringBytes, saltBytes, 16384, 8, 1, 128);
		}
		catch (GeneralSecurityException exc)
		{
			throw new RuntimeException(exc);
		}

		String hexDigits = "0123456789abcdef";
		StringBuilder buffer = new StringBuilder(256);

		for (int i = 0; i < hash.length; ++i)
		{
			byte high = (byte)((hash[i] & 0xf0) >> 4);
			byte low  = (byte)(hash[i] & 0x0f);
			buffer.append(hexDigits.charAt(high));
			buffer.append(hexDigits.charAt(low));
		}
		return buffer.toString();
	}
}
