package net.otaupdate.app.model;

import java.util.Date;

import com.amazonaws.util.DateUtils;

import net.otaupdate.app.sdk.model.DeviceArrayItem;
import net.otaupdate.app.sdk.model.ProcessorInfoItem;


public class DeviceInstanceWrapper
{
	private final DeviceTypeWrapper parent;
	private final DeviceArrayItem dai;
	
	
	public DeviceInstanceWrapper(DeviceArrayItem daiIn, DeviceTypeWrapper parentIn)
	{
		this.dai = daiIn;
		this.parent = parentIn;
	}
	
	
	public String getDeviceSerialNumber()
	{
		return this.dai.getSerialNumber();
	}
	
	
	public int getNumberOfProcessors()
	{
		return this.dai.getProcessorInfo().size();
	}
	
	
	public String getSerialNumberForProcessorAtIndex(int indexIn)
	{

		if( (indexIn < 0) || (indexIn > this.getNumberOfProcessors()) ) return null;
		ProcessorInfoItem currProc = this.dai.getProcessorInfo().get(indexIn);
		
		return currProc.getSerialNumber();
	}
	
	
	public String getCurrentFwImageNameForProcessorAtIndex(int indexIn)
	{

		if( (indexIn < 0) || (indexIn > this.getNumberOfProcessors()) ) return null;
		ProcessorInfoItem currProc = this.dai.getProcessorInfo().get(indexIn);
		
		return currProc.getFwImageName();
	}
	
	
	public Boolean getIsFwUpToDateForProcessorAtIndex(int indexIn)
	{

		if( (indexIn < 0) || (indexIn > this.getNumberOfProcessors()) ) return null;
		ProcessorInfoItem currProc = this.dai.getProcessorInfo().get(indexIn);
		
		return currProc.getIsUpToDate();
	}
	
	
	public Date getLastSeenDateForProcessorAtIndex(int indexIn)
	{

		if( (indexIn < 0) || (indexIn > this.getNumberOfProcessors()) ) return null;
		ProcessorInfoItem currProc = this.dai.getProcessorInfo().get(indexIn);
		
		return (currProc.getLastSeenUTC() != null) ? DateUtils.parseISO8601Date(currProc.getLastSeenUTC()) : null;
	}
}
