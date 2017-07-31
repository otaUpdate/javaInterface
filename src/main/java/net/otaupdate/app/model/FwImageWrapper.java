package net.otaupdate.app.model;


import java.util.List;

import net.otaupdate.app.sdk.model.FwImageArrayItem;


public class FwImageWrapper
{	
	private final ProcessorTypeWrapper parent;
	private final FwImageArrayItem fwai;
	
	
	public FwImageWrapper(FwImageArrayItem fwaiIn, ProcessorTypeWrapper parentIn)
	{
		this.fwai = fwaiIn;
		this.parent = parentIn;
	}
	
	
	public List<FwImageWrapper> getListOfPeerFwImages()
	{
		return this.parent.getFwImages();
	}
	
	
	public String getName()
	{
		return this.fwai.getName();
	}
	
	
	public void setName(String nameIn)
	{
		this.fwai.setName(nameIn);
	}
	
	
	public String getToVersionUuid()
	{
		return this.fwai.getToVersionUuid();
	}
	
	
	public void setToVersionUuid(String toVersionUuidIn)
	{
		this.fwai.setToVersionUuid(toVersionUuidIn);
	}
	
	
	public String getOrgUuid()
	{
		return this.parent.getOrgUuid();
	}
	
	
	public String getDevTypeUuid()
	{
		return this.parent.getDevTypeUuid();
	}
	
	
	public String getProcTypeUuid()
	{
		return this.parent.getUuid();
	}
	
	
	public String getUuid()
	{
		return this.fwai.getUuid();
	}
	
	
	@Override
	public String toString()
	{
		return this.fwai.getName();
	}
}
