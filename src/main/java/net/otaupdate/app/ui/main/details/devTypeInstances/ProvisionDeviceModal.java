package net.otaupdate.app.ui.main.details.devTypeInstances;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.SimpleCallback;
import net.otaupdate.app.model.ProcessorTypeWrapper;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ProvisionDeviceModal extends JDialog
{
	private static final long serialVersionUID = 3181751036822977090L;

	
	public interface ProvisionDeviceModalCallback
	{
		public void onDeviceProvisionedSuccessfully();
	}
	

	private final JPanel contentPanel = new JPanel();
	private final JTextField txtDevSerialNum;

	private final DeviceTypeWrapper dtw;
	private final ProvisionDeviceModalCallback cb;
	private final Map<ProcessorTypeWrapper, JTextField> processorTextFields = new HashMap<>();


	public ProvisionDeviceModal(DeviceTypeWrapper dtwIn, ProvisionDeviceModalCallback cbIn)
	{
		this.dtw = dtwIn;
		this.cb = cbIn;
		
		// dynamically calculate our row specs
		int numProcessors = (dtwIn != null) ? dtwIn.getProcTypes().size() : 0;
		RowSpec rs[] = new RowSpec[2 + (numProcessors * 2)];
		// add for device serial number
		rs[0] = FormSpecs.RELATED_GAP_ROWSPEC;
		rs[1] = FormSpecs.DEFAULT_ROWSPEC;
		for( int i = 0; i < numProcessors; i++ )
		{
			rs[2 + i*2 + 0] = FormSpecs.RELATED_GAP_ROWSPEC;
			rs[2 + i*2 + 1] = FormSpecs.DEFAULT_ROWSPEC;	
		}

		// setup our form
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
				rs));

		// device serial number
		JLabel lblDeviceSerialNumber = new JLabel("Device Serial Number:");
		contentPanel.add(lblDeviceSerialNumber, "2, 2, right, default");

		this.txtDevSerialNum = new JTextField();
		contentPanel.add(this.txtDevSerialNum, "4, 2, fill, default");

		// add our processor serial number fields
		for( int i = 0; i < numProcessors; i++ )
		{
			int row = 2 + (i+2);

			JLabel lbl = new JLabel(String.format("'%s' Serial Number", dtwIn.getProcTypes().get(i).getName()));
			contentPanel.add(lbl, String.format("2, %d, right, default", row));

			JTextField txt = new JTextField("", 36);
			contentPanel.add(txt, String.format("4, %d, fill, default", row));

			this.processorTextFields.put(dtwIn.getProcTypes().get(i), txt);
		}

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ProvisionDeviceModal.this.setVisible(false);
				ProvisionDeviceModal.this.dispose();
			}
		});
		buttonPane.add(cancelButton);
		
		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				ProvisionDeviceModal.this.saveToCloud();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		this.pack();
	}

	
	private void saveToCloud()
	{
		Map<ProcessorTypeWrapper, String> procSerialNums = new HashMap<>();
		Iterator<Entry<ProcessorTypeWrapper, JTextField>> it = this.processorTextFields.entrySet().iterator();
		while( it.hasNext() )
		{
			Entry<ProcessorTypeWrapper, JTextField> currEntry = it.next();
			procSerialNums.put(currEntry.getKey(), currEntry.getValue().getText());
		}
		
		ModelManager.getSingleton().createDeviceInstance(this.dtw, this.txtDevSerialNum.getText(), procSerialNums, new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn )
				{
					if( ProvisionDeviceModal.this.cb != null ) ProvisionDeviceModal.this.cb.onDeviceProvisionedSuccessfully();
					ProvisionDeviceModal.this.setVisible(false);
					ProvisionDeviceModal.this.dispose();
				}
				else JOptionPane.showMessageDialog(null, "Error provisioning device", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
