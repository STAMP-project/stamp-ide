package eu.stamp.eclipse.descartes.jira;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DescartesJiraPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text userText;
	
	private Text passwordText;
	
	private Text passwordText2;
	
	private Text urlText;
	
	public static final String PREFERENCES_KEY = "ExperimentPreferences";
	
	public static final String URL_KEY = "jiraUrl";
	
	public static final String USER_KEY = "user";
	
	public static final String PASSWORD_KEY = "password";
	
	public DescartesJiraPreferencePage() {}

	public DescartesJiraPreferencePage(String title) {
		super(title);
	}

	public DescartesJiraPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
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
		
		Label printLabel = new Label(parent,SWT.NONE);
		printLabel.setText("Print preferences : ");
		
		Button printButton = new Button(parent,SWT.PUSH);
		printButton.setText("Print nowadays data");
		printButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			if(!SecurePreferencesFactory.getDefault().nodeExists(PREFERENCES_KEY)) {
				System.out.println("No preferences set");
				return;
			}
			ISecurePreferences preferences = SecurePreferencesFactory.getDefault().node(PREFERENCES_KEY);
			try {
				String user = preferences.get(USER_KEY,null);
				if(user == null || user.isEmpty()) {
					System.out.println("No preferences set");
					return;
				}
				String url = preferences.get(URL_KEY,"");
				String password = preferences.get(PASSWORD_KEY,"");
				System.out.println("Nowadays preferences :");
				System.out.println("   - Jira URL : "
						+ url + " eccripted = " + String.valueOf(preferences.isEncrypted(URL_KEY)));
				System.out.println("   - User : " + user + 
						" encripted = " + String.valueOf(preferences.isEncrypted(USER_KEY)));
				System.out.println("   - Password : " + password
						+ " Encripted = " + String.valueOf(preferences.isEncrypted(PASSWORD_KEY)));
			} catch (StorageException e1) {
				e1.printStackTrace();
			}
			}
		});
		
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
        ISecurePreferences preferences = SecurePreferencesFactory.getDefault()
        		.node(PREFERENCES_KEY);
        try {
			preferences.put(USER_KEY,user,true);
			preferences.put(PASSWORD_KEY,password,true);
			preferences.put(URL_KEY, url,true);
		} catch (StorageException e) {
			e.printStackTrace();
		}
		return true;
	}

}
