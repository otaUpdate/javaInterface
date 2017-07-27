package net.otaupdate.app.model;

import java.io.Serializable;

import net.otaupdate.app.sdk.model.FwImageArrayItem;


public class FwImageWrapper implements Serializable
{
	private static final long serialVersionUID = -4483925860222769211L;
	
	
	private final FwImageArrayItem fwai;
	private final ProcessorTypeWrapper parent;
	
	
	public FwImageWrapper(FwImageArrayItem fwaiIn, ProcessorTypeWrapper parentIn)
	{
		this.fwai = fwaiIn;
		this.parent = parentIn;
	}
	
	
	public FwImageArrayItem getModelObject()
	{
		return this.fwai;
	}
	
	
	public ProcessorTypeWrapper getParent()
	{
		return this.parent;
	}
	
	
	@Override
	public String toString()
	{
		return this.fwai.getName();
	}
}
