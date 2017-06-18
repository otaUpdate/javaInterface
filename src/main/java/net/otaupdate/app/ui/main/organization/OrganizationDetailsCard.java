package net.otaupdate.app.ui.main.organization;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import net.otaupdate.app.model.ModelManager;
import net.otaupdate.app.model.ModelManager.AddRemoveOrganizationUserCallback;
import net.otaupdate.app.sdk.model.OrganizationArrayItem;
import net.otaupdate.app.sdk.model.OrganizationUserArrayItem;
import net.otaupdate.app.ui.cardmanager.CardManager.IntelligentCard;

import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class OrganizationDetailsCard extends JPanel implements IntelligentCard
{
	private static final long serialVersionUID = 2705719477593447724L;
	
	
	private final JTextField txtName;
	private final JLabel lblUuidValue;
	private final JTextField txtEmailToAdd;
	private final JTable table;
	private final OrganizationUsersTableModel tableModel = new OrganizationUsersTableModel();
	
	private OrganizationArrayItem org = null;

	
	public OrganizationDetailsCard()
	{
		setBorder(new EmptyBorder(4, 0, 4, 4));
		setLayout(new BorderLayout(0, 0));
		
		JPanel pnlOrgDetails = new JPanel();
		pnlOrgDetails.setBorder(new EmptyBorder(0, 0, 8, 4));
		add(pnlOrgDetails, BorderLayout.NORTH);
		pnlOrgDetails.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblName = new JLabel("Name:");
		pnlOrgDetails.add(lblName, "2, 2, right, default");
		
		txtName = new JTextField();
		txtName.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlOrgDetails.add(txtName, "4, 2, fill, default");
		txtName.setColumns(10);
		
		JLabel lblUuid = new JLabel("UUID:");
		pnlOrgDetails.add(lblUuid, "2, 4");
		
		lblUuidValue = new JLabel("<unknown>");
		lblUuidValue.setHorizontalAlignment(SwingConstants.TRAILING);
		pnlOrgDetails.add(lblUuidValue, "4, 4");
		
		JPanel pnlUsers = new JPanel();
		pnlUsers.setBorder(new TitledBorder(null, "Users:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(pnlUsers, BorderLayout.CENTER);
		pnlUsers.setLayout(new BorderLayout(0, 0));
		
		JPanel pnlAddUser = new JPanel();
		pnlUsers.add(pnlAddUser, BorderLayout.NORTH);
		pnlAddUser.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblAddUserBy = new JLabel("Add user by email:");
		pnlAddUser.add(lblAddUserBy, "2, 2, right, default");
		
		txtEmailToAdd = new JTextField();
		pnlAddUser.add(txtEmailToAdd, "4, 2, fill, default");
		txtEmailToAdd.setColumns(10);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				OrganizationDetailsCard.this.addUser();
			}
		});
		pnlAddUser.add(btnAdd, "6, 2");
		
		table = new JTable(this.tableModel);
		pnlUsers.add(table, BorderLayout.CENTER);
		
		JButton btnRemoveUser = new JButton("Remove Selected User");
		btnRemoveUser.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				OrganizationDetailsCard.this.removeUser();
			}
		});
		pnlUsers.add(btnRemoveUser, BorderLayout.SOUTH);
	}

	
	public void setOrganization(OrganizationArrayItem orgIn)
	{
		this.org = orgIn;
		this.refreshUi();
	}
	
	
	@Override
	public void onBecomesVisible()
	{
		System.out.println("org visible");
		this.refreshUi();
	}

	
	private void refreshUi()
	{
		if( this.org == null ) return;
		
		this.txtName.setText(this.org.getName());
		this.lblUuidValue.setText(this.org.getUuid());
		this.tableModel.refreshForOrganizationUuid(this.org.getUuid());
	}
	
	
	private void addUser()
	{
		String emailToAdd = this.txtEmailToAdd.getText();
		if( emailToAdd.isEmpty() ) return;
		
		ModelManager.getSingleton().addUserToOrganization(this.org.getUuid(), this.txtEmailToAdd.getText(),
				new AddRemoveOrganizationUserCallback() {
					@Override
					public void onCompletion(boolean wasSuccessfulIn)
					{
						if( wasSuccessfulIn )
						{
							// refresh our user table
							OrganizationDetailsCard.this.tableModel.refreshForOrganizationUuid(OrganizationDetailsCard.this.org.getUuid());
						}
						else
						{
							JOptionPane.showMessageDialog(OrganizationDetailsCard.this, 
									"Ensure that target user has an account registered to this email address",
									"Error Adding User", JOptionPane.ERROR_MESSAGE);
						}
					}
		});
		
		this.tableModel.refreshForOrganizationUuid(this.org.getUuid());
	}
	
	
	private void removeUser()
	{
		int selRow = OrganizationDetailsCard.this.table.getSelectedRow();
		if( selRow == -1 ) return;
		
		OrganizationUserArrayItem user = ((OrganizationUsersTableModel)OrganizationDetailsCard.this.table.getModel()).getUserAtIndex(selRow);
		if( user == null ) return;
		
		ModelManager.getSingleton().removeUserFromOrganization(this.org.getUuid(), user.getEmail(),
				new AddRemoveOrganizationUserCallback() {
					@Override
					public void onCompletion(boolean wasSuccessfulIn)
					{
						if( wasSuccessfulIn )
						{
							// refresh our user table
							OrganizationDetailsCard.this.tableModel.refreshForOrganizationUuid(OrganizationDetailsCard.this.org.getUuid());
						}
						else
						{
							JOptionPane.showMessageDialog(OrganizationDetailsCard.this, 
									"Selected user not found in organization",
									"Error Removing User", JOptionPane.ERROR_MESSAGE);
						}
					}
		});
	}
}
