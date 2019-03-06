package eu.stamp.eclipse.descartes.jira;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

public class DescartesJiraAccountsManager {
	
	private Map<String,String> accounts;
	
	private ISecurePreferences preferences;
	
	private String selection;
	
	private int max;
	
	public DescartesJiraAccountsManager() throws StorageException {
		
		accounts = new HashMap<String,String>();
		
		preferences = SecurePreferencesFactory.getDefault()
				.node(DescartesJiraPreferencePage.PREFERENCES_KEY);
		
		int i = 0;
		String urlKey = DescartesJiraPreferencePage.URL_KEY;
		String key = urlKey + String.valueOf(i);
		while(preferences.nodeExists(key)) {
			accounts.put(preferences.get(DescartesJiraPreferencePage.USER_KEY + String.valueOf(i),"")
					+ " : " + preferences.get(key,""),String.valueOf(i));
			i++;
			key = urlKey + String.valueOf(i);
		}
		max = i - 1;
		if(max > -1) selection = preferences.get(DescartesJiraPreferencePage.USER_KEY + "0","")
		+ " : " + preferences.get(urlKey + "0","");
	}
	
	public boolean isEmpty() { return max == 0; }
	
	public Set<String> getAccounts(){ return accounts.keySet(); }
	
	public String getUrl() {
		if(selection == null || selection.isEmpty()) return "";
		return selection.split(" : ")[1];
	}
	
	public String getUser() {
		if(selection == null || selection.isEmpty()) return "";
		return selection.split(" : ")[0];
	}
	
	public String getPassword() {
		if(selection == null || selection.isEmpty()) return "";
		try {
			return preferences.get(DescartesJiraPreferencePage.PASSWORD_KEY 
					+ String.valueOf(accounts.get(selection)),"");
		} catch (StorageException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public void setSelection(String selection) {
		this.selection = selection;
	}
	
	public void modifyAccount(String url,String user,String password) {
		String n = accounts.get(selection);
		String urlKey = DescartesJiraPreferencePage.URL_KEY + n;
		String userKey = DescartesJiraPreferencePage.USER_KEY + n;
		String passwordKey = DescartesJiraPreferencePage.PASSWORD_KEY + n;

			try {
			preferences.put(urlKey,url,true);
			preferences.put(userKey,user,true);
			preferences.put(passwordKey,password,true);
			accounts.put(user + " : " + url,n);
			} catch (StorageException e) {
                System.out.println("ERROR modifying account");
				e.printStackTrace();
			}
	}
	
	public void createAccount(String url,String user,String password) {
		max++;
		String n = String.valueOf(max);
		String urlKey = DescartesJiraPreferencePage.URL_KEY + n;
		String userKey = DescartesJiraPreferencePage.USER_KEY + n;
		String passwordKey = DescartesJiraPreferencePage.PASSWORD_KEY + n;
		try {
			preferences.put(urlKey,url,true);
			preferences.put(userKey,user,true);
			preferences.put(passwordKey,password,true);
			accounts.put(user + " : " + url,n);
		} catch (StorageException e) {
			e.printStackTrace();
			max--;
			System.out.println("ERROR : exception when saving new account");
		}
	}
}