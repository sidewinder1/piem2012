package money.Tracker.common.utilities;

import java.util.ArrayList;
import money.Tracker.presentation.PfmApplication;
import android.accounts.Account;
import android.accounts.AccountManager;

public class AccountProvider {
	private static AccountProvider sInstance;
	AccountManager mAccManager;
	ArrayList<Account> mAccountList;
	private Account[] mAccounts;
	public Account currentAccount;
	String CONFIG_FILE = "Fpm/FpmConfig.cnfg";
	
	public AccountProvider() {
		mAccManager = AccountManager.get(PfmApplication
				.getAppContext());
		
		mAccounts = mAccManager.getAccounts();
		
		mAccountList = getAccounts();
		updateCurrentAccount();
	}
	
	private void updateCurrentAccount(){
		String content = IOHelper.getInstance().readFile(CONFIG_FILE);

		if (content.length() != 0) {
			// Parse xml to data.
			String email = content;
			currentAccount = findAccountByEmail(email);
			return;
		}
		
		if (mAccountList.size() != 0){
			currentAccount = mAccountList.get(0);
		}
	} 

	public static AccountProvider getInstance() {
		return sInstance == null ? sInstance = new AccountProvider()
				: sInstance;
	}

	public ArrayList<Account> getAccounts() {
		if (mAccountList != null) {
			return mAccountList;
		}
		
		mAccountList = new ArrayList<Account>();
		if (mAccounts != null) {
			for (Account account : mAccounts) {
				mAccountList.add(account);
			}
		}

		if (mAccountList.size() == 0)
		{
			mAccountList.add(new Account("testAccount", "pfm.com"));
		}
		
		return mAccountList;
	}

	public Account findAccountByEmail(String email) {
		if (mAccountList == null) {
			mAccountList = getAccounts();
		}

		for (Account account : mAccountList) {
			if (email.equals(account.name)) {
				return account;
			}
		}

		return null;
	}

	public String getPasswordByEmail(String email) {
		if (mAccounts.length == 0){
			return "test1234";
		}
		
		return mAccManager.getPassword(findAccountByEmail(email));
	}

	public String getPasswordByAccount(Account account) {
		if (mAccounts.length == 0){
			return "test1234";
		}
		
		return mAccManager.getPassword(account);
	}
}
