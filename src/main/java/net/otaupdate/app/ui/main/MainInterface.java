package net.otaupdate.app.ui.main;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Component;

import net.otaupdate.app.model.DeviceWrapper;
import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.OrganizationWrapper;
import net.otaupdate.app.model.ProcessorWrapper;
import net.otaupdate.app.sdk.model.DeviceArrayItem;
import net.otaupdate.app.sdk.model.FwImageArrayItem;
import net.otaupdate.app.sdk.model.OrganizationArrayItem;
import net.otaupdate.app.sdk.model.ProcessorArrayItem;
import net.otaupdate.app.ui.cardmanager.CardManager;
import net.otaupdate.app.ui.cardmanager.CardManager.CardTransitionCallback;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;
import net.otaupdate.app.ui.main.OtaTreeView.OtaTreeViewListener;
import net.otaupdate.app.ui.main.details.DeviceDetailsCard;
import net.otaupdate.app.ui.main.details.OrganizationDetailsCard;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import net.otaupdate.app.ui.main.details.ProcessorDetailsCard;


public class MainInterface extends JPanel implements IntelligentCard, OtaTreeViewListener
{
	private static final long serialVersionUID = -5699429600721235299L;
	private static final String CARD_NO_SELECTION = "noSelection";
	private static final String CARD_ORGANIZATION_DETAILS = "orgDetails";
	private static final String CARD_DEVICE_DETAILS = "devDetails";
	private static final String CARD_PROCESSOR_DETAILS = "procDetails";
	
	
	private final CardManager cardManager;
	private final OtaTreeView otaTreeView;
	
	
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
		
		DeviceDetailsCard deviceDetailsCard = new DeviceDetailsCard();
		cardManager.add(deviceDetailsCard, CARD_DEVICE_DETAILS);
		
		ProcessorDetailsCard processorDetailsCard = new ProcessorDetailsCard();
		cardManager.add(processorDetailsCard, CARD_PROCESSOR_DETAILS);
		
		otaTreeView = new OtaTreeView();
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
	public void onDeviceSelected(DeviceWrapper devIn)
	{
		this.cardManager.showCard(CARD_DEVICE_DETAILS, new CardTransitionCallback()
		{
			@Override
			public void onCardTransition(Component oldComponentIn, Component newComponentIn)
			{
				((DeviceDetailsCard)newComponentIn).setDevice(devIn);
			}
		});
	}
	
	
	@Override
	public void onProcessorSelected(ProcessorWrapper procIn)
	{
		this.cardManager.showCard(CARD_PROCESSOR_DETAILS, new CardTransitionCallback()
		{
			@Override
			public void onCardTransition(Component oldComponentIn, Component newComponentIn)
			{
				((ProcessorDetailsCard)newComponentIn).setProcessor(procIn);
			}
		});
	}
	
	
	@Override
	public void onFirmwareImageSelected(FwImageWrapper fwImageIn)
	{
		
	}


	@Override
	public void onDeselection()
	{
		this.cardManager.showCard(CARD_NO_SELECTION);
	}


	@Override
	public void onBecomesVisible()
	{
		this.otaTreeView.refresh();
	}
}
