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
import net.otaupdate.app.sdk.model.DeviceArrayItem;
import net.otaupdate.app.sdk.model.EmailAddress;
import net.otaupdate.app.sdk.model.FwImageArrayItem;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesResult;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidDevicesDeviceUuidProcessorsRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidDevicesDeviceUuidProcessorsResult;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidDevicesRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidDevicesResult;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidUsersRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrganizationUuidUsersResult;
import net.otaupdate.app.sdk.model.GetOrgsRequest;
import net.otaupdate.app.sdk.model.GetOrgsResult;
import net.otaupdate.app.sdk.model.OrganizationArrayItem;
import net.otaupdate.app.sdk.model.OrganizationUserArrayItem;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidUsersRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidUsersResult;
import net.otaupdate.app.sdk.model.ProcessorArrayItem;
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
				List<OrganizationWrapper> organizations = new ArrayList<OrganizationWrapper>();
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
					for( OrganizationArrayItem currOai : oais )
					{
						List<DeviceWrapper> devices = new ArrayList<DeviceWrapper>();
						// fetch the devices for this organization
						for( DeviceArrayItem currDai : ModelManager.this.getDevicesForOrganization(currOai.getUuid()) )
						{
							List<ProcessorWrapper> processors = new ArrayList<ProcessorWrapper>();
							// fetch the processors for this device
							for( ProcessorArrayItem currPai : ModelManager.this.getProcessorsForDeviceAndOrganization(currDai.getUuid(), currOai.getUuid()) )
							{
								List<FwImageWrapper> fwImages = new ArrayList<FwImageWrapper>();
								// fetch the firmware iamges for this processor
								for( FwImageArrayItem currFwai : ModelManager.this.getFwImagesForProcessorAndDeviceAndOrganization(currPai.getUuid(), currDai.getUuid(), currOai.getUuid()) )
								{
									fwImages.add(new FwImageWrapper(currFwai));
								}
								
								processors.add(new ProcessorWrapper(currPai, fwImages));
							}
							
							devices.add(new DeviceWrapper(currDai, processors));
						}
						
						organizations.add(new OrganizationWrapper(currOai, devices));
					}
					
					wasSuccessful = true;
				}
				catch( Exception e )
				{
					organizations = null;
					ModelManager.this.logger.warn(String.format("getOrganizations error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful, organizations);
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
	

	private List<DeviceArrayItem> getDevicesForOrganization(String orgUuidIn)
	{
		GetOrgsOrganizationUuidDevicesResult result = WebServicesCommon.client.getOrgsOrganizationUuidDevices(new GetOrgsOrganizationUuidDevicesRequest()
				{{
					setOrganizationUuid(orgUuidIn);
				}}
				.sdkRequestConfig(SdkRequestConfig.builder()
				.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
				.build()
				));
		
		// if we made it here without exception, we're good
		return result.getDeviceArray();
	}
	
	
	private List<ProcessorArrayItem> getProcessorsForDeviceAndOrganization(String devUuidIn, String orgUuidIn)
	{
		GetOrgsOrganizationUuidDevicesDeviceUuidProcessorsResult result = WebServicesCommon.client.getOrgsOrganizationUuidDevicesDeviceUuidProcessors(new GetOrgsOrganizationUuidDevicesDeviceUuidProcessorsRequest()
				{{
					setDeviceUuid(devUuidIn);
					setOrganizationUuid(orgUuidIn);

				}}
				.sdkRequestConfig(SdkRequestConfig.builder()
				.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
				.build()
				));
		
		// if we made it here without exception, we're good
		return result.getProcessorArray();
	}
	
	
	private List<FwImageArrayItem> getFwImagesForProcessorAndDeviceAndOrganization(String procUuidIn, String devUuidIn, String orgUuidIn)
	{
		GetOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesResult result = WebServicesCommon.client.getOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimages(new GetOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesRequest()
				{{
					setProcessorUuid(procUuidIn);
					setDeviceUuid(devUuidIn);
					setOrganizationUuid(orgUuidIn);
				}}
				.sdkRequestConfig(SdkRequestConfig.builder()
				.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
				.build()
				));
		
		// if we made it here without exception, we're good
		return result.getFwImageArray();
	}
}
