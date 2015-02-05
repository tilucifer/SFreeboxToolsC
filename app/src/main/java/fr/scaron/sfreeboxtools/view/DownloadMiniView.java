package fr.scaron.sfreeboxtools.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.model.Download;

public class DownloadMiniView extends RelativeLayout {

	public static Logger log = LoggerFactory.getLogger(DownloadMiniView.class);

	ImageView imgTypeTransfert;
	TextView tvDownloadName;
	ProgressBar progressBarDownload;
	TextView progressStatus;
	TextView list_info;
	Download download;	
	

    public DownloadMiniView(Context context, Download download) {
        super(context);
        this.download = download;
        init(context, null, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
    	LayoutInflater inflater = LayoutInflater.from(context);
    	FrameLayout dwl_detail = (FrameLayout)inflater.inflate(R.layout.list_item_notifstyle_small, null);
  
		tvDownloadName = (TextView)dwl_detail.findViewById(R.id.list_title);
		imgTypeTransfert = (ImageView)dwl_detail.findViewById(R.id.list_icon);
		progressBarDownload = (ProgressBar)dwl_detail.findViewById(R.id.list_progress);
		progressStatus = (TextView)dwl_detail.findViewById(R.id.list_text);
		list_info = (TextView)dwl_detail.findViewById(R.id.list_info);
    	
    	log.debug("L'objet download is null ? "+(download==null));
    	
    	if (download!=null){
			tvDownloadName.setText(download.getName());
			progressStatus.setText(download.toString());
			list_info.setText(download.getType());
			
			String errmsg=download.getError();
			if (errmsg!=null && !errmsg.isEmpty() && !"none".equals(errmsg)){
				imgTypeTransfert.setImageResource(R.drawable.emo_im_sad);
				progressBarDownload.setVisibility(View.INVISIBLE);
				progressStatus.setPadding(0, -5, 0, 0);
				progressStatus.setText("Statut : erreur "+errmsg);
				return;
			}
			update(download);
    	}else{
			tvDownloadName.setText("Erreur");
			progressStatus.setText("Impossible de trouver les informations");
			list_info.setText("err");
    	}
		addView(dwl_detail);
		
    }
    
    
    public void update(Download download){
    	this.download = download;    	

		progressBarDownload.setVisibility(View.VISIBLE);
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
