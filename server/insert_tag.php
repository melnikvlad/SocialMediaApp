<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id= $_POST['user_id'];
	$tag 	= $_POST['tag'];
	
	
	if(!empty($user_id)&&!empty($tag))
	{
		$sql= "INSERT INTO tags(user_id,tag) VALUES ('$user_id','$tag')";
	if(mysqli_query($con,$sql))
	{
		echo "OK, tag inserted";
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