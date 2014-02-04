package com.streamsdk.util;

public class ImageSize
{
	int _width;
	int _height;

	public ImageSize(int width, int height)
	{
		_width = width;
		_height = height;
	}

	public int getWidth()
	{
		return _width;
	}

	public int getHeight()
	{
		return _height;
	}

	public boolean equals(Object object)
	{
		return object instanceof ImageSize && ((ImageSize) object).getWidth() == getWidth() && ((ImageSize) object).getHeight() == getHeight();
	}

	public String toString()
	{
		return "ImageSize : " + getWidth() + "," + getHeight();
	}
}