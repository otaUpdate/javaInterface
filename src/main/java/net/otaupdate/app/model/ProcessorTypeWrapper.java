package net.otaupdate.app.model;


import java.util.ArrayList;
import java.util.List;

import net.otaupdate.app.sdk.model.ProcTypeArrayItem;


public class ProcessorTypeWrapper
{
	private final DeviceTypeWrapper parent;
	private final ProcTypeArrayItem ptai;
	private final List<FwImageWrapper> fwImages = new ArrayList<FwImageWrapper>();
	
	
	public ProcessorTypeWrapper(ProcTypeArrayItem paiIn, DeviceTypeWrapper parentIn)
	{
		this.ptai = paiIn;
		this.parent = parentIn;
	}
	
	
	public void addFwImage(FwImageWrapper dtwIn)
	{
		this.fwImages.add(dtwIn);
	}
	
	
	public List<FwImageWrapper> getFwImages()
	{
		return this.fwImages;
	}
	
	
	public String getName()
	{
		return this.ptai.getName();
	}
	
	
	public void setName(String nameIn)
	{
		this.ptai.setName(nameIn);
	}
	
	
	public String getLatestFwImageUuid()
	{
		return this.ptai.getLatestFirmwareUuid();
	}
	
	
	public void setLatestFwImageUuid(String lfuIn)
	{
		this.ptai.setLatestFirmwareUuid(lfuIn);
	}
	
	
	public String getOrgUuid()
	{
		return this.parent.getOrgUuid();
	}
	
	
	public String getDevTypeUuid()
	{
		return this.parent.getUuid();
	}
	
	
	public String getUuid()
	{
		return this.ptai.getUuid();
	}
	
	
	@Override
	public String toString()
	{
		return this.ptai.getName();
	}
}
