package net.otaupdate.app.ui.main.details.devTypeInstances;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.ui.main.details.devTypeInstances.ProvisionDeviceModal.ProvisionDeviceModalCallback;

import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JLabel;

public class DeployedDeviceInstancePanel extends JPanel
{
	private static final long serialVersionUID = -5999548293001911379L;
	
	
	private final JTable table;
	private final DeployedInstanceTableModel tableModel = new DeployedInstanceTableModel();
	private DeviceTypeWrapper dtw = null;
	
	
	public DeployedDeviceInstancePanel()
	{
		setLayout(new BorderLayout(0, 0));
		
		// setup our popup menu
		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), table));
                        if( rowAtPoint > -1 )
                        {
                        	table.setRowSelectionAllowed(true);
                            table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                        }
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) { table.setRowSelectionAllowed(false); }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) { table.setRowSelectionAllowed(false); }
        });
		JMenuItem mntmRemoveItem = new JMenuItem("Unprovision device");
		mntmRemoveItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DeployedDeviceInstancePanel.this.tableModel.removeDeviceAtIndex(DeployedDeviceInstancePanel.this.table.getSelectedRow());
			}
		});
		popupMenu.add(mntmRemoveItem);
		
		// setup our table
		this.table = new JTable(this.tableModel);
		this.table.setGridColor(Color.LIGHT_GRAY);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.table.setCellSelectionEnabled(false);
		this.table.setColumnSelectionAllowed(false);
		this.table.setRowSelectionAllowed(true);
		this.table.setComponentPopupMenu(popupMenu);
		this.table.setDefaultRenderer(CurrentFirmwareCellRenderer.CurrentFirmwareCellDataObject.class, new CurrentFirmwareCellRenderer());
		add(new JScrollPane(this.table), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlButtons = new JPanel();
		panel.add(pnlButtons, BorderLayout.EAST);
		
		JButton btnRefreshTable = new JButton("Refresh Table");
		pnlButtons.add(btnRefreshTable);
		
		JButton btnProvisionNewDevice = new JButton("Provision New Device");
		pnlButtons.add(btnProvisionNewDevice);
		
		JPanel pnlLegend = new JPanel();
		panel.add(pnlLegend, BorderLayout.WEST);
		
		JLabel lblLegend = new JLabel("Legend:");
		pnlLegend.add(lblLegend);
		
		JPanel pnlLegendUptoDate = new JPanel();
		pnlLegendUptoDate.setBackground(CurrentFirmwareCellRenderer.COLOR_UP_TO_DATE);
		pnlLegend.add(pnlLegendUptoDate);
		
		JLabel lblNewLabel = new JLabel("UpToDate");
		pnlLegendUptoDate.add(lblNewLabel);
		
		JPanel pnlLegendOutOfDate = new JPanel();
		pnlLegendOutOfDate.setBackground(CurrentFirmwareCellRenderer.COLOR_OUT_OF_DATE);
		pnlLegend.add(pnlLegendOutOfDate);
		
		JLabel lblOutofdate = new JLabel("OutOfDate");
		pnlLegendOutOfDate.add(lblOutofdate);
		
		JPanel pnlLegendUnknown = new JPanel();
		pnlLegendUnknown.setBackground(CurrentFirmwareCellRenderer.COLOR_UNKNOWN);
		pnlLegend.add(pnlLegendUnknown);
		
		JLabel lblUnknown = new JLabel("Unknown");
		pnlLegendUnknown.add(lblUnknown);
		btnProvisionNewDevice.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ProvisionDeviceModal pdm = new ProvisionDeviceModal(DeployedDeviceInstancePanel.this.dtw, new ProvisionDeviceModalCallback()
				{
					@Override
					public void onDeviceProvisionedSuccessfully()
					{
						DeployedDeviceInstancePanel.this.tableModel.refresh();
					}
				});
				pdm.setLocationRelativeTo(SwingUtilities.getWindowAncestor(DeployedDeviceInstancePanel.this));
				pdm.setModal(true);
				pdm.setVisible(true);
			}
		});
		btnRefreshTable.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DeployedDeviceInstancePanel.this.tableModel.refresh();
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
