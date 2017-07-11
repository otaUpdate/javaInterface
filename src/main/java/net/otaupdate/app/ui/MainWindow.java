package net.otaupdate.app.ui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;

import net.otaupdate.app.AuthorizationManager;
import net.otaupdate.app.ui.cardmanager.CardManager;
import net.otaupdate.app.ui.main.MainInterface;
import net.otaupdate.app.ui.startup.LoginPanel;
import net.otaupdate.app.ui.startup.UserRegistrationPanel;
import net.otaupdate.app.ui.startup.LoginPanel.LoginPanelListener;
import net.otaupdate.app.ui.startup.UserRegistrationPanel.UserRegistrationPanelListener;

import java.awt.BorderLayout;


public class MainWindow extends JFrame
{
	private static final long serialVersionUID = -6416466165488197039L;
	private static final int WIDTH_PX = 740;
	private static final int HEIGHT_PX = 580;
	
	private static final String CARD_LOGIN = "login";
	private static final String CARD_USER_CREATION = "createUser";
	private static final String CARD_MAIN_INTERFACE = "mainInterface";
	
	
	private final CardManager cardManager;
	

	public MainWindow()
	{
		setTitle("otaUpdate.net");
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		this.setBounds(dim.width/2 - WIDTH_PX/2,
					   dim.height/2 - HEIGHT_PX/2,
					   WIDTH_PX, HEIGHT_PX);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		cardManager = new CardManager();
		getContentPane().add(cardManager, BorderLayout.CENTER);
		
		// setup our login panel
		LoginPanel loginPanel = new LoginPanel();
		cardManager.add(loginPanel, CARD_LOGIN);
		
		// setup our user registration panel
		UserRegistrationPanel userRegistrationPanel = new UserRegistrationPanel();
		cardManager.add(userRegistrationPanel, CARD_USER_CREATION);
		
		// setup our main interface
		MainInterface mainInterface = new MainInterface();
		cardManager.add(mainInterface, CARD_MAIN_INTERFACE);
		userRegistrationPanel.addListener(new UserRegistrationPanelListener()
		{
			@Override
			public void onUserRegistrationSuccessful()
			{
				MainWindow.this.cardManager.showCard(CARD_LOGIN);
			}
			
			@Override
			public void onCancel()
			{
				MainWindow.this.cardManager.showCard(CARD_LOGIN);
			}
		});
		loginPanel.addListener(new LoginPanelListener()
		{
			@Override
			public void onUserRegistrationRequested()
			{
				MainWindow.this.cardManager.showCard(CARD_USER_CREATION);
			}
			
			@Override
			public void onUserLoggedIn()
			{	
				MainWindow.this.cardManager.showCard(CARD_MAIN_INTERFACE);
			}
		});
		
		// display our login window if we need
		if( !AuthorizationManager.getSingleton().isLoggedIn() ) 
		{
			this.cardManager.showCard(CARD_LOGIN);
		}
	}
	
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					MainWindow window = new MainWindow();
					window.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

}
