
<?php //database operation
//http://www.toisl.com/51ping/CarpoolMap/android_match.php?Range_Angle=100&Range_Distance=1&Lat_from=33512614&Lat_to=33528858&Lng_from=-112124190&Lng_to=111972162	

	$Lat_from=$_REQUEST['Lat_from'];
	$Lng_from=$_REQUEST['Lng_from'];
	$Lat_to=$_REQUEST['Lat_to'];
	$Lng_to=$_REQUEST['Lng_to'];
	//$Address_from=$_REQUEST['Address_from'];
	//$Address_to=$_REQUEST['Address_to'];
	//$Date=$_REQUEST['Date'];
	//$Start_time=$_REQUEST['Start_time'];
	//$Stop_time=$_REQUEST['Stop_time'];
	//$Angle=888;//$_REQUEST['Angle'];
	//$Sex=$_REQUEST['Sex'];
	$Range_Angle=$_REQUEST['Range_Angle'];
	$Range_Distance=$_REQUEST['Range_Distance'];
	$Range_Dis_Lng=$Range_Distance/(69.11*cos(($Lat_from+$Lat_to)*0.5/1E6/180*3.14159))*1E6;
	$Range_Dis_Lat=$Range_Distance/69.11*1E6;
	
	$Angle=atan2(($Lng_to-$Lng_from)*cos(($Lat_from+$Lat_to)*0.5/1E6/180*3.14159),($Lat_to-$Lat_from))/3.14159*180;
	$Angle_th_p=180-$Range_Angle; //>120, split condition
	$Angle_th_n=-180+$Range_Angle; //<-120, split condition
	$X1=$Lat_from<=$Lat_to?$Lat_from:$Lat_to;
	$X2=$Lat_from<=$Lat_to?$Lat_to:$Lat_from;
	$Y1=$Lng_from<=$Lng_to?$Lng_from:$Lng_to;
	$Y2=$Lng_from<=$Lng_to?$Lng_to:$Lng_from;
	//connect the server
	//$con=mysql_connect("localhost","root","9945015");  //local database
	$con=mysql_connect("Carpoolandroid.db.12690699.hostedresource.com","Carpoolandroid","Carpool2014!"); //remote database
	if(!$con)		{		die('Could not connect: '.mysql_error());	}
	//connect database
	//mysql_select_db("CarpoolDatabase"); //local database
	mysql_select_db("Carpoolandroid");			//remote database
	mysql_query("SET NAMES 'utf8'");
	
	if($Angle>$Angle_th_p){
		$query = "select * from carpool_driver where 
	 				(Lat_from<('$X2'+'$Range_Dis_Lat')) and (Lat_from>('$X1'-'$Range_Dis_Lat'))
				and (Lat_to<('$X2'+'$Range_Dis_Lat')) and (Lat_to>('$X1'-'$Range_Dis_Lat'))
				and (Lng_from<('$Y2'+'$Range_Dis_Lng')) and (Lng_from>('$Y1'-'$Range_Dis_Lng'))
				and (Lng_to<('$Y2'+'$Range_Dis_Lng')) and (Lng_to>('$Y1'-'$Range_Dis_Lng'))
				and	( ((Angle>('$Angle'-'$Range_Angle')) and (Angle<180)) or ( (Angle>(-180)) and (Angle<('$Angle'+'$Range_Angle'-360)) )   )
				order by ID";
	}	
	elseif($Angle<$Angle_th_n){
		$query = "select * from carpool_driver where 
	 				(Lat_from<('$X2'+'$Range_Dis_Lat')) and (Lat_from>('$X1'-'$Range_Dis_Lat'))
				and (Lat_to<('$X2'+'$Range_Dis_Lat')) and (Lat_to>('$X1'-'$Range_Dis_Lat'))
				and (Lng_from<('$Y2'+'$Range_Dis_Lng')) and (Lng_from>('$Y1'-'$Range_Dis_Lng'))
				and (Lng_to<('$Y2'+'$Range_Dis_Lng')) and (Lng_to>('$Y1'-'$Range_Dis_Lng'))
				and	(  ( (Angle>-180) and (Angle<('$Angle'+'$Range_Angle')) ) or ((Angle>('$Angle'-'$Range_Angle'+360)) and (Angle<180) )   )
				order by ID";
	}
	else{
		$query = "select * from carpool_driver where 
	 				(Lat_from<('$X2'+'$Range_Dis_Lat')) and (Lat_from>('$X1'-'$Range_Dis_Lat'))
				and (Lat_to<('$X2'+'$Range_Dis_Lat')) and (Lat_to>('$X1'-'$Range_Dis_Lat'))
				and (Lng_from<('$Y2'+'$Range_Dis_Lng')) and (Lng_from>('$Y1'-'$Range_Dis_Lng'))
				and (Lng_to<('$Y2'+'$Range_Dis_Lng')) and (Lng_to>('$Y1'-'$Range_Dis_Lng'))
				and	(Angle>('$Angle'-'$Range_Angle')) and (Angle<('$Angle'+'$Range_Angle'))
				order by ID";
	}
	//echo "X2=$X2   x1=$X1  Y2=$Y2  Y1=$Y1 range_dis_lat=$Range_Dis_Lat Range_dis_lng=$Range_Dis_Lng angle=$Angle";
				
	/*
	$query = "select * from carpool_driver where 
	(Angle<'$Angle'+'$Range_Angle') and (Angle>'$Angle'-'$Range_Angle') 	
				and (Lat_from<'$X2'+'$Range_Dis_Lat') and (Lat_from>'X1'-'$Range_Dis_Lat')
				and (Lat_to<'$X2'+'$Range_Dis_Lat') and (Lat_from>'X1'-'$Range_Dis_Lat')
				and (Lng_from<'$Y2'+'$Range_Dis_Lng') and (Lat_from>'Y1'-'$Range_Dis_Lng')
				and (Lng_to<'$Y2'+'$Range_Dis_Lng') and (Lat_from>'Y1'-'$Range_Dis_Lng')
				order by ID";
				*/
	$result = mysql_query($query);
	if(!$result){
    	    	echo "\nerror when loading information";
    }
 
	while($e=mysql_fetch_assoc($result))
	        $output[]=$e;
	 
	print(json_encode($output));
 
	mysql_close();
?>

	
	