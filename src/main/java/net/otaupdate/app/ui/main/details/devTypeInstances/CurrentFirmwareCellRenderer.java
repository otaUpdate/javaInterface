package net.otaupdate.app.ui.main.details.devTypeInstances;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CurrentFirmwareCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = -6693963857929102867L;
	private static final Color COLOR_UNKNOWN = Color.ORANGE;
	private static final Color COLOR_UP_TO_DATE = Color.GREEN;
	private static final Color COLOR_OUT_OF_DATE = Color.RED;

	
	public static class CurrentFirmwareCellDataObject
	{
		private final String fwName;
		private final Boolean isUpToDate;
		
		public CurrentFirmwareCellDataObject(String fwNameIn, Boolean isUpToDateIn)
		{
			this.fwName = fwNameIn;
			this.isUpToDate = isUpToDateIn;
		}
		
		
		private String getName()
		{
			return (this.fwName != null) ? this.fwName : "<unknown>";
		}
		
		
		private boolean isSet()
		{
			return (this.fwName != null) && (this.isUpToDate != null);
		}
	}
	
	
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		CurrentFirmwareCellDataObject cfcdo = (CurrentFirmwareCellDataObject)value;
		
        Component c = super.getTableCellRendererComponent(table, cfcdo.getName(), isSelected, hasFocus, row, column);

        if( !cfcdo.isSet() )
        {
        		c.setForeground(COLOR_UNKNOWN);
        }
        else if( value instanceof CurrentFirmwareCellDataObject )
        {
        		c.setForeground( cfcdo.isUpToDate ? COLOR_UP_TO_DATE : COLOR_OUT_OF_DATE );
        }
        
        return c;
    }
}
