package com.MCarpool.android;

import java.io.InputStream;
import java.net.URL;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MCarpoolActivity extends MapActivity {
    /** Called when the activity is first created. */
	//private
    private MapController mapController;
    private MapView mapView;
    private LocationManager locationManager;
    private HelloItemizedOverlay itemizedOverlay;
    private MapOverlay mapOverlay;
    private List<Overlay> mapOverlaysList;
    private String s_flag,s_startaddr,s_stopaddr; //flag for startpoint or stoppoint ?
    private Button button_start,button_stop,button_starttime,button_stoptime,button_date;
    private Button button_submit;
    private EditText et_startaddr,et_stopaddr,et_starttime,et_stoptime,et_name;
    private Spinner spinner_sex;
    private String [] items_sex={"M","F","O"};
    private GeoPoint gp_startaddr,gp_stopaddr;
    private int iFontSize=12;
    
    //for the flash light control
    private Camera mCamera;
    private boolean flash_ok=false;
    int flag_toast=0; //on or off toast message
    //popup window
 
    
    
    //for database
    InputStream is;
	String result;
	JSONArray jArray;
	
    private int mHour1,mHour2;
	private int mMinute1,mMinute2;
	private int year,month,day;
    //Define
    static final int TIME_DIALOG_ID1 = 1;
    static final int TIME_DIALOG_ID2 = 2;
    static final int DATE_DIALOG_ID = 3;
    
 // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener1 =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour1 = hourOfDay;
                mMinute1 = minute;
                updateDisplay();
            }
        };
    private TimePickerDialog.OnTimeSetListener mTimeSetListener2 =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mHour2 = hourOfDay;
                mMinute2 = minute;
                updateDisplay();
            }
        };
    private DatePickerDialog.OnDateSetListener dateListener = 
        new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yr, int monthOfYear, int dayOfMonth) {
                year = yr;
                month = monthOfYear;
                day = dayOfMonth;
                updateDate();
         }
    };
            
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Load the initial page when start the program, ask for the member ID and password
        //1. Start a dialog page
        
        
        //2. remote access the database 
        
        //3. return the result based on the return from database. 
        //a. go into the next step
        //b. go to new member registration page, then goto next step.
        
        //Question: how to ensure only loading next step after login page, please check the internet for more information.
        
        
        
        //init for flashlight control
        flash_ok=getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        
        //Init gp_startaddr and gp_stopaddr
        gp_startaddr=new GeoPoint(0,0);
        gp_stopaddr=new GeoPoint(0,0);
      //EditText     
        et_startaddr=(EditText)findViewById(R.id.editText_startaddr);
    	et_stopaddr=(EditText)findViewById(R.id.editText_stopaddr);
    	et_name=(EditText)findViewById(R.id.editText_name);
        //spinner
        ArrayAdapter ad_name= new ArrayAdapter(this,android.R.layout.simple_spinner_item,items_sex);
	      ad_name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	      spinner_sex=(Spinner)findViewById(R.id.spinner_sex);
	      spinner_sex.setAdapter(ad_name);
	      spinner_sex.setOnItemSelectedListener(new OnItemSelectedListener(){
			   public void onItemSelected(AdapterView arg0, View arg1,int arg2, long arg3) {
				  				   
				    }
	
			   public void onNothingSelected(AdapterView arg0) {
			    // TODO Auto-generated method stub
			   }
	
	      });
	      
     // capture our View elements
        button_starttime = (Button) findViewById(R.id.button_starttime);
        button_stoptime = (Button) findViewById(R.id.button_stoptime);
        button_date = (Button) findViewById(R.id.button_date);
        
     // start time pick, listener
        button_starttime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID1);
            }
        });

        //stop time pick, listener
        button_stoptime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID2);
            }
        });
        //date pick, listener
        button_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        
     // get the current time
        final Calendar c = Calendar.getInstance();
        mHour1 = c.get(Calendar.HOUR_OF_DAY);
        mMinute1 = c.get(Calendar.MINUTE);
        mHour2 = c.get(Calendar.HOUR_OF_DAY)+1;
        mMinute2 = c.get(Calendar.MINUTE);
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        
        // display the current date and time
        updateDisplay();
        updateDate();
        
        //button start and stop
        button_start= (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//mapView.setSatellite(false);
				//et_startaddr.setText(mapOverlay.tapPoint.toString());
				
				//if(flag_toast==1) Toast.makeText(mapView.getContext(), "sflag is "+mapOverlay.tapPoint.toString(), Toast.LENGTH_SHORT).show();
				gp_startaddr=mapOverlay.tapPoint;
				s_startaddr=AddrConversion(mapOverlay.tapPoint);
				//s_startaddr=addr.toString();
				et_startaddr.setText(s_startaddr);
				//temporary test the drawfunction here, should move to a speciall function later
				if(gp_stopaddr.getLatitudeE6()!=0) updateDrawPath(gp_startaddr, gp_stopaddr, mapView);
			}
		});
        
        button_stop= (Button) findViewById(R.id.button_stop);
        button_stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//mapView.setSatellite(true);
				//et_stopaddr.setText(mapOverlay.tapPoint.toString());
				//if(flag_toast==1) Toast.makeText(mapView.getContext(), "sflag is "+mapOverlay.tapPoint.toString(), Toast.LENGTH_SHORT).show();
				gp_stopaddr=mapOverlay.tapPoint;
				s_stopaddr=AddrConversion(mapOverlay.tapPoint);
				//s_stopaddr=addr.toString();
				et_stopaddr.setText(s_stopaddr);
				
				
				//temporary test the drawfunction here, should move to a speciall function later
				if(gp_startaddr.getLatitudeE6()!=0) updateDrawPath(gp_startaddr, gp_stopaddr, mapView);
				//DrawPath(gp_startaddr, gp_stopaddr, Color.GREEN, mapView); 
				
				//mapView.getController().animateTo((gp_startaddr); 
				//mapView.getController().setZoom(15); 
			}
		});
        
        //Button Submit 
        button_submit= (Button) findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(gp_startaddr.getLatitudeE6()==0 || gp_stopaddr.getLatitudeE6()==0 || et_name.getText().length()==0)
				{
					Toast.makeText(MCarpoolActivity.this, "Please input name, start and stop addr first", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Intent newI=new Intent(MCarpoolActivity.this,MCarpoolServerActivity.class);
					newI.putExtra("sflag","submit");
					newI.putExtra("gp_driver_startlat",Integer.toString(gp_startaddr.getLatitudeE6()));
					newI.putExtra("gp_driver_startlng",Integer.toString(gp_startaddr.getLongitudeE6()));
					newI.putExtra("gp_driver_stoplat",Integer.toString(gp_stopaddr.getLatitudeE6()));
					newI.putExtra("gp_driver_stoplng",Integer.toString(gp_stopaddr.getLongitudeE6()));
					
					startActivityForResult(newI,0);
				}
				
			}
		});
        
        
      //create a map view
        //LinearLayout linearLayout=(LinearLayout) findViewById(R.layout.newmapview);;
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setLongClickable(true);
      //register the context menu
        registerForContextMenu(button_submit);
     
        
     // Either satellite or 2d 
		mapView.setSatellite(false);
		mapController = mapView.getController();
		mapController.setZoom(14); // Zoon 1 is world view
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// Check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to 
		// go to the settings
		
		if (!enabled) {
		  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}
		LocationListener locationListener=new GeoUpdateHandler();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,	0, locationListener);
		
		mapOverlaysList = mapView.getOverlays();
		///*
		//Drawable drawable=this.getResources().getDrawable(R.drawable.ic_launcher);
		//Drawable pin=this.getResources().getDrawable(R.drawable.ic_launcher);
		//createMarker();
        /*
        itemizedOverlay = new HelloItemizedOverlay(drawable,this);
       // createMarker();
        GeoPoint p = mapView.getMapCenter();
		OverlayItem overlayitem = new OverlayItem(p, "info", "selected");
		itemizedOverlay.addOverlay(overlayitem);
		
		//mapView.getOverlays().add(itemizedOverlay);
		//mapOverlaysList.add(itemizedOverlay);
		//*/
        //occupy most of CPU resources, check for the reason
        mapOverlay=new MapOverlay();
        mapOverlaysList.add(mapOverlay);
        

        //popup window


        mapView.invalidate();
    }//end of oncreate function
    
    //function to draw
    private void DrawPath(GeoPoint src,GeoPoint dest, int color, MapView mMapView01) 
    { 
	    // connect to map web service 
	    StringBuilder urlString = new StringBuilder(); 
	    urlString.append("http://maps.google.com/maps?f=d&hl=en"); 
	    urlString.append("&saddr=");//from 
	    urlString.append( Double.toString((double)src.getLatitudeE6()/1.0E6 )); 
	    urlString.append(","); 
	    urlString.append( Double.toString((double)src.getLongitudeE6()/1.0E6 )); 
	    urlString.append("&daddr=");//to 
	    urlString.append( Double.toString((double)dest.getLatitudeE6()/1.0E6 )); 
	    urlString.append(","); 
	    urlString.append( Double.toString((double)dest.getLongitudeE6()/1.0E6 )); 
	    urlString.append("&ie=UTF8&0&om=0&output=kml"); 
	    Log.d("xxx","URL="+urlString.toString()); 
	    
	    // get the kml (XML) doc. And parse it to get the coordinates(direction route). 
	    //Document doc = null; 
	    HttpURLConnection urlConnection= null; 
	    URL url = null; 
	    try 
	    { 
	    	Log.d("stt",urlString.toString());
		    url = new URL(urlString.toString()); 
		    //URL url=new URL("http://maps.google.com");//maps?f=d&hl=en&saddr=33.252287,-111.875763&daddr=33.233049,-111.828557&ie=UTF8&0&om=0&output=kml");
		    urlConnection=(HttpURLConnection)url.openConnection(); 
		    urlConnection.setRequestMethod("GET"); 
		    urlConnection.setDoOutput(true); 
		    urlConnection.setDoInput(true); 
		    urlConnection.connect(); 
		    
		  if( urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK ) {
		    	
		    
		    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		    DocumentBuilder db = dbf.newDocumentBuilder(); 
		    InputStream inputss = urlConnection.getInputStream();
		    if(inputss.available() > 0)
		    {
		         // parse 
		    	Log.d("good","good");
		    
		    }
		    Log.d("xxx","log 5 abc");
		    //Document doc = db.parse(urlString.toString()); 
		    Document doc = db.parse(inputss); 
		    if(doc.getElementsByTagName("Point").getLength()>0)
            {
                Log.d("point","more than 1");
            }
		    
		    Log.d("log4","log4"+doc.getDocumentURI());
		
		    if(doc.getElementsByTagName("GeometryCollection").getLength()>0) 
		    { 
			    //String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getNodeName(); 
			    String path = doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getFirstChild().getNodeValue() ; 
			    Log.d("xxx","path="+ path); 
			    String [] pairs = path.split(" "); 
			    String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude lngLat[1]=latitude lngLat[2]=height 
			    // src 
			    GeoPoint startGP = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6)); 
			    mMapView01.getOverlays().add(new MyOverLay(startGP,startGP,1)); 
			    GeoPoint gp1; 
			    GeoPoint gp2 = startGP; 
			    for(int i=1;i<pairs.length;i++) // the last one would be crash 
			    { 
				    lngLat = pairs[i].split(","); 
				    gp1 = gp2; 
				    // watch out! For GeoPoint, first:latitude, second:longitude 
				    gp2 = new GeoPoint((int)(Double.parseDouble(lngLat[1])*1E6),(int)(Double.parseDouble(lngLat[0])*1E6)); 
				    mMapView01.getOverlays().add(new MyOverLay(gp1,gp2,2,color)); 
				    Log.d("xxx","pair:" + pairs[i]); 
			    } 
			    mMapView01.getOverlays().add(new MyOverLay(dest,dest, 3)); // use the default color 
		    } 
		  }
		    else{
		    	Log.d("error","error");
		    }
	    } 
	    catch (MalformedURLException e) 
	    { 
	    	e.printStackTrace(); 
	    } 
	    catch (IOException e) 
	    { 
	    	e.printStackTrace(); 
	    } 
	    catch (ParserConfigurationException e) 
	    { 
	    	e.printStackTrace(); 
	    } 
	    catch (SAXException e) 
	    { 
	    	e.printStackTrace(); 
	    } 
	    
	  if(flag_toast==1) Toast.makeText(mapView.getContext(), "sflag is drawpath", Toast.LENGTH_SHORT).show();
		
    }
    
    //function to draw
    private void DrawPath_simple(GeoPoint src,GeoPoint dest, int color, MapView mMapView01) 
    { 
	  
		
    	mMapView01.getOverlays().add(new MyOverLay(src,dest, 2)); // use the default color 

    	mMapView01.getOverlays().add(new MyOverLay(src,src, 1)); // use the default color 
    	mMapView01.getOverlays().add(new MyOverLay(dest,dest, 3)); // use the default color  
        
		 
	    
	  if(flag_toast==1) Toast.makeText(mapView.getContext(), "sflag is drawpath", Toast.LENGTH_SHORT).show();
		
    }

    //Update DrawPath
    void updateDrawPath(GeoPoint gp_driver_startaddr,GeoPoint gp_driver_stopaddr,MapView mapView)
    {
       mapView.getOverlays().clear();
       mapOverlaysList.add(mapOverlay);
 	   DrawPath_simple(gp_driver_startaddr, gp_driver_stopaddr, Color.GREEN, mapView); 
	   
	   //if(flag_toast==1) Toast.makeText(mapView.getContext(), "The lat from is " +
		//		gp_passenger_startaddr.getLatitudeE6(), Toast.LENGTH_LONG).show();  
	   
	   int minLatitude = Integer.MAX_VALUE;
	   int maxLatitude = Integer.MIN_VALUE;
	   int minLongitude = Integer.MAX_VALUE;
	   int maxLongitude = Integer.MIN_VALUE;

	   List<GeoPoint> mGeoPoints = new ArrayList<GeoPoint>();
	   mGeoPoints.add(gp_driver_startaddr);
	   mGeoPoints.add(gp_driver_stopaddr);

	   // Find the boundaries of the item set
	   for (GeoPoint item : mGeoPoints) { //item Contain list of Geopints
	   int lat = item.getLatitudeE6();
	   int lon = item.getLongitudeE6();

	   maxLatitude = Math.max(lat, maxLatitude);
	   minLatitude = Math.min(lat, minLatitude);
	   maxLongitude = Math.max(lon, maxLongitude);
	   minLongitude = Math.min(lon, minLongitude);
	    }
	   
	   mapView.getController().zoomToSpan(Math.abs(maxLatitude - minLatitude), Math.abs(maxLongitude - minLongitude));
	    
	   mapView.getController().animateTo(new GeoPoint( 
			   (maxLatitude + minLatitude)/2, 
			   (maxLongitude + minLongitude)/2 )) ;
    }
 // updates the time we display in the TextView
	private void updateDisplay() {
	    button_starttime.setText(
	        new StringBuilder()
	                .append(pad(mHour1)).append(":")
	                .append(pad(mMinute1)));
	    button_stoptime.setText(
		        new StringBuilder()
		                .append(pad(mHour2)).append(":")
		                .append(pad(mMinute2)));
	    
	    button_starttime.setTextSize(iFontSize);
	    button_stoptime.setTextSize(iFontSize);
	}

	
	private void updateDate() {
	  
	    button_date.setText(
		        new StringBuilder()
		                .append(year).append("-")
		                .append(month).append("-")
		                .append(day));
	    button_date.setTextSize(iFontSize);
	}
	
	private static String pad(int c) {
	    if (c >= 10)
	        return String.valueOf(c);
	    else
	        return "0" + String.valueOf(c);
	}
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case TIME_DIALOG_ID1:
	        return new TimePickerDialog(this,
	                mTimeSetListener1, mHour1, mMinute1, false);
	    case TIME_DIALOG_ID2:
	        return new TimePickerDialog(this,
	                mTimeSetListener2, mHour2, mMinute2, false);
	    case DATE_DIALOG_ID:
	    	return new DatePickerDialog(this, dateListener, year, month, day);
	    	
	    }
	    return null;
	}
        
    protected String AddrConversion(GeoPoint tapPoint) {
		// TODO Auto-generated method stub
    		int ilat,ilng;
    		String addr="";
    		ilat=tapPoint.getLatitudeE6();
    		ilng=tapPoint.getLongitudeE6();
    		
    		Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
            ///*
            try {
                List<Address> addresses = geoCoder.getFromLocation(
                    ilat / 1E6, 
                    ilng / 1E6, 1);

                
                if (addresses.size() > 0) 
                {
                    for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); 
                         i++)
                       addr += addresses.get(0).getAddressLine(i) + "\n";
                }

                //if(flag_toast==1) Toast.makeText(this, "pressed start button and tried translation", Toast.LENGTH_SHORT).show();
            
            }
            catch (IOException e) {                
                e.printStackTrace();
            }   
            //*/
            if(flag_toast==1) Toast.makeText(this, addr.toString(), Toast.LENGTH_SHORT).show();
            return addr;
	}

	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mapController.animateTo(point); // mapController.setCenter(point);
			//createMarker(); //draw the icon in the center of the new map
			

		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch(status){
			case 2: //AVAILABLE:
				break;
			case 1: //TEMPORARILY_UNAVAILABLE:
				break;
			case 0: //OUT_OF_SERVICE:
				break;
			}
		}
    }
 
    private void createMarker() {
		GeoPoint p = mapView.getMapCenter();
		OverlayItem overlayitem = new OverlayItem(p, "info", "selected");
		itemizedOverlay.addOverlay(overlayitem);
		
		//mapView.getOverlays().add(itemizedOverlay);
		mapOverlaysList.add(itemizedOverlay);
	}
    

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
  //create context menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		//AdapterContextMenuInfo info=(AdapterContextMenuInfo)item.getMenuInfo();
		
		switch(item.getItemId()){
		
		case R.id.item_sat:
			
			mapView.setSatellite(true);
			if(flag_toast==1) Toast.makeText(this, "item_sat", Toast.LENGTH_SHORT).show();
			mapView.invalidate();
			return true;
			
		case R.id.item_map:
			mapView.setSatellite(false);
			if(flag_toast==1) Toast.makeText(this, "item_map", Toast.LENGTH_SHORT).show();
			mapView.invalidate();
			return true;
			
		case R.id.item_back:
			if(flag_toast==1) Toast.makeText(this, "item_back", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onContextItemSelected(item);

		}
		
		
	}
	
	//create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		
		case R.id.preferences:
			
			Intent newI=new Intent(MCarpoolActivity.this,MCarpoolLoginActivity.class);
			newI.putExtra("sflag","login");
			startActivityForResult(newI,0);
			
			//Intent newI1=new Intent(TemperatureActivity.this,TestActivityMenu1.class);
			//newI1.putExtra("tp",123);
			//startActivityForResult(newI1,0);
			if(flag_toast==1) Toast.makeText(this, "preferences", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.map_sat:
			mapView.setSatellite(true);
			if(flag_toast==1) Toast.makeText(this, "map_sat", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.map_normal:
			mapView.setSatellite(false);
			if(flag_toast==1) Toast.makeText(this, "map_normal", Toast.LENGTH_SHORT).show();
			return true;
		//case R.id.item2://input information into database
		case R.id.driver_info:
			if(et_name.getText().toString()!="")
			{
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("year","1980"));
		          
				try{
		              HttpClient httpclient = new DefaultHttpClient();
		              String strhttp="http://www.bbsangel.com/CarpoolMap/androidinput_driver.php?"
		            	 // + "Name=\""+et_name.getText().toString()+"\""
		            	 + "Name="+et_name.getText()
		            	 + "&Sex="+spinner_sex.getSelectedItem().toString()
		            	 + "&Date="+button_date.getText()
		            	 + "&Start_time="+button_starttime.getText()
		            	 + "&Stop_time="+button_stoptime.getText()
		            	 + "&Address_from="+et_startaddr.getText().toString().replaceAll(" ", "_").replaceAll("\n",";")
		            	 + "&Address_to="+et_stopaddr.getText().toString().replaceAll(" ", "_").replaceAll("\n",";")
		            	 + "&Lat_from="+gp_startaddr.getLatitudeE6()
		            	 + "&Lng_from="+gp_startaddr.getLongitudeE6()
		            	 + "&Lat_to="+gp_stopaddr.getLatitudeE6()
		            	 + "&Lng_to="+gp_stopaddr.getLongitudeE6()
		            	  ;
		             
		              if(flag_toast==1) Toast.makeText(this, strhttp, Toast.LENGTH_LONG).show();
		              HttpPost httppost = new HttpPost(strhttp);
		              //HttpPost httppost = new HttpPost("http://localhost/CarpoolMap/android.php");
		              
		              httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		              HttpResponse response = httpclient.execute(httppost);
		              HttpEntity entity = response.getEntity();
		              is = entity.getContent();
			      }catch(Exception e){
			              Log.e("log_tag", "Error in http connection "+e.toString());
			      }
				
				//clear everything after the info has been input into database
			    /*
			    et_startaddr.setText("");
				et_stopaddr.setText("");
				et_name.setText("");
				*/
				if(flag_toast==1) Toast.makeText(this, "driver information into database", Toast.LENGTH_LONG).show();
				return true;
			}
			else{
				if(flag_toast==1) Toast.makeText(this, "Please input all the information", Toast.LENGTH_SHORT).show();
				return true;
			}
		case R.id.passenger_info:
			if(et_name.getText().toString()!="")
			{
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("year","1980"));
		          
				try{
		              HttpClient httpclient = new DefaultHttpClient();
		              String strhttp="http://www.bbsangel.com/CarpoolMap/androidinput_passenger.php?"
		            	 // + "Name=\""+et_name.getText().toString()+"\""
		            	 + "Name="+et_name.getText()
		            	 + "&Sex="+spinner_sex.getSelectedItem().toString()
		            	 + "&Date="+button_date.getText()
		            	 + "&Start_time="+button_starttime.getText()
		            	 + "&Stop_time="+button_stoptime.getText()
		            	 + "&Address_from="+et_startaddr.getText().toString().replaceAll(" ", "_").replaceAll("\n",";")
		            	 + "&Address_to="+et_stopaddr.getText().toString().replaceAll(" ", "_").replaceAll("\n",";")
		            	 + "&Lat_from="+gp_startaddr.getLatitudeE6()
		            	 + "&Lng_from="+gp_startaddr.getLongitudeE6()
		            	 + "&Lat_to="+gp_stopaddr.getLatitudeE6()
		            	 + "&Lng_to="+gp_stopaddr.getLongitudeE6()
		            	  ;
		             
		              if(flag_toast==1) Toast.makeText(this, strhttp, Toast.LENGTH_LONG).show();
		              HttpPost httppost = new HttpPost(strhttp);
		              //HttpPost httppost = new HttpPost("http://localhost/CarpoolMap/android.php");
		              
		              httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		              HttpResponse response = httpclient.execute(httppost);
		              HttpEntity entity = response.getEntity();
		              is = entity.getContent();
			      }catch(Exception e){
			              Log.e("log_tag", "Error in http connection "+e.toString());
			      }
				
				//clear everything after the info has been input into database
			    /*
			    et_startaddr.setText("");
				et_stopaddr.setText("");
				et_name.setText("");
				*/
				if(flag_toast==1) Toast.makeText(this, "passenger information into database", Toast.LENGTH_LONG).show();
				return true;
			}
			else{
				if(flag_toast==1) Toast.makeText(this, "Please input all the information", Toast.LENGTH_SHORT).show();
				return true;
			}
		case R.id.torch:
			//Intent newI=new Intent(TemperatureActivity.this,TestActivity.class);
			//newI.putExtra("tp",123);
			//startActivity(newI);
			if(flag_toast==1) Toast.makeText(this, "Item4", Toast.LENGTH_SHORT).show();
			return true;
			
		case R.id.TurnOnLight:
			if(mCamera==null){
					mCamera=Camera.open();
			}
			if(flash_ok){	
				processOnClick();
			}else{
				if(flag_toast==1) Toast.makeText(this, "No Flash Light", Toast.LENGTH_SHORT).show();
			}
			if(flag_toast==1) Toast.makeText(this, "Flash On", Toast.LENGTH_SHORT).show();
			return true;
			
			
		case R.id.TurnOffLight:
			processOffClick();
			if(flag_toast==1) Toast.makeText(this, "Flash Off", Toast.LENGTH_SHORT).show();
			return true;
			
			
		case R.id.item5:
			if(flag_toast==1) Toast.makeText(this, "item5", Toast.LENGTH_SHORT).show();
			finish();
			return true;
			//break;
		

		}
		return true;
		
	}

	private void processOffClick(){
	    if( mCamera != null ){
	        //Parameters params = mCamera.getParameters();
	        //params.setFlashMode( Parameters.FLASH_MODE_OFF );
	        //mCamera.setParameters( params );
	        mCamera.stopPreview();
	        mCamera.release();
	        mCamera = null;
	
	    }
	}
	
	private void processOnClick(){
		 //mCamera.startPreview();
	    if( mCamera != null ){
	        Parameters params = mCamera.getParameters();
	        params.setFlashMode( Parameters.FLASH_MODE_TORCH);
	        params.setFlashMode( Parameters.FLASH_MODE_AUTO);
	        mCamera.setParameters( params );
	        mCamera.startPreview();
	    }
	}
	
	@Override
	protected void onResume() {
	if(mCamera==null)
		mCamera=Camera.open();
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		if(mCamera!=null){
			mCamera.release();
		}
		
		super.onDestroy();
	}
	@Override
	public void onBackPressed() {
		this.finish();
		super.onBackPressed();
	}
	@Override
	protected void onPause() {
		if(mCamera!=null){
			mCamera.release();
		}
		super.onPause();
	}

}