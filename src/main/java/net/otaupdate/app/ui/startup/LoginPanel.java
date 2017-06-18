package net.otaupdate.app.ui.startup;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.otaupdate.app.AuthorizationManager;
import net.otaupdate.app.AuthorizationManager.LoginCallback;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.border.EtchedBorder;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import javax.swing.border.TitledBorder;

import java.awt.Color;


public class LoginPanel extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = -1695581055041539500L;

	
	public interface LoginPanelListener
	{
		public void onUserLoggedIn();
		public void onUserRegistrationRequested();
	}
	
	
	private final JTextField txtEmail;
	private final JPasswordField txtPassword;
	private final JButton btnLogin;
	
	private final List<LoginPanelListener> listeners = new ArrayList<LoginPanelListener>();

	
	public LoginPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		add(horizontalGlue_1);
		
		JPanel pnlCenter = new JPanel();
		pnlCenter.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Login", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		pnlCenter.setPreferredSize(new Dimension(400, 125));
		pnlCenter.setMinimumSize(new Dimension(400, 125));
		pnlCenter.setMaximumSize(new Dimension(400, 125));
		add(pnlCenter);
		pnlCenter.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlInput = new JPanel();
		pnlCenter.add(pnlInput, BorderLayout.CENTER);
		pnlInput.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblEmailAddress = new JLabel("E-mail Address:");
		lblEmailAddress.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlInput.add(lblEmailAddress, "2, 2, right, default");
		
		txtEmail = new JTextField();
		
		pnlInput.add(txtEmail, "4, 2, fill, default");
		txtEmail.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlInput.add(lblPassword, "2, 4, right, default");
		
		txtPassword = new JPasswordField();
		pnlInput.add(txtPassword, "4, 4, fill, default");
		
		JPanel pnlButtons = new JPanel();
		pnlCenter.add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setLayout(new BorderLayout(0, 0));
		
		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AuthorizationManager.getSingleton().login(txtEmail.getText(), txtPassword.getText(), 
						new LoginCallback()
						{
							@Override
							public void onAuthorizationComplete(boolean wasSuccessfulIn, String errorMsgIn)
							{
								if( wasSuccessfulIn )
								{
									// notify our listeners
									for( LoginPanelListener currListener : LoginPanel.this.listeners )
									{
										currListener.onUserLoggedIn();
									}
								}
								else
								{
									JOptionPane.showMessageDialog(LoginPanel.this,"Login failed", "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						});
			}
		});
		pnlButtons.add(btnLogin, BorderLayout.EAST);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// notify our listeners
				for( LoginPanelListener currListener : LoginPanel.this.listeners )
				{
					currListener.onUserRegistrationRequested();
				}
			}
		});
		pnlButtons.add(btnRegister, BorderLayout.WEST);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{txtEmail, txtPassword, btnLogin, btnRegister}));
	}
	
	
	public void addListener(LoginPanelListener listenerIn)
	{
		this.listeners.add(listenerIn);
	}
	
	
	@Override
	public void onBecomesVisible()
	{
		txtEmail.setText(AuthorizationManager.getSingleton().getSavedEmailAddress());
		txtPassword.setText(AuthorizationManager.getSingleton().getSavedPassword());
		
		this.getRootPane().setDefaultButton(this.btnLogin);
		this.txtEmail.requestFocus();
	}
}
