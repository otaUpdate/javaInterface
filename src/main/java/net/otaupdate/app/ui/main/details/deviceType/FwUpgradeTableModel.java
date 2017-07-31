package net.otaupdate.app.ui.main.details.deviceType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.otaupdate.app.model.FwImageWrapper;

public class FwUpgradeTableModel extends AbstractTableModel
{	
	private static final long serialVersionUID = -1590053687279335863L;
	
	
	private List<FwImageWrapper> allFws = new ArrayList<FwImageWrapper>();
	
	
	public void refresh(FwImageWrapper currFwIn, List<FwImageWrapper> allFws)
	{
		this.allFws.clear();
		
		// remove our current firmware image from the list and save it
		Iterator<FwImageWrapper> it = allFws.iterator();
		while( it.hasNext() )
		{
			FwImageWrapper itFwImage = it.next();
			if( currFwIn != itFwImage )
			{
				this.allFws.add(itFwImage);
			}
		}
		
		this.fireTableDataChanged();
	}
	
	
	public String getUuidForRow(int rowIn)
	{
		return (rowIn > 0) ? this.allFws.get(rowIn-1).getUuid() : "";
	}
	

	@Override
	public int getRowCount()
	{
		return ((this.allFws != null) ? this.allFws.size() : 0) + 1;
	}

	
	@Override
	public int getColumnCount()
	{
		return 2;
	}
	
	
	@Override
	public String getColumnName(int columnIndex)
	{
		String retVal = null;
		if( columnIndex == 0 )
		{
			retVal = "Name";
		}
		else if( columnIndex == 1 )
		{
			retVal = "UUID";
		}
		return retVal;
	}

	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if( rowIndex == 0 ) return "<none>";
		
		
		FwImageWrapper currFwImage = this.allFws.get(rowIndex-1);
		
		Object retVal = null;
		if( columnIndex == 0 )
		{
			retVal = currFwImage.getName();
		}
		else if( columnIndex == 1 )
		{
			retVal = currFwImage.getUuid();
		}
		return retVal;
	}

}
