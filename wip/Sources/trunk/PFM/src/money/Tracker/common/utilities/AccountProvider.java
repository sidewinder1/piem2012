package money.Tracker.common.utilities;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.PfmApplication;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.database.Cursor;

public class AccountProvider {
	private static AccountProvider sInstance;
	AccountManager mAccManager;
	ArrayList<Account> mAccountList;
	private Account[] mAccounts;
	private Account currentAccount;

	public AccountProvider() {
		mAccManager = AccountManager.get(PfmApplication.getAppContext());
		mAccounts = mAccManager.getAccounts();
		mAccountList = getAccounts();
		updateCurrentAccount();
	}

	public void refreshAccount() {
		mAccManager = AccountManager.get(PfmApplication.getAppContext());
		mAccounts = mAccManager.getAccounts();
		mAccountList = null;
		mAccountList = getAccounts();
	}

	public void setCurrentAccount(String email) {
		currentAccount = findAccountByEmail(email);
	}

	public Account getCurrentAccount() {
		return currentAccount;
	}

	private void updateCurrentAccount() {
		Cursor lastSyncCursor = SqlHelper.instance.select("AppInfo",
				"UserName", "Status = 1");
		if (lastSyncCursor != null && lastSyncCursor.moveToFirst()) {
			currentAccount = findAccountByEmail(lastSyncCursor.getString(0));
			return;
		}

		setDefaultLocalAccount();
	}

	public void setDefaultLocalAccount() {
		currentAccount = new Account("LocalAccount", "pfm.com");
	}

	public static AccountProvider getInstance() {
		return sInstance == null ? sInstance = new AccountProvider()
				: sInstance;
	}

	public ArrayList<Account> getAccounts() {
		if (mAccountList != null) {
			return mAccountList;
		}

		ArrayList<String> checkDuplicatedAccount = new ArrayList<String>();
		mAccountList = new ArrayList<Account>();
		if (mAccounts != null) {
			for (Account account : mAccounts) {
				if (checkDuplicatedAccount.contains(account.name)) {
					continue;
				}

				checkDuplicatedAccount.add(account.name);
				mAccountList.add(account);
				Cursor accountCheck = SqlHelper.instance.select("AppInfo",
						"UserName", "UserName = '" + account.name + "'");
				if (accountCheck == null || !accountCheck.moveToFirst()) {
					SqlHelper.instance.insert("AppInfo", new String[] {
							"UserName", "LastSync", "Status", "ScheduleWarn",
							"ScheduleRing", "ScheduleRemind", "BorrowWarn",
							"BorrowRing", "BorrowRemind" }, new String[] {
							account.name, "1990-01-20 00:00:00", "0",
							"50", "#DEFAULT", "10",
							"168", "#DEFAULT", "10" });
				}
			}
		}

		if (mAccountList.size() == 0) {
			mAccountList.add(new Account("DebugAccount2", "debug.com"));
			mAccountList.add(new Account("DebugAccount1", "debug.com"));
			mAccountList.add(new Account("DebugAccount3", "debug.com"));
		}

		return mAccountList;
	}

	public Account findAccountByEmail(String email) {
		if (mAccounts == null) {
			refreshAccount();
		}

		for (Account account : mAccountList) {
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
