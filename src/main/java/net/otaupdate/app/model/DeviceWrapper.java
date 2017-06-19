package net.otaupdate.app.model;

import java.util.List;

import net.otaupdate.app.sdk.model.DeviceArrayItem;


public class DeviceWrapper
{
	private final DeviceArrayItem dai;
	private final List<ProcessorWrapper> processors;
	
	
	public DeviceWrapper(DeviceArrayItem daiIn, List<ProcessorWrapper> procsIn)
	{
		this.dai = daiIn;
		this.processors = procsIn;
	}
	
	
	public DeviceArrayItem getModelObject()
	{
		return this.dai;
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
