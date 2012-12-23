package money.Tracker.presentation.activities;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import money.Tracker.common.utilities.ExcelHelper;
import money.Tracker.common.utilities.Logger;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FileExplorerActivity extends Activity {
	/** Called when the activity is first created. */
	// Stores names of traversed directories
	ArrayList<String> str = new ArrayList<String>();
	// Check if the first level of the directory structure is the one showing
	String[] allowedExtension;

	private List<Item> fileList;
	private Item item;
	private File path = new File(Environment.getExternalStorageDirectory() + "");
	private String chosenFile = "";
	private int file_icon;
	ListView listViewDir;
	ListAdapter adapter;
	ListFile listFileAdaptor;
	int value = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_explorer_activity);
		listViewDir = (ListView) findViewById(R.id.explorer_list);

		Bundle extras = getIntent().getExtras();
		allowedExtension = extras
				.getStringArray("FileExplorerActivity.allowedExtension");
		file_icon = extras.getInt("FileExplorerActivity.file_icon");
		if (isSdPresent()) {
//			Alert.getInstance().show(FileExplorerActivity.this, "Unmounted");
			loadFileList();
			listFileAdaptor = new ListFile(FileExplorerActivity.this, fileList);
			listViewDir.setAdapter(listFileAdaptor);
//		} else {
//			Alert.getInstance().show(FileExplorerActivity.this, "Mounted");
		}

		listViewDir.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// if (value == 0) {
				chosenFile = fileList.get(position).getFile();
				/*
				 * } else { chosenFile = backList.get(position).getFile(); value
				 * =0; }
				 */
				File sel = new File(path + "/" + chosenFile);

				if (sel.isDirectory()) {

					// Adds chosen directory to list
					str.add(chosenFile);

					path = new File(sel + "");

					loadFileList();
					listFileAdaptor = new ListFile(FileExplorerActivity.this,
							fileList);
					listViewDir.setAdapter(listFileAdaptor);
				}

				// Checks if 'up' was clicked
				// File picked
				else {
					switch (file_icon) {
					case R.drawable.audio_file_icon:
						
						break;
					case R.drawable.xls_file_icon:
						ExcelHelper.getInstance().importData(path + "/" + chosenFile);
						finish();
						break;
					default:
						break;
					}
				}
			}
		});
	}

	public static boolean isSdPresent() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	private class Item {
		public String file;
		public int icon;

		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}

		public int getIcon() {
			return icon;
		}

		public void setIcon(int icon) {
			this.icon = icon;
		}

	}

	private void loadFileList() {
		try {
			path.mkdirs();
		} catch (SecurityException e) {
			Logger.Log(e.getMessage(), "FileExplorerActivity");
		}

		// Checks whether path exists
		if (path.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					// Filters based on whether the file is hidden or not
					return (sel.isFile() || sel.isDirectory())
							&& !sel.isHidden();

				}
			};

			String[] fList = path.list(filter);
			fileList = new ArrayList<FileExplorerActivity.Item>();

			for (int i = 0; i < fList.length; i++) {
				item = new Item();
				// item.setIcon(R.drawable.file_icon);
				// item.setFile(fList[i]);
				// fileList.add(item);
				// Convert into file path
				File sel = new File(path, fList[i]);

				String name = sel.getName();

				if (sel.isDirectory()) {
					item.setFile(name);
					item.setIcon(R.drawable.directory_icon);
					fileList.add(item);

				} else {
					if (checkExtension(name)) {
						item.setFile(name);
						item.setIcon(file_icon);
						fileList.add(item);
					} else {
						fileList.remove(item);
					}
				}
			}

		} else {
			Logger.Log(getResources().getString(R.string.file_not_exist),
					"FileExplorerActivity");
		}

	}

	private boolean checkExtension(String name) {
		for (String extension : allowedExtension) {
			if (name.endsWith(extension)) {
				return true;
			}
		}

		return false;
	}

	class ViewHolder {
		TextView textViewName;
	}

	class ListFile extends BaseAdapter {
		List<Item> fileList;
		private LayoutInflater inflator;
		Context context;

		ListFile(Context context, List<Item> fileList) {
			this.fileList = fileList;
			this.context = context;
			inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return fileList.size();
		}

		public Object getItem(int position) {
			return fileList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				if (getItemViewType(position) == 0) {
					convertView = inflator.inflate(R.layout.explorer_file_item,
							null);
				}

				holder = new ViewHolder();
				holder.textViewName = (TextView) convertView
						.findViewById(R.id.explorer_file_item_text);

				convertView.setTag(holder);
				// Set the display text
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textViewName.setText(fileList.get(position).getFile());

			holder.textViewName.setCompoundDrawablesWithIntrinsicBounds(
					fileList.get(position).getIcon(), 0, 0, 0);

			return convertView;
		}
	}
}