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
		Logger.Log(mAccManager.getAccounts().length + "", "Account");
		mAccounts = mAccManager.getAccounts();

		mAccountList = getAccounts();
		Logger.Log(mAccountList.size() + "", "Account");
		updateCurrentAccount();
		Logger.Log(currentAccount.name + "", "Account");
	}

	private void updateCurrentAccount() {
		String account = XmlParser.getInstance().getConfigContent("account");

		if (account.length() == 0) {
			currentAccount = new Account("testAccount", "pfm.com");
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
		if (mAccountList == null) {
			mAccountList = getAccounts();
		}

		for (Account account : mAccountList) {
			if (email.equals(account.name)) {
				return account;
			}
		}

		if (mAccountList.size() != 0) {
			return mAccountList.get(0);
		}

		return null;
	}
}
