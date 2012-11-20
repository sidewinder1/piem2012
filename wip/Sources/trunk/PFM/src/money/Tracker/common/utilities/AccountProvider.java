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

	public AccountProvider() {
		mAccManager = AccountManager.get(PfmApplication.getAppContext());
		mAccounts = mAccManager.getAccounts();
		mAccountList = getAccounts();
		updateCurrentAccount();
	}

	private void updateCurrentAccount() {
		String account = XmlParser.getInstance().getConfigContent("account");
		if ("".equals(account)) {
			currentAccount = null;
			return;
		}

		// Parse xml to data.
		currentAccount = findAccountByEmail(account);
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

		if (mAccountList.size() == 0) {
			mAccountList.add(new Account("testAccount", "pfm.com"));
		}

		return mAccountList;
	}

	public Account findAccountByEmail(String email) {
		if (mAccounts == null) {
			mAccounts = mAccManager.getAccounts();
		}

		for (Account account : mAccounts) {
			if (email.equals(account.name)) {
				Logger.Log("Level1.2 " + account, "AccountProvider");
				return account;
			}
		}

		if (mAccountList.size() != 0) {
			return mAccountList.get(0);
		}

		Logger.Log(email + " has not been found, return null",
				"AccountProvider");
		return null;
	}
}
