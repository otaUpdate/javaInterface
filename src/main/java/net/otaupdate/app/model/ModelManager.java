package net.otaupdate.app.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.opensdk.SdkRequestConfig;

import net.otaupdate.app.WebServicesCommon;
import net.otaupdate.app.sdk.model.CreateDeviceRequest;
import net.otaupdate.app.sdk.model.CreateDeviceTypeRequest;
import net.otaupdate.app.sdk.model.CreateFwRequest;
import net.otaupdate.app.sdk.model.CreateOrgRequest;
import net.otaupdate.app.sdk.model.CreateProcTypeRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrgUuidDevtypesDevTypeUuidDevicesDevSerialNumberRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidResult;
import net.otaupdate.app.sdk.model.DeleteOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrgUuidDevtypesDevTypeUuidRequest;
import net.otaupdate.app.sdk.model.DeleteOrgsOrgUuidRequest;
import net.otaupdate.app.sdk.model.DeviceArrayItem;
import net.otaupdate.app.sdk.model.DeviceTypeArrayItem;
import net.otaupdate.app.sdk.model.EmailAddress;
import net.otaupdate.app.sdk.model.FwImageArrayItem;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidDevicesRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidDevicesResult;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidDevicesUnprovisionedprocsRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidDevicesUnprovisionedprocsResult;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidFwuploadlinkRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidFwuploadlinkResult;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesResult;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidProctypesRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesDevTypeUuidProctypesResult;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidDevtypesResult;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidUsersRequest;
import net.otaupdate.app.sdk.model.GetOrgsOrgUuidUsersResult;
import net.otaupdate.app.sdk.model.GetOrgsRequest;
import net.otaupdate.app.sdk.model.GetOrgsResult;
import net.otaupdate.app.sdk.model.OrganizationArrayItem;
import net.otaupdate.app.sdk.model.OrganizationUserArrayItem;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesDevTypeUuidDevicesRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidResult;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesResult;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesDevTypeUuidProctypesRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesDevTypeUuidRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidDevtypesRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidUsersRemoveuserfromorgRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidUsersRequest;
import net.otaupdate.app.sdk.model.PostOrgsOrgUuidUsersResult;
import net.otaupdate.app.sdk.model.PostOrgsRequest;
import net.otaupdate.app.sdk.model.ProcTypeArrayItem;
import net.otaupdate.app.sdk.model.ProcessorsItem;
import net.otaupdate.app.sdk.model.UnprovProcessorArrayItem;
import net.otaupdate.app.sdk.model.UpdateDeviceTypeRequest;
import net.otaupdate.app.sdk.model.UpdateFwRequest;
import net.otaupdate.app.sdk.model.UpdateProcTypeRequest;
import net.otaupdate.app.util.Dispatch;
import net.otaupdate.app.util.FileBodyWithProgress;
import net.otaupdate.app.util.FileBodyWithProgress.ProgessFileEntityListener;


@SuppressWarnings("serial")
public class ModelManager
{
	private static final ModelManager SINGLETON = new ModelManager();


	public interface RefreshOrgAndDevTypeTreeCallback
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


