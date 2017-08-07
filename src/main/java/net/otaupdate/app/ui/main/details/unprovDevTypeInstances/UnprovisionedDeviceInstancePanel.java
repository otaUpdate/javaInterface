package net.otaupdate.app.ui.main.details.unprovDevTypeInstances;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.UnprovisionedProcessorWrapper;
import net.otaupdate.app.ui.main.details.devTypeInstances.CurrentFirmwareCellRenderer;

import javax.swing.JButton;
import javax.swing.JMenuItem;


public class UnprovisionedDeviceInstancePanel extends JPanel
{
	private static final long serialVersionUID = 3046355308986245760L;
	
	
	private final JTable table;
	private final UnprovisionedInstanceTableModel tableModel = new UnprovisionedInstanceTableModel();
	private DeviceTypeWrapper dtw = null;
	
	
	public UnprovisionedDeviceInstancePanel()
	{
		setLayout(new BorderLayout(0, 0));
		
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
		JMenuItem copyProcSerialNum = new JMenuItem("Copy Processor Serial Number");
		copyProcSerialNum.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				UnprovisionedProcessorWrapper proc = UnprovisionedDeviceInstancePanel.this.tableModel.getProcessorAtIndex(UnprovisionedDeviceInstancePanel.this.table.getSelectedRow());
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(new StringSelection(proc.getSerialNumber()), null);
			}
		});
		popupMenu.add(copyProcSerialNum);
		
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
	
	
	public UnprovisionedProcessorWrapper getSelectedProcessor()
	{
		return this.tableModel.getProcessorAtIndex(this.table.getSelectedRow());
	}
}
