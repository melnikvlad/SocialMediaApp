<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id=$_POST['user_id'];
	$tag= $_POST['tag'];

	if(!empty($user_id)&&!empty($tag))
	{
		$sql= "DELETE FROM tags WHERE user_id='$user_id' AND tag='$tag'";
	if(mysqli_query($con,$sql))
	{

  		echo "OK, tag deleted";	
	}
	else{
		echo "Could not delete";}
	}
	else{
		echo "Empty params";}
}
else{
	echo "Error";}
?>