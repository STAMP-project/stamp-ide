/*******************************************************************************
 * Copyright (c) 2019 Atos
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * 	Ricardo Jose Tejada Garcia (Atos) - main developer
 * 	Jesús Gorroñogoitia (Atos) - architect
 * Initially developed in the context of STAMP EU project https://www.stamp-project.eu
 *******************************************************************************/
package eu.stamp.eclipse.descartes.jira;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
/**
 * An instance of this class represents the Descartes Jira preferences page
 * to be displayed in Window > Preferences
 * @see eu.stamp.eclipse.descartes.jira.DescartesJiraAccountsManager
 */
public class DescartesJiraPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Text userText,passwordText,passwordText2,urlText;
	/**
	 *  true if the modify an existing account option is selected,
	 *  false if te create a new account option is selected
	 */
	private boolean modify;
	/**
	 *  A combo to select a registered account
	 */
	private Combo existingCombo;
	
	private Button modifyButton;
	
	// keys for saving and loaded data in the secure storage
	public static final String PREFERENCES_KEY = "ExperimentPreferences";
	public static final String URL_KEY = "jiraUrl";
	public static final String USER_KEY = "user";
	public static final String PASSWORD_KEY = "password";

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
		
		DescartesJiraAccountsManager manager = DescartesJiraAccountsManager.getInstance();
		
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
		GridDataFactory.swtDefaults().indent(0,8).applyTo(createButton);
		
		modifyButton = new Button(parent,SWT.RADIO);
		modifyButton.setTextDirection(SWT.LEFT);
		modifyButton.setText(" Modify an existing account");
		GridDataFactory.swtDefaults().span(2,1).indent(0,8).applyTo(modifyButton);
		
		// Existing accounts
		Label existingLabel = new Label(parent,SWT.NONE);
		existingLabel.setText("Existing accounts : ");
		GridDataFactory.swtDefaults().indent(0,4).applyTo(existingLabel);
		
		existingCombo = new Combo(parent,SWT.BORDER | SWT.READ_ONLY);
	    List<String> accounts = manager.getAccounts();
	    if(accounts.size() < 1) modifyButton.setEnabled(false);
		GridDataFactory.fillDefaults().indent(0,4).span(2,1)
		.grab(true,false).applyTo(existingCombo);
	    for(String account : accounts) existingCombo.add(account);
		existingCombo.add("");
		existingCombo.setText("");
		existingCombo.setEnabled(false);
		
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
		
		Button removeButton = new Button(parent,SWT.PUSH);
		removeButton.setText("Remove account");
		GridDataFactory.swtDefaults().span(3,1).indent(0,4).applyTo(removeButton);
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sr = existingCombo.getText();
				if(sr == null || sr.isEmpty()) return;
				manager.removeAccount(sr);
				existingCombo.remove(sr);
				if(existingCombo.getItemCount() < 1) {
					createButton.setSelection(true);
					createButton.notifyListeners(SWT.Selection,new Event());
					modifyButton.setEnabled(false);
					modifyButton.setSelection(false);
				}
			}
		});
		
		createButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				existingCombo.setEnabled(false);
				existingCombo.add("");
				existingCombo.setText("");
				urlText.setText("");
				userText.setText("");
				passwordText.setText("");
				passwordText2.setText("");
				modify = !createButton.getSelection();
				removeButton.setEnabled(false);
			}
		});
		
		modifyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			      existingCombo.setEnabled(true);
			      modify = modifyButton.getSelection();
			      int i = existingCombo.indexOf("");
			      while(i > -1) {
			      existingCombo.remove(i);
			      i = existingCombo.indexOf("");
			      }
			      existingCombo.select(0);
			      existingCombo.notifyListeners(SWT.Selection,new Event());
			      removeButton.setEnabled(true);
			}
				});
		
		if(accounts != null && !accounts.isEmpty()) {
			modifyButton.setSelection(true);
			modifyButton.notifyListeners(SWT.Selection,new Event());
		} else createButton.setSelection(true);
		
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
		if(modify) DescartesJiraAccountsManager.getInstance().modify(url,user,password);
		else{
			String summary = DescartesJiraAccountsManager.getInstance().createAccount(url,user, password);
			if(existingCombo != null && !existingCombo.isDisposed()) {
				existingCombo.add(summary);
				modifyButton.setEnabled(true);
			}
		}
		return true;
	}
}