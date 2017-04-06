<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id= $_POST['user_id'];

	
	if(!empty($user_id))
	{
		$sql= "SELECT * FROM tags WHERE user_id='$user_id'";
	if(mysqli_query($con,$sql))
	{
		$json = array();
		$result = mysqli_query ($con, $sql);
		while($row = mysqli_fetch_array ($result))     
		{
		    $bus = array(
		        'user_id' => $row['user_id'],
		        'tag' => $row['tag']
		    );
		    array_push($json, $bus);
		}

		$jsonstring = json_encode($json);
		if(!empty($json)){
			echo $jsonstring;
		}
		else{
			echo "Wrong user_id";
		}
		
	}
	else{
		echo "Could not create";
	}
	}
	else{
		echo "Empty params";
	}
}
else{
	echo "Error";
}
?>