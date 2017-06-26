package net.otaupdate.app.ui.main;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import net.otaupdate.app.model.DeviceWrapper;
import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.RefreshTreeCallback;
import net.otaupdate.app.ui.main.TreeViewContextMenu.TreeViewContextMenuListener;
import net.otaupdate.app.model.OrganizationWrapper;
import net.otaupdate.app.model.ProcessorWrapper;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class OtaTreeView extends JPanel implements TreeViewContextMenuListener
{
	private static final long serialVersionUID = -191603825660058155L;


	public interface OtaTreeViewListener
	{
		public void onOrganizationSelected(OrganizationWrapper orgIn);
		
		public void onDeviceSelected(DeviceWrapper devIn);
		
		public void onProcessorSelected(ProcessorWrapper procIn);
		
		public void onFirmwareImageSelected(FwImageWrapper fwImageIn, List<FwImageWrapper> allFwImagesIn);
		
		public void onUploadFirmwareImageSelected();
		
		public void onDeselection();
	}
	
	
	private final JTree tree;
	private DefaultMutableTreeNode organizations;
	
	private List<OtaTreeViewListener> listeners = new ArrayList<>();
	
	
	public OtaTreeView()
	{
		setLayout(new BorderLayout(0, 0));
		
		// setup our tree
		this.organizations = new DefaultMutableTreeNode("organizations");
		tree = new JTree(this.organizations);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				
				if( (node != null) && (node.getUserObject() instanceof OrganizationWrapper) )
				{
					OtaTreeView.this.notifyListeners_orgSelect(((OrganizationWrapper)node.getUserObject()));
				}
				else if( (node != null) && (node.getUserObject() instanceof DeviceWrapper) )
				{
					OtaTreeView.this.notifyListeners_devSelect(((DeviceWrapper)node.getUserObject()));
				}
				else if( (node != null) && (node.getUserObject() instanceof ProcessorWrapper) )
				{
					OtaTreeView.this.notifyListeners_procSelect(((ProcessorWrapper)node.getUserObject()));
				}
				else if( (node != null) && (node.getUserObject() instanceof FwImageWrapper) )
				{
					OtaTreeView.this.notifyListeners_fwSelect(((FwImageWrapper)node.getUserObject()), OtaTreeView.this.getAllFirmwaresGivenFirmwareNode(node));
				}
				else
				{
					OtaTreeView.this.notifyListeners_deselect();
					return;
				}
			}
		});
		add(tree, BorderLayout.CENTER);
		
		TreeViewContextMenu tvcm = new TreeViewContextMenu(this);
		tvcm.addListener(this);
		this.tree.setComponentPopupMenu(tvcm);
		
		JPanel pnlButtons = new JPanel();
		add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setLayout(new BorderLayout(0, 0));
		
		JButton btnRefresh = new JButton("Refresh");
		pnlButtons.add(btnRefresh, BorderLayout.CENTER);
		btnRefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				OtaTreeView.this.refresh();
			}
		});
	}

	
	public void refresh()
	{
		ModelManager.getSingleton().refreshTree(new RefreshTreeCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn, List<OrganizationWrapper> organizationsIn)
			{
				if( !wasSuccessfulIn )
				{
					JOptionPane.showMessageDialog(OtaTreeView.this.getParent(), "Error refreshing", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				// if we made it here, we were successful
				OtaTreeView.this.organizations.removeAllChildren();
				if( (organizationsIn == null) || (organizationsIn.isEmpty()) )
				{
					OtaTreeView.this.organizations.add(new DefaultMutableTreeNode("<none>"));
				}
				else
				{
					for( OrganizationWrapper currOrg : organizationsIn )
					{
						DefaultMutableTreeNode orgNode = new DefaultMutableTreeNode(currOrg);
						
						for( DeviceWrapper currDevice : currOrg.getDevices() )
						{
							DefaultMutableTreeNode devNode = new DefaultMutableTreeNode(currDevice);
							
							for( ProcessorWrapper currProc : currDevice.getProcessors() )
							{
								DefaultMutableTreeNode procNode = new DefaultMutableTreeNode(currProc);
								
								for( FwImageWrapper currFwImage : currProc.getFirmwareImages() )
								{
									DefaultMutableTreeNode fwNode = new DefaultMutableTreeNode(currFwImage);
									
									procNode.add(fwNode);
								}
								
								devNode.add(procNode);
							}
							
							orgNode.add(devNode);
						}
						
						OtaTreeView.this.organizations.add(orgNode);
					}
				}
				
				((DefaultTreeModel)OtaTreeView.this.tree.getModel()).reload();
			}
		});
	}
	
	
	public void addListener(OtaTreeViewListener listenerIn)
	{
		this.listeners.add(listenerIn);
	}
	
	
	public Object getSelectedItem()
	{
		return ((DefaultMutableTreeNode)this.tree.getLastSelectedPathComponent()).getUserObject();
	}
	
	
	@Override
	public void onUploadFirmwareImageSelected()
	{
		this.notifyListeners_fwUploadSelect();
	}
	
	
	private List<FwImageWrapper> getAllFirmwaresGivenFirmwareNode(DefaultMutableTreeNode nodeIn)
	{
		TreeNode procNode = nodeIn.getParent();
		if( procNode == null ) return null;
		
		List<FwImageWrapper> retVal = new ArrayList<FwImageWrapper>();
		Enumeration<?> en = procNode.children();
		while( en.hasMoreElements() )
		{
			Object currObject = ((DefaultMutableTreeNode)en.nextElement()).getUserObject();
			if( currObject instanceof FwImageWrapper ) retVal.add((FwImageWrapper)currObject);
		}
		
		return retVal;
	}
	
	
	private void notifyListeners_deselect()
	{
		for( OtaTreeViewListener currListener : OtaTreeView.this.listeners )
		{
			currListener.onDeselection();
		}
	}
	
	
	private void notifyListeners_orgSelect(OrganizationWrapper aiIn)
	{
		for( OtaTreeViewListener currListener : OtaTreeView.this.listeners )
		{
			currListener.onOrganizationSelected(aiIn);
		}
	}
	
	
	private void notifyListeners_devSelect(DeviceWrapper aiIn)
	{
		for( OtaTreeViewListener currListener : OtaTreeView.this.listeners )
		{
			currListener.onDeviceSelected(aiIn);
		}
	}
	
	
	private void notifyListeners_procSelect(ProcessorWrapper aiIn)
	{
		for( OtaTreeViewListener currListener : OtaTreeView.this.listeners )
		{
			currListener.onProcessorSelected(aiIn);
		}
	}
	
	
	private void notifyListeners_fwSelect(FwImageWrapper aiIn, List<FwImageWrapper> allFwImagesIn)
	{
		for( OtaTreeViewListener currListener : OtaTreeView.this.listeners )
		{
			currListener.onFirmwareImageSelected(aiIn, allFwImagesIn);
		}
	}
	
	
	private void notifyListeners_fwUploadSelect()
	{
		for( OtaTreeViewListener currListener : OtaTreeView.this.listeners )
		{
			currListener.onUploadFirmwareImageSelected();
		}
	}
}
