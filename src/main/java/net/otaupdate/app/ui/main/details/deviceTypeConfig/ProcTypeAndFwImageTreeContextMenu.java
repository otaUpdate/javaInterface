package net.otaupdate.app.ui.main.details.deviceTypeConfig;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.CreateFwImageCallback;
import net.otaupdate.app.model.ModelManager.SimpleCallback;
import net.otaupdate.app.model.ModelManager.UploadFwImageCallback;
import net.otaupdate.app.ui.util.ProgressDialog;
import net.otaupdate.app.model.ProcessorTypeWrapper;

public class ProcTypeAndFwImageTreeContextMenu extends JPopupMenu implements PopupMenuListener
{
	private static final long serialVersionUID = -1274596196172586854L;
	private static final String KEY_LASTDIR_FW = "lastFwDir";
	
	
	private final DeviceTypeConfigurationPanel parent;
	private JMenuItem mnuAdd;
	private JMenuItem mnuDelete;
	
	private Object selectedItem = null;
	

	public ProcTypeAndFwImageTreeContextMenu(DeviceTypeConfigurationPanel parentIn)
	{
		this.parent = parentIn;
		this.addPopupMenuListener(this);
		
		mnuAdd = new JMenuItem("Add");
		this.mnuAdd.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ProcTypeAndFwImageTreeContextMenu.this.addItem();
			}
		});
		this.add(mnuAdd);
		
		mnuDelete = new JMenuItem("Delete Selected Item");
		this.mnuDelete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ProcTypeAndFwImageTreeContextMenu.this.removeItem();
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
			this.mnuAdd.setText("Add Processor");
			this.mnuAdd.setVisible(true);
			this.mnuDelete.setVisible(false);
		}
		else if( selectedItem instanceof ProcessorTypeWrapper )
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
			this.addProcessorType();
		}
		else if( this.selectedItem instanceof ProcessorTypeWrapper )
		{
			this.addFirmwareImage();
		}
	}
	
	
	private void removeItem()
	{
		if( this.selectedItem instanceof ProcessorTypeWrapper )
		{
			this.deleteProcessorType();
		}
		else if( this.selectedItem instanceof FwImageWrapper )
		{
			this.deleteFirmwareImage();
		}
	}
	
	
	private void addProcessorType()
	{
		// get the organization name
		String name = JOptionPane.showInputDialog(null, "Please enter name of new processor", "New Processor", JOptionPane.PLAIN_MESSAGE);
		if( (name == null) || (name.length() == 0) ) return;
		
		ModelManager.getSingleton().addProcessorTypeToDevice(name, this.parent.getDeviceType(), new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn )
				{
					ProcTypeAndFwImageTreeContextMenu.this.parent.refresh();
				}
				else JOptionPane.showMessageDialog(null, "Error adding processor type to device", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void deleteProcessorType()
	{
		ModelManager.getSingleton().deleteProcessorType((ProcessorTypeWrapper)this.selectedItem, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn )
				{
					ProcTypeAndFwImageTreeContextMenu.this.parent.refresh();
				}
				else JOptionPane.showMessageDialog(null, "Error deleting processor type", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	private void addFirmwareImage()
	{
		ProcessorTypeWrapper ptw = (ProcessorTypeWrapper)this.selectedItem; 
		
		// get the firmware image name
		String name = JOptionPane.showInputDialog(null, "Please enter name of new firmware image", "New Firmware Image", JOptionPane.PLAIN_MESSAGE);
		if( (name == null) || (name.length() == 0) ) return;
		
		// if we made it here, we're gonna try to create a new firmware image
		String uuid_proc = ptw.getUuid();
		String uuid_dev = ptw.getDevTypeUuid();
		String uuid_org = ptw.getOrgUuid();
		
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
							String defaultDir = Preferences.userNodeForPackage(ProcTypeAndFwImageTreeContextMenu.class).get(KEY_LASTDIR_FW, null);
							
							// now we need to choose the file
							JFileChooser jfc = new JFileChooser(defaultDir);
							jfc.setDialogTitle("Select Firmware Binary");
							if( jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION )
							{
								// save the parent directory for later
								Preferences.userNodeForPackage(ProcTypeAndFwImageTreeContextMenu.class).put(KEY_LASTDIR_FW, jfc.getSelectedFile().getParent());
								
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
										ProcTypeAndFwImageTreeContextMenu.this.parent.refresh();
									}
								});
							}
							ProcTypeAndFwImageTreeContextMenu.this.parent.refresh();
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
	
	
	private void deleteFirmwareImage()
	{
		ModelManager.getSingleton().deleteFirmwareImage((FwImageWrapper)this.selectedItem, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn )
				{
					ProcTypeAndFwImageTreeContextMenu.this.parent.refresh();
				}
				else JOptionPane.showMessageDialog(null, "Error deleting firmware image", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
