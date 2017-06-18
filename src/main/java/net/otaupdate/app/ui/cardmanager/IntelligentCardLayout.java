package net.otaupdate.app.ui.cardmanager;

import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.HashMap;
import java.util.Map;


public class IntelligentCardLayout extends CardLayout
{
	private static final long serialVersionUID = -4225047011631024582L;

	
	private final Map<String, Component> components = new HashMap<>();
	private String currentComponentName = null;
	
	
	public Component getCurrentlyVisibleComponent()
	{
		return this.components.get(this.currentComponentName);
	}
	
	
	@Override
	public void addLayoutComponent(Component comp, Object constraints)
	{
		super.addLayoutComponent(comp, constraints);
		
		String compName = (String)constraints;
		
		this.components.put(compName, comp);
		if( this.currentComponentName == null ) this.currentComponentName = compName;
	}
	
	
	@Override
	public void show(Container parent, String name)
	{
		super.show(parent, name);
		
		this.currentComponentName = name;
		Component currComp = this.getCurrentlyVisibleComponent();
		if( currComp instanceof IntelligentCard )
		{
			((IntelligentCard)currComp).onBecomesVisible();
		}
	}
}
