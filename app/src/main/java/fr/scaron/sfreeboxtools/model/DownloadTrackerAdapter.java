package fr.scaron.sfreeboxtools.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.R;

public class DownloadTrackerAdapter extends ArrayAdapter<DownloadTracker>{


	public static Logger log = LoggerFactory.getLogger(DownloadTrackerAdapter.class);
	LayoutInflater inflater;
	JSONArray jsonDownloads;
	int ressource;
	Follower follower;
	private List<DownloadTracker> downloads = new ArrayList<DownloadTracker>();
	
	public DownloadTrackerAdapter(Follower follower, int ressource){
		super(follower.getContext(), ressource);
		inflater = LayoutInflater.from(follower.getContext());
		this.ressource = ressource;
		this.follower = follower;
	}
	
	public DownloadTrackerAdapter(Follower follower,
			int ressource, List<DownloadTracker> downloads){
		super(follower.getContext(), ressource);
		inflater = LayoutInflater.from(follower.getContext());
		this.ressource = ressource;
		this.follower = follower;
		this.downloads = downloads;
		setDownloadTrackers(downloads);
	}
	
	public void setDownloadTrackers(List<DownloadTracker> downloads){
		this.downloads = downloads;
		log.debug("On mets à jour la liste des fichiers "+downloads.toString());
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return downloads.size();
	}

	@Override
	public DownloadTracker getItem(int position) {
		return downloads.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.valueOf(""+downloads.get(position));
	}
	
	private class ViewDwlInfo {
		ImageView imgTypeTransfert;
		TextView tvDownloadName;
		ProgressBar progressBarDownload;
		TextView progressStatus;
		TextView list_info;
		DownloadTracker dwl;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewDwlInfo dwlInfo;
		if(convertView == null) {
			dwlInfo = new ViewDwlInfo();
			convertView = inflater.inflate(R.layout.list_item_notifstyle_small, null);
			convertView.setBackgroundColor(Color.TRANSPARENT);
			dwlInfo.tvDownloadName = (TextView)convertView.findViewById(R.id.list_title);
			dwlInfo.imgTypeTransfert = (ImageView)convertView.findViewById(R.id.list_icon);
			dwlInfo.progressBarDownload = (ProgressBar)convertView.findViewById(R.id.list_progress);
			dwlInfo.progressStatus = (TextView)convertView.findViewById(R.id.list_text);
			dwlInfo.list_info = (TextView)convertView.findViewById(R.id.list_info);
			convertView.setTag(dwlInfo);
		} else {
			dwlInfo = (ViewDwlInfo) convertView.getTag();
		}
		DownloadTracker download = downloads.get(position);
		dwlInfo.dwl = download;
		dwlInfo.tvDownloadName.setText(download.getAnnounce());
		dwlInfo.progressStatus.setText(download.toString());
		dwlInfo.list_info.setText(download.getNleechers()+" leechers, "+download.getNseeders()+" seeders");
		dwlInfo.progressBarDownload.setVisibility(View.GONE);
		
		if ("announcing".equals(download.getStatus())){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.ic_menu_download);
		}
		else if ("announce_failed".equals(download.getStatus())){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.emo_im_sad);
		}
		else if ("announced".equals(download.getStatus())){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.btn_check_buttonless_on);
		}
		else if ("unannounced".equals(download.getStatus())){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.ic_menu_upload);			
		}
		return convertView;
	}

}
