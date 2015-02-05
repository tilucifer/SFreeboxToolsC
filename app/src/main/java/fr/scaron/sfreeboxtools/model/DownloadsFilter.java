package fr.scaron.sfreeboxtools.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.widget.Filter;

@SuppressLint("DefaultLocale")
public class DownloadsFilter extends Filter {
	public static Logger log = LoggerFactory.getLogger(DownloadsFilter.class);

	private final Object lock = new Object();
	private List<Download> mOriginalValues;
	private List<Download> mActivitiesList;
	private DownloadAdapter adapter;

	public DownloadsFilter(List<Download> originalValues,
			DownloadAdapter adapter) {
		this.mOriginalValues = originalValues;
		this.adapter = adapter;
	}


	@Override
	protected FilterResults performFiltering(CharSequence prefix) {
		FilterResults results = new FilterResults();

		log.debug("filter with '" + prefix + "'");

		if (mOriginalValues == null) {
			synchronized (lock) {
				mOriginalValues = new ArrayList<Download>(mActivitiesList);
			}
		}

		if (prefix == null || prefix.length() == 0) {
			synchronized (lock) {
				ArrayList<Download> list = new ArrayList<Download>(
						mOriginalValues);
				results.values = list;
				results.count = list.size();
			}
		} else {
			final String prefixString = prefix.toString().toLowerCase();

			ArrayList<Download> values = (ArrayList<Download>) mOriginalValues;
			int count = values.size();

			ArrayList<Download> newValues = new ArrayList<Download>(count);

			for (int i = 0; i < count; i++) {
				Download item = values.get(i);
				if (item.getName().toLowerCase().contains(prefixString)) {
					newValues.add(item);
				}
			}

			results.values = newValues;
			results.count = newValues.size();
		}
		log.debug("filter return '" + results.count + "' with values '"
				+ results.values + "'");
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
		// noinspection unchecked
		adapter.setDownloads((List<Download>) results.values);
		if (results.count > 0) {
			adapter.notifyDataSetChanged();
		} else {
			adapter.notifyDataSetInvalidated();
		}
	}
}
