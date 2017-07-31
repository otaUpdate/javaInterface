package net.otaupdate.app.ui.main.details;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.SimpleCallback;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;
import net.otaupdate.app.ui.main.details.deviceType.DeviceTypeConfigurationPanel;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;


public class DeviceTypeDetailsCard extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = -8758246768261616073L;
	
	
	private final JTextField txtName;
	private final JLabel lblUuidValue;
	
	private DeviceTypeWrapper dtw = null;


	private DeviceTypeConfigurationPanel devTypeConfigurationPanel;

	
	public DeviceTypeDetailsCard()
	{
		setBorder(new EmptyBorder(4, 0, 4, 4));
		setLayout(new BorderLayout(0, 0));
		
		JPanel pnlDevDetails = new JPanel();
		pnlDevDetails.setBorder(new EmptyBorder(0, 0, 8, 4));
		add(pnlDevDetails, BorderLayout.NORTH);
		pnlDevDetails.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblName = new JLabel("Name:");
		pnlDevDetails.add(lblName, "2, 2, right, default");
		
		txtName = new JTextField();
		txtName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DeviceTypeDetailsCard.this.dtw.setName(DeviceTypeDetailsCard.this.txtName.getText());
				
				ModelManager.getSingleton().updateDeviceType(DeviceTypeDetailsCard.this.dtw, new SimpleCallback()
				{
					@Override
					public void onCompletion(boolean wasSuccessfulIn)
					{
						if( !wasSuccessfulIn ) JOptionPane.showMessageDialog(DeviceTypeDetailsCard.this, "Error setting device type name", "Error", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		});
		txtName.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlDevDetails.add(txtName, "4, 2, fill, default");
		txtName.setColumns(10);
		
		JLabel lblUuid = new JLabel("UUID:");
		pnlDevDetails.add(lblUuid, "2, 4");
		
		lblUuidValue = new JLabel("<unknown>");
		lblUuidValue.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlDevDetails.add(lblUuidValue, "4, 4");
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		
		devTypeConfigurationPanel = new DeviceTypeConfigurationPanel();
		tabbedPane.addTab("Configuration", null, devTypeConfigurationPanel, null);
		
		JPanel pnlDeployment = new JPanel();
		tabbedPane.addTab("Deployment", null, pnlDeployment, null);
	}

	
	public void setDevice(DeviceTypeWrapper dtwIn)
	{
		this.dtw = dtwIn;
		this.devTypeConfigurationPanel.setDeviceType(dtwIn);
		this.refreshUi();
	}
	
	
	@Override
	public void onBecomesVisible()
	{
		this.refreshUi();
	}

	
	private void refreshUi()
	{
		if( this.dtw == null ) return;
		
		this.txtName.setText(this.dtw.getName());
		this.lblUuidValue.setText(this.dtw.getUuid());
	}
}
