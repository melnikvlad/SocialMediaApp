<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id		= $_POST['user_id'];
	$date 			= $_POST['date'];
	$description	= $_POST['description'];
	$photo 			= $_POST['photo'];
	
	
	if(!empty($user_id)&&!empty($date)&&!empty($description))
	{
		$sql= "INSERT INTO posts(user_id,date,description,photo) VALUES ('$user_id','$date','$description','$photo')";
		if(mysqli_query($con,$sql)){
			echo "OK, post inserted";
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