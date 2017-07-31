package net.otaupdate.app.ui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.SimpleCallback;
import net.otaupdate.app.model.OrganizationWrapper;

public class OrgAndDevTypeTreeContextMenu extends JPopupMenu implements PopupMenuListener
{
	private static final long serialVersionUID = -1274596196172586854L;
	
	
	private final OrgAndDevTypeTree parent;
	private JMenuItem mnuAdd;
	private JMenuItem mnuDelete;
	
	private Object selectedItem = null;
	

	public OrgAndDevTypeTreeContextMenu(OrgAndDevTypeTree parentIn)
	{
		this.parent = parentIn;
		this.addPopupMenuListener(this);
		
		mnuAdd = new JMenuItem("Add");
		this.mnuAdd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// add pressed
				OrgAndDevTypeTreeContextMenu.this.addItem();
			}
		});
		this.add(mnuAdd);
		
		mnuDelete = new JMenuItem("Delete Selected Item");
		this.mnuDelete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// delete pressed
				OrgAndDevTypeTreeContextMenu.this.removeItem();
			}
		});
		this.add(mnuDelete);
	}


	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e)
	{
		// name our add item appropriately
		Object selectedItem = this.parent.getSelectedItem();
		if( selectedItem == null )
		{
			this.mnuAdd.setVisible(false);
			this.mnuDelete.setVisible(false);
		}
		else if( selectedItem instanceof String )
		{
			this.mnuAdd.setText("Add Organization");
			this.mnuAdd.setVisible(true);
			this.mnuDelete.setVisible(false);
		}
		else if( selectedItem instanceof OrganizationWrapper )
		{
			this.mnuAdd.setText("Add DeviceType ");
			this.mnuAdd.setVisible(true);
			this.mnuDelete.setVisible(true);
		}
		
		this.selectedItem = selectedItem;
	}


	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { }


	@Override
	public void popupMenuCanceled(PopupMenuEvent e) { }
	
	
	private void addItem()
	{	
		if( this.selectedItem instanceof String )
		{
			this.createOrganization();
		}
		else if( this.selectedItem instanceof OrganizationWrapper )
		{
			this.addDeviceTypeToOrganization((OrganizationWrapper)this.selectedItem);
		}
	}
	
	
	private void removeItem()
	{
		if( this.selectedItem instanceof OrganizationWrapper )
		{
			this.deleteOrganization((OrganizationWrapper)this.selectedItem);
		}
		else if( this.selectedItem instanceof DeviceTypeWrapper )
		{
			this.deleteDeviceType((DeviceTypeWrapper)this.selectedItem);
		}
	}
	
	
	private void createOrganization()
	{
		// get the organization name
		String name = JOptionPane.showInputDialog(null, "Please enter name of new organization", "New Organization", JOptionPane.PLAIN_MESSAGE);
		if( (name == null) || (name.length() == 0) ) return;
		
		ModelManager.getSingleton().createNewOrganization(name, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) OrgAndDevTypeTreeContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error creating new organization", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void deleteOrganization(OrganizationWrapper orgIn)
	{
		ModelManager.getSingleton().deleteOrganization(orgIn, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) OrgAndDevTypeTreeContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error deleting organization", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void addDeviceTypeToOrganization(OrganizationWrapper orgIn)
	{
		// get the device name
		String name = JOptionPane.showInputDialog(null, "Please enter name of new device type", "New Device Type", JOptionPane.PLAIN_MESSAGE);
		if( (name == null) || (name.length() == 0) ) return;
		
		ModelManager.getSingleton().addDeviceTypeToOrganization(name, orgIn, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) OrgAndDevTypeTreeContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error adding device type to organization", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void deleteDeviceType(DeviceTypeWrapper devIn)
	{
		ModelManager.getSingleton().deleteDeviceType(devIn, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) OrgAndDevTypeTreeContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error deleting device type", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
