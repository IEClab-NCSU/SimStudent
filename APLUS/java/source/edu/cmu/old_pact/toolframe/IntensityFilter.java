package edu.cmu.old_pact.toolframe;

import java.awt.image.RGBImageFilter;

public class IntensityFilter extends RGBImageFilter {
	public IntensityFilter()
	{
		canFilterIndexColorModel=true;
	}
	
	
	public int filterRGB(int x,int y,int rgb)
	{
		int r=( rgb & 0xff0000) >>16;
		int g=( rgb & 0xff00) >>8;
		int b=( rgb & 0xff);
		int i=(((r+g+b)/3) & 0xff);
		
		return ((rgb & 0xff000000) | (i << 16) | (i << 8) | i);
	}
}			


