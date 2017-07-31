package net.otaupdate.app.ui.main;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Component;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.OrganizationWrapper;
import net.otaupdate.app.ui.cardmanager.CardManager;
import net.otaupdate.app.ui.cardmanager.CardManager.CardTransitionCallback;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;
import net.otaupdate.app.ui.main.OrgAndDevTypeTree.OtaTreeViewListener;
import net.otaupdate.app.ui.main.details.DeviceTypeDetailsCard;
import net.otaupdate.app.ui.main.details.OrganizationDetailsCard;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.otaupdate.app.ui.main.details.deviceType.FwImageDetailsCard;
import net.otaupdate.app.ui.main.details.deviceType.ProcessorDetailsCard;

import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;


public class MainInterface extends JPanel implements IntelligentCard, OtaTreeViewListener
{
	private static final long serialVersionUID = -5699429600721235299L;
	private static final String CARD_NO_SELECTION = "noSelection";
	private static final String CARD_ORGANIZATION_DETAILS = "orgDetails";
	private static final String CARD_DEVICE_DETAILS = "devDetails";
	private static final String CARD_PROCESSOR_DETAILS = "procDetails";
	private static final String CARD_FW_DETAILS = "fwDetails";
	
	
	private final CardManager cardManager;
	private final OrgAndDevTypeTree otaTreeView;
	
	
	public MainInterface()
	{
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		add(splitPane);
		
		cardManager = new CardManager();
		splitPane.setRightComponent(cardManager);
		
		JPanel pnlNoSelection = new JPanel();
		cardManager.add(pnlNoSelection, CARD_NO_SELECTION);
		pnlNoSelection.setLayout(new BorderLayout(0, 0));
		
		OrganizationDetailsCard organizationDetailsCard = new OrganizationDetailsCard();
		cardManager.add(organizationDetailsCard, CARD_ORGANIZATION_DETAILS);
		
		JLabel lblPleaseSelectAn = new JLabel("Please select an item to the left");
		lblPleaseSelectAn.setHorizontalAlignment(SwingConstants.CENTER);
		pnlNoSelection.add(lblPleaseSelectAn, BorderLayout.CENTER);
		
		DeviceTypeDetailsCard deviceDetailsCard = new DeviceTypeDetailsCard();
		cardManager.add(deviceDetailsCard, CARD_DEVICE_DETAILS);
		
		ProcessorDetailsCard processorDetailsCard = new ProcessorDetailsCard();
		cardManager.add(processorDetailsCard, CARD_PROCESSOR_DETAILS);
		
		FwImageDetailsCard fwImageDetailsCard = new FwImageDetailsCard();
		cardManager.add(fwImageDetailsCard, CARD_FW_DETAILS);
		
		otaTreeView = new OrgAndDevTypeTree();
		otaTreeView.setMinimumSize(new Dimension(180, 29));
		this.otaTreeView.addListener(this);
		splitPane.setLeftComponent(otaTreeView);
	}


	@Override
	public void onOrganizationSelected(OrganizationWrapper orgIn)
	{
		this.cardManager.showCard(CARD_ORGANIZATION_DETAILS, new CardTransitionCallback()
		{
			@Override
			public void onCardTransition(Component oldComponentIn, Component newComponentIn)
			{
				((OrganizationDetailsCard)newComponentIn).setOrganization(orgIn);
			}
		});
	}
	
	
	@Override
	public void onDeviceSelected(DeviceTypeWrapper devIn)
	{
		this.cardManager.showCard(CARD_DEVICE_DETAILS, new CardTransitionCallback()
		{
			@Override
			public void onCardTransition(Component oldComponentIn, Component newComponentIn)
			{
				((DeviceTypeDetailsCard)newComponentIn).setDevice(devIn);
			}
		});
	}


	@Override
	public void onDeselection()
	{
		this.cardManager.showCard(CARD_NO_SELECTION);
	}


	@Override
	public void onBecomesVisible()
	{
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						MainInterface.this.otaTreeView.refresh();
					}
				});
			}
		}, 1000);
	}
}
