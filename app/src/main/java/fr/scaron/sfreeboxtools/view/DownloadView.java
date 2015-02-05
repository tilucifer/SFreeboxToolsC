package fr.scaron.sfreeboxtools.view;

import java.util.List;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.connect.HttpConnect;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.control.FreeboxControler;
import fr.scaron.sfreeboxtools.model.Download;
import fr.scaron.sfreeboxtools.model.DownloadFile;
import fr.scaron.sfreeboxtools.model.DownloadFileAdapter;
import fr.scaron.sfreeboxtools.model.DownloadTracker;
import fr.scaron.sfreeboxtools.model.DownloadTrackerAdapter;
import fr.scaron.sfreeboxtools.model.FbxHttpRaster;

public class DownloadView extends RelativeLayout {


	ImageView imgTypeTransfert;
	TextView tvDownloadName;
	ProgressBar progressBarDownload;
	TextView progressStatus;
	TextView progressUpStatus;
	TextView tvSize;
	TextView tvUrl;
	TextView tvErrmsg;
	ListView listFiles;
	Button btnShowFiles;
	Button btnShowTrackers;
	
	Download download;
//	DownloadFiles downloads;
	DownloadFileAdapter adapterFiles;
	DownloadTrackerAdapter adapterTrackers;
	

    public DownloadView(Follower follower, Download download) {
        super(follower.getContext());
        this.download = download;
        init(follower, null, 0);
    }

    private void init(final Follower follower, AttributeSet attrs, int defStyle) {
    	LayoutInflater inflater = LayoutInflater.from(follower.getContext());
    	RelativeLayout dwl_detail = (RelativeLayout)inflater.inflate(R.layout.dwl_detail, null);
    	tvDownloadName = (TextView)dwl_detail.findViewById(R.id.tvDownloadName);
		imgTypeTransfert = (ImageView)dwl_detail.findViewById(R.id.imgTypeTransfert);
		progressBarDownload = (ProgressBar)dwl_detail.findViewById(R.id.progressBarDownload);
		progressStatus = (TextView)dwl_detail.findViewById(R.id.progressStatus);
		progressUpStatus = (TextView)dwl_detail.findViewById(R.id.progressUpStatus);
		tvSize = (TextView)dwl_detail.findViewById(R.id.tvSize);
		tvErrmsg = (TextView)dwl_detail.findViewById(R.id.tvErrmsg);
		tvUrl = (TextView)dwl_detail.findViewById(R.id.tvUrl);
		listFiles = (ListView)dwl_detail.findViewById(R.id.listFiles);
		adapterFiles = new DownloadFileAdapter(follower,
				R.layout.list_item_notifstyle_small);
		adapterTrackers = new DownloadTrackerAdapter(follower,
				R.layout.list_item_notifstyle_small);
		listFiles.setAdapter(adapterFiles);
		btnShowFiles = (Button)dwl_detail.findViewById(R.id.btnShowFiles);
		btnShowTrackers  = (Button)dwl_detail.findViewById(R.id.btnShowTrackers);

		OnClickListener clickShowFiles = new OnClickListener() {
			@Override
			public void onClick(View v) {
				FbxHttpRaster fbxGetDownloadFiles = (FbxHttpRaster) FreeboxControler.createRequest(
						Params.FBX_REQ_DOWNLOAD_FILES, follower);
				HttpConnect.getInstance().execute(fbxGetDownloadFiles);
				
			}
		};
		btnShowFiles.setOnClickListener(clickShowFiles);

		OnClickListener clickShowTrackers = new OnClickListener() {
			@Override
			public void onClick(View v) {
				FbxHttpRaster fbxGetDownloadTrackers = (FbxHttpRaster) FreeboxControler.createRequest(
						Params.FBX_REQ_DOWNLOAD_TRACKERS, follower);
				HttpConnect.getInstance().execute(fbxGetDownloadTrackers);
				
			}
		};
		btnShowTrackers.setOnClickListener(clickShowTrackers);
		
		String errmsg=download.getError();
		if (errmsg!=null && !"none".equals(errmsg)){
			imgTypeTransfert.setImageResource(R.drawable.emo_im_sad);
			progressBarDownload.setVisibility(View.GONE);
//			progressStatus.setPadding(0, -5, 0, 0);
			progressStatus.setText("Statut : erreur");
			tvSize.setText("");
			tvErrmsg.setVisibility(VISIBLE);
			tvErrmsg.setText("Erreur : "+errmsg);
			addView(dwl_detail);
			return;
		}
		update(download);
		addView(dwl_detail);
		
    }

