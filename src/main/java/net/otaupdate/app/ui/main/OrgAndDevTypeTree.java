package net.otaupdate.app.ui.main;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.RefreshOrgAndDevTypeTreeCallback;
import net.otaupdate.app.model.OrganizationWrapper;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class OrgAndDevTypeTree extends JPanel
{
	private static final long serialVersionUID = -191603825660058155L;


	public interface OtaTreeViewListener
	{
		public void onOrganizationSelected(OrganizationWrapper orgIn);
		
		public void onDeviceSelected(DeviceTypeWrapper devIn);
		
		public void onDeselection();
	}
	
	
	private final JTree tree;
	private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Organizations", true);
	private final List<OtaTreeViewListener> listeners = new ArrayList<>();
	
	
	public OrgAndDevTypeTree()
	{
		setLayout(new BorderLayout(0, 0));
		
		// setup our tree
		tree = new JTree(rootNode);
		((DefaultTreeModel)this.tree.getModel()).setAsksAllowsChildren(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				
				if( (node != null) && (node.getUserObject() instanceof OrganizationWrapper) )
				{
					OrgAndDevTypeTree.this.notifyListeners_orgSelect(((OrganizationWrapper)node.getUserObject()));
				}
				else if( (node != null) && (node.getUserObject() instanceof DeviceTypeWrapper) )
				{
					OrgAndDevTypeTree.this.notifyListeners_devSelect(((DeviceTypeWrapper)node.getUserObject()));
				}
				else
				{
					OrgAndDevTypeTree.this.notifyListeners_deselect();
					return;
				}
			}
		});
		add(tree, BorderLayout.CENTER);
		
		OrgAndDevTypeTreeContextMenu tvcm = new OrgAndDevTypeTreeContextMenu(this);
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
				OrgAndDevTypeTree.this.refresh();
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
	
	
	public void refresh()
	{
		ModelManager.getSingleton().refreshOrgAndDevTypeTree(new RefreshOrgAndDevTypeTreeCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn, List<OrganizationWrapper> organizationsIn)
			{
				if( !wasSuccessfulIn )
				{
					JOptionPane.showMessageDialog(null, "Error refreshing", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// if we made it here, we were successful
				OrgAndDevTypeTree.this.rootNode.removeAllChildren();
				for( OrganizationWrapper currOrg : organizationsIn )
				{
					DefaultMutableTreeNode newOrgNode = new DefaultMutableTreeNode(currOrg);
					for( DeviceTypeWrapper currDevType :currOrg.getDevTypes() )
					{
						newOrgNode.add(new DefaultMutableTreeNode(currDevType, false));
					}
					OrgAndDevTypeTree.this.rootNode.add(newOrgNode);
				}
				
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						((DefaultTreeModel)OrgAndDevTypeTree.this.tree.getModel()).reload();
						OrgAndDevTypeTree.expandAllNodes(OrgAndDevTypeTree.this.tree, 0, OrgAndDevTypeTree.this.tree.getRowCount());
					}
				});
			}
		});
	}
	
	
	private void notifyListeners_deselect()
	{
		for( OtaTreeViewListener currListener : OrgAndDevTypeTree.this.listeners )
		{
			currListener.onDeselection();
		}
	}
	
	
	private void notifyListeners_orgSelect(OrganizationWrapper aiIn)
	{
		for( OtaTreeViewListener currListener : OrgAndDevTypeTree.this.listeners )
		{
			currListener.onOrganizationSelected(aiIn);
		}
	}
	
	
	private void notifyListeners_devSelect(DeviceTypeWrapper aiIn)
	{
		for( OtaTreeViewListener currListener : OrgAndDevTypeTree.this.listeners )
		{
			currListener.onDeviceSelected(aiIn);
		}
	}
	
	
	private static void expandAllNodes(JTree tree, int startingIndex, int rowCount)
	{
	    for( int i = startingIndex; i<rowCount; ++i )
	    {
	        tree.expandRow(i);
	    }

	    if( tree.getRowCount()!=rowCount )
	    {
	        expandAllNodes(tree, rowCount, tree.getRowCount());
	    }
	}
}
