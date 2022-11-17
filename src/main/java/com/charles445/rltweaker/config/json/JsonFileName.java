package com.charles445.rltweaker.config.json;

public enum JsonFileName
{
	lessCollisions("lessCollisions.json"),
	reskillableTransmutation("reskillableTransmutation.json");
	
	private String fileName;
	
	private JsonFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	@Override
	public String toString()
	{
		return this.fileName;
	}
	
	public String get()
	{
		return this.toString();
	}
}
