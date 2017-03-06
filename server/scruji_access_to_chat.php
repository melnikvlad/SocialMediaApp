<?php
require_once('init.php');
if($_SERVER['REQUEST_METHOD']=='GET')
{
	$user_id = $_GET['user_id'];
	$liked_user_id = $_GET['other_user_id'];

	$sql = "INSERT INTO INSERT INTO filteres (user_id,likes) VALUES ('$user_id','$liked_user_id')";

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