    public void setDownloadFiles(List<DownloadFile> downloadFiles){
    	if (downloadFiles!=null){
	    	adapterFiles.setDownloadFiles(downloadFiles);
			listFiles.setAdapter(adapterFiles);
			btnShowFiles.setSelected(true);
			btnShowFiles.setEnabled(false);
			btnShowFiles.setTextColor(0xffff8800);//getResources().getColor(android.R.color.holo_orange_dark)
			btnShowTrackers.setSelected(false);
			btnShowTrackers.setEnabled(true);
			btnShowTrackers.setTextColor(getResources().getColor(android.R.color.white));
			adapterFiles.setNotifyOnChange(true);
    	}
    }
    public void setDownloadTrackers(List<DownloadTracker> downloadTrackers){
    	if (downloadTrackers!=null){
	    	adapterTrackers.setDownloadTrackers(downloadTrackers);
			listFiles.setAdapter(adapterTrackers);
			btnShowTrackers.setSelected(true);
			btnShowTrackers.setEnabled(false);
			btnShowTrackers.setTextColor(0xffff8800);//getResources().getColor(android.R.color.holo_orange_dark)
			btnShowFiles.setSelected(false);
			btnShowFiles.setEnabled(true);
			btnShowFiles.setTextColor(getResources().getColor(android.R.color.white));
	    	adapterTrackers.setNotifyOnChange(true);
    	}
    }
    
    public ListView getListView(){
    	return listFiles;
    }
    public DownloadFileAdapter getListViewAdapter(){
    	return adapterFiles;
    }
    
    public void update(Download download){
    	this.download = download;    	
		tvErrmsg.setVisibility(GONE);
    	tvUrl.setText("Téléchargé sur: "+download.getDownload_dir());
		tvDownloadName.setText("("+download.getId().intValue()+")"+download.getName());
		tvSize.setText("Taille : "+download.getOctetValue(download.getSize()));
		
		Double tx_pct = download.getTx_pct();
		if (tx_pct == 0){
			
		}
		Double rx_pct = download.getRx_pct();
		if (rx_pct == 0){
			
		}
		progressStatus.setText("Téléchargement à "+download.getOctetValue(download.getRx_rate())
				+"/s, "+(download.getRx_pct()/100)+" % (" + 
				download.getOctetValue(download.getRx_bytes())+" recus)");
		progressUpStatus.setText("Partage à "+download.getOctetValue(download.getTx_rate())
				+"/s, "+(download.getTx_pct()/100)+" % (" + 
				download.getOctetValue(download.getTx_bytes())+" envoyés)");
		progressBarDownload.setMax(100);
		progressBarDownload.setSecondaryProgress((int)(download.getRx_pct()/100));///download.getSize()));
		progressBarDownload.setProgress((int)(download.getTx_pct()/100));
		
		
		if ("stopped".equals(download.getStatus())){
			
		}
		else if ("queued".equals(download.getStatus())){
			
		}
		else if ("starting".equals(download.getStatus())){
			
		}
		else if ("downloading".equals(download.getStatus())){
			imgTypeTransfert.setImageResource(R.drawable.ic_menu_download);
		} 
		else if ("stopping".equals(download.getStatus())){
			
		}
		else if ("error".equals(download.getStatus())){
			imgTypeTransfert.setImageResource(R.drawable.emo_im_sad);
		}
		else if ("done".equals(download.getStatus())){
			imgTypeTransfert.setImageResource(R.drawable.btn_check_buttonless_on);
		}
		else if ("seeding".equals(download.getStatus())){
			imgTypeTransfert.setImageResource(R.drawable.ic_menu_upload);			
		}
		else if ("retry".equals(download.getStatus())){
			
		}
    }
    
    
}
