
<?php //database operation
	
	$Angle=$_REQUEST['Angle'];
	$Range_Angle=$_REQUEST['Range_Angle'];
	//$Range_Distance=$_REQUEST['Range_Distance'];
	//connect the server
	$con=mysql_connect("localhost","root","9945015");  //local database
	//$con=mysql_connect("Carpoolandroid.db.12690699.hostedresource.com","Carpoolandroid","Carpool2014!"); //remote database
	if(!$con)		{		die('Could not connect: '.mysql_error());	}
	//connect database
	//mysql_select_db("CarpoolDatabase"); //local database
	mysql_select_db("baby51ping");			//remote database
	
	mysql_query("SET NAMES 'utf8'");
	
	$query = "select * from carpool_driver where (Angle<'$Angle'+'$Range_Angle') and (Angle>'$Angle'-'$Range_Angle') order by ID";
	//$q=mysql_query("SELECT * FROM people WHERE birthyear>'".$_REQUEST['year']."'");
	$result = mysql_query($query);
	if(!$result){
    	    	echo "\nerror when loading information";
    }
 
	while($e=mysql_fetch_assoc($result))
	        $output[]=$e;
	 
	print(json_encode($output));
 
	mysql_close();
?>

	
	