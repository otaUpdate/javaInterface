package net.otaupdate.app.model;

import net.otaupdate.app.sdk.model.OrganizationArrayItem;

public class OrganizationWrapper
{
	private final OrganizationArrayItem oai;
	
	
	public OrganizationWrapper(OrganizationArrayItem oaiIn)
	{
		this.oai = oaiIn;
	}
	
	
	public OrganizationArrayItem getModelObject()
	{
		return this.oai;
	}
	
	
	@Override
	public String toString()
	{
		return this.oai.getName();
	}
}
