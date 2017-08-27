package net.otaupdate.app.model;

public class LoginResult
{
	private final String token;

	private final boolean needsPasswordChange;
	private final String changePasswordSession;

	private LoginResult(String tokenIn, boolean needsPwdChangeIn, String changePasswordSessionIn)
	{
		this.token = tokenIn;
		this.needsPasswordChange = needsPwdChangeIn;
		this.changePasswordSession = changePasswordSessionIn;
	}


	public String getToken()
	{
		return this.token;
	}


	public boolean getNeedsPasswordReset()
	{
		return this.needsPasswordChange;
	}


	public String getChangePasswordSession()
	{
		return this.changePasswordSession;
	}
	
	
	public boolean getWasSuccessful()
	{
		return (this.token != null);
	}

	
	public static LoginResult getResultWithFailedLogin()
	{
		return new LoginResult(null, false, null);
	}
	

	public static LoginResult getResultWithAuthToken(String authTokenIn)
	{
		return new LoginResult(authTokenIn, false, null);
	}


	public static LoginResult getResultWithPasswordChangeToken(String pwdChangeTokenIn)
	{
		return new LoginResult(null, true, pwdChangeTokenIn);
	}
}