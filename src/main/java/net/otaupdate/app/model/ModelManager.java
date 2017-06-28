package net.otaupdate.app.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.opensdk.SdkRequestConfig;

import net.otaupdate.app.AuthorizationManager;
import net.otaupdate.app.WebServicesCommon;
import net.otaupdate.app.sdk.model.CreateDeviceRequest;
import net.otaupdate.app.sdk.model.CreateFwRequest;
import net.otaupdate.app.sdk.model.CreateOrgRequest;
import net.otaupdate.app.sdk.model.CreateProcRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuidRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuidResult;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidDevicesDeviceUuidRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidUsersUserEmailRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrganizationUuidUsersUserEmailResult;
import net.otaupdate.app.sdk.model.DeviceArrayItem;
import net.otaupdate.app.sdk.model.EmailAddress;
import net.otaupdate.app.sdk.model.FwImageArrayItem;
import net.otaupdate.app.sdk.model.GenerateFwDownloadRequest;
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
import net.otaupdate.app.sdk.model.PostDevapiGeneratefwdownloadlinkRequest;
import net.otaupdate.app.sdk.model.PostDevapiGeneratefwdownloadlinkResult;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuidRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuidResult;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesResult;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidDevicesRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidUsersRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrganizationUuidUsersResult;
import net.otaupdate.app.sdk.model.PostOrgsRequest;
import net.otaupdate.app.sdk.model.ProcessorArrayItem;
import net.otaupdate.app.sdk.model.UpdateFwRequest;
import net.otaupdate.app.util.Dispatch;
import net.otaupdate.app.util.FileBodyWithProgress;
import net.otaupdate.app.util.FileBodyWithProgress.ProgessFileEntityListener;


