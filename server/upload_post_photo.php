<?php
 
 if($_SERVER['REQUEST_METHOD']=='POST'){

 
 	$image 			= $_POST['image'];
	$user_id 		= $_POST['user_id'];
 	$description 	= $_POST['description'];
 
 require_once('init.php');
 
 
 $photo_name = $user_id."_".$description;
 $path = "uploads/posts/$photo_name.png";
 
 $actualpath = "http://10.0.2.2/server/$path";
 
 $sql = "INSERT INTO post_photos (user_id,photo) VALUES ('$user_id','$photo_name')";
 
 if(mysqli_query($con,$sql)){
 file_put_contents($path,base64_decode($image));
 echo "Successfully Uploaded";
 }
 
 mysqli_close($con);
 }
 else{
 echo "Error";
 }

 ?>