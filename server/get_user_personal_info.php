<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id = $_POST['user_id'];

	
	if(!empty($user_id))
	{
			$sql= "SELECT * FROM profiles WHERE user_id='$user_id'";
		if(mysqli_query($con,$sql))
		{
			$json= array();
			$result = mysqli_query ($con, $sql);
			while($row = mysqli_fetch_array ($result))     
			{
				 $user = array(
				 	'id' 		=> $row['user_id'],
			        'name' 		=> $row['user_name'],
			        'age' 		=> $row['user_age'],
			        'country' 	=> $row['user_country'],
			        'city' 		=> $row['user_city']
			    );
				 array_push($json, $user);
			}
			$result = json_encode($json);
			echo $result;
		}
		else{
			echo "MySQL error";
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