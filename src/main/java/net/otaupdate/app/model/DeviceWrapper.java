package net.otaupdate.app.model;

import java.util.ArrayList;
import java.util.List;

import net.otaupdate.app.sdk.model.DeviceArrayItem;


public class DeviceWrapper
{
	private final DeviceArrayItem dai;
	private final List<ProcessorWrapper> processors = new ArrayList<ProcessorWrapper>();
	private final OrganizationWrapper parent;
	
	
	public DeviceWrapper(DeviceArrayItem daiIn, OrganizationWrapper parentIn)
	{
		this.dai = daiIn;
		this.parent = parentIn;
	}
	
	
	public DeviceArrayItem getModelObject()
	{
		return this.dai;
	}
	
	
	public OrganizationWrapper getParent()
	{
		return this.parent;
	}
	
	
	public void addProcessor(ProcessorWrapper itemIn)
	{
		this.processors.add(itemIn);
	}
	
	
	public List<ProcessorWrapper> getProcessors()
	{
		return this.processors;
	}
	
	
	@Override
	public String toString()
	{
		return this.dai.getName();
	}
}
