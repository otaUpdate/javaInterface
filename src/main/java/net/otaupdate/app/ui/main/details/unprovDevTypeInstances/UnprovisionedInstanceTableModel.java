package net.otaupdate.app.ui.main.details.unprovDevTypeInstances;

import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.ocpsoft.prettytime.PrettyTime;

import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.GetUnprovisionedProcessorsCallback;
import net.otaupdate.app.ui.main.details.devTypeInstances.CurrentFirmwareCellRenderer;
import net.otaupdate.app.model.UnprovisionedProcessorWrapper;


public class UnprovisionedInstanceTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -9123822380115687310L;
	
	private static final PrettyTime TIME_PRINTER = new PrettyTime();
	private static final int COL_IDX_PROC_TYPE = 0;
	private static final int COL_IDX_SERIAL_NUM = 1;
	private static final int COL_IDX_LAST_SEEN = 2;
	private static final int COL_IDX_CURR_FW = 3;

	
	private DeviceTypeWrapper dtw = null;
	private List<UnprovisionedProcessorWrapper> unprovisionedProcessors = null;
	
	
	public void setDeviceType(DeviceTypeWrapper dtwIn)
	{
		this.dtw = dtwIn;
		if( this.dtw == null ) return;
		
		this.refresh();
	}
	
	
	public void refresh()
	{
		ModelManager.getSingleton().getUnprovisionedProcessorsForDeviceType(this.dtw, new GetUnprovisionedProcessorsCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn, List<UnprovisionedProcessorWrapper> unprovisionedProcsIn)
			{
				if( wasSuccessfulIn )
				{
					UnprovisionedInstanceTableModel.this.unprovisionedProcessors = unprovisionedProcsIn;
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							UnprovisionedInstanceTableModel.this.fireTableStructureChanged();
						}
					});
				}
				else JOptionPane.showMessageDialog(null, "Error refreshing unprovisioned instances", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	

	@Override
	public int getRowCount()
	{
		return !this.isEmpty() ? this.unprovisionedProcessors.size() : 0;
	}
	
	
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		Class<?> retVal = String.class;
		if( columnIndex == 3 )
		{
		retVal = CurrentFirmwareCellRenderer.CurrentFirmwareCellDataObject.class;
		}
		
		return retVal;
	}
	
	
	@Override
	public int getColumnCount()
	{
		return 4;
	}
	
	
	@Override
	public String getColumnName(int columnIndex)
	{
		String retVal = null;
		if( columnIndex == COL_IDX_PROC_TYPE )
		{
			retVal = "Processor Type Name";
		}
		else if( columnIndex == COL_IDX_SERIAL_NUM )
		{
			retVal = "Serial Number";
		}
		else if( columnIndex == COL_IDX_LAST_SEEN )
		{
			retVal = "Last Seen";
		}
		else if( columnIndex == COL_IDX_CURR_FW )
		{
			retVal = "Current Firmware";
		}
		return retVal;
	}

	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object retVal = null;
		
		UnprovisionedProcessorWrapper unprovProc = this.unprovisionedProcessors.get(rowIndex);
		if( unprovProc == null ) return null;
		
		if( columnIndex == COL_IDX_PROC_TYPE )
		{
			retVal = unprovProc.getProcTypeName();
		}
		else if( columnIndex == COL_IDX_SERIAL_NUM )
		{
			retVal = unprovProc.getSerialNumber();
		}
		else if( columnIndex == COL_IDX_CURR_FW )
		{
			String fwImageName = unprovProc.getCurrentFwImageName();
			Boolean isFwUpToDate = unprovProc.getIsFwUpToDate();
			retVal = new CurrentFirmwareCellRenderer.CurrentFirmwareCellDataObject(fwImageName, isFwUpToDate);
		}
		else if( columnIndex == COL_IDX_LAST_SEEN )
		{
			Date targetDate = unprovProc.getLastSeenDate();
			retVal = (targetDate != null) ? TIME_PRINTER.format(targetDate) : "<never>";
		}
		
		return retVal;
	}
	
	
	private boolean isEmpty()
	{
		return (this.unprovisionedProcessors == null) || this.unprovisionedProcessors.isEmpty();
	}

}
