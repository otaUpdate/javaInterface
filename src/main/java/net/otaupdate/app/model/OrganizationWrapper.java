package net.otaupdate.app.model;

import java.util.List;

import net.otaupdate.app.sdk.model.OrganizationArrayItem;

public class OrganizationWrapper
{
	private final OrganizationArrayItem oai;
	private final List<DeviceWrapper> devices;
	
	
	public OrganizationWrapper(OrganizationArrayItem oaiIn, List<DeviceWrapper> devicesIn)
	{
		this.oai = oaiIn;
		this.devices = devicesIn;
	}
	
	
	public OrganizationArrayItem getModelObject()
	{
		return this.oai;
	}
	
	
	public List<DeviceWrapper> getDevices()
	{
		return this.devices;
	}
	
	
	@Override
	public String toString()
	{
		return this.oai.getName();
	}
}
