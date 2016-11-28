/*
** Copyright 2009-2016 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.commons.lang.math.NumberUtils;

import static com.livinglogic.utils.SetUtils.makeSet;

public class Color implements Collection, UL4Repr, UL4GetItemString, UL4Attributes, UL4Len, UL4Type
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

	public void reprUL4(UL4Repr.Formatter formatter)
	{
		formatter.append("#");
		if (((r>>4) == (r&0xf)) && ((g>>4) == (g&0xf)) && ((b>>4) == (b&0xf)) && ((a>>4) == (a&0xf)))
		{
			formatter.append(Integer.toHexString(r>>4));
			formatter.append(Integer.toHexString(g>>4));
			formatter.append(Integer.toHexString(b>>4));
			if (a != 255)
				formatter.append(Integer.toHexString(a>>4));
		}
		else
		{
			String sr = Integer.toHexString(r);
			if (sr.length() < 2)
				formatter.append("0");
			formatter.append(sr);

			String sg = Integer.toHexString(g);
			if (sg.length() < 2)
				formatter.append("0");
			formatter.append(sg);

			String sb = Integer.toHexString(b);
			if (sb.length() < 2)
				formatter.append("0");
			formatter.append(sb);

			if (a != 255)
			{
				String sa = Integer.toHexString(a);
				if (sa.length() < 2)
					formatter.append("0");
				formatter.append(sa);
			}
		}
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
		// Scale our values to the range [0, 1]
		double rt = r/255.;
		double gt = g/255.;
		double bt = b/255.;
		double at = a/255.;

		// Convert to premultiplied alpha
		rt *= at;
		gt *= at;
		bt *= at;

		// Scale other values to the range [0, 1]
		double ao = color.a/255.;
		double ro = color.r/255.;
		double go = color.g/255.;
		double bo = color.b/255.;

		// Convert to premultiplied alpha
		ro *= ao;
		go *= ao;
		bo *= ao;

		// Blend colors
		double rf = rt + ro * (1 - at);
		double gf = gt + go * (1 - at);
		double bf = bt + bo * (1 - at);
		double af = ao + at - ao * at;

		// Unmultiply alpha
		if (af != 0)
		{
			rf /= af;
			gf /= af;
			bf /= af;
		}

		// Scale back to [0, 255]
		int r = (int)(255*rf);
		int g = (int)(255*gf);
		int b = (int)(255*bf);
		int a = (int)(255*af);

		// create final color
		return new Color(r, g, b, a);
	}

	public Vector<Double> hls()
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

	public Vector<Double> hlsa()
	{
		Vector retVal = hls();
		retVal.add(new Double(a/255.));
		return retVal;
	}

	public Vector<Double> hsv()
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

	public Vector<Double> hsva()
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
			return fromhls(0., lum, 0., a/255.);

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

		return fromhls(h, lum, s, a/255.);
	}

	public Color witha(int a)
	{
		return new Color(r, g, b, a);
	}

	public Color abslum(double f)
	{
		Vector<Double> v = hlsa();
		return fromhls(v.get(0), v.get(1)+f, v.get(2), v.get(3));
	}

	public Color rellum(double f)
	{
		Vector<Double> v = hlsa();
		double newlum = v.get(1);
		if (f > 0)
			newlum += (1-newlum)*f;
		else if (f < 0)
			newlum += newlum*f;
		return fromhls(v.get(0), newlum, v.get(2), v.get(3));
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

	private static class BoundMethodR extends BoundMethod<Color>
	{
		public BoundMethodR(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.r";
		}

		public Object evaluate(BoundArguments args)
		{
			return (int)object.r;
		}
	}

	private static class BoundMethodG extends BoundMethod<Color>
	{
		public BoundMethodG(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.g";
		}

		public Object evaluate(BoundArguments args)
		{
			return (int)object.g;
		}
	}

	private static class BoundMethodB extends BoundMethod<Color>
	{
		public BoundMethodB(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.b";
		}

		public Object evaluate(BoundArguments args)
		{
			return (int)object.b;
		}
	}

	private static class BoundMethodA extends BoundMethod<Color>
	{
		public BoundMethodA(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.a";
		}

		public Object evaluate(BoundArguments args)
		{
			return (int)object.a;
		}
	}

	private static class BoundMethodLum extends BoundMethod<Color>
	{
		public BoundMethodLum(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.lum";
		}

		public Object evaluate(BoundArguments args)
		{
			return object.lum();
		}
	}

	private static class BoundMethodHLS extends BoundMethod<Color>
	{
		public BoundMethodHLS(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.hls";
		}

		public Object evaluate(BoundArguments args)
		{
			return object.hls();
		}
	}

	private static class BoundMethodHLSA extends BoundMethod<Color>
	{
		public BoundMethodHLSA(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.hlsa";
		}

		public Object evaluate(BoundArguments args)
		{
			return object.hlsa();
		}
	}

	private static class BoundMethodHSV extends BoundMethod<Color>
	{
		public BoundMethodHSV(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.hsv";
		}

		public Object evaluate(BoundArguments args)
		{
			return object.hsv();
		}
	}

	private static class BoundMethodHSVA extends BoundMethod<Color>
	{
		public BoundMethodHSVA(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.hsva";
		}

		public Object evaluate(BoundArguments args)
		{
			return object.hsva();
		}
	}

	private static class BoundMethodWithA extends BoundMethod<Color>
	{
		public BoundMethodWithA(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.witha";
		}

		private static final Signature signature = new Signature("a", Signature.required);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return object.witha(Utils.toInt(args.get(0)));
		}
	}

	private static class BoundMethodWithLum extends BoundMethod<Color>
	{
		public BoundMethodWithLum(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.withlum";
		}

		private static final Signature signature = new Signature("lum", Signature.required);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return object.withlum(Utils.toDouble(args.get(0)));
		}
	}

	private static class BoundMethodAbsLum extends BoundMethod<Color>
	{
		public BoundMethodAbsLum(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.abslum";
		}

		private static final Signature signature = new Signature("f", Signature.required);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return object.abslum(Utils.toDouble(args.get(0)));
		}
	}

	private static class BoundMethodRelLum extends BoundMethod<Color>
	{
		public BoundMethodRelLum(Color object)
		{
			super(object);
		}

		public String nameUL4()
		{
			return "color.rellum";
		}

		private static final Signature signature = new Signature("f", Signature.required);

		public Signature getSignature()
		{
			return signature;
		}

		public Object evaluate(BoundArguments args)
		{
			return object.rellum(Utils.toDouble(args.get(0)));
		}
	}

	protected static Set<String> attributes = makeSet("r", "g", "b", "a", "lum", "hls", "hlsa", "hsv", "hsva", "witha", "withlum", "abslum", "rellum");

	public Set<String> getAttributeNamesUL4()
	{
		return attributes;
	}

	public Object getItemStringUL4(String key)
	{
		switch (key)
		{
			case "r":
				return new BoundMethodR(this);
			case "g":
				return new BoundMethodG(this);
			case "b":
				return new BoundMethodB(this);
			case "a":
				return new BoundMethodA(this);
			case "lum":
				return new BoundMethodLum(this);
			case "hls":
				return new BoundMethodHLS(this);
			case "hlsa":
				return new BoundMethodHLSA(this);
			case "hsv":
				return new BoundMethodHSV(this);
			case "hsva":
				return new BoundMethodHSVA(this);
			case "witha":
				return new BoundMethodWithA(this);
			case "withlum":
				return new BoundMethodWithLum(this);
			case "abslum":
				return new BoundMethodAbsLum(this);
			case "rellum":
				return new BoundMethodRelLum(this);
			default:
				return new UndefinedKey(key);
		}
	}
}
