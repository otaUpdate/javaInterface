package net.otaupdate.app.ui.cardmanager;


import java.awt.Component;

import javax.swing.JPanel;


public class CardManager extends JPanel
{
	private static final long serialVersionUID = 1084856578847060915L;

	
	public interface IntelligentCard
	{
		public abstract void onBecomesVisible();
	}
	
	
	public interface CardTransitionCallback
	{
		public abstract void onCardTransition(Component oldComponentIn, Component newComponentIn);
	}
	
	
	private final IntelligentCardLayout icl;
	
	
	public CardManager()
	{
		this.icl = new IntelligentCardLayout();
		this.setLayout(icl);
	}
	
	
	public void showCard(String cardIdIn)
	{
		this.showCard(cardIdIn, null);
	}
	
	
	public void showCard(String cardIdIn, CardTransitionCallback ctcIn)
	{
		Component currVisibleComp = this.icl.getCurrentlyVisibleComponent();
		((IntelligentCardLayout)this.getLayout()).show(this, cardIdIn);
		Component newComponent = this.icl.getCurrentlyVisibleComponent();
		
		if( ctcIn != null ) ctcIn.onCardTransition(currVisibleComp, newComponent);
	}
}
