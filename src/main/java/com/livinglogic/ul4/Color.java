/*
** Copyright 2009-2022 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import static com.livinglogic.utils.MapUtils.makeMap;
import static com.livinglogic.utils.SetUtils.makeSet;

public class Color implements Collection, UL4Instance, UL4Repr, UL4GetItem, UL4Dir, UL4Len
{
	protected static class Type extends AbstractInstanceType
	{
		@Override
		public String getModuleName()
		{
			return "color";
		}

		@Override
		public String getNameUL4()
		{
			return "Color";
		}

		@Override
		public String getDoc()
		{
			return "An RGBA color (with 8-bit red, green, blue and alpha values).";
		}

		@Override
		public boolean instanceCheck(Object object)
		{
			return object instanceof Color;
		}

		private static final Signature signature = new Signature().addBoth("r").addBoth("g").addBoth("b").addBoth("a", 255);

		@Override
		public Signature getSignature()
		{
			return signature;
		}

		@Override
		public Color create(EvaluationContext context, BoundArguments args)
		{
			int r = args.getInt(0);
			int g = args.getInt(1);
			int b = args.getInt(2);
			int a = args.getInt(3);

			return new Color(r, g, b, a);
		}
	}

	public static final Type type = new Type();

	@Override
	public UL4Type getTypeUL4()
	{
		return type;
	}

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

	@Override
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
			DecimalFormat df = new DecimalFormat("0.###", new DecimalFormatSymbols(Locale.US));
			return "rgba(" + Integer.toString(r) + "," + Integer.toString(g) + "," + Integer.toString(b) + "," + df.format((double)a/255.) + ")";
		}
	}

	@Override
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

	public static Color _fromrepr(String value)
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
			return null;
		return new Color(r, g, b, a);
	}

	public static Color fromrepr(String value)
	{
		Color color = _fromrepr(value);
		if (color == null)
			throw new ColorFormatException(Utils.formatMessage("Invalid color repr {!r}", value));
		return color;
	}

	public static final Color maroon = new Color(0x80, 0x00, 0x00);
	public static final Color red = new Color(0xff, 0x00, 0x00);
	public static final Color orange = new Color(0xff, 0xa5, 0x00);
	public static final Color yellow = new Color(0xff, 0xff, 0x00);
	public static final Color olive = new Color(0x80, 0x80, 0x00);
	public static final Color purple = new Color(0x80, 0x00, 0x80);
	public static final Color fuchsia = new Color(0xff, 0x00, 0xff);
	public static final Color white = new Color(0xff, 0xff, 0xff);
	public static final Color lime = new Color(0x00, 0xff, 0x00);
	public static final Color green = new Color(0x00, 0x80, 0x00);
	public static final Color navy = new Color(0x00, 0x00, 0x80);
	public static final Color blue = new Color(0x00, 0x00, 0xff);
	public static final Color aqua = new Color(0x00, 0xff, 0xff);
	public static final Color teal = new Color(0x00, 0x80, 0x80);
	public static final Color black = new Color(0x00, 0x00, 0x00);
	public static final Color silver = new Color(0xc0, 0xc0, 0xc0);
	public static final Color gray = new Color(0x80, 0x80, 0x80);
	// Aliases
	public static final Color magenta = purple;
	public static final Color cyan = aqua;

	private static final Map<String, Color> cssColors = makeMap(
		"maroon", maroon,
		"red", red,
		"orange", orange,
		"yellow", yellow,
		"olive", olive,
		"purple", purple,
		"fuchsia", fuchsia,
		"white", white,
		"lime", lime,
		"green", green,
		"navy", navy,
		"blue", blue,
		"aqua", aqua,
		"teal", teal,
		"black", black,
		"silver", silver,
		"gray", gray,
		"magenta", magenta,
		"cyan", cyan,
		// Aliases
		"magenta", magenta,
		"cyan", cyan
	);

	private static int parseRGB(String c)
	{
		if (c.endsWith("%"))
			return (int)(Double.valueOf(c.substring(0, c.length()-1))/100*255);
		else
			return Integer.valueOf(c);
	}

	private static int parseA(String c)
	{
		if (c.endsWith("%"))
			return (int)(Double.valueOf(c.substring(0, c.length()-1))/100*255);
		else
			return (int)(Double.valueOf(c)*255);
	}

	private static Color _fromCSS(String value)
	{
		if (value == null)
			return null;

		if (value.startsWith("#"))
			return _fromrepr(value);
		else if (value.startsWith("rgb(") && value.endsWith(")"))
		{
			String[] components = StringUtils.splitByWholeSeparatorPreserveAllTokens(value.substring(4, value.length()-1), ",");
			if (components.length != 3)
				return null;
			return new Color(
				parseRGB(components[0]),
				parseRGB(components[1]),
				parseRGB(components[2]),
				255
			);
		}
		else if (value.startsWith("rgba(") && value.endsWith(")"))
		{
			String[] components = StringUtils.splitByWholeSeparatorPreserveAllTokens(value.substring(5, value.length()-1), ",");
			if (components.length != 4)
				return null;
			return new Color(
				parseRGB(components[0]),
				parseRGB(components[1]),
				parseRGB(components[2]),
				parseA(components[3])
			);
		}
		else
		{
			// This might return {@code null}, which means error
			return cssColors.get(value);
		}

	}

	public static Color fromCSS(String value)
	{
		Color color = _fromCSS(value);
		if (color == null)
			throw new ColorFormatException(Utils.formatMessage("Can't interpret {!r} as css value", value));
		return color;
	}

	public static Color fromCSS(String value, Color defaultValue)
	{
		Color color = _fromCSS(value);
		if (color == null)
			return defaultValue;
		return color;
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

	public ArrayList<Double> hls()
	{
		int maxc = NumberUtils.max((int)r, (int)g, (int)b);
		int minc = NumberUtils.min((int)r, (int)g, (int)b);

		double dmaxc = maxc/255.;
		double dminc = minc/255.;

		double l = (dminc+dmaxc)/2.0;

		if (minc == maxc)
		{
			ArrayList retVal = new ArrayList(3);
			retVal.add(0.0);
			retVal.add(l);
			retVal.add(0.0);
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

		ArrayList retVal = new ArrayList(3);
		retVal.add(h);
		retVal.add(l);
		retVal.add(s);
		return retVal;
	}

	public ArrayList<Double> hlsa()
	{
		ArrayList retVal = hls();
		retVal.add(a/255.);
		return retVal;
	}

	public ArrayList<Double> hsv()
	{
		int maxc = NumberUtils.max((int)r, (int)g, (int)b);
		int minc = NumberUtils.min((int)r, (int)g, (int)b);

		double dmaxc = maxc/255.;
		double dminc = minc/255.;

		double v = dmaxc;
		if (minc == maxc)
		{
			ArrayList retVal = new ArrayList(3);
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

		ArrayList retVal = new ArrayList(3);
		retVal.add(h);
		retVal.add(s);
		retVal.add(v);
		return retVal;
	}

	public ArrayList<Double> hsva()
	{
		ArrayList<Double> retVal = hsv();
		retVal.add(a/255.);
		return retVal;
	}

	public double hue()
	{
		ArrayList<Double> hls = hls();
		return hls.get(0);
	}

	public double light()
	{
		int maxc = NumberUtils.max((int)r, (int)g, (int)b);
		int minc = NumberUtils.min((int)r, (int)g, (int)b);

		double dmaxc = maxc/255.;
		double dminc = minc/255.;

		return (dminc+dmaxc)/2.0;
	}

	public double sat()
	{
		ArrayList<Double> hls = hls();
		return hls.get(2);
	}

	public double lum()
	{
		return (0.2126 * ((int)r) + 0.7152 * ((int)g) + 0.0722 * ((int)b))/255.;
	}

	public Color withhue(double hue)
	{
		ArrayList<Double> v = hlsa();
		return fromhls(hue, v.get(1), v.get(2), v.get(3));
	}

	public Color withlight(double light)
	{
		ArrayList<Double> v = hlsa();
		return fromhls(v.get(0), light, v.get(2), v.get(3));
	}

	public Color withsat(double sat)
	{
		ArrayList<Double> v = hlsa();
		return fromhls(v.get(0), v.get(1), sat, v.get(3));
	}

	public Color witha(int a)
	{
		return new Color(r, g, b, a);
	}

	public Color abslight(double f)
	{
		ArrayList<Double> v = hlsa();
		return fromhls(v.get(0), v.get(1)+f, v.get(2), v.get(3));
	}

	public Color rellight(double f)
	{
		ArrayList<Double> v = hlsa();
		double newlum = v.get(1);
		if (f > 0)
			newlum += (1-newlum)*f;
		else if (f < 0)
			newlum += newlum*f;
		return fromhls(v.get(0), newlum, v.get(2), v.get(3));
	}

	private double interpolate(double lower, double upper, double factor)
	{
		return factor*upper + (1.0-factor) * lower;
	}

	public Color withlum(double lum)
	{
		double lum_old = lum();
		if (lum_old == 0.0 || lum_old == 1.0)
		{
			int v = (int)(lum*255.);
			return new Color(v, v, v, a);
		}
		else if (lum > lum_old)
		{
			double f = (lum-lum_old)/(1.0-lum_old);
			return new Color(
				(int)interpolate(r, 255., f),
				(int)interpolate(g, 255., f),
				(int)interpolate(b, 255., f),
				a
			);
		}
		else if (lum < lum_old)
		{
			double f = lum/lum_old;
			return new Color(
				(int)interpolate(0., r, f),
				(int)interpolate(0., g, f),
				(int)interpolate(0., b, f),
				a
			);
		}
		else
			return this;
	}

	public Color abslum(double f)
	{
		return withlum(lum() + f);
	}

	public Color rellum(double f)
	{
		double lum = lum();
		if (f > 0)
			lum += (1.0-lum)*f;
		else if (f < 0)
			lum += lum*f;
		return withlum(lum);
	}

	public Color invert(double f)
	{
		double invf = 1.0 - f;
		return new Color(
			(int)(invf * ((int)r) + f * (255-((int)r))),
			(int)(invf * ((int)g) + f * (255-((int)g))),
			(int)(invf * ((int)b) + f * (255-((int)b))),
			a
		);
	}

	// Collection interface
	@Override
	public boolean add(Object o)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o)
	{
		if (o == null || !(o instanceof Integer))
			return false;
		int ov = ((Integer)o);

		return ((r == ov) || (g == ov) || (b == ov) || (a == ov));
	}

	@Override
	public boolean containsAll(Collection c)
	{
		for (Object o : c)
		{
			if (!contains(o))
				return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof Color))
			return false;
		Color co = (Color)o;
		return ((r == co.r) && (g == co.g) && (b == co.b) && (a == co.a));
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}

	@Override
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

		@Override
		public boolean hasNext()
		{
			return index < 4;
		}

		@Override
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

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException("Colors don't support component removal!");
		}
	};

	@Override
	public Iterator iterator()
	{
		return new ColorIterator();
	}

	@Override
	public boolean remove(Object o)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection c)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray()
	{
		Object[] retVal = new Object[4];
		retVal[0] = Integer.valueOf(r);
		retVal[1] = Integer.valueOf(g);
		retVal[2] = Integer.valueOf(b);
		retVal[3] = Integer.valueOf(a);
		return retVal;
	}

	@Override
	public Object[] toArray(Object[] a)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int size()
	{
		return 4;
	}

	@Override
	public int lenUL4(EvaluationContext context)
	{
		return 4;
	}

	@Override
	public Object getItemUL4(EvaluationContext context, Object key)
	{
		if (key instanceof Boolean)
			return (int)(((Boolean)key).booleanValue() ? g : r);
		else if (key instanceof Number)
			return getItemInteger(((Number)key).intValue());
		else if (key instanceof Slice)
		{
			Slice slice = (Slice)key;
			int startIndex = slice.getStartIndex(4);
			int endIndex = slice.getStopIndex(4);
			if (endIndex < startIndex)
				endIndex = startIndex;
			ArrayList<Integer> result = new ArrayList<Integer>(endIndex - startIndex);
			for (int index = startIndex; index < endIndex; ++index)
				result.set(index - startIndex, getItemInteger(index));
			return result;
		}
		else
			throw new ArgumentTypeMismatchException("color[{!t}]", key);
	}

	private int getItemInteger(int index)
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

	protected static Set<String> attributes = makeSet("r", "g", "b", "a", "hue", "light", "sat", "lum", "hls", "hlsa", "hsv", "hsva", "invert", "combine", "withhue", "witha", "withlight", "abslight", "rellight", "withsat", "withlum", "abslum", "rellum");

	@Override
	public Set<String> dirUL4(EvaluationContext context)
	{
		return attributes;
	}

	private static final Signature signatureWithA = new Signature().addBoth("a");
	private static final Signature signatureWithHue = new Signature().addBoth("hue");
	private static final Signature signatureWithLight = new Signature().addBoth("light");
	private static final Signature signatureWithSat = new Signature().addBoth("sat");
	private static final Signature signatureWithF = new Signature().addBoth("f");
	private static final Signature signatureWithLum = new Signature().addBoth("lum");
	private static final Signature signatureInvert = new Signature().addBoth("f", 1.0);
	private static final Signature signatureCombine = new Signature().addBoth("r", null).addBoth("g", null).addBoth("b", null).addBoth("a", null);

	private static final MethodDescriptor<Color> methodR = new MethodDescriptor<Color>(type, "r", Signature.noParameters);
	private static final MethodDescriptor<Color> methodG = new MethodDescriptor<Color>(type, "g", Signature.noParameters);
	private static final MethodDescriptor<Color> methodB = new MethodDescriptor<Color>(type, "b", Signature.noParameters);
	private static final MethodDescriptor<Color> methodA = new MethodDescriptor<Color>(type, "a", Signature.noParameters);
	private static final MethodDescriptor<Color> methodHue = new MethodDescriptor<Color>(type, "hue", Signature.noParameters);
	private static final MethodDescriptor<Color> methodLight = new MethodDescriptor<Color>(type, "light", Signature.noParameters);
	private static final MethodDescriptor<Color> methodSat = new MethodDescriptor<Color>(type, "sat", Signature.noParameters);
	private static final MethodDescriptor<Color> methodLum = new MethodDescriptor<Color>(type, "lum", Signature.noParameters);
	private static final MethodDescriptor<Color> methodHLS = new MethodDescriptor<Color>(type, "hls", Signature.noParameters);
	private static final MethodDescriptor<Color> methodHLSA = new MethodDescriptor<Color>(type, "hlsa", Signature.noParameters);
	private static final MethodDescriptor<Color> methodHSV = new MethodDescriptor<Color>(type, "hsv", Signature.noParameters);
	private static final MethodDescriptor<Color> methodHSVA = new MethodDescriptor<Color>(type, "hsva", Signature.noParameters);
	private static final MethodDescriptor<Color> methodWithA = new MethodDescriptor<Color>(type, "witha", signatureWithA);
	private static final MethodDescriptor<Color> methodWithHue = new MethodDescriptor<Color>(type, "withhue", signatureWithHue);
	private static final MethodDescriptor<Color> methodWithLight = new MethodDescriptor<Color>(type, "withlight", signatureWithLight);
	private static final MethodDescriptor<Color> methodAbsLight = new MethodDescriptor<Color>(type, "abslight", signatureWithF);
	private static final MethodDescriptor<Color> methodRelLight = new MethodDescriptor<Color>(type, "rellight", signatureWithF);
	private static final MethodDescriptor<Color> methodWithSat = new MethodDescriptor<Color>(type, "withsat", signatureWithSat);
	private static final MethodDescriptor<Color> methodWithLum = new MethodDescriptor<Color>(type, "withlum", signatureWithLum);
	private static final MethodDescriptor<Color> methodAbsLum = new MethodDescriptor<Color>(type, "abslum", signatureWithF);
	private static final MethodDescriptor<Color> methodRelLum = new MethodDescriptor<Color>(type, "rellum", signatureWithF);
	private static final MethodDescriptor<Color> methodInvert = new MethodDescriptor<Color>(type, "invert", signatureInvert);
	private static final MethodDescriptor<Color> methodCombine = new MethodDescriptor<Color>(type, "combine", signatureCombine);

	@Override
	public Object callAttrUL4(EvaluationContext context, String key, List<Object> args, Map<String, Object> kwargs)
	{
		switch (key)
		{
			case "r":
				BoundArguments boundRArgs = methodR.bindArguments(args, kwargs);
				return (int)r;
			case "g":
				BoundArguments boundGArgs = methodG.bindArguments(args, kwargs);
				return (int)g;
			case "b":
				BoundArguments boundBArgs = methodB.bindArguments(args, kwargs);
				return (int)b;
			case "a":
				BoundArguments boundAArgs = methodA.bindArguments(args, kwargs);
				return (int)a;
			case "hue":
				BoundArguments boundHueArgs = methodHue.bindArguments(args, kwargs);
				return hue();
			case "light":
				BoundArguments boundLightArgs = methodLight.bindArguments(args, kwargs);
				return light();
			case "sat":
				BoundArguments boundSatArgs = methodSat.bindArguments(args, kwargs);
				return sat();
			case "lum":
				BoundArguments boundLumArgs = methodLum.bindArguments(args, kwargs);
				return lum();
			case "hls":
				BoundArguments boundHLSArgs = methodHLS.bindArguments(args, kwargs);
				return hls();
			case "hlsa":
				BoundArguments boundHLSAArgs = methodHLSA.bindArguments(args, kwargs);
				return hlsa();
			case "hsv":
				BoundArguments boundHSVArgs = methodHSV.bindArguments(args, kwargs);
				return hsv();
			case "hsva":
				BoundArguments boundHSVAArgs = methodHSVA.bindArguments(args, kwargs);
				return hsva();
			case "witha":
				BoundArguments boundWithAArgs = methodWithA.bindArguments(args, kwargs);
				return witha(boundWithAArgs.getInt(0));
			case "withhue":
				BoundArguments boundWithHueArgs = methodWithHue.bindArguments(args, kwargs);
				return withhue(boundWithHueArgs.getDouble(0));
			case "withlight":
				BoundArguments boundWithLightArgs = methodWithLight.bindArguments(args, kwargs);
				return withlight(boundWithLightArgs.getDouble(0));
			case "abslight":
				BoundArguments boundAbsLightArgs = methodAbsLight.bindArguments(args, kwargs);
				return abslight(boundAbsLightArgs.getDouble(0));
			case "rellight":
				BoundArguments boundRelLightArgs = methodRelLight.bindArguments(args, kwargs);
				return rellight(boundRelLightArgs.getDouble(0));
			case "withsat":
				BoundArguments boundWithSatArgs = methodWithSat.bindArguments(args, kwargs);
				return withsat(boundWithSatArgs.getDouble(0));
			case "withlum":
				BoundArguments boundWithLumArgs = methodWithLum.bindArguments(args, kwargs);
				return withlum(boundWithLumArgs.getDouble(0));
			case "abslum":
				BoundArguments boundAbsLumArgs = methodAbsLum.bindArguments(args, kwargs);
				return abslum(boundAbsLumArgs.getDouble(0));
			case "rellum":
				BoundArguments boundRelLumArgs = methodRelLum.bindArguments(args, kwargs);
				return rellum(boundRelLumArgs.getDouble(0));
			case "invert":
				BoundArguments boundInvertArgs = methodInvert.bindArguments(args, kwargs);
				return invert(boundInvertArgs.getDouble(0));
			case "combine":
				BoundArguments boundCombineArgs = methodCombine.bindArguments(args, kwargs);
				int newR = boundCombineArgs.getInt(0, r);
				int newG = boundCombineArgs.getInt(1, g);
				int newB = boundCombineArgs.getInt(2, b);
				int newA = boundCombineArgs.getInt(3, a);

				return new Color(newR, newG, newB, newA);
			default:
				return UL4Instance.super.callAttrUL4(context, key, args, kwargs);
		}
	}

	@Override
	public Object getAttrUL4(EvaluationContext context, String key)
	{
		switch (key)
		{
			case "r":
				return methodR.bindMethod(this);
			case "g":
				return methodG.bindMethod(this);
			case "b":
				return methodB.bindMethod(this);
			case "a":
				return methodA.bindMethod(this);
			case "hue":
				return methodHue.bindMethod(this);
			case "light":
				return methodLight.bindMethod(this);
			case "sat":
				return methodSat.bindMethod(this);
			case "lum":
				return methodLum.bindMethod(this);
			case "hls":
				return methodHLS.bindMethod(this);
			case "hlsa":
				return methodHLSA.bindMethod(this);
			case "hsv":
				return methodHSV.bindMethod(this);
			case "hsva":
				return methodHSVA.bindMethod(this);
			case "witha":
				return methodWithA.bindMethod(this);
			case "withhue":
				return methodWithHue.bindMethod(this);
			case "withlight":
				return methodWithLight.bindMethod(this);
			case "abslight":
				return methodAbsLight.bindMethod(this);
			case "rellight":
				return methodRelLight.bindMethod(this);
			case "withsat":
				return methodWithSat.bindMethod(this);
			case "withlum":
				return methodWithLum.bindMethod(this);
			case "abslum":
				return methodAbsLum.bindMethod(this);
			case "rellum":
				return methodRelLum.bindMethod(this);
			case "invert":
				return methodInvert.bindMethod(this);
			case "combine":
				return methodCombine.bindMethod(this);
			default:
				return UL4Instance.super.getAttrUL4(context, key);
		}
	}
}
