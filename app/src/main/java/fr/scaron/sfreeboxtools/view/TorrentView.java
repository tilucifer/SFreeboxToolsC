package fr.scaron.sfreeboxtools.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.activity.AbstractActivity;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.control.T411Controler;
import fr.scaron.sfreeboxtools.model.T411GetTorrentFile;
import fr.scaron.sfreeboxtools.model.T411Torrent;

/**
 * TODO: document your custom view class.
 */
public class TorrentView extends RelativeLayout {

	public static Logger log = LoggerFactory.getLogger(TorrentView.class);

	ImageView imgTypeTransfert;
	TextView tvDownloadName;
	ProgressBar progressBarDownload;
	TextView progressStatus;
	TextView tvSize;
	TextView tvUrl;
	TextView tvErrmsg;
	T411Torrent torrent;
	private ImageView btnDownload, torrentIcon;	
    TextView tdtSeeders, tdtLeechers, tdtNote, tdtVotes, tdtComplets, tdtTaille;
    ImageView star1, star2, star3, star4, star5;
	private TextView torrentName;
	private TextView tdUploader;
	private WebView details_www;
	private Button btnGetT411File;
	private Button btnCancelT411;
	
	

    public TorrentView(final AbstractActivity follower, final T411Torrent torrent) {
        super(follower.getApplicationContext());
        this.torrent = torrent;        
        init(follower, null, 0);
    }

    private void init(final AbstractActivity follower, AttributeSet attrs, int defStyle) {
    	LayoutInflater inflater = LayoutInflater.from(follower);
    	RelativeLayout t411_detail = (RelativeLayout)inflater.inflate(R.layout.t411_detail, null);
    	//btnCancelT411,btnGetT411File

    	btnCancelT411 = (Button) t411_detail.findViewById(R.id.btnCancelT411);
    	btnGetT411File = (Button) t411_detail.findViewById(R.id.btnGetT411File);
        btnCancelT411.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				follower.finish();
			}
        });
        btnGetT411File.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				log.debug("Lancement du téléchargement de ("
						+ torrent.getTorrent_ID() + ") "
						+ torrent.getTorrent_Name());

				T411GetTorrentFile t411GetTorrentFile = new T411GetTorrentFile(
						Params.T411_URL_GET_TORRENT, Params.GET,
						follower);
				t411GetTorrentFile.setId(torrent
						.getTorrent_ID());
				t411GetTorrentFile.setName(torrent
						.getTorrent_Name());
				t411GetTorrentFile.setFlagResponseFile(true);
				t411GetTorrentFile.setFlagParamsString(true);
				T411Controler.requestProcess(t411GetTorrentFile,
						follower);
				
			}
		});

    	Integer icon = torrent.getIcon();
    	if (icon==null){
    		icon = R.drawable.ic_new_t411;
    	}    	/*
    	torrentIcon = (ImageView) t411_detail.findViewById(R.id.torrent_icon);
    	torrentIcon.setImageResource(icon);
    	

    	torrentName = (TextView) t411_detail.findViewById(R.id.tdNom);
        torrentName.setText(torrent.getTorrent_Name());
        
        tdUploader = (TextView) t411_detail.findViewById(R.id.tdUploader);
        tdUploader.setText(torrent.getTduploader());*/

        tdtSeeders = (TextView) t411_detail.findViewById(R.id.tdt_seeders);
        tdtSeeders.setText(torrent.getTdt_seeders() + " Seeders");

        tdtLeechers = (TextView) t411_detail.findViewById(R.id.tdt_leechers);
        tdtLeechers.setText(torrent.getTdt_leechers() + " Leechers");

        tdtNote = (TextView) t411_detail.findViewById(R.id.tdt_note);
        tdtNote.setText(torrent.getTdt_note());

        tdtVotes = (TextView) t411_detail.findViewById(R.id.tdt_votes);
        tdtVotes.setText(torrent.getTdt_votes());

        tdtComplets = (TextView) t411_detail.findViewById(R.id.tdt_complets);
        tdtComplets.setText(torrent.getTdt_complets() + " Complets");

        tdtTaille = (TextView) t411_detail.findViewById(R.id.tdt_taille);
        tdtTaille.setText(torrent.getTdt_taille());

        star1 = (ImageView) t411_detail.findViewById(R.id.star1);
        star2 = (ImageView) t411_detail.findViewById(R.id.star2);
        star3 = (ImageView) t411_detail.findViewById(R.id.star3);
        star4 = (ImageView) t411_detail.findViewById(R.id.star4);
        star5 = (ImageView) t411_detail.findViewById(R.id.star5);

        if (torrent.getNote() > 0)
            star1.setImageResource(R.drawable.ic_star_mid);
        if (torrent.getNote() > 0.7)
            star1.setImageResource(R.drawable.ic_star_on);
        if (torrent.getNote() > 1)
            star2.setImageResource(R.drawable.ic_star_mid);
        if (torrent.getNote() > 1.7)
            star2.setImageResource(R.drawable.ic_star_on);
        if (torrent.getNote() > 2)
            star3.setImageResource(R.drawable.ic_star_mid);
        if (torrent.getNote() > 2.7)
            star3.setImageResource(R.drawable.ic_star_on);
        if (torrent.getNote() > 3)
            star4.setImageResource(R.drawable.ic_star_mid);
        if (torrent.getNote() > 3.7)
            star4.setImageResource(R.drawable.ic_star_on);
        if (torrent.getNote() > 4)
            star5.setImageResource(R.drawable.ic_star_mid);
        if (torrent.getNote() > 4.7)
            star5.setImageResource(R.drawable.ic_star_on);
        
        details_www = (WebView) t411_detail.findViewById(R.id.prez);
        details_www.getSettings().setUseWideViewPort(true);
        details_www.getSettings().setLoadWithOverviewMode(true);
        details_www.getSettings().setJavaScriptEnabled(true);

        final String mimeType = "text/html";
        final String encoding = "utf-8";
        details_www.loadDataWithBaseURL(null, torrent.getPrez(), mimeType, encoding, "");
        
        
		addView(t411_detail);
		
    }
    
    
}
