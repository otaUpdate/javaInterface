package net.otaupdate.app.ui.main;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.otaupdate.app.model.DeviceWrapper;
import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.CreateFwImageCallback;
import net.otaupdate.app.model.ModelManager.SimpleCallback;
import net.otaupdate.app.model.ModelManager.UploadFwImageCallback;
import net.otaupdate.app.ui.util.ProgressDialog;
import net.otaupdate.app.model.OrganizationWrapper;
import net.otaupdate.app.model.ProcessorWrapper;

public class TreeViewContextMenu extends JPopupMenu implements PopupMenuListener
{
	private static final long serialVersionUID = -1274596196172586854L;
	private static final String KEY_LASTDIR_FW = "lastDirectory_fw";
	
	
	public interface TreeViewContextMenuListener
	{
		public abstract void onUploadFirmwareImageSelected();
	}
	
	
	private final OtaTreeView parent;
	private JMenuItem mnuAdd;
	private JMenuItem mnuDelete;
	
	private final List<TreeViewContextMenuListener> listeners = new ArrayList<TreeViewContextMenuListener>();
	
	private Object selectedItem = null;
	

	public TreeViewContextMenu(OtaTreeView parentIn)
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
				TreeViewContextMenu.this.addItem();
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
				TreeViewContextMenu.this.removeItem();
			}
		});
		this.add(mnuDelete);
	}
	
	
	public void addListener(TreeViewContextMenuListener listenerIn)
	{
		this.listeners.add(listenerIn);
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
			this.mnuAdd.setText("Add Device");
			this.mnuAdd.setVisible(true);
			this.mnuDelete.setVisible(true);
		}
		else if( selectedItem instanceof DeviceWrapper )
		{
			this.mnuAdd.setText("Add Processor");
			this.mnuAdd.setVisible(true);
			this.mnuDelete.setVisible(true);
		}
		else if( selectedItem instanceof ProcessorWrapper )
		{
			this.mnuAdd.setText("Add Firmware Image");
			this.mnuAdd.setVisible(true);
			this.mnuDelete.setVisible(true);
		}
		else if( selectedItem instanceof FwImageWrapper )
		{
			this.mnuAdd.setVisible(false);
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
			this.addDeviceToOrganization((OrganizationWrapper)this.selectedItem);
		}
		else if( this.selectedItem instanceof DeviceWrapper )
		{
			this.addProcessorToDevice((DeviceWrapper)this.selectedItem);
		}
		else if( this.selectedItem instanceof ProcessorWrapper )
		{
			this.addFirmwareImageToProcessor((ProcessorWrapper)this.selectedItem);
		}
	}
	
	
	private void removeItem()
	{
		if( this.selectedItem instanceof OrganizationWrapper )
		{
			this.deleteOrganization((OrganizationWrapper)this.selectedItem);
		}
		else if( this.selectedItem instanceof DeviceWrapper )
		{
			this.deleteDevice((DeviceWrapper)this.selectedItem);
		}
		else if( this.selectedItem instanceof ProcessorWrapper )
		{
			this.deleteProcessor((ProcessorWrapper)this.selectedItem);
		}
		else if( this.selectedItem instanceof FwImageWrapper )
		{
			this.deleteFirmwareImage((FwImageWrapper)this.selectedItem);
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
				if( wasSuccessfulIn ) TreeViewContextMenu.this.parent.refresh();
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
				if( wasSuccessfulIn ) TreeViewContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error deleting organization", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void addDeviceToOrganization(OrganizationWrapper orgIn)
	{
		// get the device name
		String name = JOptionPane.showInputDialog(null, "Please enter name of new device", "New Device", JOptionPane.PLAIN_MESSAGE);
		if( (name == null) || (name.length() == 0) ) return;
		
		ModelManager.getSingleton().addDeviceToOrganization(name, orgIn, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) TreeViewContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error adding device to organization", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void deleteDevice(DeviceWrapper devIn)
	{
		ModelManager.getSingleton().deleteDevice(devIn, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) TreeViewContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error deleting device", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void addProcessorToDevice(DeviceWrapper devIn)
	{
		// get the processor name
		String name = JOptionPane.showInputDialog(null, "Please enter name of new processor", "New Processor", JOptionPane.PLAIN_MESSAGE);
		if( (name == null) || (name.length() == 0) ) return;
		
		ModelManager.getSingleton().addProcessorToDevice(name, devIn, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) TreeViewContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error adding processor", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void deleteProcessor(ProcessorWrapper procIn)
	{
		ModelManager.getSingleton().deleteProcessor(procIn, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) TreeViewContextMenu.this.parent.refresh();
				else JOptionPane.showMessageDialog(null, "Error deleting processor", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void addFirmwareImageToProcessor(ProcessorWrapper procWrapperIn)
	{
		// get the firmware image name
		String name = JOptionPane.showInputDialog(null, "Please enter name of new firmware image", "New Firmware Image", JOptionPane.PLAIN_MESSAGE);
		if( (name == null) || (name.length() == 0) ) return;
		
		// if we made it here, we're gonna try to create a new firmware image
		String uuid_proc = procWrapperIn.getModelObject().getUuid();
		String uuid_dev = procWrapperIn.getParent().getModelObject().getUuid();
		String uuid_org = procWrapperIn.getParent().getParent().getModelObject().getUuid();
		
		// now we need to create the firmware image (so we can get a UUID for it)
		ModelManager.getSingleton().createNewFwImage(name, uuid_proc, uuid_dev, uuid_org, new CreateFwImageCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn, String newFwUuidIn )
			{
				if( wasSuccessfulIn )
				{
					Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
					clpbrd.setContents(new StringSelection(newFwUuidIn), null);
					
					// now we should display the new UUID to the user
					JOptionPane.showMessageDialog(null, String.format("New firmware UUID:\n\n%s\n\nUUID has been copied to the clipboard.", newFwUuidIn), 
												  "Firmware Created Successfully", JOptionPane.INFORMATION_MESSAGE);
					
					// needs to be run on main thread
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							String defaultDir = Preferences.userNodeForPackage(TreeViewContextMenu.class).get(KEY_LASTDIR_FW, null);
							
							// now we need to choose the file
							JFileChooser jfc = new JFileChooser(defaultDir);
							jfc.setDialogTitle("Select Firmware Binary");
							if( jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION )
							{
								// save the parent directory for later
								Preferences.userNodeForPackage(TreeViewContextMenu.class).put(KEY_LASTDIR_FW, jfc.getSelectedFile().getParent());
								
								// file was chosen...upload it
								ProgressDialog pd = new ProgressDialog();
								pd.setVisible(true);
								ModelManager.getSingleton().uploadFirmwareImage(newFwUuidIn, uuid_proc, uuid_dev, uuid_org, jfc.getSelectedFile(), new UploadFwImageCallback()
								{
									@Override
									public void onProgressUpdate(long totalNumBytesWrittenIn, long totalNumBytesExpected)
									{
										pd.updateProgress((float)totalNumBytesWrittenIn / (float)totalNumBytesExpected,
												  String.format("%d / %d bytes", totalNumBytesWrittenIn, totalNumBytesExpected));
									}
									
									@Override
									public void onCompletion(boolean wasSuccessfulIn)
									{
										pd.setVisible(false);
										pd.dispose();
										
										if( wasSuccessfulIn )
										{
											JOptionPane.showMessageDialog(null, "Firmware image upload complete", "Upload complete", JOptionPane.INFORMATION_MESSAGE);
										}
										else
										{
											JOptionPane.showMessageDialog(null, "Error creating firmware image", "Error", JOptionPane.ERROR_MESSAGE);
										}
										TreeViewContextMenu.this.parent.refresh();
									}
								});
							}	
							TreeViewContextMenu.this.parent.refresh();
						}
					});
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Error creating firmware image", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	
	private void deleteFirmwareImage(FwImageWrapper fwIn)
	{
		ModelManager.getSingleton().deleteFirmwareImage(fwIn, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn )
				{
					TreeViewContextMenu.this.parent.refresh();
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Error deleting firmware image", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}
