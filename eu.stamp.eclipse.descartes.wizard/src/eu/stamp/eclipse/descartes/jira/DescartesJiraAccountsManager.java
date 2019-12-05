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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

import eu.stamp.wp4.descartes.view.DescartesIssuesView;
/**
 * This singleton is responsible to track the register of Jira accounts,
 * track the selected one and provide information for the preferences page and
 * the Jira issue creation wizard
 */
public class DescartesJiraAccountsManager {
	
	private static DescartesJiraAccountsManager INSTANCE;

	// constants for storage
	private static final String OUT_KEY = "DescartesJiraStorage";
	private static final String IN_KEY = "DescartesJiraAccounts";
	private static final String SEPARATOR = ",";
	
	private AccountList accounts;
	
	private int selection;
	
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
	/**
	 * this method is only to avoid nulls
	 */
	private String processString(String string) {
		if(string == null) return "";
		return string;
	}
	
	// public methods
	/**
	 * @return : the url of the selected account
	 */
	public String getUrl() { return processString(accounts.get(selection).url); }
	/**
	 * @return : the user name of the selected account
	 */
	public String getUser() { return processString(accounts.get(selection).user); }
	/**
	 * @return : the password of the selected account
	 */
	public String getPassword() { return processString(accounts.get(selection).password); }
	/**
	 * @return : a list with the string summaries of the existing accounts
	 */
	public List<String> getAccounts() {
		if(accounts == null) {
			accounts = new AccountList(1);
			return new ArrayList<String>(1);
		}
		List<String> result = new ArrayList<String>(accounts.size());
		for(Account account : accounts)if(account.url != null)
            result.add(account.getSummary());
		return result;
	}
	/**
	 * @param summary : the summary string that identifies an account
	 */
	public void setSelection(String summary) {
         selection = find(summary);
	}
    /**
     * @return : true if there is no accounts
     */
	public boolean empty() {
		return accounts == null || accounts.isEmpty();
	}
	/**
	 * register a new account
	 * @param url : url of the account
	 * @param user : user name
	 * @param password
	 * @return : the string summary of this account
	 */
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
	/**
	 * remove an account
	 * @param summary : the string summary identifying the account to remove
	 */
	public void removeAccount(String summary) {
		int i = find(summary);
		accounts.remove(i);
		if(selection > i) selection --;
		else if(selection == i) selection = 0;
	}
	/**
     * modify the selected account
	 * @param url
	 * @param user
	 * @param password
	 */
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
	/**
	 *  keep the accounts in the Eclipse secure store (encrypted)
	 */
	private void save() {
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault().node(OUT_KEY);
		try {
			preferences.put(IN_KEY,serialization(),true);
			DescartesIssuesView.resetWizard();
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	/**
	 * load the account list from its serialization String
	 * @param serializationString : the string representation of the account list kept in the secure store
	 */
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
	/**
	 * @return : the string serialization of the accounts information
	 */
	private String serialization() {
		if(accounts == null || accounts.isEmpty()) return "";
		StringBuilder builder = new StringBuilder();
		builder.append(accounts.get(0).serialize());
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
	
	// Nested classes
	/**
	 * An instance of this class represents a Jira account (URL,user name and password)
	 */
	private class Account {
		
		static final String INNER_SEPARATOR = ";";
		
		String url;
		String user;
		String password;
		
		String getSummary() { return user + " : " + url; }
		/**
		 * check if two accounts are the same
		 * @param summary : the summary of an account
		 * @return : true if the given summary identifies the same account
		 */
		boolean match(String summary) { 
			if(!summary.contains(" : ")) return false;
			String[] target = summary.split(" : ");
			if(target.length != 2) return false;
			boolean result = target[0].equalsIgnoreCase(user)
					&& target[1].equalsIgnoreCase(url);
			return result;
		}
		/**
		 * load the property values from a serialization string
		 * @param serialization : an String representation of an Account instance
		 */
		void deserialize(String serialization) {
			if(!serialization.contains(INNER_SEPARATOR))return;
		    String[] target = serialization.split(INNER_SEPARATOR);
		    if(target.length != 3) return;
		    url = target[0];
		    user = target[1];
		    password = target[2];
		}
		/**
		 * @return : a string serialization of this instance
		 */
		String serialize() {
			StringBuilder builder = new StringBuilder();
			builder.append(url).append(INNER_SEPARATOR).append(user)
			.append(INNER_SEPARATOR).append(password);
			return builder.toString();
		}
	}
	/**
	 * A list to keep accounts without repetition
	 */
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