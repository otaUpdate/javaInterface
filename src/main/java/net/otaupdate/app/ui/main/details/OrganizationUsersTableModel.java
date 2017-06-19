package net.otaupdate.app.ui.main.details;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.GetUsersForOrganizationCallback;
import net.otaupdate.app.sdk.model.OrganizationUserArrayItem;

public class OrganizationUsersTableModel extends AbstractTableModel
{	
	private static final long serialVersionUID = 394818285177430614L;

	
	private List<OrganizationUserArrayItem> users = null;
	
	
	public void refreshForOrganizationUuid(String orgUuidIn)
	{
		ModelManager.getSingleton().getUsersForOrganization(orgUuidIn, new GetUsersForOrganizationCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn, List<OrganizationUserArrayItem> itemsIn)
			{
				OrganizationUsersTableModel.this.users = itemsIn;
				OrganizationUsersTableModel.this.fireTableDataChanged();
			}
		});
	}
	
	
	public OrganizationUserArrayItem getUserAtIndex(int indexIn)
	{
		return this.users.get(indexIn);
	}
	

	@Override
	public int getRowCount()
	{
		return (this.users != null) ? this.users.size() : 0;
	}

	
	@Override
	public int getColumnCount()
	{
		return 1;
	}

	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		OrganizationUserArrayItem currUser = this.users.get(rowIndex);
		
		Object retVal = null;
		if( columnIndex == 0 )
		{
			retVal = currUser.getEmail();
		}
		return retVal;
	}

}
