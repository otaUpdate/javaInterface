package net.otaupdate.app.ui.main.details.deviceType;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.SimpleCallback;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class FwImageDetailsCard extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = 7672032160259853676L;
	
	
	private final JTextField txtName;
	private final JLabel lblUuidValue;
	private final JTable tblUpgradeTo;
	private final FwUpgradeTableModel tableModel = new FwUpgradeTableModel();
	
	private FwImageWrapper fw = null;
	private List<FwImageWrapper> allFws = null;

	private boolean isUpdatingUi = false;
	
	
	public FwImageDetailsCard()
	{
		setBorder(new EmptyBorder(4, 0, 4, 4));
		setLayout(new BorderLayout(0, 0));
		
		JPanel pnlFwViz = new JPanel();
		pnlFwViz.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Upgradeable To", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		add(pnlFwViz, BorderLayout.CENTER);
		pnlFwViz.setLayout(new BorderLayout(0, 0));
		
		
		tblUpgradeTo = new JTable(this.tableModel);
		tblUpgradeTo.setShowVerticalLines(false);
		tblUpgradeTo.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if( FwImageDetailsCard.this.isUpdatingUi ) return;
				
				String upgradeTargetUuid = FwImageDetailsCard.this.tableModel.getUuidForRow(FwImageDetailsCard.this.tblUpgradeTo.getSelectedRow());
				
				FwImageDetailsCard.this.changeToVersionByUuid(upgradeTargetUuid);
			}
		});
		this.tblUpgradeTo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
		JScrollPane scrollPane = new JScrollPane(tblUpgradeTo);	
		pnlFwViz.add(scrollPane, BorderLayout.CENTER);
		
		JPanel pnlDevDetails = new JPanel();
		pnlDevDetails.setBorder(new EmptyBorder(0, 0, 8, 4));
		add(pnlDevDetails, BorderLayout.NORTH);
		pnlDevDetails.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlDevDetailFields = new JPanel();
		pnlDevDetails.add(pnlDevDetailFields);
		pnlDevDetailFields.setBorder(new EmptyBorder(0, 0, 8, 4));
		pnlDevDetailFields.setLayout(new FormLayout(new ColumnSpec[] {
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
		pnlDevDetailFields.add(lblName, "2, 2");
		
		txtName = new JTextField();
		txtName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				FwImageDetailsCard.this.updateFwName(FwImageDetailsCard.this.txtName.getText());
			}
		});
		txtName.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlDevDetailFields.add(txtName, "4, 2, fill, default");
		txtName.setColumns(10);
		
		JLabel lblUuid = new JLabel("UUID:");
		pnlDevDetailFields.add(lblUuid, "2, 4");
		
		lblUuidValue = new JLabel("<unknown>");
		lblUuidValue.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlDevDetailFields.add(lblUuidValue, "4, 4");
		
		JPanel pnlButtons = new JPanel();
		FlowLayout fl_pnlButtons = (FlowLayout) pnlButtons.getLayout();
		fl_pnlButtons.setAlignment(FlowLayout.RIGHT);
		pnlDevDetails.add(pnlButtons, BorderLayout.SOUTH);
		
		JButton btnCopyUuid = new JButton("Copy UUID");
		btnCopyUuid.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(new StringSelection(FwImageDetailsCard.this.fw.getUuid()), null);
				JOptionPane.showMessageDialog(FwImageDetailsCard.this, "UUID copied to clipboard", "Firmware UUID", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		pnlButtons.add(btnCopyUuid);
	}

	
	public void setFwImage(FwImageWrapper fwIn, List<FwImageWrapper> allFwImagesIn)
	{
		this.fw = fwIn;
		this.allFws = allFwImagesIn;
		this.refreshUi();
	}
	
	
	@Override
	public void onBecomesVisible()
	{
		this.refreshUi();
	}

	
	private void refreshUi()
	{
		if( this.fw == null ) return;
		
		this.isUpdatingUi = true;
		
		this.txtName.setText(this.fw.getName());
		this.lblUuidValue.setText(this.fw.getUuid());
		
		this.tableModel.refresh(this.fw, this.allFws);
		int selectedRowIndex = 0;
		for( int i = 0; i < this.tblUpgradeTo.getRowCount(); i++ )
		{
			if( this.tableModel.getUuidForRow(i).equals(this.fw.getToVersionUuid()) )
			{
				selectedRowIndex = i;
				break;
			}
		}
		this.tblUpgradeTo.changeSelection(selectedRowIndex, 0, false, false);
		
		this.isUpdatingUi = false;
	}
	
	
	private void changeToVersionByUuid(String toVersionUuidIn)
	{
		this.fw.setToVersionUuid(toVersionUuidIn);
		ModelManager.getSingleton().updateFirmwareImage(this.fw, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( !wasSuccessfulIn )
				{
					JOptionPane.showMessageDialog(FwImageDetailsCard.this, "Error updating firmware image", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	
	private void updateFwName(String newNameIn)
	{
		this.fw.setName(newNameIn);
		ModelManager.getSingleton().updateFirmwareImage(this.fw, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( !wasSuccessfulIn )
				{
					JOptionPane.showMessageDialog(FwImageDetailsCard.this, "Error updating firmware name", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
}
