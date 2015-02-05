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

public class DownloadFileAdapter extends ArrayAdapter<DownloadFile>{


	public static Logger log = LoggerFactory.getLogger(DownloadFileAdapter.class);
	LayoutInflater inflater;
	JSONArray jsonDownloads;
	int ressource;
	Follower follower;
	private List<DownloadFile> downloads = new ArrayList<DownloadFile>();
	
	public DownloadFileAdapter(Follower follower, int ressource){
		super(follower.getContext(), ressource);
		inflater = LayoutInflater.from(follower.getContext());
		this.ressource = ressource;
		this.follower = follower;
	}
	
	public DownloadFileAdapter(Follower follower,
			int ressource, List<DownloadFile> downloads){
		super(follower.getContext(), ressource);
		inflater = LayoutInflater.from(follower.getContext());
		this.ressource = ressource;
		this.follower = follower;
		this.downloads = downloads;
		setDownloadFiles(downloads);
	}
	
	public void setDownloadFiles(List<DownloadFile> downloads){
		this.downloads = downloads;
		log.debug("On mets à jour la liste des fichiers "+downloads.toString());
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return downloads.size();
	}

	@Override
	public DownloadFile getItem(int position) {
		return downloads.get(position);
	}

	@Override
	public long getItemId(int position) {
		return downloads.get(position).getId().longValue();
	}
	
	private class ViewDwlInfo {
		ImageView imgTypeTransfert;
		TextView tvDownloadName;
		ProgressBar progressBarDownload;
		TextView progressStatus;
		TextView list_info;
		DownloadFile dwl;
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
		DownloadFile download = downloads.get(position);
		dwlInfo.dwl = download;
		dwlInfo.tvDownloadName.setText(download.getPath());
		dwlInfo.progressStatus.setText(download.toString());
		dwlInfo.list_info.setText(download.getOctetValue(download.getRx_bytes()));
		String errmsg = download.getErrmsg();
		if (errmsg!=null && !errmsg.isEmpty() && !"none".equals(errmsg)){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.emo_im_sad);
			dwlInfo.progressBarDownload.setVisibility(View.INVISIBLE);
			dwlInfo.progressStatus.setPadding(0, -5, 0, 0);
			dwlInfo.progressStatus.setText("Statut : erreur "+errmsg);
			return convertView;
		}
		dwlInfo.progressBarDownload.setVisibility(View.VISIBLE);
		dwlInfo.progressBarDownload.setMax(100);
		dwlInfo.progressBarDownload.setSecondaryProgress((int)(download.getRx_bytes()*100/download.getSize()));
		dwlInfo.progressBarDownload.setProgress(0);
		if ("stopped".equals(download.getStatus())){
			
		}
		else if ("queued".equals(download.getStatus())){
			
		}
		else if ("starting".equals(download.getStatus())){
			
		}
		else if ("downloading".equals(download.getStatus())){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.ic_menu_download);
		} 
		else if ("stopping".equals(download.getStatus())){
			
		}
		else if ("error".equals(download.getStatus())){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.emo_im_sad);
		}
		else if ("done".equals(download.getStatus())){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.btn_check_buttonless_on);
		}
		else if ("seeding".equals(download.getStatus())){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.ic_menu_upload);			
		}
		else if ("retry".equals(download.getStatus())){
			
		}
		return convertView;
	}

}
