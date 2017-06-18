package net.otaupdate.app.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.opensdk.SdkRequestConfig;

import net.otaupdate.app.AuthorizationManager;
import net.otaupdate.app.WebServicesCommon;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidUsersUserEmailRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidUsersUserEmailResult;
import net.otaupdate.app.sdk.model.EmailAddress;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidUsersRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidUsersResult;
import net.otaupdate.app.sdk.model.GetOrgsRequest;
import net.otaupdate.app.sdk.model.GetOrgsResult;
import net.otaupdate.app.sdk.model.OrganizationArrayItem;
import net.otaupdate.app.sdk.model.OrganizationUserArrayItem;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidUsersRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidUsersResult;
import net.otaupdate.app.util.Dispatch;


public class ModelManager
{
	private static final ModelManager SINGLETON = new ModelManager();
	
	
	public interface GetOrganizationCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, List<OrganizationWrapper> itemsIn);
	}
	
	
	public interface GetUsersForOrganizationCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, List<OrganizationUserArrayItem> itemsIn);
	}
	
	
	public interface AddRemoveOrganizationUserCallback
	{
		public void onCompletion(boolean wasSuccessfulIn);
	}
	
	
	private final Logger logger = LogManager.getLogger(this.getClass());
	
	
	private ModelManager()
	{
	}
	
	
	public void getOrganizations(GetOrganizationCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				List<OrganizationWrapper> retVal = null;
				boolean wasSuccessful = false;
				try
				{
					GetOrgsResult result = WebServicesCommon.client.getOrgs(new GetOrgsRequest().sdkRequestConfig(
							SdkRequestConfig.builder()
							.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
							.build()
							));
					List<OrganizationArrayItem> oais = result.getOrganizationArray();
					
					// wrap the amazon-provided object nicely
					retVal = new ArrayList<OrganizationWrapper>();
					for( OrganizationArrayItem currOai : oais )
					{
						retVal.add(new OrganizationWrapper(currOai));
					}
					
					wasSuccessful = true;
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("getOrganizations error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful, retVal);
			}
		});
	}
	
	
	public void getUsersForOrganization(String organizationUuidIn, GetUsersForOrganizationCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				List<OrganizationUserArrayItem> retVal = null;
				boolean wasSuccessful = false;
				try
				{
					GetOrgsOrganizationUuidUsersResult result = WebServicesCommon.client.getOrgsOrganizationUuidUsers(new GetOrgsOrganizationUuidUsersRequest()
							{{
								setOrganizationUuid(organizationUuidIn);
							}}
							.sdkRequestConfig(SdkRequestConfig.builder()
							.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
							.build()
							));
					retVal = result.getOrganizationUserArray();
					wasSuccessful = true;
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("getUsersForOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful, retVal);
			}
		});
	}
	
	
	public void addUserToOrganization(String organizationUuidIn, String emailAddressIn, AddRemoveOrganizationUserCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					PostOrgsOrganizationUuidUsersResult result = WebServicesCommon.client.postOrgsOrganizationUuidUsers(new PostOrgsOrganizationUuidUsersRequest()
							{{
								setEmailAddress(new EmailAddress() {{
									setEmail(emailAddressIn);
								}});
								setOrganizationUuid(organizationUuidIn);
							}}
							.sdkRequestConfig(SdkRequestConfig.builder()
							.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
							.build()
							));
					
					// if we made it here without exception, we're good
					wasSuccessful = true;
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("addUserToOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void removeUserFromOrganization(String organizationUuidIn, String emailAddressIn, AddRemoveOrganizationUserCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					DeleteOrgsOrganizationUuidUsersUserEmailResult result = WebServicesCommon.client.deleteOrgsOrganizationUuidUsersUserEmail(new DeleteOrgsOrganizationUuidUsersUserEmailRequest()
							{{
								setUserEmail(emailAddressIn);
								setOrganizationUuid(organizationUuidIn);
							}}
							.sdkRequestConfig(SdkRequestConfig.builder()
							.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
							.build()
							));
					
					// if we made it here without exception, we're good
					wasSuccessful = true;
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("removeUserFromOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	
	public static ModelManager getSingleton()
	{
		return SINGLETON;
	}
}
