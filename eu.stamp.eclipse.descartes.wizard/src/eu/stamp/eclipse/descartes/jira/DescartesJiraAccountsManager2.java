package eu.stamp.eclipse.descartes.jira;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

public class DescartesJiraAccountsManager2 {

	// constants for storage
	private static final String OUT_KEY = "DescartesJiraStorage";
	private static final String IN_KEY = "DescartesJiraAccounts";
	private static final String SEPARATOR = ",";
	
	private List<Account> accounts;
	
	private int selection;
	
	private boolean error;
	
	public DescartesJiraAccountsManager2(){
		
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault().node(OUT_KEY);
		try {
			String serializationString = preferences.get(IN_KEY,"");
			error = !deserialization(serializationString);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	
	// public methods
	public String getUrl() {
		if(error) return "";
		return accounts.get(selection).url;
	}
	
	public String getUser() {
		if(error) return "";
		return accounts.get(selection).user;
	}
	
	public String getPassword() {
		if(error) return "";
		return accounts.get(selection).password;
	}
	
	public String[] getAccounts() {
		if(error) return new String[] {""};
		String[] result = new String[accounts.size()];
		for(int i = 0; i < accounts.size(); i++)
			result[i] = accounts.get(i).getSummary();
		return result;
	}
	
	public void setSelection(String summary) {
         selection = find(summary);
	}
	
	public boolean empty() {
		return accounts == null || accounts.isEmpty();
	}
	
	public void createAccount(String url,String user,String password) {
		Account account = new Account();
		account.url = url;
		account.user = user;
		account.password = password;
		if(accounts == null) accounts = new ArrayList<Account>(1);
		accounts.add(account);
		save();
	}
	
	public void modify(String url,String user,String password){
		if(accounts.isEmpty()) {
			createAccount(url,user,password);
			return;
		}
		Account account = accounts.get(selection);
		account.url = url;
		account.user = user;
		account.password = password;
		save();
	}
	
	// inner logic
	private void save() {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault().node(OUT_KEY);
		try {
			preferences.put(IN_KEY,serialization(),true);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	
	private boolean deserialization(String serializationString) {
		if(serializationString == null || serializationString.isEmpty()) return false;
		if(!serializationString.contains(SEPARATOR)) {
			accounts = new ArrayList<Account>(1);
			Account account = new Account();
			if(account.deserialize(serializationString)) {
				accounts.add(account);
				return true;
			}
			return false;
		}
		String[] strings = serializationString.split(SEPARATOR);
		accounts = new ArrayList<Account>(strings.length);
		Account account = null;
		boolean sucess = false;
		for(String string : strings) {
			account = new Account();
			if(account.deserialize(string)) {
				accounts.add(account);
				sucess = true;
			}
		}
		return sucess;
	}
	
	private String serialization() {
		if(accounts == null || accounts.isEmpty()) return "";
		StringBuilder builder = new StringBuilder();
		builder.append(accounts.get(0).toString());
		for(int i = 1; i < accounts.size(); i++){
			builder.append(SEPARATOR);
			builder.append(accounts.get(i).serialize());
		}
		return builder.toString();
	}
	
	private int find(String summary){
		for(int i = 0; i < accounts.size(); i++)
			if(accounts.get(i).match(summary)) return i;
		return 0;
	}
	
	// Nested class
	private class Account {
		
		static final String INNER_SEPARATOR = ";";
		
		String url;
		String user;
		String password;
		
		String getSummary() { return user + " : " + url; }
		
		boolean match(String summary) { 
			if(!summary.contains(" : ")) return false;
			String[] target = summary.split(" : ");
			if(target.length != 2) return false;
			boolean result = target[0].equalsIgnoreCase(user)
					&& target[1].equalsIgnoreCase(url);
			return result;
		}
		
		boolean deserialize(String serialization) {
			if(!serialization.contains(INNER_SEPARATOR))return false;
		    String[] target = serialization.split(INNER_SEPARATOR);
		    if(target.length != 3) return false;
		    url = target[0];
		    user = target[1];
		    password = target[2];
		    return true;
		}
		
		String serialize() {
			StringBuilder builder = new StringBuilder();
			builder.append(url).append(INNER_SEPARATOR).append(user)
			.append(INNER_SEPARATOR).append(password);
			return builder.toString();
		}
	}
}