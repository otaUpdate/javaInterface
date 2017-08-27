package net.otaupdate.app;

import com.amazonaws.ImmutableRequest;

import net.otaupdate.app.sdk.OtaUpdate;
import net.otaupdate.app.sdk.OtaUpdateClientBuilder;
import net.otaupdate.app.sdk.auth.OtaUpdateUsers;


public class WebServicesCommon
{
	public static OtaUpdate client;
	static
	{
		OtaUpdateClientBuilder builder = OtaUpdate.builder();
		builder.setSigner(new OtaUpdateUsers()
		{
			@Override
			public String generateToken(ImmutableRequest<?> arg0)
			{
				return AuthorizationManager.getSingleton().getCurrentAuthToken();
			}
		});
		
		client = builder.build();
	}
}
