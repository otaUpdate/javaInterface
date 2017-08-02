package net.otaupdate.app.model;

import java.util.Date;

import com.amazonaws.util.DateUtils;

import net.otaupdate.app.sdk.model.UnprovProcessorArrayItem;

public class UnprovisionedProcessorWrapper
{
	private final DeviceTypeWrapper parent;
	private final UnprovProcessorArrayItem upai;
	
	
	public UnprovisionedProcessorWrapper(UnprovProcessorArrayItem upaiIn, DeviceTypeWrapper parentIn)
	{
		this.upai = upaiIn;
		this.parent = parentIn;
	}
	
	
	public String getProcTypeName()
	{
		return this.upai.getTypeName();
	}
	
	
	public String getSerialNumber()
	{
		return this.upai.getSerialNumber();
	}
	
	
	public String getCurrentFwImageName()
	{
		return this.upai.getFwImageName();
	}
	
	
	public Boolean getIsFwUpToDate()
	{
		return this.upai.getIsUpToDate();
	}
	
	
	public Date getLastSeenDate()
	{
		return DateUtils.parseISO8601Date(this.upai.getLastSeenUTC());
	}
}
