package fr.scaron.sfreeboxtools;

import android.content.Context;
import fr.scaron.sfreeboxtools.model.HttpRaster;

public interface Follower {
	public void updateView(HttpRaster httpRaster);
	public void setRefreshVisible(boolean visible);
	public void updateTitle(String title);
	public void sendMessage(int type, String message);
	public Context getContext();
}
