package net.otaupdate.app;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.InternalErrorException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;

import net.otaupdate.app.model.LoginResult;
import net.otaupdate.app.util.Dispatch;


public class AuthorizationManager
{
	private static final AuthorizationManager SINGLETON = new AuthorizationManager();
	private static final String PREFS_KEY_EMAIL = "email";
	private static final String PREFS_KEY_PASSWORD = "password";

	private static final String CLIENT_ID = "64k7mua2nmqdfocb0g4fses3e3";
	private static final String POOL_ID = "us-east-2_9lt2r4XGQ";
	private static final AWSCognitoIdentityProvider COGNITO_CLIENT = AWSCognitoIdentityProviderClientBuilder.defaultClient();


	public interface LoginCallback
	{
		void onAuthorizationComplete(LoginResult loginResultIn, String errorMsgIn);
	}


	public interface ChangePasswordCallback
	{
		void onPasswordChangeComplete(boolean wasSuccessfulIn, String errorMsgIn);
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
				String errorMessage = null;

				LoginResult res = AuthorizationManager.this.loginWithUsernameOrEmailGetToken(emailAddressIn, null, passwordIn);
				
				// do some logging
				if( res.getWasSuccessful() )
				{
					AuthorizationManager.this.authToken = res.getToken();
					AuthorizationManager.this.logger.debug(String.format("Login as '%s' successful", emailAddressIn));
				}
				else
				{
					AuthorizationManager.this.logger.warn(String.format("Login failed: '%s", errorMessage));
				}

				// update our saved credentials
				AuthorizationManager.this.saveCredentials(emailAddressIn, passwordIn);

				// call our callback
				if( lcIn != null ) lcIn.onAuthorizationComplete(res, errorMessage);
			}
		});
	}
	
	
	public void changePassword(String emailAddressIn, String newPasswordIn, String sessionIn, ChangePasswordCallback cbIn)
	{		
		Dispatch.async(new Runnable()
		{
			@Override
			public void run()
			{
				boolean wasSuccessful = false;
				String errorMessage = null;

				Map<String, String> challengeResponses = new HashMap<String, String>();
				challengeResponses.put("USERNAME", emailAddressIn);
				challengeResponses.put("NEW_PASSWORD", newPasswordIn);
				
				try
				{
					AdminRespondToAuthChallengeResult res = COGNITO_CLIENT.adminRespondToAuthChallenge(new AdminRespondToAuthChallengeRequest()
							.withClientId(CLIENT_ID)
							.withUserPoolId(POOL_ID)
							.withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
							.withSession(sessionIn)
							.withChallengeResponses(challengeResponses));
					
					wasSuccessful = (res != null);
				}
				catch( Exception e )
				{
					errorMessage = e.getMessage();
				}

				// do some logging
				if( wasSuccessful ) AuthorizationManager.this.logger.debug(String.format("Password change for '%s' successful", emailAddressIn));
				else AuthorizationManager.this.logger.warn(String.format("Password change failed: '%s", errorMessage));

				// call our callback
				if( cbIn != null ) cbIn.onPasswordChangeComplete(wasSuccessful, errorMessage);
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
	
	
	private LoginResult loginWithUsernameOrEmailGetToken(String emailIn, String usernameIn, String passwordIn)
	{
		// give preference to the username
		if( (usernameIn == null) && (emailIn == null) ) return null;

		Map<String, String> authParams = new HashMap<String, String>();
		authParams.put("USERNAME",  (usernameIn != null) ? usernameIn : emailIn);
		authParams.put("PASSWORD", passwordIn);

		AdminInitiateAuthRequest authReq = new AdminInitiateAuthRequest()
				.withClientId(CLIENT_ID)
				.withUserPoolId(POOL_ID)
				.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
				.withAuthParameters(authParams);

		LoginResult retVal = null;
		try
		{
			AdminInitiateAuthResult result = COGNITO_CLIENT.adminInitiateAuth(authReq);

			AuthenticationResultType authResult = result.getAuthenticationResult();
			if( authResult != null )
			{
				retVal = LoginResult.getResultWithAuthToken(authResult.getIdToken());
			}
			else
			{
				if( (result.getChallengeName() != null) && result.getChallengeName().equals(ChallengeNameType.NEW_PASSWORD_REQUIRED.toString()) )
				{
					retVal = LoginResult.getResultWithPasswordChangeToken(result.getSession());
				}
			}
		}
		catch( InternalErrorException e )
		{
			// this occurs for first-time logins...try using the username.sub instead of the email...
			if( emailIn != null )
			{
				String username = this.getUsernameFromEmail(emailIn);
				if( username != null ) retVal = loginWithUsernameOrEmailGetToken(emailIn, username, passwordIn);
			}
		}
		catch( Exception e )
		{
			this.logger.warn(String.format("login error: '%s'", e.toString()));
		}
		return retVal;
	}
	
	
	private String getUsernameFromEmail(String emailIn)
	{
		AdminGetUserRequest agur = new AdminGetUserRequest()
				.withUserPoolId(POOL_ID)
				.withUsername(emailIn);

		String retVal = null;
		try
		{
			retVal = COGNITO_CLIENT.adminGetUser(agur).getUsername();
		}
		catch( UserNotFoundException e ) { }
		catch( Exception e )
		{
			this.logger.warn(String.format("username lookup error: '%s'", e.toString()));
		}
		return retVal;
	}
}
