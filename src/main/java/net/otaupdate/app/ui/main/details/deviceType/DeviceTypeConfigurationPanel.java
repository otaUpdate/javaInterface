package net.otaupdate.app.ui.main.details.deviceType;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.ProcessorTypeWrapper;
import net.otaupdate.app.ui.cardmanager.CardManager;
import net.otaupdate.app.ui.cardmanager.CardManager.CardTransitionCallback;

import java.awt.Dimension;


public class DeviceTypeConfigurationPanel extends JPanel
{
	private static final long serialVersionUID = -5748995595841354127L;
	private static final String CARD_NO_SELECTION = "noSelection";
	private static final String CARD_PROCESSOR = "processor";
	private static final String CARD_FWIMAGE = "fwImage";
	
	
	private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Processors", true);
	private final JTree tree;
	private final CardManager cardManager;
	
	private DeviceTypeWrapper dtw = null;
	
	
	public DeviceTypeConfigurationPanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		this.cardManager = new CardManager();
		this.add(this.cardManager, BorderLayout.CENTER);
		
		JPanel pnlNoSelection = new JPanel();
		cardManager.add(pnlNoSelection, CARD_NO_SELECTION);
		pnlNoSelection.setLayout(new BorderLayout(0, 0));
		JLabel lblPleaseSelectAn = new JLabel("Please select an item to the left");
		lblPleaseSelectAn.setHorizontalAlignment(SwingConstants.CENTER);
		pnlNoSelection.add(lblPleaseSelectAn, BorderLayout.CENTER);
		
		this.cardManager.add(CARD_NO_SELECTION, pnlNoSelection);
		this.cardManager.add(CARD_PROCESSOR, new ProcessorDetailsCard());
		this.cardManager.add(CARD_FWIMAGE, new FwImageDetailsCard());
		
		this.tree = new JTree(rootNode);
		((DefaultTreeModel)this.tree.getModel()).setAsksAllowsChildren(true);
		this.tree.setPreferredSize(new Dimension(160, 19));
		this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				
				if( (node != null) && (node.getUserObject() instanceof ProcessorTypeWrapper) )
				{
					DeviceTypeConfigurationPanel.this.cardManager.showCard(CARD_PROCESSOR, new CardTransitionCallback()
					{
						@Override
						public void onCardTransition(Component oldComponentIn, Component newComponentIn)
						{
							((ProcessorDetailsCard)newComponentIn).setProcessor((ProcessorTypeWrapper)node.getUserObject());
						}
					});
				}
				else if( (node != null) && (node.getUserObject() instanceof FwImageWrapper) )
				{
					DeviceTypeConfigurationPanel.this.cardManager.showCard(CARD_FWIMAGE, new CardTransitionCallback()
					{
						@Override
						public void onCardTransition(Component oldComponentIn, Component newComponentIn)
						{
							FwImageWrapper fiw = (FwImageWrapper)node.getUserObject();
							
							((FwImageDetailsCard)newComponentIn).setFwImage(fiw, fiw.getListOfPeerFwImages());
						}
					});
				}
				else
				{
					DeviceTypeConfigurationPanel.this.cardManager.showCard(CARD_NO_SELECTION);
					return;
				}
			}
		});
		add(this.tree, BorderLayout.WEST);
		
		ProcTypeAndFwImageTreeContextMenu popupMenu = new ProcTypeAndFwImageTreeContextMenu(this);
		this.tree.setComponentPopupMenu(popupMenu);
		
	}
	
	
	public void setDeviceType(DeviceTypeWrapper dtwIn)
	{
		this.dtw = dtwIn;
		
		this.refresh();
	}
	
	
	protected Object getSelectedItem()
	{
		return ((DefaultMutableTreeNode)this.tree.getLastSelectedPathComponent()).getUserObject();
	}
	
	
	protected DeviceTypeWrapper getDeviceType()
	{
		return this.dtw;
	}
	
	
	protected void refresh()
	{
		// update our data model
		this.rootNode.removeAllChildren();
		for( ProcessorTypeWrapper currProcType : this.dtw.getProcTypes() )
		{
			DefaultMutableTreeNode newProcNode = new DefaultMutableTreeNode(currProcType);
			for( FwImageWrapper currFwImage : currProcType.getFwImages() )
			{
				newProcNode.add(new DefaultMutableTreeNode(currFwImage, false));
			}
			this.rootNode.add(newProcNode);
		}
		DeviceTypeConfigurationPanel.expandAllNodes(this.tree, 0, this.tree.getRowCount());
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				((DefaultTreeModel)DeviceTypeConfigurationPanel.this.tree.getModel()).reload();				
			}
		});
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
