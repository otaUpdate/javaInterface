package net.otaupdate.app.ui.main.details;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import net.otaupdate.app.model.FwImageWrapper;
import net.otaupdate.app.model.ProcessorWrapper;
import net.otaupdate.app.sdk.model.DeviceArrayItem;
import net.otaupdate.app.sdk.model.ProcessorArrayItem;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class ProcessorDetailsCard extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = -8758246768261616073L;
	
	
	private final JTextField txtName;
	private final JLabel lblUuidValue;
	
	private ProcessorWrapper proc = null;


	private final mxGraph fwGraph;

	
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
		add(pnlFwViz, BorderLayout.CENTER);
		pnlFwViz.setLayout(new BorderLayout(0, 0));
		
		fwGraph = new mxGraph();
		mxGraphComponent graphComponent = new mxGraphComponent(fwGraph);
		add(graphComponent);
	}

	
	public void setProcessor(ProcessorWrapper procIn)
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
		
		this.txtName.setText(this.proc.getModelObject().getName());
		this.lblUuidValue.setText(this.proc.getModelObject().getUuid());
		this.setupGraph();
	}
	
	
	private void setupGraph()
	{	
		Object parent = fwGraph.getDefaultParent();

		fwGraph.getModel().beginUpdate();
		try
		{
			Map<String, Object> uuidToVertices = new HashMap<String, Object>();
			
			// create our vertices
			for( FwImageWrapper currFwImage : this.proc.getFirmwareImages() )
			{
				uuidToVertices.put(currFwImage.getModelObject().getUuid(), this.fwGraph.insertVertex(parent, null, currFwImage, 20, 20, 80, 30));
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
			fwGraph.getModel().endUpdate();
		}
		
		// now do a layout
		mxIGraphLayout layout = new mxFastOrganicLayout(this.fwGraph);
		layout.execute(parent);
	}
}
