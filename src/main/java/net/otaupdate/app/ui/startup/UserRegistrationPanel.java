package net.otaupdate.app.ui.startup;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.otaupdate.app.AuthorizationManager;
import net.otaupdate.app.AuthorizationManager.CreateUserCallback;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
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

public class UserRegistrationPanel extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = -5765398695828583390L;


	public interface UserRegistrationPanelListener
	{
		public void onCancel();
		public void onUserRegistrationSuccessful();
	}
	
	
	private final JTextField txtEmail;
	private final JTextField txtPassword;
	private final JButton btnRegister;
	
	private final List<UserRegistrationPanelListener> listeners = new ArrayList<UserRegistrationPanelListener>();

	
	public UserRegistrationPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		add(horizontalGlue_1);
		
		JPanel pnlCenter = new JPanel();
		pnlCenter.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Create a New User", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
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
		txtEmail.setText(AuthorizationManager.getSingleton().getSavedEmailAddress());
		pnlInput.add(txtEmail, "4, 2, fill, default");
		txtEmail.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlInput.add(lblPassword, "2, 4, right, default");
		
		txtPassword = new JTextField();
		txtPassword.setText(AuthorizationManager.getSingleton().getSavedPassword());
		pnlInput.add(txtPassword, "4, 4, fill, default");
		
		JPanel pnlButtons = new JPanel();
		pnlCenter.add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setLayout(new BorderLayout(0, 0));
		
		btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AuthorizationManager.getSingleton().createNewUser(txtEmail.getText(), txtPassword.getText(), 
						new CreateUserCallback()
						{
							@Override
							public void onUserCreateComplete(boolean wasSuccessfulIn, String errorMsgIn)
							{
								if( wasSuccessfulIn )
								{
									// notify our listeners
									for( UserRegistrationPanelListener currListener : UserRegistrationPanel.this.listeners )
									{
										currListener.onUserRegistrationSuccessful();
									}
								}
								else
								{
									JOptionPane.showMessageDialog(UserRegistrationPanel.this,"Login failed", "Error", JOptionPane.ERROR_MESSAGE);
								}
							}
						});
			}
		});
		pnlButtons.add(btnRegister, BorderLayout.EAST);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// notify our listeners
				for( UserRegistrationPanelListener currListener : UserRegistrationPanel.this.listeners )
				{
					currListener.onCancel();
				}
			}
		});
		pnlButtons.add(btnCancel, BorderLayout.WEST);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{txtEmail, txtPassword, btnRegister, btnCancel}));
	}
	
	
	public void addListener(UserRegistrationPanelListener listenerIn)
	{
		this.listeners.add(listenerIn);
	}
	
	
	@Override
	public void onBecomesVisible()
	{
		this.getRootPane().setDefaultButton(this.btnRegister);
		this.txtEmail.requestFocus();
	}
}
