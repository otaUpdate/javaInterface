package net.otaupdate.app.ui.main.details;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.SimpleCallback;
import net.otaupdate.app.model.ProcessorTypeWrapper;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import javax.swing.DefaultComboBoxModel;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ProcessorDetailsCard extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = -8758246768261616073L;
	
	
	private final JTextField txtName;
	private final JLabel lblUuidValue;
	private final mxGraph fwGraph;
	private final JComboBox<Object> cmbLatestFirmwareVersion;
	
	
	private ProcessorTypeWrapper proc = null;
	private boolean isUpdatingUi = false;

	
	public ProcessorDetailsCard()
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblName = new JLabel("Name:");
		pnlDevDetails.add(lblName, "2, 2");
		
		txtName = new JTextField();
		txtName.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if( ProcessorDetailsCard.this.isUpdatingUi ) return;
				
				ProcessorDetailsCard.this.proc.getModelObject().setName(ProcessorDetailsCard.this.txtName.getText());
				
				ModelManager.getSingleton().updateProcessorType(ProcessorDetailsCard.this.proc, new SimpleCallback()
				{
					@Override
					public void onCompletion(boolean wasSuccessfulIn)
					{
						if( !wasSuccessfulIn ) JOptionPane.showMessageDialog(ProcessorDetailsCard.this, "Error setting processor name", "Error", JOptionPane.ERROR_MESSAGE);
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
		
		JLabel lblLatestFirmwareVersion = new JLabel("Latest Firmware Version:");
		pnlDevDetails.add(lblLatestFirmwareVersion, "2, 6");
		
		cmbLatestFirmwareVersion = new JComboBox<Object>();
		cmbLatestFirmwareVersion.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if( ProcessorDetailsCard.this.isUpdatingUi ) return;
				
				Object selectedItem = (Object)ProcessorDetailsCard.this.cmbLatestFirmwareVersion.getSelectedItem();
				String latestFwVersion = (selectedItem instanceof FwImageWrapper) ? ((FwImageWrapper)selectedItem).getModelObject().getUuid() : ""; 
				ProcessorDetailsCard.this.proc.getModelObject().setLatestFirmwareUuid(latestFwVersion);
				
				ModelManager.getSingleton().updateProcessorType(ProcessorDetailsCard.this.proc, new SimpleCallback()
				{
					@Override
					public void onCompletion(boolean wasSuccessfulIn)
					{
						if( !wasSuccessfulIn ) JOptionPane.showMessageDialog(ProcessorDetailsCard.this, "Error setting latest firmware", "Error", JOptionPane.ERROR_MESSAGE);
					}
				});
			}
		});
		pnlDevDetails.add(cmbLatestFirmwareVersion, "4, 6");
		
		fwGraph = new mxGraph();
		
		JPanel pnlMigrationViz = new JPanel();
		pnlMigrationViz.setBorder(new TitledBorder(null, "Migration Visualization", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(pnlMigrationViz, BorderLayout.CENTER);
		pnlMigrationViz.setLayout(new GridLayout(0, 1, 0, 0));
		mxGraphComponent graphComponent = new mxGraphComponent(this.fwGraph);
		pnlMigrationViz.add(graphComponent);
		graphComponent.setDragEnabled(false);
	}

	
	public void setProcessor(ProcessorTypeWrapper procIn)
	{
		this.proc = procIn;
		this.refreshUi();
	}
	
	
	@Override
	public void onBecomesVisible()
	{
		this.refreshUi();
	}

	
	private void refreshUi()
	{
		if( this.proc == null ) return;
		
		this.isUpdatingUi = true;
		
		this.txtName.setText(this.proc.getModelObject().getName());
		this.lblUuidValue.setText(this.proc.getModelObject().getUuid());
	
		this.setupDropdown();
		this.setupGraph();
		
		this.isUpdatingUi = false;
	}
	
	
	private void setupDropdown()
	{
		this.cmbLatestFirmwareVersion.setModel(new DefaultComboBoxModel<Object>()
		{
			private static final long serialVersionUID = 4393656274912290839L;

			@Override
			public int getSize()
			{
				return ProcessorDetailsCard.this.proc.getFirmwareImages().size() + 1;
			}
			
			@Override
			public Object getElementAt(int index)
			{
				return (index == 0 ) ? "<none>" : ProcessorDetailsCard.this.proc.getFirmwareImages().get(index-1);
			}
		});
		
		// make sure the correct item is selected (default first)
		this.cmbLatestFirmwareVersion.setSelectedIndex(0);
		for( int i = 0; i < this.proc.getFirmwareImages().size(); i++ )
		{
			if( this.proc.getFirmwareImages().get(i).getModelObject().getUuid().equals(this.proc.getModelObject().getLatestFirmwareUuid()) )
			{
				this.cmbLatestFirmwareVersion.setSelectedIndex(i+1);
				break;
			}
		}
	}
	
	
	private void setupGraph()
	{	
		this.fwGraph.removeCells(this.fwGraph.getChildCells(this.fwGraph.getDefaultParent(), true, true));
		
		Object parent = this.fwGraph.getDefaultParent();

		this.fwGraph.getModel().beginUpdate();
		try
		{
			Map<String, Object> uuidToVertices = new HashMap<String, Object>();
			
			// create our vertices
			for( FwImageWrapper currFwImage : this.proc.getFirmwareImages() )
			{
				mxCell newVert = (mxCell)this.fwGraph.insertVertex(parent, null, currFwImage, 20, 20, 80, 30);
				newVert.setConnectable(false);
				
				uuidToVertices.put(currFwImage.getModelObject().getUuid(), newVert);
			}
			
			// create our edges
			for( FwImageWrapper currFwImage : this.proc.getFirmwareImages() )
			{
				Object vert_from = uuidToVertices.get(currFwImage.getModelObject().getUuid());
				Object vert_to = uuidToVertices.get(currFwImage.getModelObject().getToVersionUuid());
				
				if( (vert_from != null) && (vert_to != null) )
				{
					this.fwGraph.insertEdge(parent, null, null, vert_from, vert_to);
				}
			}
		}
		finally
		{
			this.fwGraph.getModel().endUpdate();
		}
		
		// now do a layout
		mxIGraphLayout layout = new mxHierarchicalLayout(this.fwGraph);
		layout.execute(parent);
	}
}
