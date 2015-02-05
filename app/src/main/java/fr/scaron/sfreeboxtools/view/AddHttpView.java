package fr.scaron.sfreeboxtools.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import fr.scaron.sfreeboxtools.R;

public class AddHttpView extends LinearLayout {

	private View addHttpView;
	
	public AddHttpView(Context context) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		addHttpView = inflater.inflate(R.layout.add_http_downloadlink, this);
	}

	public String getUrl() {
		final EditText httpAddLink = (EditText)addHttpView.findViewById(R.id.httpAddLink);
		return httpAddLink.getText().toString();
	}
}
