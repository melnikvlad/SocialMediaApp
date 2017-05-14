<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$name = $_POST['name'];


	$sql= "SELECT * FROM users  WHERE name='$name'";
	if(mysqli_query($con,$sql))
	{
		$json = array();
		$result = mysqli_query ($con, $sql);
		while($row = mysqli_fetch_array ($result))     
		{
		    $bus = array(
		        'user_id' => $row['unique_id'],
		        'name' => $row['name']
		    );
		    array_push($json, $bus);
		}

		$jsonstring = json_encode($json);
		if(!empty($json)){
			echo $jsonstring;
		}
		
	}
	else{
		echo "Could not select";
	}
}
else{
	echo "Error";
}
?>