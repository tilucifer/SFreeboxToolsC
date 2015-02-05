package fr.scaron.sfreeboxtools.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.activity.GetDownloadActivity;

public class DownloadAdapter extends ArrayAdapter<Download> implements Filterable{


	public static Logger log = LoggerFactory.getLogger(DownloadAdapter.class);
	LayoutInflater inflater;
	JSONArray jsonDownloads;
	int ressource;
	AbstractActivity follower;
	private List<Download> downloads = new ArrayList<Download>();
	private DownloadsFilter filter;
	
	
	public DownloadAdapter(AbstractActivity follower,
			int ressource, List<Download> downloads){
		super(follower, ressource);
		inflater = LayoutInflater.from(follower.getApplicationContext());
		this.ressource = ressource;
		this.follower = follower;
		this.downloads = downloads;
		setDownloads(downloads);
		filter = new DownloadsFilter(downloads, this);
	}
	
	public void setDownloads(List<Download> downloads){
		follower.updateTitle(String.valueOf(downloads.size()));
		this.downloads = downloads;
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return downloads.size();
	}

	@Override
	public Download getItem(int position) {
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
		Download dwl;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewDwlInfo dwlInfo;
		if(convertView == null) {
			dwlInfo = new ViewDwlInfo();
			convertView = inflater.inflate(R.layout.list_item_notifstyle_small, null);
			dwlInfo.tvDownloadName = (TextView)convertView.findViewById(R.id.list_title);
			dwlInfo.imgTypeTransfert = (ImageView)convertView.findViewById(R.id.list_icon);
			dwlInfo.progressBarDownload = (ProgressBar)convertView.findViewById(R.id.list_progress);
			dwlInfo.progressStatus = (TextView)convertView.findViewById(R.id.list_text);
			dwlInfo.list_info = (TextView)convertView.findViewById(R.id.list_info);
			convertView.setTag(dwlInfo);
		} else {
			dwlInfo = (ViewDwlInfo) convertView.getTag();
		}
		Download download = downloads.get(position);
		dwlInfo.dwl = download;
		dwlInfo.tvDownloadName.setText(download.getName());
		dwlInfo.progressStatus.setText(download.toString());
		dwlInfo.list_info.setText(download.getOctetValue(download.getRx_bytes()));//download.getType());
		
		String errmsg=download.getError();
		if (errmsg!=null && !errmsg.isEmpty() && !"none".equals(errmsg)){
			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.emo_im_sad);
			dwlInfo.progressBarDownload.setVisibility(View.INVISIBLE);
			dwlInfo.progressStatus.setPadding(0, -5, 0, 0);
			dwlInfo.progressStatus.setText("Statut : erreur "+errmsg);
			return convertView;
		}
		dwlInfo.progressBarDownload.setVisibility(View.VISIBLE);
		dwlInfo.progressBarDownload.setMax(100);
		dwlInfo.progressBarDownload.setSecondaryProgress((int)(download.getRx_pct()/100));///download.getSize()));
		dwlInfo.progressBarDownload.setProgress((int)(download.getTx_pct()/100));
		
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
		
//		if ("downloading".equals(download.getStatus())
//				||"stopped".equals(download.getStatus())){
//			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.ic_menu_download);
//			dwlInfo.progressBarDownload.setMax(100);
//			dwlInfo.progressBarDownload.setIndeterminate(false);
//			dwlInfo.progressBarDownload.setProgress((int)((download.getRx_bytes()*100)/download.getSize()));
//			dwlInfo.progressBarDownload.setVisibility(View.VISIBLE);
//		}else if (download.getTx_rate()==0){
//			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.btn_check_buttonless_on);
//			dwlInfo.progressBarDownload.setVisibility(View.GONE);
//			//dwlInfo.progressStatus.setPadding(0, -5, 0, 0);
//		}else if (download.getTx_rate()>0){
//			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.ic_menu_upload);
//			dwlInfo.progressBarDownload.setVisibility(View.GONE);
//			//dwlInfo.progressStatus.setPadding(0, -5, 0, 0);
//		}else{
//			dwlInfo.imgTypeTransfert.setImageResource(R.drawable.emo_im_sad);
//			dwlInfo.progressBarDownload.setVisibility(View.GONE);
//			//dwlInfo.progressStatus.setPadding(0, -5, 0, 0);
//		}
		
		OnClickListener yourClickListener = new OnClickListener() {
            public void onClick(View v) {
            	Object tag = v.getTag();
            	if (tag instanceof ViewDwlInfo){
            		ViewDwlInfo dwlInfoClicked = (ViewDwlInfo)tag;
            		
            		
            		// Launching Download Detail Screen
    				Intent i = new Intent(follower,
    						GetDownloadActivity.class);
    				i.putExtra("Download", dwlInfoClicked.dwl);
    				follower.startActivity(i);
//    				follower.finishAffinity();
            		
            		
//					SharedPreferences settings = PreferenceManager
//							.getDefaultSharedPreferences(follower);
//					SharedPreferences.Editor editor = settings.edit();
//					editor.putInt("dwl_id", dwlInfoClicked.dwl.getId());
//					editor.apply();
//					follower.sendMessage(Params.MSG_IND, "Détail du téléchargement");
//					FbxHttpRaster fbxGetDownload = (FbxHttpRaster) FreeboxControler.createRequest(Params.FBX_REQ_DOWNLOAD, follower);
//					HttpConnect.getInstance().execute(fbxGetDownload);	
            	}
            }
        };
        
        convertView.setOnClickListener(yourClickListener);
		return convertView;
	}

	
	
	@Override
	public void sort(Comparator<? super Download> comparator) {
		super.sort(comparator);
	}

	public Filter getFilter() {
	    if (filter == null)
	        filter = new DownloadsFilter(downloads, this);
	    return filter;  
	}

}
