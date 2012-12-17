package money.Tracker.common.utilities;

import money.Tracker.presentation.activities.R;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class ViewHelper {
	// This method is used to setup a tab with Name tab and content of tab.
	public static void setupTab(final Intent intent, final String tag,
			TabHost mTabHost) {
		View tabview = createTabView(mTabHost.getContext(), tag);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(intent);
		mTabHost.addTab(setContent);
	}

	public static Dialog createAppDialog(Context context, int titleId,
			View contentView) {
		Dialog dialog = new Dialog(context, R.style.CustomDialogTheme);
		dialog.setContentView(R.layout.app_dialog);
		((TextView) dialog.findViewById(R.id.app_dialog_title))
				.setText(titleId);
		((LinearLayout) dialog.findViewById(R.id.app_dialog_main_content))
				.addView(contentView);
		
		return dialog;
	}
	
	public static void attachAction(Dialog dialog, View.OnClickListener positiveAction,
			View.OnClickListener negativeAction){
		((Button)dialog.findViewById(R.id.app_dialog_doneBtn)).setOnClickListener(positiveAction);
		((Button)dialog.findViewById(R.id.app_dialog_cancelBtn)).setOnClickListener(negativeAction);
	}

	// Create tab view.
	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}
