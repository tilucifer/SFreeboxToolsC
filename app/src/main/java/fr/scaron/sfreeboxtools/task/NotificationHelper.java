package fr.scaron.sfreeboxtools.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import fr.scaron.sfreeboxtools.R;
import fr.scaron.sfreeboxtools.model.Download;
 
public class NotificationHelper {
	public static Logger log = LoggerFactory.getLogger(NotificationHelper.class);
    private Context mContext;
    private int NOTIFICATION_ID = 1;
    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mContentIntent;
    private CharSequence mContentTitle;
    public NotificationHelper(Context context)
    {
        mContext = context;
    }
    
    public int getNotifId(){
    	return NOTIFICATION_ID;
    }
 
    /**
     * Put the notification into the status bar
     */
    /*public void createNotification() {
        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
 
        //create the notification
        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = mContext.getString(R.string.download); //Initial text that appears in the status bar
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);
 
        //create the content which is shown in the notification pulldown
        mContentTitle = mContext.getString(R.string.menu_freebox_add_torrent); //Full title of the notification in the pull down
        CharSequence contentText = "0% complete"; //Text of the notification in the pull down
 
        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
 
        //add the additional content and intent to the notification
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
 
        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }*/
    
    
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
    public void createNotification(){
        long when = System.currentTimeMillis();
        mNotificationManager=(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent();//mContext,MainActivity.class);
        PendingIntent  pending=PendingIntent.getActivity(mContext, 0, intent, 0);

        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = mContext.getString(R.string.download);
        mContentTitle = mContext.getString(R.string.menu_freebox_add_torrent);
        CharSequence contentText = "0% complete";
        if (Build.VERSION.SDK_INT < 11) {
        	mNotification = new Notification(icon, tickerText, when);
        	mNotification.setLatestEventInfo(
        			mContext,
        			mContentTitle,
        			contentText,
                    pending);
        } else {
        	mNotification = new Notification.Builder(mContext)
                    .setContentTitle(mContentTitle)
                    .setContentText(
                    		contentText).setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentIntent(pending).setWhen(when).setAutoCancel(false)
                    .build();
        }
        mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotificationManager.notify(getNotifId(), mNotification);
    }
    

    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
    public void createNotification(Download download){
        long when = System.currentTimeMillis();
        mNotificationManager=(NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent();//mContext,MainActivity.class);
        PendingIntent  pending=PendingIntent.getActivity(mContext, 0, intent, 0);

        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = mContext.getString(R.string.download);
        mContentTitle = download.getName();
        CharSequence contentText = (int)(download.getRx_pct()/100)+"% complete";
        if (Build.VERSION.SDK_INT < 11) {
        	mNotification = new Notification(icon, tickerText, when);
        	mNotification.setLatestEventInfo(
        			mContext,
        			mContentTitle,
        			contentText,
                    pending);
        } else {
        	mNotification = new Notification.Builder(mContext)
                    .setContentTitle(mContentTitle)
                    .setContentText(
                    		contentText).setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentIntent(pending).setWhen(when).setAutoCancel(false)
                    .build();
        }
        mNotification.flags = Notification.FLAG_ONGOING_EVENT|Notification.FLAG_SHOW_LIGHTS|Notification.DEFAULT_VIBRATE;
        mNotification.defaults |= Notification.DEFAULT_SOUND; 
        NOTIFICATION_ID = download.getId();
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
    
    /**
     * Put the notification into the status bar
     */
    /*public void createNotification(Download download) {
    	
        //get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
 
        //create the notification
        int icon = android.R.drawable.stat_sys_download;
        CharSequence tickerText = mContext.getString(R.string.download); //Initial text that appears in the status bar
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);
 
        //create the content which is shown in the notification pulldown
        mContentTitle = download.getName();//mContext.getString(R.string.menu_freebox_add_torrent); //Full title of the notification in the pull down
        CharSequence contentText = (int)(download.getRx_pct()/100)+"% complete"; //Text of the notification in the pull down
 
        //you have to set a PendingIntent on a notification to tell the system what you want it to do when the notification is selected
        //I don't want to use this here so I'm just creating a blank one
        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
 
        //add the additional content and intent to the notification
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
 
        //make this notification appear in the 'Ongoing events' section
        mNotification.flags = Notification.FLAG_ONGOING_EVENT|Notification.FLAG_SHOW_LIGHTS|Notification.DEFAULT_VIBRATE;

        
        NOTIFICATION_ID = download.getId();//UUID.fromString(download.getId()+"_"+download.getName().hashCode()).hashCode();
        //show the notification
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }*/
 
    /**
     * Receives progress updates from the background task and updates the status bar notification appropriately
     * @param percentageComplete
     */
    public void progressUpdate(int percentageComplete) {
    	log.debug("Update Progression to "+percentageComplete+"%");
        //build up the new status message
        CharSequence contentText = percentageComplete + "% complete";
        //publish it to the status bar
        mNotification.setLatestEventInfo(mContext, mContentTitle, contentText, mContentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
 
    /**
     * called when the background task is complete, this removes the notification from the status bar.
     * We could also use this to add a new ‘task complete’ notification
     */
    public void completed(){
        //remove the notification from the status bar
    	mNotification.flags = Notification.FLAG_AUTO_CANCEL;

        mNotification.icon = android.R.drawable.stat_sys_download_done;
//        mNotificationManager.cancel(NOTIFICATION_ID);
        mNotification.setLatestEventInfo(mContext, mContentTitle, "terminé", mContentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }
}
