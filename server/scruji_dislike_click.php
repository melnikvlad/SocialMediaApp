<?php
require_once('init.php');
if($_SERVER['REQUEST_METHOD']=='GET')
{
	$user_id = $_GET['user_id'];
	$disliked_user_id = $_GET['disliked_user_id'];

	$sql = "INSERT INTO INSERT INTO filteres (user_id,diliked) VALUES ('$user_id','$disliked_user_id')";

	if(mysqli_query($con,$sql))
	{
		echo "Inserted succesfully";
	}
	else
	{
		echo "Could not insert";
	}
}

?>