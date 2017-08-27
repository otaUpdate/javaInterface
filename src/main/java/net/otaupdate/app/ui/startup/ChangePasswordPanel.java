package net.otaupdate.app.ui.startup;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.otaupdate.app.AuthorizationManager;
import net.otaupdate.app.AuthorizationManager.ChangePasswordCallback;
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

public class ChangePasswordPanel extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = -5765398695828583390L;


	public interface UserRegistrationPanelListener
	{
		public void onCancel();
		public void onPasswordChangeSuccessful();
	}


	private final JTextField txtEmail;
	private final JTextField txtPassword;
	private final JButton btnChange;

	private final List<UserRegistrationPanelListener> listeners = new ArrayList<UserRegistrationPanelListener>();


	private String changePasswordSession = null;


	public ChangePasswordPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		add(horizontalGlue_1);

		JPanel pnlCenter = new JPanel();
		pnlCenter.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Password change required", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
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

		JLabel lblPassword = new JLabel("New Password:");
		lblPassword.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlInput.add(lblPassword, "2, 4, right, default");

		txtPassword = new JTextField();
		txtPassword.setText(AuthorizationManager.getSingleton().getSavedPassword());
		pnlInput.add(txtPassword, "4, 4, fill, default");

		JPanel pnlButtons = new JPanel();
		pnlCenter.add(pnlButtons, BorderLayout.SOUTH);
		pnlButtons.setLayout(new BorderLayout(0, 0));

		btnChange = new JButton("Change");
		btnChange.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AuthorizationManager.getSingleton().changePassword(txtEmail.getText(), txtPassword.getText(), changePasswordSession, new ChangePasswordCallback()
				{

					@Override
					public void onPasswordChangeComplete(boolean wasSuccessfulIn, String errorMsgIn)
					{
						if( wasSuccessfulIn )
						{
							// notify our listeners
							for( UserRegistrationPanelListener currListener : ChangePasswordPanel.this.listeners )
							{
								currListener.onPasswordChangeSuccessful();
							}
						}
						else
						{
							JOptionPane.showMessageDialog(ChangePasswordPanel.this, "Password change failed", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
		});
		pnlButtons.add(btnChange, BorderLayout.EAST);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// notify our listeners
				for( UserRegistrationPanelListener currListener : ChangePasswordPanel.this.listeners )
				{
					currListener.onCancel();
				}
			}
		});
		pnlButtons.add(btnCancel, BorderLayout.WEST);

		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{txtEmail, txtPassword, btnChange, btnCancel}));
	}


	public void addListener(UserRegistrationPanelListener listenerIn)
	{
		this.listeners.add(listenerIn);
	}


	public void setChangePasswordSession(String changePasswordSessionIn)
	{
		this.changePasswordSession = changePasswordSessionIn;
	}


	@Override
	public void onBecomesVisible()
	{
		this.getRootPane().setDefaultButton(this.btnChange);
		this.txtEmail.requestFocus();
	}
}
