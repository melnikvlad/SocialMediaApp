<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{
	$user_id 		= $_POST['user_id'];
	$other_user_id 	= $_POST['other_user_id'];

	$sql= "SELECT * FROM friendships WHERE user_id = '$user_id' AND other_user_id = '$other_user_id'";
	if(mysqli_query($con,$sql))
	{
		$json = array();
		$result = mysqli_query ($con, $sql);

		while($row = mysqli_fetch_array ($result)){
			
			if(!is_null($row['other_user_id'])){
				$pack = array(
		        'other_user_id' => $row['other_user_id'],
		        'message'  		=> "Yes");
			}
		}
		if(is_null($pack)){
			$pack = array(
				'other_user_id' => $row['other_user_id'],
				'message' => "No"
				);
		}
		array_push($json, $pack);
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