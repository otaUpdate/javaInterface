package net.otaupdate.app.model;

import java.util.ArrayList;
import java.util.List;

import net.otaupdate.app.sdk.model.ProcessorArrayItem;


public class ProcessorWrapper
{
	private final ProcessorArrayItem pai;
	private final List<FwImageWrapper> fwImages = new ArrayList<FwImageWrapper>();
	private final DeviceWrapper parent;
	
	
	public ProcessorWrapper(ProcessorArrayItem paiIn, DeviceWrapper parentIn)
	{
		this.pai = paiIn;
		this.parent = parentIn;
	}
	
	
	public ProcessorArrayItem getModelObject()
	{
		return this.pai;
	}
	
	
	public DeviceWrapper getParent()
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
		return this.pai.getName();
	}
}
