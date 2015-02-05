package fr.scaron.sfreeboxtools.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import fr.scaron.sfreeboxtools.Follower;
import fr.scaron.sfreeboxtools.model.HttpRaster;

public abstract class AbstractTask extends AsyncTask<Object, Object, Object> implements Follower{
	Handler handle;
	Context context;
	public AbstractTask(Context context){
		this.context = context;
        handle = new Handler();
	}
	public abstract void updateView(HttpRaster httpRaster);
	@Override
	public void sendMessage(int type, String message) {
		handle.sendMessage(handle.obtainMessage(type, message));

	}
	@Override
	public Context getContext() {
		return this.context;
	}
}
