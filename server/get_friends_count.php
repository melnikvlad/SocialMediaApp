<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id= $_POST['user_id'];

	
	if(!empty($user_id))
	{
		$sql= "SELECT * FROM friendships WHERE user_id='$user_id'";
		if(mysqli_query($con,$sql))
		{
			$json = array();
			$result = mysqli_query ($con, $sql);
			while($row = mysqli_fetch_array ($result))     
			{
			    $post = array(
			        'user_id' => $row['user_id']
			    );
			    array_push($json, $post);
			}

			$jsonstring = json_encode($json);
			if(!empty($json)){
				echo count($json);
			}
			else{
				echo "0";
			}
		}
		else{
			echo "Can't get";
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