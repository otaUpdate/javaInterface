package net.otaupdate.app.ui.main.details.unprovDevTypeInstances;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.ui.main.details.devTypeInstances.CurrentFirmwareCellRenderer;

import javax.swing.JButton;


public class UnprovisionedDeviceInstancePanel extends JPanel
{
	private static final long serialVersionUID = 3046355308986245760L;
	
	
	private final JTable table;
	private final UnprovisionedInstanceTableModel tableModel = new UnprovisionedInstanceTableModel();
	private DeviceTypeWrapper dtw = null;
	
	
	public UnprovisionedDeviceInstancePanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		// setup our table
		this.table = new JTable(this.tableModel);
		this.table.setGridColor(Color.LIGHT_GRAY);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setCellSelectionEnabled(false);
		this.table.setColumnSelectionAllowed(false);
		this.table.setRowSelectionAllowed(true);
		this.table.setDefaultRenderer(CurrentFirmwareCellRenderer.CurrentFirmwareCellDataObject.class, new CurrentFirmwareCellRenderer());
		add(new JScrollPane(this.table), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlButtons = new JPanel();
		panel.add(pnlButtons, BorderLayout.EAST);
		
		JButton btnRefreshTable = new JButton("Refresh Table");
		pnlButtons.add(btnRefreshTable);
		btnRefreshTable.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				UnprovisionedDeviceInstancePanel.this.tableModel.refresh();
			}
		});
	}
	
	
	public void setDeviceType(DeviceTypeWrapper dtwIn)
	{
		this.dtw = dtwIn;
		this.tableModel.setDeviceType(this.dtw);
		this.tableModel.refresh();
	}
}