	public interface GetDownloadFwImageCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, String downloadUrlIn);
	}


	public interface CreateFwImageCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, String newFwUuidIn);
	}


	public interface UploadFwImageCallback
	{
		public void onProgressUpdate(long totalNumBytesWrittenIn, long totalNumBytesExpected);
		public void onCompletion(boolean wasSuccessfulIn);
	}


	public interface GetDeviceInstancesCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, List<DeviceInstanceWrapper> devInstancesIn);
	}


	public interface GetUnprovisionedProcessorsCallback
	{
		public void onCompletion(boolean wasSuccessfulIn, List<UnprovisionedProcessorWrapper> unprovisionedProcsIn);
	}


	private final Logger logger = LogManager.getLogger(this.getClass());


	private ModelManager()
	{
	}


	public void refreshOrgAndDevTypeTree(RefreshOrgAndDevTypeTreeCallback cbIn)
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
					GetOrgsResult result = WebServicesCommon.client.getOrgs(new GetOrgsRequest().sdkRequestConfig(SdkRequestConfig.builder().build()));
					List<OrganizationArrayItem> oais = result.getOrganizationArray();

					for( OrganizationArrayItem currOai : oais )
					{
						OrganizationWrapper org = new OrganizationWrapper(currOai);

						// fetch the devices for this organization
						for( DeviceTypeArrayItem currDtai : ModelManager.this.getDeviceTypesForOrganization(currOai.getUuid()) )
						{
							DeviceTypeWrapper device = new DeviceTypeWrapper(currDtai, org);

							// fetch the processors for this device
							for( ProcTypeArrayItem currPtai : ModelManager.this.getProcessorTypesForDeviceAndOrganization(currDtai.getUuid(), currOai.getUuid()) )
							{
								ProcessorTypeWrapper proc = new ProcessorTypeWrapper(currPtai, device);

								// fetch the firmware images for this processor
								for( FwImageArrayItem currFwai : ModelManager.this.getFwImagesForProcessorTypeAndDeviceTypeAndOrganization(currPtai.getUuid(), currDtai.getUuid(), currOai.getUuid()) )
								{
									proc.addFwImage(new FwImageWrapper(currFwai, proc));
								}

								device.addProcType(proc);
							}

							org.addDevType(device);
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
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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
					WebServicesCommon.client.deleteOrgsOrgUuid(new DeleteOrgsOrgUuidRequest()
					{{
						setOrgUuid(orgIn.getUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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
					GetOrgsOrgUuidUsersResult result = WebServicesCommon.client.getOrgsOrgUuidUsers(new GetOrgsOrgUuidUsersRequest()
					{{
						setOrgUuid(organizationUuidIn);
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));
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
					PostOrgsOrgUuidUsersResult result = WebServicesCommon.client.postOrgsOrgUuidUsers(new PostOrgsOrgUuidUsersRequest()
					{{
						setEmailAddress(new EmailAddress() {{
							setEmail(emailAddressIn);
						}});
						setOrgUuid(organizationUuidIn);
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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
					WebServicesCommon.client.postOrgsOrgUuidUsersRemoveuserfromorg(new PostOrgsOrgUuidUsersRemoveuserfromorgRequest()
					{{
						setEmailAddress( new EmailAddress()
						{{
							setEmail(emailAddressIn);
						}});
						setOrgUuid(organizationUuidIn);
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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


	public void addDeviceTypeToOrganization(String devNameIn, OrganizationWrapper orgIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.postOrgsOrgUuidDevtypes(new PostOrgsOrgUuidDevtypesRequest()
					{{
						setOrgUuid(orgIn.getUuid());

						setCreateDeviceTypeRequest(new CreateDeviceTypeRequest()
						{{
							setName(devNameIn);
						}});
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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


	public void updateDeviceType(DeviceTypeWrapper devIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.postOrgsOrgUuidDevtypesDevTypeUuid(new PostOrgsOrgUuidDevtypesDevTypeUuidRequest()
					{{
						setDevTypeUuid(devIn.getUuid());
						setOrgUuid(devIn.getOrgUuid());
						setUpdateDeviceTypeRequest(new UpdateDeviceTypeRequest()
						{{
							setName(devIn.getName());
						}});
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

					// if we made it here without exception, we're good
					wasSuccessful = true;
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("updateProcessor error: '%s", e.getMessage()));
				}

				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}


	public void deleteDeviceType(DeviceTypeWrapper devIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.deleteOrgsOrgUuidDevtypesDevTypeUuid(new DeleteOrgsOrgUuidDevtypesDevTypeUuidRequest()
					{{
						setDevTypeUuid(devIn.getUuid());
						setOrgUuid(devIn.getOrgUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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


	public void addProcessorTypeToDevice(String procNameIn, DeviceTypeWrapper devIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.postOrgsOrgUuidDevtypesDevTypeUuidProctypes(new PostOrgsOrgUuidDevtypesDevTypeUuidProctypesRequest()
					{{
						setDevTypeUuid(devIn.getUuid());
						setOrgUuid(devIn.getOrgUuid());

						setCreateProcTypeRequest(new CreateProcTypeRequest()
						{{
							setName(procNameIn);
						}});
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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


	public void updateProcessorType(ProcessorTypeWrapper procIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.postOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuid(new PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidRequest()
					{{
						setProcTypeUuid(procIn.getUuid());
						setDevTypeUuid(procIn.getDevTypeUuid());
						setOrgUuid(procIn.getOrgUuid());
						setUpdateProcTypeRequest(new UpdateProcTypeRequest()
						{{
							setName(procIn.getName());
							setLatestFirmwareUuid(procIn.getLatestFwImageUuid());
						}});
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

					// if we made it here without exception, we're good
					wasSuccessful = true;
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("updateProcessor error: '%s", e.getMessage()));
				}

				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}


	public void deleteProcessorType(ProcessorTypeWrapper procIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.deleteOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuid(new DeleteOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidRequest()
					{{
						setProcTypeUuid(procIn.getUuid());
						setDevTypeUuid(procIn.getDevTypeUuid());
						setOrgUuid(procIn.getOrgUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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
			CreateFwImageCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				String newUuid = null;
				try
				{
					PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesResult result = WebServicesCommon.client.postOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimages(new PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesRequest()
					{{
						setCreateFwRequest(new CreateFwRequest() {{
							setName(fwImageNameIn);
						}});
						setProcTypeUuid(procUuidIn);
						setDevTypeUuid(devUuidIn);
						setOrgUuid(orgUuidIn);
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

					// if we made it here without exception, we're good
					wasSuccessful = (result != null);
					newUuid = result.getCreateFwResponse().getUuid();
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("removeUserFromOrganization error: '%s", e.getMessage()));
				}

				if( cbIn != null ) cbIn.onCompletion(wasSuccessful, newUuid);
			}
		});
	}


	public void uploadFirmwareImage(String fwUuidIn, String procUuidIn, String devUuidIn, String orgUuidIn, 
			File fwImageIn, UploadFwImageCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				// first, we need to get the url to which we should be uploading
				String url = ModelManager.this.getUploadUrlForFwUuid(fwUuidIn, procUuidIn, devUuidIn, orgUuidIn);
				if( url == null )
				{
					if( cbIn!= null ) cbIn.onCompletion(false);
					return;
				}
				ModelManager.this.logger.debug(String.format("uploading to '%s'", url));

				// we have a valid URL...put it
				HttpPut put = new HttpPut(url);

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
				put.setEntity(new FileEntity(fwImageIn));
				//				put.setEntity(MultipartEntityBuilder.create().addPart("bin", fb).build());

				// actually do the put
				boolean wasSuccessful = false;
				try
				{
					HttpClients.createDefault().execute(put);
					wasSuccessful = true;
				}
				catch( Exception e ) { }

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
					PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidResult result = WebServicesCommon.client.postOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuid(new PostOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidRequest()
					{{
						setUpdateFwRequest(new UpdateFwRequest()
						{{
							setName(fwIn.getName());
							setToVersionUuid(fwIn.getToVersionUuid());
						}});

						setFwUuid(fwIn.getUuid());
						setProcTypeUuid(fwIn.getProcTypeUuid());
						setDevTypeUuid(fwIn.getDevTypeUuid());
						setOrgUuid(fwIn.getOrgUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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
					DeleteOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidResult result = WebServicesCommon.client.deleteOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuid(new DeleteOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidRequest()
					{{
						setFwUuid(fwIn.getUuid());
						setProcTypeUuid(fwIn.getProcTypeUuid());
						setDevTypeUuid(fwIn.getDevTypeUuid());
						setOrgUuid(fwIn.getOrgUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

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


	public void getDeviceInstancesForDeviceType(DeviceTypeWrapper dtwIn, GetDeviceInstancesCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				List<DeviceInstanceWrapper> devInstances = null;
				try
				{
					GetOrgsOrgUuidDevtypesDevTypeUuidDevicesResult result = WebServicesCommon.client.getOrgsOrgUuidDevtypesDevTypeUuidDevices(new GetOrgsOrgUuidDevtypesDevTypeUuidDevicesRequest()
					{{
						setDevTypeUuid(dtwIn.getUuid());
						setOrgUuid(dtwIn.getOrgUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

					// if we made it here without exception, we're good
					wasSuccessful = (result != null);

					devInstances = new ArrayList<DeviceInstanceWrapper>();
					for( DeviceArrayItem currDai : result.getDeviceArray() )
					{
						devInstances.add(new DeviceInstanceWrapper(currDai, dtwIn));
					}
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("getDeviceInstances error: '%s", e.getMessage()));
				}

				if( cbIn != null ) cbIn.onCompletion(wasSuccessful, devInstances);
			}
		});
	}


	public void createDeviceInstance(DeviceTypeWrapper dtwIn, String devSerialNumIn, Map<ProcessorTypeWrapper, String> procSerialNumbersIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					// create our list of processors
					List<ProcessorsItem> procs = new ArrayList<ProcessorsItem>();
					Iterator<Entry<ProcessorTypeWrapper, String>> it = procSerialNumbersIn.entrySet().iterator();
					while( it.hasNext() )
					{
						Entry<ProcessorTypeWrapper, String> currEntry = it.next();

						ProcessorsItem newItem = new ProcessorsItem();
						newItem.setProcTypeUuid(currEntry.getKey().getUuid());
						newItem.setSerialNumber(currEntry.getValue());

						procs.add(newItem);
					}

					// do our post
					WebServicesCommon.client.postOrgsOrgUuidDevtypesDevTypeUuidDevices(new PostOrgsOrgUuidDevtypesDevTypeUuidDevicesRequest()
					{{
						setCreateDeviceRequest(new CreateDeviceRequest() {{
							setSerialNumber(devSerialNumIn);
							setProcessors(procs);
						}});
						setDevTypeUuid(dtwIn.getUuid());
						setOrgUuid(dtwIn.getOrgUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

					// if we made it here without exception, we're good
					wasSuccessful = true;

				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("getDeviceInstances error: '%s", e.getMessage()));
				}

				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}


	public void deleteDeviceInstance(DeviceTypeWrapper dtwIn, String devSerialNumIn, SimpleCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				try
				{
					WebServicesCommon.client.deleteOrgsOrgUuidDevtypesDevTypeUuidDevicesDevSerialNumber(new DeleteOrgsOrgUuidDevtypesDevTypeUuidDevicesDevSerialNumberRequest()
					{{
						setDevSerialNumber(devSerialNumIn);
						setDevTypeUuid(dtwIn.getUuid());
						setOrgUuid(dtwIn.getOrgUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));

					// if we made it here without exception, we're good
					wasSuccessful = true;

				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("getDeviceInstances error: '%s", e.getMessage()));
				}

				if( cbIn != null ) cbIn.onCompletion(wasSuccessful);
			}
		});
	}


	public void getUnprovisionedProcessorsForDeviceType(DeviceTypeWrapper dtwIn, GetUnprovisionedProcessorsCallback cbIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				List<UnprovisionedProcessorWrapper> unprovProcInstances = null;
				try
				{
					GetOrgsOrgUuidDevtypesDevTypeUuidDevicesUnprovisionedprocsResult result = WebServicesCommon.client.getOrgsOrgUuidDevtypesDevTypeUuidDevicesUnprovisionedprocs(new GetOrgsOrgUuidDevtypesDevTypeUuidDevicesUnprovisionedprocsRequest()
					{{
						setDevTypeUuid(dtwIn.getUuid());
						setOrgUuid(dtwIn.getOrgUuid());
					}}
					.sdkRequestConfig(SdkRequestConfig.builder().build()));


					// if we made it here without exception, we're good
					wasSuccessful = (result != null);

					unprovProcInstances = new ArrayList<UnprovisionedProcessorWrapper>();
					for( UnprovProcessorArrayItem currDai : result.getUnprovProcessorArray() )
					{
						unprovProcInstances.add(new UnprovisionedProcessorWrapper(currDai, dtwIn));
					}
				}
				catch( Exception e )
				{
					ModelManager.this.logger.warn(String.format("getDeviceInstances error: '%s", e.getMessage()));
				}

				if( cbIn != null ) cbIn.onCompletion(wasSuccessful, unprovProcInstances);
			}
		});
	}


	public static ModelManager getSingleton()
	{
		return SINGLETON;
	}


	private List<DeviceTypeArrayItem> getDeviceTypesForOrganization(String orgUuidIn)
	{
		GetOrgsOrgUuidDevtypesResult result = WebServicesCommon.client.getOrgsOrgUuidDevtypes(new GetOrgsOrgUuidDevtypesRequest()
		{{
			setOrgUuid(orgUuidIn);
		}}
		.sdkRequestConfig(SdkRequestConfig.builder().build()));

		// if we made it here without exception, we're good
		return result.getDeviceTypeArray();
	}


	private List<ProcTypeArrayItem> getProcessorTypesForDeviceAndOrganization(String devUuidIn, String orgUuidIn)
	{
		GetOrgsOrgUuidDevtypesDevTypeUuidProctypesResult result = WebServicesCommon.client.getOrgsOrgUuidDevtypesDevTypeUuidProctypes(new GetOrgsOrgUuidDevtypesDevTypeUuidProctypesRequest()
		{{
			setDevTypeUuid(devUuidIn);
			setOrgUuid(orgUuidIn);

		}}
		.sdkRequestConfig(SdkRequestConfig.builder().build()));

		// if we made it here without exception, we're good
		return result.getProcTypeArray();
	}


	private List<FwImageArrayItem> getFwImagesForProcessorTypeAndDeviceTypeAndOrganization(String procUuidIn, String devUuidIn, String orgUuidIn)
	{
		GetOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesResult result = WebServicesCommon.client.getOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimages(new GetOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesRequest()
		{{
			setProcTypeUuid(procUuidIn);
			setDevTypeUuid(devUuidIn);
			setOrgUuid(orgUuidIn);
		}}
		.sdkRequestConfig(SdkRequestConfig.builder().build()));

		// if we made it here without exception, we're good
		return result.getFwImageArray();
	}


	private String getUploadUrlForFwUuid(String fwUuidIn, String procUuidIn, String devUuidIn, String orgUuidIn)
	{
		String retVal = null;
		try
		{
			GetOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidFwuploadlinkResult result = WebServicesCommon.client.getOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidFwuploadlink(new GetOrgsOrgUuidDevtypesDevTypeUuidProctypesProcTypeUuidFwimagesFwUuidFwuploadlinkRequest()
			{{
				setFwUuid(fwUuidIn);
				setProcTypeUuid(procUuidIn);
				setDevTypeUuid(devUuidIn);
				setOrgUuid(orgUuidIn);
			}}
			.sdkRequestConfig(SdkRequestConfig.builder().build()));

			// if we made it here without exception, we're good
			retVal = result.getFwUploadLinkResponse().getLink();
		}
		catch( Exception e )
		{
			ModelManager.this.logger.warn(String.format("deleteFirmwareImage error: '%s", e.getMessage()));
		}

		return retVal;
	}
}
