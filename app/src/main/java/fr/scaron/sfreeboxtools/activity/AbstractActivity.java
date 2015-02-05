package fr.scaron.sfreeboxtools.activity;



import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.contexte.Params;
import fr.scaron.sfreeboxtools.model.HttpRaster;
import android.support.v7.widget.*;


public class AbstractActivity extends ActionBarActivity implements Follower{

	ProgressDialog progressDialog;
	Handler handle;
	Context context;
	MenuItem refreshItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //setContentView(getLayoutResource());
	}
	
	public void setProgressIcon(int icon){
		progressDialog.setIcon(icon);
	}
	
//	@SuppressLint("NewApi")
	public void setBarIcon(int icon){
//		if (android.os.Build.VERSION.SDK_INT >= 11){
//			ActionBar actionBar = getActionBar();
//            if (actionBar!=null) {
//                actionBar.setIcon(icon);
//            }
//		}
        ActionBar actionBar = getSupportActionBar();
            if (actionBar!=null) {
                actionBar.setIcon(icon);
            }
	}
	

//	@SuppressLint("NewApi")
	public void setBarSubtitle(String subTitle){
//		if (android.os.Build.VERSION.SDK_INT >= 11){
//			ActionBar actionBar = getActionBar();
//            if (actionBar!=null) {
//                actionBar.setSubtitle(subTitle);
//            }
//		}
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
                actionBar.setSubtitle(subTitle);
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		refreshItem = menu.findItem(R.id.action_refresh);
		return super.onCreateOptionsMenu(menu);

	}
	
	public void showDialogConfig(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setCancelable(false).setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		alertDialogBuilder
				.setMessage("Veuillez d'abord param\351trer l'application");

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	@Override
	public void updateView(HttpRaster httpRaster) {
		// TODO Auto-generated method stub
		
	}
	
	public void setRefreshVisible(boolean visible) {
		if (visible) {
			if (refreshItem != null) {
				MenuItemCompat.setActionView(refreshItem, R.layout.actionbar_indeterminate_progress);
			}
		} else {
			if (refreshItem != null) {
				MenuItemCompat.setActionView(refreshItem,null);
			}
		}
    }
	
	@Override
	public void updateTitle(String title) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(int type, String message) {
		handle.sendMessage(handle.obtainMessage(type, message));

	}
	@Override
	public Context getContext() {
		return this.context;
	}
	
	
	public void init(Context context){
		this.context = context;
		progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Initialisation...");
		progressDialog.setTitle("Chargement en cours");
		handle= new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try{
					progressDialog.show();
				}catch(Throwable throwa){}
				try{
					progressDialog.setIndeterminate(true);
				}catch(Throwable throwa){}
				switch (msg.what) {
				case Params.MSG_IND:
					try{
						if (progressDialog.isShowing()) {
							progressDialog.setMessage(((String) msg.obj));
						}
					}catch(Throwable throwa){}
					break;
				case Params.MSG_ERR:	
					
					setRefreshVisible(false);
					try{
						if (progressDialog.isShowing()) {
							progressDialog.setMessage(((String) msg.obj));
							progressDialog.setIndeterminate(false);
							progressDialog.dismiss();
						}
					}catch(Throwable throwa){}
                    if (AbstractActivity.this.isFinishing()){
                        Toast.makeText(AbstractActivity.this.context,
                                "Info: " + (String) msg.obj, Toast.LENGTH_SHORT)
                                .show();
                    }else{
//                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                                AbstractActivity.this.context, AlertDialog.THEME_HOLO_DARK);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                AbstractActivity.this.context);
//                        Builder alertDialogBuilder = new Builder(AbstractActivity.this.context);
                        alertDialogBuilder.setCancelable(false).setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialogBuilder.setTitle("Erreur");
                        alertDialogBuilder
                                .setMessage((String) msg.obj);
                        AlertDialog alertDialog = alertDialogBuilder.create();
					    alertDialog.show();
                    }
					break;
				case Params.MSG_CNF:
					setRefreshVisible(false);
					try{
						if (progressDialog.isShowing()) {
							progressDialog.setMessage(((String) msg.obj));
							progressDialog.dismiss();
						}
					}catch(Throwable throwa){}
					break;
				case Params.MSG_TOAST:
					Toast.makeText(AbstractActivity.this.context,
							"Info: " + (String) msg.obj, Toast.LENGTH_SHORT)
							.show();
					break;
				default: // should never happen
					break;
				}

				super.handleMessage(msg);
			}
		};
	}
}
