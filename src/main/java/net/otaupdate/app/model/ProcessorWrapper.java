package net.otaupdate.app.model;

import java.util.List;

import net.otaupdate.app.sdk.model.ProcessorArrayItem;


public class ProcessorWrapper
{
	private final ProcessorArrayItem pai;
	private final List<FwImageWrapper> fwImages;
	
	
	public ProcessorWrapper(ProcessorArrayItem paiIn, List<FwImageWrapper> fwImagesIn)
	{
		this.pai = paiIn;
		this.fwImages = fwImagesIn;
	}
	
	
	public ProcessorArrayItem getModelObject()
	{
		return this.pai;
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
