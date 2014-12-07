package com.MCarpool.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class MCarpoolServerActivity extends MapActivity {
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
    private Button button_back;
    private EditText et_startaddr,et_stopaddr,et_starttime,et_stoptime,et_sex,et_date,et_data;
    //private Spinner spinner_sex;
    //private String [] items_sex={"ÄÐ","Å®","ÎÞ"};
    int flag_record=1;
    int flag_toast=0; //on or off toast message
    private GeoPoint gp_passenger_startaddr,gp_passenger_stopaddr,gp_driver_startaddr,gp_driver_stopaddr;
    
    //for database
    InputStream is;
	String result;
	JSONArray jArray;
	Spinner spinner_name;
	String [] items_name;
    
    private int mHour1,mHour2;
	private int mMinute1,mMinute2;
	private int year,month,day;
	private int iFontSize=10;
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
        setContentView(R.layout.mainserver);
        
        
        //initialize some values
        flag_record=1;  
        
        
        ///*
        //get value from parent activity
        gp_driver_startaddr=new GeoPoint((int)Double.parseDouble(getIntent().getExtras().getString("gp_driver_startlat").toString()),
        		(int)Double.parseDouble(getIntent().getExtras().get("gp_driver_startlng").toString()));
        
        gp_driver_stopaddr=new GeoPoint((int)Double.parseDouble(getIntent().getExtras().getString("gp_driver_stoplat").toString()),
        		(int)Double.parseDouble(getIntent().getExtras().get("gp_driver_stoplng").toString()));
        
        //*/
      //EditText     
        et_startaddr=(EditText)findViewById(R.id.editText_startaddr);
    	et_stopaddr=(EditText)findViewById(R.id.editText_stopaddr);
    	et_sex=(EditText)findViewById(R.id.editText_sex);
    	et_starttime=(EditText)findViewById(R.id.editText_starttime);
    	et_stoptime=(EditText)findViewById(R.id.editText_stoptime);
    	et_date=(EditText)findViewById(R.id.editText_date);
    	et_data=(EditText)findViewById(R.id.editText_data);
        	
            
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
        
        //register the context menu
        //registerForContextMenu(findViewById(R.id.mapview));
        //
      
        //button start and stop
        button_start= (Button) findViewById(R.id.button_start);
        button_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//mapView.setSatellite(false);
				//et_startaddr.setText(mapOverlay.tapPoint.toString());
				
				if(flag_toast==1) Toast.makeText(mapView.getContext(), "sflag is "+mapOverlay.tapPoint.toString(), Toast.LENGTH_SHORT).show();
				s_startaddr=AddrConversion(mapOverlay.tapPoint);
				//s_startaddr=addr.toString();
				et_startaddr.setText(s_startaddr);
			}
		});
        
        button_stop= (Button) findViewById(R.id.button_stop);
        button_stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//mapView.setSatellite(true);
				//et_stopaddr.setText(mapOverlay.tapPoint.toString());
				if(flag_toast==1) Toast.makeText(mapView.getContext(), "sflag is "+mapOverlay.tapPoint.toString(), Toast.LENGTH_SHORT).show();
				
				s_stopaddr=AddrConversion(mapOverlay.tapPoint);
				//s_stopaddr=addr.toString();
				et_stopaddr.setText(s_stopaddr);
			}
		});
        
      //Button Back 
        button_back= (Button) findViewById(R.id.button_back);
        button_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent in=getIntent();
				in.putExtra("sflag","return");
				setResult(RESULT_OK,in);
				finish();
				
				if(flag_toast==1) Toast.makeText(MCarpoolServerActivity.this, "Came back to the previous window", Toast.LENGTH_SHORT).show();
			}
		});
      //create a map view
        //LinearLayout linearLayout=(LinearLayout) findViewById(R.layout.newmapview);;
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
     
        
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
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,	10, new GeoUpdateHandler());
		DrawPath(gp_driver_startaddr, gp_driver_stopaddr, Color.GREEN, mapView);
		
		///*
		mapOverlaysList = mapView.getOverlays();
        
        
        //mapOverlay=new MapOverlay();
        //mapOverlaysList.add(mapOverlay);
        
        //sflag=getIntent().getExtras().getString("sflag");
        //if(flag_toast==1) Toast.makeText(this, "sflag is "+sflag, Toast.LENGTH_SHORT).show();
        //*/
		
        
       //connect to database--and load all matching data------------------------------------------
        if(flag_record!=0)
		{
			
			///*
          ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
          nameValuePairs.add(new BasicNameValuePair("year","1980"));
      	
          try{
              HttpClient httpclient = new DefaultHttpClient();
              String strhttp="http://www.bbsangel.com/CarpoolMap/android_match.php?"
	            	 // + "Name=\""+et_name.getText().toString()+"\""
	            	 //+ "Name="+et_name.getText()
	            	 //+ "&Sex="+spinner_sex.getSelectedItem().toString()
	            	 //+ "&Date="+button_date.getText()
	            	 //+ "&Start_time="+button_starttime.getText()
	            	 //+ "&Stop_time="+button_stoptime.getText()
	            	 //+ "&Address_from="+et_startaddr.getText().toString().replaceAll(" ", "_").replaceAll("\n",";")
	            	 //+ "&Address_to="+et_stopaddr.getText().toString().replaceAll(" ", "_").replaceAll("\n",";")
	            	 + "&Lat_from="+gp_driver_startaddr.getLatitudeE6()
	            	 + "&Lng_from="+gp_driver_startaddr.getLongitudeE6()
	            	 + "&Lat_to="+gp_driver_stopaddr.getLatitudeE6()
	            	 + "&Lng_to="+gp_driver_stopaddr.getLongitudeE6()
	            	 + "&Range_Angle=30"
	            	 + "&Range_Distance=1"  
	            	  ; //Range_Distance = millage
	             
              HttpPost httppost = new HttpPost(strhttp);
                            
              httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
              HttpResponse response = httpclient.execute(httppost);
              HttpEntity entity = response.getEntity();
              is = entity.getContent();
	      }catch(Exception e){
	              Log.e("log_tag", "Error in http connection "+e.toString());
	      }
   
	      //convert response to string
	      try{
	              BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"),8);
	              StringBuilder sb = new StringBuilder();
	              String line = null;
	             
	              while ((line = reader.readLine()) != null) {
	              	  sb.append(line + "\n");
	              }
	              is.close();
	              Log.e("log_tag",sb.toString());
	              
	              result=sb.toString();
	      }catch(Exception e){
	              Log.e("log_tag", "Error converting result "+e.toString());
	      }
       //editText2.setText("abcde");
      //parse json data
  
	      try{
	              jArray = new JSONArray(result); //JSONArray has all the data listed
	              items_name=new String[jArray.length()];
	              for(int i=0;i<jArray.length();i++){
	                      JSONObject json_data = jArray.getJSONObject(i);
	                      Log.i("log_tag","id: "+json_data.getInt("ID")+
	                              ", name: "+json_data.getString("Name")+
	                              ", sex: "+json_data.getString("Cellphone")+
	                              ", birthyear: "+json_data.getString("Start_time")
	                      );
	                      items_name[i]=json_data.getString("Name");
	                      
	              }
	              //et_data.setText(result);
	         
	 	   	     
	    	      ArrayAdapter ad_name= new ArrayAdapter(this,android.R.layout.simple_spinner_item,items_name);
	    	      ad_name.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	      spinner_name=(Spinner)findViewById(R.id.spinner_name);
	    	      spinner_name.setAdapter(ad_name);
	    	      spinner_name.setOnItemSelectedListener(new OnItemSelectedListener(){
	    			   public void onItemSelected(AdapterView arg0, View arg1,int arg2, long arg3) {
	    				   if(flag_toast==1) Toast.makeText(arg0.getContext(), "The passenger is " +
	    						   arg0.getItemAtPosition(arg2).toString(), Toast.LENGTH_LONG).show();
	    				   updateRecord(arg2); //get new GeoPoint
	    				 //draw new route
	    				   updateDrawPath(gp_driver_startaddr,gp_driver_stopaddr,gp_passenger_startaddr,gp_passenger_stopaddr,mapView);
	    			   
	    				    }
	    	
	    			   public void onNothingSelected(AdapterView arg0) {
	    			    // TODO Auto-generated method stub
	    			   }
	    	
	    	      });
	    	      
	    	     
	      }
	      catch(JSONException e){
	              Log.e("log_tag", "Error parsing data "+e.toString());
	              Toast.makeText(this, "No matching data was found",
	            		  Toast.LENGTH_LONG).show();
	
	              
	      }
	          
	      //*/
  
	      
	      
	      
		}//end of flag_record
        
        //String sflag=getIntent().getExtras().getString("gp_driver_startlat");
        //if(flag_toast==1) Toast.makeText(this, "sflag is "+sflag, Toast.LENGTH_SHORT).show();
    }//end of create function
    
    //function
    public void updateRecord(int id){
        
    	try {
			JSONObject json_data = jArray.getJSONObject(id);
			//get value from parent activity
			///*
	        gp_passenger_startaddr=new GeoPoint((int)Double.parseDouble(json_data.getString("Lat_from")),
	        		(int)Double.parseDouble(json_data.getString("Lng_from")));
	        
	        gp_passenger_stopaddr=new GeoPoint((int)Double.parseDouble(json_data.getString("Lat_to")),
	        		(int)Double.parseDouble(json_data.getString("Lng_to")));
	        //*/
	        //if(flag_toast==1) Toast.makeText(mapView.getContext(), "The lat from is " +
			//		json_data.getString("Lat_from"), Toast.LENGTH_LONG).show();
			
			et_startaddr.setText(json_data.getString("Address_from"));
			et_stopaddr.setText(json_data.getString("Address_to"));
			et_sex.setText(json_data.getString("Sex"));
			et_starttime.setText(json_data.getString("Start_time"));
			et_stoptime.setText(json_data.getString("Stop_time"));
			et_date.setText(json_data.getString("Date"));
			/*
			gp_passenger_startaddr=new GeoPoint((gp_driver_startaddr.getLatitudeE6()+gp_driver_stopaddr.getLatitudeE6())/2,
					(gp_driver_startaddr.getLongitudeE6()+gp_driver_stopaddr.getLongitudeE6())/2);
			//gp_passenger_stopaddr=new GeoPoint(gp_driver_stopaddr.getLatitudeE6()/2,gp_driver_stopaddr.getLongitudeE6()/2);
			gp_passenger_stopaddr=gp_passenger_startaddr;
			*/
			/* if(flag_toast==1) Toast.makeText(mapView.getContext(), "The passenger is " +
					json_data.getString("Name"), Toast.LENGTH_LONG).show(); */
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    void updateDrawPath(GeoPoint gp_driver_startaddr,GeoPoint gp_driver_stopaddr,GeoPoint gp_passenger_startaddr,GeoPoint gp_passenger_stopaddr,MapView mapView)
    {

 
       mapView.getOverlays().clear();
 	   DrawPath_simple(gp_driver_startaddr, gp_passenger_startaddr, Color.GREEN, mapView); 
	   DrawPath_simple(gp_passenger_startaddr, gp_passenger_stopaddr, Color.BLUE, mapView); 
	   DrawPath_simple(gp_passenger_stopaddr, gp_driver_stopaddr, Color.GREEN, mapView); 
	   mapView.getOverlays().add(new MyOverLay(gp_driver_startaddr,gp_driver_startaddr, 1)); // use the default color 
	   mapView.getOverlays().add(new MyOverLay(gp_driver_stopaddr,gp_driver_stopaddr, 3)); // use the default color  
	   //if(flag_toast==1) Toast.makeText(mapView.getContext(), "The lat from is " +
		//		gp_passenger_startaddr.getLatitudeE6(), Toast.LENGTH_LONG).show();  
	   
	   int minLatitude = Integer.MAX_VALUE;
	   int maxLatitude = Integer.MIN_VALUE;
	   int minLongitude = Integer.MAX_VALUE;
	   int maxLongitude = Integer.MIN_VALUE;

	   List<GeoPoint> mGeoPoints = new ArrayList<GeoPoint>();
	   mGeoPoints.add(gp_driver_startaddr);
	   mGeoPoints.add(gp_driver_stopaddr);
	   mGeoPoints.add(gp_passenger_startaddr);
	   mGeoPoints.add(gp_passenger_stopaddr);
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
	    et_starttime.setText(
	        new StringBuilder()
	                .append(pad(mHour1)).append(":")
	                .append(pad(mMinute1)));
	    et_stoptime.setText(
		        new StringBuilder()
		                .append(pad(mHour2)).append(":")
		                .append(pad(mMinute2)));
	    et_starttime.setTextSize(iFontSize);
	    et_stoptime.setTextSize(iFontSize);
	}

	
	private void updateDate() {
	  
	    et_date.setText(
		        new StringBuilder()
		        		.append(year).append("/")
		                .append(month).append("/")
		                .append(day));
	    et_date.setTextSize(iFontSize);
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
		}
    }
 
    private void createMarker() {
		GeoPoint p = mapView.getMapCenter();
		OverlayItem overlayitem = new OverlayItem(p, "", "");
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
			return true;
			
		case R.id.item_map:
			mapView.setSatellite(false);
			if(flag_toast==1) Toast.makeText(this, "item_map", Toast.LENGTH_SHORT).show();
			return true;
			
		case R.id.item_back:
			if(flag_toast==1) Toast.makeText(this, "item_back", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onContextItemSelected(item);

		}
		
		
	}
	
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
	    Document doc = null; 
	    HttpURLConnection urlConnection= null; 
	    URL url = null; 
	    try 
	    { 
		    url = new URL(urlString.toString()); 
		    urlConnection=(HttpURLConnection)url.openConnection(); 
		    urlConnection.setRequestMethod("GET"); 
		    urlConnection.setDoOutput(true); 
		    urlConnection.setDoInput(true); 
		    urlConnection.connect(); 
		
		    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		    DocumentBuilder db = dbf.newDocumentBuilder(); 
		    doc = db.parse(urlConnection.getInputStream()); 
		
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
    }



private void DrawPath_simple(GeoPoint src,GeoPoint dest, int color, MapView mMapView01) 
{ 
	mMapView01.getOverlays().add(new MyOverLay(src,dest, 2)); // use the default color 

	mMapView01.getOverlays().add(new MyOverLay(src,src, 4)); // use the default color 
	mMapView01.getOverlays().add(new MyOverLay(dest,dest, 4)); // use the default color  
    
  if(flag_toast==1) Toast.makeText(mapView.getContext(), "sflag is drawpath", Toast.LENGTH_SHORT).show();
	
}

}
