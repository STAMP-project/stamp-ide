package eu.stamp.eclipse.descartes.jira;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DescartesJiraPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text userText,passwordText,passwordText2,urlText;
	
	private DescartesJiraAccountsManager2 manager;
	
	private boolean modify;
	
	public static final String PREFERENCES_KEY = "ExperimentPreferences";
	
	public static final String URL_KEY = "jiraUrl";
	
	public static final String USER_KEY = "user";
	
	public static final String PASSWORD_KEY = "password";
	
	public DescartesJiraPreferencePage() { initialization(); }

	public DescartesJiraPreferencePage(String title) {
		super(title);
		initialization();
	}

	public DescartesJiraPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		initialization();
	}
	
	private void initialization() {
		manager = new DescartesJiraAccountsManager2();
	}

	@Override
	public void init(IWorkbench workbench) {
		// do nothing
	}

	@Override
	protected Control createContents(Composite parent) {
        
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		parent.setLayout(layout);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);
		
		// URL
		Label urlLabel = new Label(parent,SWT.NONE);
		urlLabel.setText("Jira Url : ");
		
		urlText = new Text(parent,SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true,false).span(2,1).applyTo(urlText);
		
		// User
		Label userLabel = new Label(parent,SWT.NONE);
		userLabel.setText("User : ");
		GridDataFactory.swtDefaults().indent(0,8).applyTo(userLabel);
		
		userText = new Text(parent,SWT.BORDER);
		GridDataFactory.fillDefaults().span(2,1)
		.indent(0,8).grab(true,false).applyTo(userText);
		
		// Password
		Label passwordLabel = new Label(parent,SWT.NONE);
		passwordLabel.setText("PassWord : ");
		GridDataFactory.swtDefaults().indent(0,8).applyTo(passwordLabel);
		
		passwordText = new Text(parent,SWT.PASSWORD | SWT.BORDER);
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,8).applyTo(passwordText);
		
		// Repeat Password
		Label passwordLabel2 = new Label(parent,SWT.NONE);
		passwordLabel2.setText("Repeat password : ");
		GridDataFactory.swtDefaults().indent(0,4).applyTo(passwordLabel2);
		
		passwordText2 = new Text(parent,SWT.PASSWORD | SWT.BORDER);
		GridDataFactory.fillDefaults().span(2,1).grab(true,false)
		.indent(0,4).applyTo(passwordText2);
		
	    // create or modify
		Button createButton = new Button(parent,SWT.RADIO);
		createButton.setTextDirection(SWT.LEFT);
		createButton.setText("Create new account");
		createButton.setSelection(true);
		GridDataFactory.swtDefaults().indent(0,8).applyTo(createButton);
		
		Button modifyButton = new Button(parent,SWT.RADIO);
		modifyButton.setTextDirection(SWT.LEFT);
		modifyButton.setText(" Modify an existing account");
		GridDataFactory.swtDefaults().span(2,1).indent(0,8).applyTo(modifyButton);
		
		// Existing accounts
		Label existingLabel = new Label(parent,SWT.NONE);
		existingLabel.setText("Existing accounts : ");
		GridDataFactory.swtDefaults().indent(0,4).applyTo(existingLabel);
		
		Combo existingCombo = new Combo(parent,SWT.BORDER | SWT.READ_ONLY);
	    String[] accounts = manager.getAccounts();
		GridDataFactory.fillDefaults().indent(0,4).span(2,1)
		.grab(true,false).applyTo(existingCombo);
	    for(String account : accounts) existingCombo.add(account);
		existingCombo.add("");
		existingCombo.setText("");
		existingCombo.setEnabled(false);
		
		createButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				existingCombo.setEnabled(false);
				urlText.setText("");
				userText.setText("");
				passwordText.setText("");
				passwordText2.setText("");
				modify = !createButton.getSelection();
			}
		});
		
		modifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
	            existingCombo.setEnabled(true); 
	            modify = modifyButton.getSelection();
			}
		});
		
		existingCombo.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if(existingCombo.getText().isEmpty()) return;
                manager.setSelection(existingCombo.getText());
                urlText.setText(manager.getUrl());
                userText.setText(manager.getUser());
                String password = manager.getPassword();
                passwordText.setText(password);
                passwordText2.setText(password);
            }
		});
		// TODO combo listener
		
		parent.pack();
		return parent;
	}
	
	@Override
	public boolean performOk() {
		
		// checking
		String url = urlText.getText();
		if(url == null || url.isEmpty()) {
			setErrorMessage("Jira Url field is empty");
			return false;
		}
		
		String password = passwordText.getText();
		if(password == null || password.isEmpty()) {
			setErrorMessage("Password field is empty");
			return false;
		}
		if(!passwordText2.getText().equalsIgnoreCase(password)) {
			setErrorMessage("The two password fields don't match");
			return false;
		}
		String user = userText.getText();
		if(user == null || user.isEmpty()) {
			setErrorMessage("User is Empty");
			return false;
		}
		
		// save data
		if(modify) manager.modify(url,user,password);
		else manager.createAccount(url,user, password);
		return true;
	}
}