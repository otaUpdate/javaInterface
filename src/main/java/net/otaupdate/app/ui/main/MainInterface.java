package net.otaupdate.app.ui.main;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Component;

import net.otaupdate.app.sdk.model.OrganizationArrayItem;
import net.otaupdate.app.ui.cardmanager.CardManager;
import net.otaupdate.app.ui.cardmanager.CardManager.CardTransitionCallback;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;
import net.otaupdate.app.ui.main.OtaTreeView.OtaTreeViewListener;
import net.otaupdate.app.ui.main.organization.OrganizationDetailsCard;

import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class MainInterface extends JPanel implements IntelligentCard, OtaTreeViewListener
{
	private static final long serialVersionUID = -5699429600721235299L;
	private static final String CARD_NO_SELECTION = "noSelection";
	private static final String CARD_ORGANIZATION_DETAILS = "orgDetails";
	
	
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
		
		otaTreeView = new OtaTreeView();
		this.otaTreeView.addListener(this);
		splitPane.setLeftComponent(otaTreeView);
	}


	@Override
	public void onOrganizationSelected(OrganizationArrayItem orgIn)
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
