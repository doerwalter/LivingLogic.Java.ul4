/*
** Copyright 2009-2013 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

public class Color implements Collection, UL4Repr, UL4Len, UL4Type, UL4MethodCall
{
	private char r;
	private char g;
	private char b;
	private char a;

	public Color(int r, int g, int b, int a)
	{
		if (r < 0)
			r = 0;
		else if (r > 255)
			r = 255;
		this.r = (char)r;

		if (g < 0)
			g = 0;
		else if (g > 255)
			g = 255;
		this.g = (char)g;

		if (b < 0)
			b = 0;
		else if (b > 255)
			b = 255;
		this.b = (char)b;

		if (a < 0)
			a = 0;
		else if (a > 255)
			a = 255;
		this.a = (char)a;
	}

	public Color(int r, int g, int b)
	{
		this(r, g, b, 255);
	}

	public static Color fromrgb(double r, double g, double b, double a)
	{
		return new Color((int)(255*r), (int)(255*g), (int)(255*b), (int)(255*a));
	}

	public static Color fromrgb(double r, double g, double b)
	{
		return fromrgb(r, g, b, 1.0);
	}

	public static Color fromhsv(double h, double s, double v, double a)
	{
		h %= 1.0;

		if (s < 0.0)
			s = 0.0;
		else if (s > 1.0)
			s = 1.0;

		if (v < 0.0)
			v = 0.0;
		else if (v > 1.0)
			v = 1.0;

		if (a < 0.0)
			a = 0.0;
		else if (a > 1.0)
			a = 1.0;

		int rr = 0;
		int rg = 0;
		int rb = 0;
		int ra = (int)(255.*a);

		if (s == 0.0)
 			rr = rg = rb = (int)(255.*v);
		else
		{
			int i = (int)(h*6.0);
			double f = (h*6.0) - i;
			double p = v*(1.0 - s);
			double q = v*(1.0 - s*f);
			double t = v*(1.0 - s*(1.0-f));

			switch (i)
			{
				case 0:
				case 6:
					rr = (int)(255.*v);
					rg = (int)(255.*t);
					rb = (int)(255.*p);
					break;
				case 1:
					rr = (int)(255.*q);
					rg = (int)(255.*v);
					rb = (int)(255.*p);
					break;
				case 2:
					rr = (int)(255.*p);
					rg = (int)(255.*v);
					rb = (int)(255.*t);
					break;
				case 3:
					rr = (int)(255.*p);
					rg = (int)(255.*q);
					rb = (int)(255.*v);
					break;
				case 4:
					rr = (int)(255.*t);
					rg = (int)(255.*p);
					rb = (int)(255.*v);
					break;
				case 5:
					rr = (int)(255.*v);
					rg = (int)(255.*p);
					rb = (int)(255.*q);
					break;
			}
		}
		return new Color(rr, rg, rb, ra);
	}

	public static Color fromhsv(double h, double s, double v)
	{
		return fromhsv(h, s, v, 1.0);
	}

	private static double _v(double m1, double m2, double hue)
	{
		hue %= 1.0;

		if (hue < 1./6.)
			return m1 + (m2-m1)*hue*6.0;
		else if (hue < 0.5)
			return m2;
		else if (hue < 2./3.)
			return m1 + (m2-m1)*(2./3.-hue)*6.0;
		else
			return m1;
	}

	public static Color fromhls(double h, double l, double s, double a)
	{
		h %= 1.0;

		if (l < 0.0)
			l = 0.0;
		else if (l > 1.0)
			l = 1.0;

		if (s < 0.0)
			s = 0.0;
		else if (s > 1.0)
			s = 1.0;

		if (a < 0.0)
			a = 0.0;
		else if (a > 1.0)
			a = 1.0;

		if (s == 0.0)
			return new Color((int)(255.*l), (int)(255.*l), (int)(255.*l), (int)(255.*a));

		double m2 = l <= 0.5 ? l * (1.0+s) : l+s-(l*s);
		double m1 = 2.0*l - m2;

		double r = _v(m1, m2, h+1./3.);
		double g = _v(m1, m2, h);
		double b = _v(m1, m2, h-1./3.);
		return new Color((int)(255.*r), (int)(255.*g), (int)(255.*b), (int)(255.*a));
	}

	public static Color fromhls(double h, double l, double s)
	{
		return fromhls(h, l, s, 1.0);
	}

	public int getR()
	{
		return r;
	}

	public int getG()
	{
		return g;
	}

	public int getB()
	{
		return b;
	}

	public int getA()
	{
		return a;
	}

	public String toString()
	{
		if (a == 255)
		{
			if (((r>>4) == (r&0xf)) && ((g>>4) == (g&0xf)) && ((b>>4) == (b&0xf)))
				return "#" + Integer.toHexString(r>>4) + Integer.toHexString(g>>4) + Integer.toHexString(b>>4);
			else
			{
				String sr = Integer.toHexString(r);
				if (sr.length() < 2)
					sr = "0" + sr;

				String sg = Integer.toHexString(g);
				if (sg.length() < 2)
					sg = "0" + sg;

				String sb = Integer.toHexString(b);
				if (sb.length() < 2)
					sb = "0" + sb;

				return "#" + sr + sg + sb;
			}
		}
		else
		{
			return "rgba(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) + "," + a/255. + ")";
		}
	}

	public String reprUL4()
	{
		StringBuilder buffer = new StringBuilder(9);

		buffer.append("#");
		if (((r>>4) == (r&0xf)) && ((g>>4) == (g&0xf)) && ((b>>4) == (b&0xf)) && ((a>>4) == (a&0xf)))
		{
			buffer.append(Integer.toHexString(r>>4));
			buffer.append(Integer.toHexString(g>>4));
			buffer.append(Integer.toHexString(b>>4));
			if (a != 255)
				buffer.append(Integer.toHexString(a>>4));
		}
		else
		{
			String sr = Integer.toHexString(r);
			if (sr.length() < 2)
				buffer.append("0");
			buffer.append(sr);

			String sg = Integer.toHexString(g);
			if (sg.length() < 2)
				buffer.append("0");
			buffer.append(sg);

			String sb = Integer.toHexString(b);
			if (sb.length() < 2)
				buffer.append("0");
			buffer.append(sb);

			if (a != 255)
			{
				String sa = Integer.toHexString(a);
				if (sa.length() < 2)
					buffer.append("0");
				buffer.append(sa);
			}
		}
		return buffer.toString();
	}

	public String dump()
	{
		StringBuilder buffer = new StringBuilder(8);

		String sr = Integer.toHexString(r);
		if (sr.length() < 2)
			buffer.append("0");
		buffer.append(sr);

		String sg = Integer.toHexString(g);
		if (sg.length() < 2)
			buffer.append("0");
		buffer.append(sg);

		String sb = Integer.toHexString(b);
		if (sb.length() < 2)
			buffer.append("0");
		buffer.append(sb);

		String sa = Integer.toHexString(a);
		if (sa.length() < 2)
			buffer.append("0");
		buffer.append(sa);

		return buffer.toString();
	}

	public static Color fromdump(String value)
	{
		int r = Integer.valueOf(value.substring(0, 2), 16);
		int g = Integer.valueOf(value.substring(2, 4), 16);
		int b = Integer.valueOf(value.substring(4, 6), 16);
		int a = Integer.valueOf(value.substring(6, 8), 16);
		return new Color(r, g, b, a);
	}

	public static Color fromrepr(String value)
	{
		if (value == null)
			return null;
		int len = value.length();
		int r;
		int g;
		int b;
		int a;
		if (len == 4 || len == 5)
		{
			r = Integer.valueOf(value.substring(1, 2), 16) * 0x11;
			g = Integer.valueOf(value.substring(2, 3), 16) * 0x11;
			b = Integer.valueOf(value.substring(3, 4), 16) * 0x11;
			a = (len == 4) ? 0xff : (Integer.valueOf(value.substring(4, 5), 16) * 0x11);
		}
		else if (len == 7 || len == 9)
		{
			r = Integer.valueOf(value.substring(1, 3), 16);
			g = Integer.valueOf(value.substring(3, 5), 16);
			b = Integer.valueOf(value.substring(5, 7), 16);
			a = (len == 7) ? 0xff : Integer.valueOf(value.substring(7, 9), 16);
		}
		else
			throw new RuntimeException("Invalid color repr '" + value + "'");
		return new Color(r, g, b, a);
	}

	public Color blend(Color color)
	{
		double sa = a/255.;
		double rsa = 1.-sa;
		int nr = (int)(r*sa+rsa*color.r);
		int ng = (int)(g*sa+rsa*color.g);
		int nb = (int)(b*sa+rsa*color.b);
		int na = (int)(255-rsa*(255-color.a));
		return new Color(nr, ng, nb, na);
	}

	public Vector hls()
	{
		int maxc = NumberUtils.max((int)r, (int)g, (int)b);
		int minc = NumberUtils.min((int)r, (int)g, (int)b);

		double dmaxc = maxc/255.;
		double dminc = minc/255.;

		double l = (dminc+dmaxc)/2.0;

		if (minc == maxc)
		{
			Vector retVal = new Vector(3);
			retVal.add(new Double(0.0));
			retVal.add(new Double(l));
			retVal.add(new Double(0.0));
			return retVal;
		}
		double s = l <= 0.5 ? (dmaxc-dminc) / (dmaxc+dminc) : (dmaxc-dminc) / (2.0-dmaxc-dminc);

		double rc = (dmaxc-r/255.) / (dmaxc-dminc);
		double gc = (dmaxc-g/255.) / (dmaxc-dminc);
		double bc = (dmaxc-b/255.) / (dmaxc-dminc);

		double h;
		if (r == maxc)
			h = bc-gc;
		else if (g == maxc)
			h = 2.0+rc-bc;
		else
			h = 4.0+gc-rc;
		h = (h/6.0) % 1.0;

		Vector retVal = new Vector(3);
		retVal.add(new Double(h));
		retVal.add(new Double(l));
		retVal.add(new Double(s));
		return retVal;
	}

	public Vector hlsa()
	{
		Vector retVal = hls();
		retVal.add(new Double(a/255.));
		return retVal;
	}

	public Vector hsv()
	{
		int maxc = NumberUtils.max((int)r, (int)g, (int)b);
		int minc = NumberUtils.min((int)r, (int)g, (int)b);

		double dmaxc = maxc/255.;
		double dminc = minc/255.;

		double v = dmaxc;
		if (minc == maxc)
		{
			Vector retVal = new Vector(3);
			retVal.add(0.0d);
			retVal.add(0.0d);
			retVal.add(v);
			return retVal;
		}
		double s = (dmaxc-dminc) / dmaxc;

		double rc = (dmaxc-r/255.) / (dmaxc-dminc);
		double gc = (dmaxc-g/255.) / (dmaxc-dminc);
		double bc = (dmaxc-b/255.) / (dmaxc-dminc);

		double h;
		if (r == maxc)
			h = bc-gc;
		else if (g == maxc)
			h = 2.0+rc-bc;
		else
			h = 4.0+gc-rc;
		h = (h/6.0) % 1.0;

		Vector retVal = new Vector(3);
		retVal.add(h);
		retVal.add(s);
		retVal.add(v);
		return retVal;
	}

	public Vector hsva()
	{
		Vector retVal = hsv();
		retVal.add(new Double(a/255.));
		return retVal;
	}

	public double lum()
	{
		int maxc = NumberUtils.max((int)r, (int)g, (int)b);
		int minc = NumberUtils.min((int)r, (int)g, (int)b);

		double dmaxc = maxc/255.;
		double dminc = minc/255.;

		return (dminc+dmaxc)/2.0;
	}

	public Color withlum(double lum)
	{
		int maxc = NumberUtils.max((int)r, (int)g, (int)b);
		int minc = NumberUtils.min((int)r, (int)g, (int)b);

		double dmaxc = maxc/255.;
		double dminc = minc/255.;

		double l = (dminc+dmaxc)/2.0;

		if (minc == maxc)
			return fromhls(0., lum, 0., a);

		double s = l <= 0.5 ? (dmaxc-dminc) / (dmaxc+dminc) : (dmaxc-dminc) / (2.0-dmaxc-dminc);

		double rc = (dmaxc-r/255.) / (dmaxc-dminc);
		double gc = (dmaxc-g/255.) / (dmaxc-dminc);
		double bc = (dmaxc-b/255.) / (dmaxc-dminc);

		double h;
		if (r == maxc)
			h = bc-gc;
		else if (g == maxc)
			h = 2.0+rc-bc;
		else
			h = 4.0+gc-rc;
		h = (h/6.0) % 1.0;

		return fromhls(h, lum, s, a);
	}

	public Color witha(int a)
	{
		return new Color(r, g, b, a);
	}

	// Collection interface
	public boolean add(Object o)
	{
		throw new UnsupportedOperationException();
	}

	public boolean addAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	public boolean contains(Object o)
	{
		if (o == null || !(o instanceof Integer))
			return false;
		int ov = ((Integer)o);

		return ((r == ov) || (g == ov) || (b == ov) || (a == ov));
	}

	public boolean containsAll(Collection c)
	{
		for (Object o : c)
		{
			if (!contains(o))
				return false;
		}
		return true;
	}

	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof Color))
			return false;
		Color co = (Color)o;
		return ((r == co.r) && (g == co.g) && (b == co.b) && (a == co.a));
	}

	public boolean isEmpty()
	{
		return false;
	}

	public int hashCode()
	{
		return r ^ g ^ b ^ a;
	}

	public class ColorIterator implements Iterator
	{
		int index;

		public ColorIterator()
		{
			index = 0;
		}

		public boolean hasNext()
		{
			return index < 4;
		}

		public Object next()
		{
			switch (index++)
			{
				case 0:
					return r;
				case 1:
					return g;
				case 2:
					return b;
				case 3:
					return a;
				default:
					throw new NoSuchElementException("No more components available!");
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException("Colors don't support component removal!");
		}
	};

	public Iterator iterator()
	{
		return new ColorIterator();
	}

	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}

	public boolean removeAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	public Object[] toArray()
	{
		Object[] retVal = new Object[4];
		retVal[0] = new Integer(r);
		retVal[1] = new Integer(g);
		retVal[2] = new Integer(b);
		retVal[3] = new Integer(a);
		return retVal;
	}

	public Object[] toArray(Object[] a)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		return 4;
	}

	public int lenUL4()
	{
		return 4;
	}

	public String typeUL4()
	{
		return "color";
	}

	int getItemIntegerUL4(int index)
	{
		switch (index)
		{
			case 0:
			case -4:
				return r;
			case 1:
			case -3:
				return g;
			case 2:
			case -2:
				return b;
			case 3:
			case -1:
				return a;
			default:
				throw new ArrayIndexOutOfBoundsException(index);
		}
	}

	private Signature signatureR = new Signature("r", null, null);
	private Signature signatureG = new Signature("g", null, null);
	private Signature signatureB = new Signature("b", null, null);
	private Signature signatureA = new Signature("a", null, null);
	private Signature signatureLum = new Signature("lum", null, null);
	private Signature signatureHLS = new Signature("hls", null, null);
	private Signature signatureHLSA = new Signature("hlsa", null, null);
	private Signature signatureHSV = new Signature("hsv", null, null);
	private Signature signatureHSVA = new Signature("hsva", null, null);
	private Signature signatureWithA = new Signature("witha", null, null, "a", Signature.required);
	private Signature signatureWithLum = new Signature("withlum", null, null, "lum", Signature.required);

	public Object callMethodUL4(String methodName, Object[] args, Map<String, Object> kwargs)
	{
		if ("r".equals(methodName))
		{
			args = signatureR.makeArgumentArray(args, kwargs);
			return (int)r;
		}
		else if ("g".equals(methodName))
		{
			args = signatureG.makeArgumentArray(args, kwargs);
			return (int)g;
		}
		else if ("b".equals(methodName))
		{
			args = signatureB.makeArgumentArray(args, kwargs);
			return (int)b;
		}
		else if ("a".equals(methodName))
		{
			args = signatureA.makeArgumentArray(args, kwargs);
			return (int)a;
		}
		else if ("lum".equals(methodName))
		{
			args = signatureLum.makeArgumentArray(args, kwargs);
			return lum();
		}
		else if ("hls".equals(methodName))
		{
			args = signatureHLS.makeArgumentArray(args, kwargs);
			return hls();
		}
		else if ("hlsa".equals(methodName))
		{
			args = signatureHLSA.makeArgumentArray(args, kwargs);
			return hlsa();
		}
		else if ("hsv".equals(methodName))
		{
			args = signatureHSV.makeArgumentArray(args, kwargs);
			return hsv();
		}
		else if ("hsva".equals(methodName))
		{
			args = signatureHSVA.makeArgumentArray(args, kwargs);
			return hsva();
		}
		else if ("witha".equals(methodName))
		{
			args = signatureWithA.makeArgumentArray(args, kwargs);
			return witha(Utils.toInt(args[0]));
		}
		else if ("withlum".equals(methodName))
		{
			args = signatureWithLum.makeArgumentArray(args, kwargs);
			return withlum(Utils.toDouble(args[0]));
		}
		else
			throw new UnknownMethodException(methodName);
	}
}
