package net.otaupdate.app.model;

import java.util.ArrayList;
import java.util.List;

import net.otaupdate.app.sdk.model.OrganizationArrayItem;

public class OrganizationWrapper
{
	private final OrganizationArrayItem oai;
	private final List<DeviceTypeWrapper> devices = new ArrayList<DeviceTypeWrapper>();
	
	
	public OrganizationWrapper(OrganizationArrayItem oaiIn)
	{
		this.oai = oaiIn;
	}
	
	
	public OrganizationArrayItem getModelObject()
	{
		return this.oai;
	}
	
	
	public void addDevice(DeviceTypeWrapper itemIn)
	{
		this.devices.add(itemIn);
	}
	
	
	public List<DeviceTypeWrapper> getDevices()
	{
		return this.devices;
	}
	
	
	@Override
	public String toString()
	{
		return this.oai.getName();
	}
}
