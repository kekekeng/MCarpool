package com.MCarpool.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;


public class HelloItemizedOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;

	public HelloItemizedOverlay(Drawable defaultMarker) {
		//super(defaultMarker);
		// TODO Auto-generated constructor stub
		super(boundCenterBottom(defaultMarker));
	}

	public HelloItemizedOverlay(Drawable defaultMarker, Context context) {
		//super(defaultMarker);
		// TODO Auto-generated constructor stub
		this(defaultMarker);
		mContext=context;
	}
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}
	@Override
	public int size() {
		// TODO Auto-generated method stub
		//return 0;
		return mOverlays.size();
	}
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	///*
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		/*
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.setPositiveButton("Yes", new OnClickListener() {    
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
		dialog.show();
		*/
		Toast.makeText(this.mContext, "item_back", Toast.LENGTH_SHORT).show();
		return true;
	}
	//*/
}
