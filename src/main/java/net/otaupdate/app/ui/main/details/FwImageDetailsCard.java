package net.otaupdate.app.ui.main.details;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.awt.BorderLayout;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JScrollPane;


public class FwImageDetailsCard extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = 7672032160259853676L;
	
	
	private final JTextField txtName;
	private final JLabel lblUuidValue;
	private final JTable table;
	private final FwUpgradeTableModel tableModel = new FwUpgradeTableModel();
	
	private FwImageWrapper fw = null;
	private List<FwImageWrapper> allFws = null;

	
	public FwImageDetailsCard()
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
		txtName.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlDevDetails.add(txtName, "4, 2, fill, default");
		txtName.setColumns(10);
		
		JLabel lblUuid = new JLabel("UUID:");
		pnlDevDetails.add(lblUuid, "2, 4");
		
		lblUuidValue = new JLabel("<unknown>");
		lblUuidValue.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlDevDetails.add(lblUuidValue, "4, 4");
		
		JPanel pnlFwViz = new JPanel();
		pnlFwViz.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Upgradeable To", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		add(pnlFwViz, BorderLayout.CENTER);
		pnlFwViz.setLayout(new BorderLayout(0, 0));
		
		
		table = new JTable(this.tableModel);
		table.setShowVerticalLines(false);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	
		JScrollPane scrollPane = new JScrollPane(table);	
		pnlFwViz.add(scrollPane, BorderLayout.CENTER);
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
		
		this.txtName.setText(this.fw.getModelObject().getName());
		this.lblUuidValue.setText(this.fw.getModelObject().getUuid());
		
		this.tableModel.refresh(this.fw, this.allFws);
		int selectedRowIndex = 0;
		for( int i = 0; i < this.allFws.size(); i++ )
		{
			if( this.allFws.get(i).getModelObject().getUuid().equals(this.fw.getModelObject().getToVersionUuid()) )
			{
				// +1 is to account for empty row at top (<none>)
				selectedRowIndex = i+1;
				break;
			}
		}
		this.table.changeSelection(selectedRowIndex, 0, false, false);
	}
	
}
