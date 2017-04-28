<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{


	
	
		$sql= "SELECT * FROM locations";
	if(mysqli_query($con,$sql))
	{
		$json = array();
		$result = mysqli_query ($con, $sql);
		while($row = mysqli_fetch_array ($result))     
		{
		    $bus = array(
		        'user_id' => $row['user_id'],
		        'latitude' => $row['latitude'],
		        'longtitude' => $row['longtitude']
		    );
		    array_push($json, $bus);
		}

		$jsonstring = json_encode($json);
		if(!empty($json)){
			echo $jsonstring;
		}
		
	}
	else{
		echo "Could not create";
	}
}
else{
	echo "Error";
}
?>