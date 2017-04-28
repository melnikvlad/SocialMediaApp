<?php
		
 if($_SERVER['REQUEST_METHOD']=='POST'){

 $user_id 	= $_POST['user_id'];
 $image 	= $_POST['image'];

 
 require_once('init.php');
 
 $sql ="SELECT id FROM photos ORDER BY id ASC";
 
 $res = mysqli_query($con,$sql);
 
 $id = 0;
 
 while($row = mysqli_fetch_array($res))
	 {
	 	$id = $row['id'];
	 }
 
 $path = "uploads/main/$user_id.png";
 
 $actualpath = "http://10.0.2.2/server/$path";
 
 $sql = "INSERT INTO photos (photo,user_id) VALUES ('$actualpath','$user_id')";
 
 if(mysqli_query($con,$sql)){
 file_put_contents($path,base64_decode($image));
 echo "Successfully Uploaded";
 }
 
 mysqli_close($con);
 }else{
 echo "Error";
 }

 ?>
