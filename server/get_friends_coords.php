<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id = $_POST['user_id'];

	
	if(!empty($user_id))
	{
		$sql= "SELECT * FROM friendships WHERE user_id='$user_id'";
	if(mysqli_query($con,$sql))
	{
		$json= array();
		$result = mysqli_query ($con, $sql);
		while($row = mysqli_fetch_array ($result))     
		{
			 $ids = array(
		        'other_user_id' => $row['other_user_id']
		    );
			 array_push($json, $ids);
		}
		$getIds = json_encode($json);
	}
	
	}
	else{
		echo "Empty params";
	}

	$item = json_decode($getIds);
	$json2= array();

	foreach($item as $v){
	$sql= "SELECT * FROM locations WHERE user_id='$v->other_user_id'";

	if(mysqli_query($con,$sql))
	{
		
		$result = mysqli_query ($con, $sql);

		while($row = mysqli_fetch_array ($result))     
		{
			 $data = array(
			 	'user_id' 		=> $row['user_id'],
		        'latitude' 		=> $row['latitude'],
		        'longtitude' 	=> $row['longtitude']
		    );
			 array_push($json2, $data);
		}	
	}
	else{
		echo "MYSQL error";
	}
}

$result = json_encode($json2);
echo $result;
}
else{
	echo "Error";
}
?>