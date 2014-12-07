
<?php //database operation

	$Name=$_REQUEST['Name'];
	//$Cellphone=$_REQUEST['Cellphone'];
	$Lat_from=$_REQUEST['Lat_from'];
	$Lng_from=$_REQUEST['Lng_from'];
	$Lat_to=$_REQUEST['Lat_to'];
	$Lng_to=$_REQUEST['Lng_to'];
	$Address_from=$_REQUEST['Address_from'];
	$Address_to=$_REQUEST['Address_to'];
	$Date=$_REQUEST['Date'];
	$Start_time=$_REQUEST['Start_time'];
	$Stop_time=$_REQUEST['Stop_time'];
	$Angle=888;//$_REQUEST['Angle'];
	$Sex=$_REQUEST['Sex'];
	//$Charge=$_POST['Charge'];
	//$SpecialReq=$_POST['SpecialReq'];
	//$PassNumber=$_POST['PassNumber'];
	//$PackNumber=$_POST['PackNumber'];	
	
	$Angle=atan2(($Lng_to-$Lng_from)*cos(($Lat_from+$Lat_to)*0.5/1E6/180*3.14159),($Lat_to-$Lat_from))/3.14159*180;
	echo "name is $Name\n";
	echo "Lat from is $Lat_from\n";
	//connect the server
	//$con=mysql_connect("localhost","root","9945015");  //local database
	$con=mysql_connect("Carpoolandroid.db.12690699.hostedresource.com","Carpoolandroid","Carpool2014!"); //remote database
	if(!$con)		{		die('Could not connect: '.mysql_error());	}
	//connect database
	//mysql_select_db("CarpoolDatabase"); //local database
	mysql_select_db("Carpoolandroid");			//remote database
	
	mysql_query("SET NAMES 'utf8'");
	
	$ID=date("YmdGis");
	
	///*
	$query = "insert into carpool_driver 
	 		(ID, Name, Sex, Lat_from, Lng_from,Lat_to,Lng_to,Address_from,Address_to,Angle,Date, Start_time,Stop_time) 
	 		values ('$ID','$Name','$Sex','$Lat_from','$Lng_from','$Lat_to','$Lng_to','$Address_from','$Address_to','$Angle','$Date','$Start_time','$Stop_time')";
	//*/
	/*$query = "insert into carpool_driver 
	 		(ID, Name, Sex, Lat_from, Lng_from,Lat_to,Lng_to) 
	 		values ('$ID','$Name','$Sex','$Lat_from','$Lng_from','$Lat_to','$Lng_to')";
	//*/
	//$query = "insert into carpool_driver (ID, Name, Lat_from) values ('$ID','$Name','$Lat_from')";
	//$query = "insert into carpool_driver (ID, Name, Lat_from, Lng_from,Lat_to,Lng_to) values ('$ID','abcd',33,44,55,66)";
	 	 	
		
		$result = mysql_query($query);


	if(!$result){
    	    	echo "\nerror when uploading information";
    }
    else{
    	echo "\ngood result";
    }
 
	/*while($e=mysql_fetch_assoc($result))
	        $output[]=$e;
	 
	print(json_encode($output));
 	*/
	mysql_close();
?>

	
	