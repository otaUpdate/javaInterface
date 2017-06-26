package net.otaupdate.app.ui.util;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;


public class ProgressDialog extends JDialog
{
	private static final long serialVersionUID = -2691516426809083043L;

	
	private final JPanel contentPanel = new JPanel();
	private final JProgressBar progressBar;
	private final JLabel lblExplanation;


	public ProgressDialog()
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setTitle("Uploading Firmware Image...");
		this.setBounds(100, 100, 450, 82);
		this.getContentPane().setLayout(new BorderLayout());
		this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
		this.contentPanel.setLayout(new BorderLayout(0, 0));
		
		this.progressBar = new JProgressBar();
		this.progressBar.setMinimum(0);
		this.progressBar.setMaximum(100);
		this.contentPanel.add(this.progressBar);
	
		this.lblExplanation = new JLabel("xxx / xxx");
		this.lblExplanation.setHorizontalAlignment(SwingConstants.TRAILING);
		this.contentPanel.add(this.lblExplanation, BorderLayout.SOUTH);
	}
	
	
	public void updateProgress(float pcntCompleteIn, String explanationStrIn)
	{
		this.progressBar.setValue( (int)(pcntCompleteIn * 100.0) );
		this.lblExplanation.setText(explanationStrIn);
	}

}
