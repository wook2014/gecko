/*
 * Copyright 2014 Sascha Winter, Tobias Mann, Hans-Martin Haase, Leon Kuchenbecker and Katharina Jahn
 *
 * This file is part of Gecko3.
 *
 * Gecko3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gecko3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Gecko3.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.unijena.bioinf.gecko3.gui.util;

import java.awt.*;

public class ColorUtils {

	/**
	 * This sample code is made available as part of the book "Digital Image
	 * Processing - An Algorithmic Introduction using Java" by Wilhelm Burger
	 * and Mark J. Burge, Copyright (C) 2005-2008 Springer-Verlag Berlin, 
	 * Heidelberg, New York.
	 * Note that this code comes with absolutely no warranty of any kind.
	 * See http://www.imagingbook.com for details and licensing conditions.
	 * 
	 * This software is released under the terms of the GNU Lesser General 
	 * Public License (LGPL).
	 * 
	 * Date: 2007/11/10
	 */

	/** Methods for converting between RGB and HSV color spaces.
	*/


	public static float[] RGBtoHSV (int R, int G, int B, float[] HSV) {
		// R,G,B in [0,255]
		float H = 0, S = 0, V;
		float cMax = 255.0f;
		int cHi = Math.max(R,Math.max(G,B));	// highest color value
		int cLo = Math.min(R,Math.min(G,B));	// lowest color value
		int cRng = cHi - cLo;				    // color range
		
		// compute value V
		V = cHi / cMax;
		
		// compute saturation S
		if (cHi > 0)
			S = (float) cRng / cHi;

		// compute hue H
		if (cRng > 0) {	// hue is defined only for color pixels
			float rr = (float)(cHi - R) / cRng;
			float gg = (float)(cHi - G) / cRng;
			float bb = (float)(cHi - B) / cRng;
			float hh;
			if (R == cHi)                      // r is highest color value
				hh = bb - gg;
			else if (G == cHi)                 // g is highest color value
				hh = rr - bb + 2.0f;
			else                               // b is highest color value
				hh = gg - rr + 4.0f;
			if (hh < 0)
				hh= hh + 6;
			H = hh / 6;
		}
		
		if (HSV == null)	// create a new HSV array if needed
			HSV = new float[3];
		HSV[0] = H; HSV[1] = S; HSV[2] = V;
		return HSV;
	}
	
	public static int HSVtoRGB (float h, float s, float v) {
		// h,s,v in [0,1]
		float rr, gg, bb;
		float hh = (6 * h) % 6;                 
		int   c1 = (int) hh;                     
		float c2 = hh - c1;
		float x = (1 - s) * v;
		float y = (1 - (s * c2)) * v;
		float z = (1 - (s * (1 - c2))) * v;	
		switch (c1) {
			case 0: rr=v; gg=z; bb=x; break;
			case 1: rr=y; gg=v; bb=x; break;
			case 2: rr=x; gg=v; bb=z; break;
			case 3: rr=x; gg=y; bb=v; break;
			case 4: rr=z; gg=x; bb=v; break;
			case 5: rr=v; gg=x; bb=y; break;
            default: throw new RuntimeException("Ooops! That was not supposed to happen!");
		}
		int N = 256;
		int r = Math.min(Math.round(rr*N),N-1);
		int g = Math.min(Math.round(gg*N),N-1);
		int b = Math.min(Math.round(bb*N),N-1);
		// create int-packed RGB-color:
		return ((r&0xff)<<16) | ((g&0xff)<<8) | b&0xff;
	}

	public static Color getTranslucentColor(Color color) {
		return getTranslucentColor(color, 0.5f);
	}
    /**
     * Returns the given color but with an alpha value of 0.5
     * @param color
     * @return
     */
    public static Color getTranslucentColor(Color color, float alpha) {
        float[] rgb = color.getRGBColorComponents(null);
        return new Color(rgb[0], rgb[1], rgb[2], alpha);
    }
}
