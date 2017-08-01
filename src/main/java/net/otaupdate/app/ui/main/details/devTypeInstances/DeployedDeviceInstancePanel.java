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
		add(new JScrollPane(this.table), BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(panel, BorderLayout.SOUTH);
		
		JButton btnProvisionNewDevice = new JButton("Provision New Device");
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
		
		JButton btnRefreshTable = new JButton("Refresh Table");
		btnRefreshTable.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DeployedDeviceInstancePanel.this.tableModel.refresh();
			}
		});
		panel.add(btnRefreshTable);
		panel.add(btnProvisionNewDevice);
	}
	
	
	public void setDeviceType(DeviceTypeWrapper dtwIn)
	{
		this.dtw = dtwIn;
		this.tableModel.setDeviceType(this.dtw);
		this.tableModel.refresh();
	}
}
