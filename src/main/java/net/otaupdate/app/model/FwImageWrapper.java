package net.otaupdate.app.model;

import java.io.Serializable;

import net.otaupdate.app.sdk.model.FwImageArrayItem;


public class FwImageWrapper implements Serializable
{
	private static final long serialVersionUID = -4483925860222769211L;
	
	
	private final FwImageArrayItem fwai;
	
	
	public FwImageWrapper(FwImageArrayItem fwaiIn)
	{
		this.fwai = fwaiIn;
	}
	
	
	public FwImageArrayItem getModelObject()
	{
		return this.fwai;
	}
	
	
	@Override
	public String toString()
	{
		return this.fwai.getName();
	}
}
