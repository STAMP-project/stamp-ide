package eu.stamp.eclipse.descartes.jira;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import eu.stamp.wp4.descartes.view.DescartesIssuesView;

public class DescartesJiraAccountsManager {
	
	private static DescartesJiraAccountsManager INSTANCE;

	// constants for storage
	private static final String OUT_KEY = "DescartesJiraStorage";
	private static final String IN_KEY = "DescartesJiraAccounts";
	private static final String SEPARATOR = ",";
	
	private AccountList accounts;
	
	private int selection;
	
	//private boolean error;
	
	public static DescartesJiraAccountsManager getInstance() {
		if(INSTANCE == null) INSTANCE = new DescartesJiraAccountsManager();
		return INSTANCE;
	}
	
	private DescartesJiraAccountsManager(){
		
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault().node(OUT_KEY);
		try {
			String serializationString = preferences.get(IN_KEY,"");
	        deserialization(serializationString);
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	
	private String processString(String string) {
		if(string == null) return "";
		return string;
	}
	
	// public methods
	public String getUrl() { return processString(accounts.get(selection).url); }
	
	public String getUser() { return processString(accounts.get(selection).user); }
	
	public String getPassword() { return processString(accounts.get(selection).password); }
	
	public List<String> getAccounts() {
		List<String> result = new ArrayList<String>(accounts.size());
		for(Account account : accounts)if(account.url != null)
            result.add(account.getSummary());
		return result;
	}
	
	public void setSelection(String summary) {
         selection = find(summary);
	}
	
	public boolean empty() {
		return accounts == null || accounts.isEmpty();
	}
	
	public String createAccount(String url,String user,String password) {
		Account account = new Account();
		account.url = url;
		account.user = user;
		account.password = password;
		if(accounts == null) accounts = new AccountList(1);
		accounts.add(account);
		selection = accounts.size() - 1;
		save();
		return account.getSummary();
	}
	
	public void removeAccount(String summary) {
		int i = find(summary);
		accounts.remove(i);
		if(selection > i) selection --;
		else if(selection == i) selection = 0;
	}
	
	public void modify(String url,String user,String password){
		if(accounts.isEmpty()) {
			createAccount(url,user,password);
			save();
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
			DescartesIssuesView.resetWizard();
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	
	private void deserialization(String serializationString) {
		if(serializationString == null || serializationString.isEmpty()) return;
		if(!serializationString.contains(SEPARATOR)) {
			accounts = new AccountList(1);
			Account account = new Account();
			account.deserialize(serializationString);
		    accounts.add(account);
			return;
		}
		String[] strings = serializationString.split(SEPARATOR);
		accounts = new AccountList(strings.length);
		Account account = null;
		for(String string : strings) {
			account = new Account();
			account.deserialize(string);
			accounts.add(account);
		}
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
		
		void deserialize(String serialization) {
			if(!serialization.contains(INNER_SEPARATOR))return;
		    String[] target = serialization.split(INNER_SEPARATOR);
		    if(target.length != 3) return;
		    url = target[0];
		    user = target[1];
		    password = target[2];
		}
		
		String serialize() {
			StringBuilder builder = new StringBuilder();
			builder.append(url).append(INNER_SEPARATOR).append(user)
			.append(INNER_SEPARATOR).append(password);
			return builder.toString();
		}
	}
	
	// Nested classes
	private class AccountList implements Iterable<Account> {
		
		final List<Account> list;
		
		AccountList(int size) { list = new ArrayList<Account>(size); }
		
		void add(Account account) {
			if(account == null || account.url == null || account.url.isEmpty()) return;
			for(Account element : list)if(element.url.equalsIgnoreCase(account.url)
					&& element.user.equalsIgnoreCase(account.user)) return;
			list.add(account);
		}
		
		Account get(int index) { return list.get(index); }

		int size() { return list.size(); }
		
		boolean isEmpty() { return list.isEmpty(); }
		
		void remove(int index) { list.remove(index); }
		
		@Override
		public Iterator<Account> iterator() {
			return list.iterator();
		}
	}
}