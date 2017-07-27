package net.otaupdate.app.model;

import java.util.ArrayList;
import java.util.List;

import net.otaupdate.app.sdk.model.ProcTypeArrayItem;


public class ProcessorTypeWrapper
{
	private final ProcTypeArrayItem ptai;
	private final List<FwImageWrapper> fwImages = new ArrayList<FwImageWrapper>();
	private final DeviceTypeWrapper parent;
	
	
	public ProcessorTypeWrapper(ProcTypeArrayItem paiIn, DeviceTypeWrapper parentIn)
	{
		this.ptai = paiIn;
		this.parent = parentIn;
	}
	
	
	public ProcTypeArrayItem getModelObject()
	{
		return this.ptai;
	}
	
	
	public DeviceTypeWrapper getParent()
	{
		return this.parent;
	}
	
	
	public void addFirmwareImage(FwImageWrapper itemIn)
	{
		this.fwImages.add(itemIn);
	}
	
	
	public List<FwImageWrapper> getFirmwareImages()
	{
		return this.fwImages;
	}
	
	
	public FwImageWrapper getFwImageByUuid(String uuidIn)
	{
		for( FwImageWrapper currFwImage : this.fwImages )
		{
			if( currFwImage.getModelObject().getUuid().equals(uuidIn) ) return currFwImage;
		}
		
		return null;
	}
	
	
	@Override
	public String toString()
	{
		return this.ptai.getName();
	}
}
