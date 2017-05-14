<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id		= $_POST['user_id'];
	$friend 		= $_POST['other_user_id'];
	
	
	if(!empty($user_id)&&!empty($friend))
	{
		$sql= "INSERT INTO friendships (user_id,other_user_id) VALUES ('$user_id','$friend')";
		if(mysqli_query($con,$sql)){
			echo "OK";
		}
		else{
			echo "Could not insert";
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