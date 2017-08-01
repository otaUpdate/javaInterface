package net.otaupdate.app.ui.main.details.devTypeInstances;

import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.ocpsoft.prettytime.PrettyTime;

import net.otaupdate.app.model.DeviceInstanceWrapper;
import net.otaupdate.app.model.DeviceTypeWrapper;
import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.GetDeviceInstancesCallback;
import net.otaupdate.app.model.ModelManager.SimpleCallback;
import net.otaupdate.app.model.ProcessorTypeWrapper;


public class DeployedInstanceTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 4924830356445243040L;
	private static final PrettyTime TIME_PRINTER = new PrettyTime();
	private static final int NUM_PROC_COLS = 3;
	
	
	private DeviceTypeWrapper dtw = null;
	private List<DeviceInstanceWrapper> devInstances = null;
	
	
	public void setDeviceType(DeviceTypeWrapper dtwIn)
	{
		this.dtw = dtwIn;
		if( this.dtw == null ) return;
		
		this.refresh();
	}
	
	
	public void refresh()
	{
		ModelManager.getSingleton().getDeviceInstancesForDeviceType(this.dtw, new GetDeviceInstancesCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn, List<DeviceInstanceWrapper> devInstancesIn)
			{
				if( wasSuccessfulIn )
				{
					DeployedInstanceTableModel.this.devInstances = devInstancesIn;
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
						public void run()
						{
							DeployedInstanceTableModel.this.fireTableStructureChanged();
						}
					});
				}
				else JOptionPane.showMessageDialog(null, "Error refreshing device instances", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	
	public void removeDeviceAtIndex(int indexIn)
	{
		ModelManager.getSingleton().deleteDeviceInstance(this.dtw, this.devInstances.get(indexIn).getDeviceSerialNumber(), new SimpleCallback()
		{
			@Override
			public void onCompletion(boolean wasSuccessfulIn)
			{
				if( wasSuccessfulIn ) DeployedInstanceTableModel.this.refresh();
				else JOptionPane.showMessageDialog(null, "Error deleting device instance", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	

	@Override
	public int getRowCount()
	{
		return !this.isEmpty() ? this.devInstances.size() : 0;
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		Class<?> retVal = String.class;
		if( columnIndex > 0 )
		{
			int subProcIndex = (columnIndex - 1) % NUM_PROC_COLS;
			
			if( subProcIndex == 1 )
			{
				retVal = CurrentFirmwareCellRenderer.CurrentFirmwareCellDataObject.class;
			}
		}
		
		return retVal;
	}
	
	
	@Override
	public int getColumnCount()
	{
		return (this.dtw != null) ? (1 + (this.dtw.getProcTypes().size() * NUM_PROC_COLS)) : 0;
	}
	
	
	@Override
	public String getColumnName(int columnIndex)
	{
		String retVal = null;
		if( columnIndex == 0 )
		{
			retVal = "Device Serial Number";
		}
		else if( columnIndex > 0 )
		{
			int procIndex = (columnIndex - 1) / NUM_PROC_COLS;
			int subProcIndex = (columnIndex - 1) % NUM_PROC_COLS;
			ProcessorTypeWrapper currProc = this.dtw.getProcTypes().get(procIndex);
			
			if( subProcIndex == 0 )
			{
				retVal = String.format("'%s' Serial Number", currProc.getName());
			}
			else if( subProcIndex == 1 )
			{
				retVal = String.format("'%s' Current Firmware", currProc.getName());
			}
			else if ( subProcIndex == 2 )
			{
				retVal = String.format("'%s' Last Seen", currProc.getName());
			}
		}
		return retVal;
	}

	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object retVal = null;
		
		DeviceInstanceWrapper devInstance = this.devInstances.get(rowIndex);
		if( devInstance == null ) return null;
		
		if( columnIndex == 0 )
		{
			retVal = devInstance.getDeviceSerialNumber();
		}
		else if( columnIndex > 0 )
		{
			int procIndex = (columnIndex - 1) / NUM_PROC_COLS;
			int subProcIndex = (columnIndex - 1) % NUM_PROC_COLS;
			
			if( subProcIndex == 0 )
			{
				retVal = devInstance.getSerialNumberForProcessorAtIndex(procIndex);
			}
			else if( subProcIndex == 1 )
			{
				String fwImageName = devInstance.getCurrentFwImageNameForProcessorAtIndex(procIndex);
				Boolean isFwUpToDate = devInstance.getIsFwUpToDateForProcessorAtIndex(procIndex);
				retVal = new CurrentFirmwareCellRenderer.CurrentFirmwareCellDataObject(fwImageName, isFwUpToDate);
			}
			else if ( subProcIndex == 2 )
			{
				Date targetDate = devInstance.getLastSeenDateForProcessorAtIndex(procIndex);
				retVal = (targetDate != null) ? TIME_PRINTER.format(targetDate) : "<never>";
			}
		}
		
		return retVal;
	}
	
	
	private boolean isEmpty()
	{
		return (this.devInstances == null) || this.devInstances.isEmpty();
	}

}
