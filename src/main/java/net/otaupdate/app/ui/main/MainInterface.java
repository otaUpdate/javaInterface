package net.otaupdate.app.ui.main;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.OrganizationWrapper;
import net.otaupdate.app.model.ProcessorTypeWrapper;
import net.otaupdate.app.ui.cardmanager.CardManager;
import net.otaupdate.app.ui.cardmanager.CardManager.CardTransitionCallback;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;
import net.otaupdate.app.ui.main.OtaTreeView.OtaTreeViewListener;
import net.otaupdate.app.ui.main.details.DeviceDetailsCard;
import net.otaupdate.app.ui.main.details.OrganizationDetailsCard;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import net.otaupdate.app.ui.main.details.ProcessorDetailsCard;
import net.otaupdate.app.ui.main.details.FwImageDetailsCard;
import java.awt.Dimension;


public class MainInterface extends JPanel implements IntelligentCard, OtaTreeViewListener
{
	private static final long serialVersionUID = -5699429600721235299L;
	private static final String CARD_NO_SELECTION = "noSelection";
	private static final String CARD_ORGANIZATION_DETAILS = "orgDetails";
	private static final String CARD_DEVICE_DETAILS = "devDetails";
	private static final String CARD_PROCESSOR_DETAILS = "procDetails";
	private static final String CARD_FW_DETAILS = "fwDetails";
	
	
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
		
		FwImageDetailsCard fwImageDetailsCard = new FwImageDetailsCard();
		cardManager.add(fwImageDetailsCard, CARD_FW_DETAILS);
		
		otaTreeView = new OtaTreeView();
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
				((DeviceDetailsCard)newComponentIn).setDevice(devIn);
			}
		});
	}
	
	
	@Override
	public void onProcessorSelected(ProcessorTypeWrapper procIn)
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
	public void onFirmwareImageSelected(FwImageWrapper fwImageIn, List<FwImageWrapper> allFwImagesIn)
	{
		this.cardManager.showCard(CARD_FW_DETAILS, new CardTransitionCallback()
		{
			@Override
			public void onCardTransition(Component oldComponentIn, Component newComponentIn)
			{
				((FwImageDetailsCard)newComponentIn).setFwImage(fwImageIn, allFwImagesIn);
			}
		});
	}
	
	
	@Override
	public void onUploadFirmwareImageSelected()
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
