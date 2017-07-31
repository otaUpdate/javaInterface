package net.otaupdate.app.model;


import java.util.ArrayList;
import java.util.List;

import net.otaupdate.app.sdk.model.OrganizationArrayItem;


public class OrganizationWrapper
{	
	private final OrganizationArrayItem oai;
	private final List<DeviceTypeWrapper> devTypes = new ArrayList<DeviceTypeWrapper>();
	
	
	public OrganizationWrapper(OrganizationArrayItem oaiIn)
	{
		super();
		this.oai = oaiIn;
	}
	
	
	public void addDevType(DeviceTypeWrapper dtwIn)
	{
		this.devTypes.add(dtwIn);
	}
	
	
	public List<DeviceTypeWrapper> getDevTypes()
	{
		return this.devTypes;
	}
	
	
	public String getName()
	{
		return this.oai.getName();
	}
	
	
	public String getUuid()
	{
		return this.oai.getUuid();
	}
	
	
	@Override
	public String toString()
	{
		return this.getName();
	}
}
