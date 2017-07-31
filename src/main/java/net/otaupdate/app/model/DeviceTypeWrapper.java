package net.otaupdate.app.model;


import java.util.ArrayList;
import java.util.List;

import net.otaupdate.app.sdk.model.DeviceTypeArrayItem;


public class DeviceTypeWrapper
{
	private final OrganizationWrapper parent;
	private final DeviceTypeArrayItem dai;
	private final List<ProcessorTypeWrapper> procTypes = new ArrayList<ProcessorTypeWrapper>();
	
	
	public DeviceTypeWrapper(DeviceTypeArrayItem daiIn, OrganizationWrapper parentIn)
	{
		this.dai = daiIn;
		this.parent = parentIn;
	}
	
	
	public void addProcType(ProcessorTypeWrapper dtwIn)
	{
		this.procTypes.add(dtwIn);
	}
	
	
	public List<ProcessorTypeWrapper> getProcTypes()
	{
		return this.procTypes;
	}
	
	
	public String getName()
	{
		return this.dai.getName();
	}
	
	
	public void setName(String nameIn)
	{
		this.dai.setName(nameIn);
	}
	
	
	public String getOrgUuid()
	{
		return this.parent.getUuid();
	}
	
	
	public String getUuid()
	{
		return this.dai.getUuid();
	}
	
	
	@Override
	public String toString()
	{
		return this.dai.getName();
	}
}
