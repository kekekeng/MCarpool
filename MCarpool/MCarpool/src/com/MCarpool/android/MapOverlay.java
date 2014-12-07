package com.MCarpool.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MapOverlay extends com.google.android.maps.Overlay
{
	public GeoPoint tapPoint;
	private boolean   isReleased,isMoved;
	private boolean		isTrig;
	private boolean 	isLongPressed;
	private int mRadius=6; 
	private long lastTouchTime = -1;
	 
	public MapOverlay(){
		isReleased=true;
		isMoved=false;
		isLongPressed=false;
		isTrig=false;
	    
	}
	
	
	///* consume too much cpu resources
	 @Override
     public boolean draw(Canvas canvas, MapView mapView, 
     boolean shadow, long when) 
     {
                           
		 Paint paint = new Paint(); 
		 paint.setAntiAlias(true); 
         ///*
         //---translate the GeoPoint to screen pixels---
         if(isTrig){
        	 Point screenPts = new Point();
             mapView.getProjection().toPixels(tapPoint, screenPts);
             //---add the marker---
             
             paint.setColor(Color.BLUE); 
			 RectF oval=new RectF(screenPts.x - mRadius, screenPts.y - mRadius, 
					 screenPts.x + mRadius, screenPts.y + mRadius); 
				// start point 
				canvas.drawOval(oval, paint); 
             /*
             Bitmap bmp = BitmapFactory.decodeResource(
                 mapView.getResources(), R.drawable.ic_launcher);            
             canvas.drawBitmap(bmp, screenPts.x, screenPts.y-50, null); */
             isTrig=false;
         }

             
         return super.draw(canvas, mapView, shadow,when); 
         //*/
     }
     //*/
	///*


///*
@Override
public boolean onTap(GeoPoint p, MapView map){

    if ( !isReleased ){
    	//isTrig=true;  
        return false;
    }else{
        //Log.i(TAG,"TAP!");
        if ( p!=null ){
        	tapPoint=p;
        	isTrig=true;
            handleGeoPoint(p,map);
            return true;            // We handled the tap
        }else{
        	//isTrig=true;  
            return false;           // Null GeoPoint
        }
    }
   //return true;
  
}

private void handleGeoPoint(GeoPoint p, MapView map) {
	// TODO Auto-generated method stub
	 Toast.makeText(map.getContext(), "Location Selected: "+
             p.getLatitudeE6() / 1E6 + "," + 
             p.getLongitudeE6() /1E6 , 
             Toast.LENGTH_SHORT).show();
}

///*get the status of touch, can be triggered multiple times with a simple touch
@Override
public boolean onTouchEvent(MotionEvent e, MapView mapView)
{
	//isTrig=true;
    int fingers = e.getPointerCount();
    if( e.getAction()==MotionEvent.ACTION_DOWN ){
        //isReleased=true;  // Touch DOWN, don't know if it's a pinch yet
        //isTrig=true;
    	
    	long thisTime = System.currentTimeMillis();
        lastTouchTime = thisTime;
       
    }
    if( e.getAction()==MotionEvent.ACTION_UP ){
        isReleased=true;  // Touch DOWN, don't know if it's a pinch yet
        long thisTime = System.currentTimeMillis();
        
        if (thisTime - lastTouchTime >500) {

            // Double tap
            isLongPressed=true;
            

          } else {

            // Too slow 
            isLongPressed=false;
          }
        
        //isTrig=true;
    }
    
    if( e.getAction()==MotionEvent.ACTION_MOVE && fingers==2){
        isMoved=true;   // Two fingers, def a pinch
        //isTrig=true;
    }
    /* Toast.makeText(mapView.getContext(), isMoved?"true":"false",
            Toast.LENGTH_SHORT).show(); */
    //return true;
    return super.onTouchEvent(e,mapView);
}
//*/


}