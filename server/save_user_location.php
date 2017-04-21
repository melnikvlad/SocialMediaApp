<?php
 
 if($_SERVER['REQUEST_METHOD']=='POST'){

	$user_id 		= $_POST['user_id'];
 	$lat 			= $_POST['lat'];
 	$lng 			= $_POST['lng'];
 	
 require_once('init.php');

 
 $sql = "UPDATE locations SET user_id = '$user_id' , latitude = '$lat' , longtitude = '$lng' WHERE user_id = '$user_id' ";
 
 if(mysqli_query($con,$sql)){
 echo "Successfully Uploaded";
 }
 
 mysqli_close($con);
 }
 else{
 echo "Error";
 }

 ?>