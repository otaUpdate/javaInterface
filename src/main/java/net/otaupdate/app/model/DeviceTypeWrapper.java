package net.otaupdate.app.model;

import java.util.ArrayList;
import java.util.List;

import net.otaupdate.app.sdk.model.DeviceTypeArrayItem;


public class DeviceTypeWrapper
{
	private final DeviceTypeArrayItem dai;
	private final List<ProcessorTypeWrapper> processors = new ArrayList<ProcessorTypeWrapper>();
	private final OrganizationWrapper parent;
	
	
	public DeviceTypeWrapper(DeviceTypeArrayItem daiIn, OrganizationWrapper parentIn)
	{
		this.dai = daiIn;
		this.parent = parentIn;
	}
	
	
	public DeviceTypeArrayItem getModelObject()
	{
		return this.dai;
	}
	
	
	public OrganizationWrapper getParent()
	{
		return this.parent;
	}
	
	
	public void addProcessor(ProcessorTypeWrapper itemIn)
	{
		this.processors.add(itemIn);
	}
	
	
	public List<ProcessorTypeWrapper> getProcessors()
	{
		return this.processors;
	}
	
	
	@Override
	public String toString()
	{
		return this.dai.getName();
	}
}