@SuppressWarnings("serial")
public class ModelManager
{
	private static final ModelManager SINGLETON = new ModelManager();
	
	
	public interface RefreshTreeCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, List<OrganizationWrapper> organizationsIn);
	}
	
	
	public interface GetUsersForOrganizationCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, List<OrganizationUserArrayItem> itemsIn);
	}
	
	
	public interface SimpleCallback
	{
		public void onCompletion(boolean wasSuccessfulIn);
	}
	
	
	public interface CreateUpdateFwImageCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, String tempPutUrlIn);
	}
	
	
	public interface GetDownloadFwImageCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, String downloadUrlIn);
	}
	
	
	public interface UploadFwImageCallback
	{
		public void onProgressUpdate(long totalNumBytesWrittenIn, long totalNumBytesExpected);
		public void onCompletion(boolean wasSuccessfulIn);
	}
	
	
	private final Logger logger = LogManager.getLogger(this.getClass());
	
	
	private ModelManager()
	{
	}
	
	
	public void refreshTree(RefreshTreeCallback cbIn)
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
						OrganizationWrapper org = new OrganizationWrapper(currOai);
						
						// fetch the devices for this organization
						for( DeviceArrayItem currDai : ModelManager.this.getDevicesForOrganization(currOai.getUuid()) )
						{
							DeviceWrapper device = new DeviceWrapper(currDai, org);
							
							// fetch the processors for this device
							for( ProcessorArrayItem currPai : ModelManager.this.getProcessorsForDeviceAndOrganization(currDai.getUuid(), currOai.getUuid()) )
							{
								ProcessorWrapper proc = new ProcessorWrapper(currPai, device);
								
								// fetch the firmware images for this processor
								for( FwImageArrayItem currFwai : ModelManager.this.getFwImagesForProcessorAndDeviceAndOrganization(currPai.getUuid(), currDai.getUuid(), currOai.getUuid()) )
								{
									proc.addFirmwareImage(new FwImageWrapper(currFwai, proc));
								}
								
								device.addProcessor(proc);
							}
							
							org.addDevice(device);
						}

						organizations.add(org);
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
	
	
	public void createNewOrganization(String organizationNameIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.postOrgs(new PostOrgsRequest()
							{{
								setCreateOrgRequest(new CreateOrgRequest()
										{{
											setName(organizationNameIn);
										}});
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
					ModelManager.this.logger.warn(String.format("createOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void deleteOrganization(OrganizationWrapper orgIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.deleteOrgsOrganizationUuid(new DeleteOrgsOrganizationUuidRequest()
							{{
								setOrganizationUuid(orgIn.getModelObject().getUuid());
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
					ModelManager.this.logger.warn(String.format("deleteOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
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
	
	
	public void addUserToOrganization(String organizationUuidIn, String emailAddressIn, SimpleCallback cbIn)
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
					wasSuccessful = (result != null);
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("addUserToOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void removeUserFromOrganization(String organizationUuidIn, String emailAddressIn, SimpleCallback cbIn)
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
					wasSuccessful = (result != null);
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("removeUserFromOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void addDeviceToOrganization(String devNameIn, OrganizationWrapper orgIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.postOrgsOrganizationUuidDevices(new PostOrgsOrganizationUuidDevicesRequest()
							{{
								setOrganizationUuid(orgIn.getModelObject().getUuid());
								
								setCreateDeviceRequest(new CreateDeviceRequest()
										{{
											setName(devNameIn);
										}});
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
					ModelManager.this.logger.warn(String.format("addDeviceToOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void deleteDevice(DeviceWrapper devIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.deleteOrgsOrganizationUuidDevicesDeviceUuid(new DeleteOrgsOrganizationUuidDevicesDeviceUuidRequest()
							{{
								setDeviceUuid(devIn.getModelObject().getUuid());
								setOrganizationUuid(devIn.getParent().getModelObject().getUuid());
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
					ModelManager.this.logger.warn(String.format("deleteDevice error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void addProcessorToDevice(String procNameIn, DeviceWrapper devIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.postOrgsOrganizationUuidDevicesDeviceUuidProcessors(new PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsRequest()
							{{
								setDeviceUuid(devIn.getModelObject().getUuid());
								setOrganizationUuid(devIn.getParent().getModelObject().getUuid());
								
								setCreateProcRequest(new CreateProcRequest()
										{{
											setName(procNameIn);
										}});
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
					ModelManager.this.logger.warn(String.format("addProcessorToDevice error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void deleteProcessor(ProcessorWrapper procIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.deleteOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuid(new DeleteOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidRequest()
							{{
								setProcessorUuid(procIn.getModelObject().getUuid());
								setDeviceUuid(procIn.getParent().getModelObject().getUuid());
								setOrganizationUuid(procIn.getParent().getParent().getModelObject().getUuid());
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
					ModelManager.this.logger.warn(String.format("deleteProcessor error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void createNewFwImage(String fwImageNameIn, String procUuidIn, String devUuidIn, String orgUuidIn,
								 CreateUpdateFwImageCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				String putUrl = null;
				try
				{
					PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesResult result = WebServicesCommon.client.postOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimages(new PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesRequest()
							{{
								setCreateFwRequest(new CreateFwRequest() {{
									setName(fwImageNameIn);
								}});
								setProcessorUuid(procUuidIn);
								setDeviceUuid(devUuidIn);
								setOrganizationUuid(orgUuidIn);
							}}
							.sdkRequestConfig(SdkRequestConfig.builder()
									.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
									.build()
									));

					// if we made it here without exception, we're good
					wasSuccessful = true;
					putUrl = result.getCreateFwResponse().getPutUrl();
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("removeUserFromOrganization error: '%s", e.getMessage()));
				}
				
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful, putUrl);
			}
		});
	}
	
	
	public void uploadFirmwareImage(String fwUrlIn, File fwImageIn, UploadFwImageCallback cbIn)
	{
		this.logger.debug(String.format("url: '%s'", fwUrlIn));
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				// they selected a file...upload it
				HttpPut put = new HttpPut(fwUrlIn);
				
				// setup our file body and listener
				FileBodyWithProgress fb = new FileBodyWithProgress(fwImageIn);
				fb.addListener(new ProgessFileEntityListener()
				{
					@Override
					public void onUpdate(long totalNumBytesWrittenIn, long totalNumBytesExpected)
					{
						if( cbIn != null ) cbIn.onProgressUpdate(totalNumBytesWrittenIn,totalNumBytesExpected);
					}
				});
				put.setEntity(MultipartEntityBuilder.create().addPart("bin", fb).build());
				
				// actually do the put
				boolean wasSuccessful = false;
				try
				{
				    HttpClients.createDefault().execute(put);
					wasSuccessful = true;
				}
				catch( Exception e )
				{
					JOptionPane.showMessageDialog(null, "Error uploading firmware image", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void updateFirmwareImage(FwImageWrapper fwIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuidResult result = WebServicesCommon.client.postOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuid(new PostOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuidRequest()
							{{
								setUpdateFwRequest(new UpdateFwRequest()
								{{
									setName(fwIn.getModelObject().getName());
									setToVersionUuid(fwIn.getModelObject().getToVersionUuid());
								}});
								
								setFwUuid(fwIn.getModelObject().getUuid());
								setProcessorUuid(fwIn.getParent().getModelObject().getUuid());
								setDeviceUuid(fwIn.getParent().getParent().getModelObject().getUuid());
								setOrganizationUuid(fwIn.getParent().getParent().getParent().getModelObject().getUuid());
							}}
							.sdkRequestConfig(SdkRequestConfig.builder()
							.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
							.build()
							));
					
					// if we made it here without exception, we're good
					wasSuccessful = (result != null);
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("deleteFirmwareImage error: '%s", e.getMessage()));
				}
				
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}
	
	
	public void getDownloadLinkForFirmwareImage(FwImageWrapper fwIn, GetDownloadFwImageCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				String url = null;
				try
				{
					PostDevapiGeneratefwdownloadlinkResult result = WebServicesCommon.client.postDevapiGeneratefwdownloadlink(new PostDevapiGeneratefwdownloadlinkRequest()
							{{
								setGenerateFwDownloadRequest(new GenerateFwDownloadRequest()
										{{
											setTargetFwUuid(fwIn.getModelObject().getUuid());
										}});
							}});
					
					// if we made it here without exception, we're good
					wasSuccessful = (result != null);
					url = result.getGenerateFwDownloadResponse().getUrl();
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("createOrganization error: '%s", e.getMessage()));
				}
		
				if( cbIn != null ) cbIn.onCompletion(wasSuccessful, url);
			}
		});
	}
	
	
	public void deleteFirmwareImage(FwImageWrapper fwIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					DeleteOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuidResult result = WebServicesCommon.client.deleteOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuid(new DeleteOrgsOrganizationUuidDevicesDeviceUuidProcessorsProcessorUuidFwimagesFwUuidRequest()
							{{
								setOrganizationUuid(fwIn.getParent().getParent().getParent().getModelObject().getUuid());
								setDeviceUuid(fwIn.getParent().getParent().getModelObject().getUuid());
								setProcessorUuid(fwIn.getParent().getModelObject().getUuid());
								setFwUuid(fwIn.getModelObject().getUuid());
							}}
							.sdkRequestConfig(SdkRequestConfig.builder()
							.customHeader("Authorization", String.format("Basic %s", AuthorizationManager.getSingleton().getCurrentAuthToken()))
							.build()
							));
					
					// if we made it here without exception, we're good
					wasSuccessful = (result != null);
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("deleteFirmwareImage error: '%s", e.getMessage()));
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
