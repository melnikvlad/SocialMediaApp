<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='POST')
{

	$user_id		= $_POST['user_id'];
	$name 			= $_POST['name'];
	$surname 		= $_POST['surname'];
	$age 			= $_POST['age'];
	$country 		= $_POST['country'];
	$city 			= $_POST['city'];
	

	if(!empty($user_id)&&!empty($name)&&!empty($surname)&&!empty($age)&&!empty($country)&&!empty($city))
	{
		$sql= "INSERT INTO profiles(user_id,user_name,user_surname,user_age,user_country,user_city) VALUES ('$user_id','$name','$surname','$age','$country','$city')";
		$sql2 = "INSERT INTO locations (user_id,latitude,longtitude) VALUES ('$user_id',"nothing","nothing")";
	if(mysqli_query($con,$sql)&&mysqli_query($con,$sql2))
	{
		echo "ProfileOlololo created";
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