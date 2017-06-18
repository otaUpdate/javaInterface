package net.otaupdate.app;

import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.otaupdate.app.sdk.model.CreateUserRequest;
import net.otaupdate.app.sdk.model.PostLoginRequest;
import net.otaupdate.app.sdk.model.PostLoginResult;
import net.otaupdate.app.sdk.model.PostRegisterRequest;
import net.otaupdate.app.sdk.model.PostRegisterResult;
import net.otaupdate.app.sdk.model.UserAuthRequest;
import net.otaupdate.app.util.Dispatch;


public class AuthorizationManager
{
	private static final AuthorizationManager SINGLETON = new AuthorizationManager();
	private static final String PREFS_KEY_EMAIL = "email";
	private static final String PREFS_KEY_PASSWORD = "password";
	
	
	public interface LoginCallback
	{
		void onAuthorizationComplete(boolean wasSuccessfulIn, String errorMsgIn);
	}
	
	
	public interface CreateUserCallback
	{
		void onUserCreateComplete(boolean wasSuccessfulIn, String errorMsgIn);
	}
	

	private final Logger logger = LogManager.getLogger(this.getClass());
	private String authToken = null;

	
	private AuthorizationManager()
	{
	}
	
	
	public void login(String emailAddressIn, String passwordIn, LoginCallback lcIn)
	{		
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				String errorMessage = null;
				
				// send our request
				try
				{
					PostLoginResult result = WebServicesCommon.client.postLogin( new PostLoginRequest()
							{{
								setUserAuthRequest(new UserAuthRequest()
								{{
									setEmailAddress(emailAddressIn);
									setPassword(passwordIn);
								}});
							}});
					
					AuthorizationManager.this.authToken = result.getUserAuthResponse().getToken();
					wasSuccessful = AuthorizationManager.this.authToken != null;
					errorMessage = null;
				}
				catch( Exception e )
				{
					wasSuccessful = false;
					errorMessage = e.getMessage();
				}
				
				// do some logging
				if( wasSuccessful ) AuthorizationManager.this.logger.debug(String.format("Login as '%s' successful", emailAddressIn));
				else AuthorizationManager.this.logger.warn(String.format("Login failed: '%s", errorMessage));
				
				// update our saved credentials
				AuthorizationManager.this.saveCredentials(emailAddressIn, passwordIn);
				
				// call our callback
				if( lcIn != null ) lcIn.onAuthorizationComplete(wasSuccessful, errorMessage);
			}
		});
	}
	
	
	public void createNewUser(String emailAddressIn, String passwordIn, CreateUserCallback cucIn)
	{
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				String errorMessage = null;
				
				// send our request
				try
				{
					PostRegisterResult result = WebServicesCommon.client.postRegister(new PostRegisterRequest()
							{{
								setCreateUserRequest(new CreateUserRequest()
										{{
											setEmailAddress(emailAddressIn);
											setPassword(passwordIn);
										}});
							}});
					
					// if we made it here without exceptions, we're good
					wasSuccessful = true;
					errorMessage = null;
				}
				catch( Exception e )
				{
					wasSuccessful = false;
					errorMessage = e.getMessage();
				}
				
				// do some logging
				if( wasSuccessful ) AuthorizationManager.this.logger.debug("User '%s' created successfully", emailAddressIn);
				else AuthorizationManager.this.logger.warn("User creation failed: '%s", errorMessage);
				
				// update our saved credentials
				AuthorizationManager.this.saveCredentials(emailAddressIn, passwordIn);
				
				// call our callback
				if( cucIn != null ) cucIn.onUserCreateComplete(wasSuccessful, errorMessage);
			}
		});
	}
	
	
	public boolean isLoggedIn()
	{
		return this.authToken != null;
	}
	
	
	public String getCurrentAuthToken()
	{
		return this.authToken;
	}
	
	
	public String getSavedEmailAddress()
	{
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		return prefs.get(PREFS_KEY_EMAIL, "");
	}
	
	
	public String getSavedPassword()
	{
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		return prefs.get(PREFS_KEY_PASSWORD, "");
	}
	
	
	public static final AuthorizationManager getSingleton()
	{
		return SINGLETON;
	}
	
	
	private void saveCredentials(String emailAddressIn, String passwordIn)
	{
		Preferences prefs = Preferences.userNodeForPackage(this.getClass());
		prefs.put(PREFS_KEY_EMAIL, emailAddressIn);
		prefs.put(PREFS_KEY_PASSWORD, passwordIn);
	}
}
