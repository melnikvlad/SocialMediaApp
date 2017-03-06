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
	$sex			= $_POST['sex'];
	$height			= $_POST['height'];
	$eye_clr		= $_POST['eye_clr'];
	$hair_clr		= $_POST['hair_clr'];

	if(!empty($user_id)&&!empty($name)&&!empty($surname)&&!empty($age)&&!empty($country)&&!empty($city)&&!empty($sex)&&!empty($height)&&!empty($eye_clr)&&
		!empty($hair_clr))
	{
		$sql= "INSERT INTO profiles(user_id,user_name,user_surname,user_age,user_country,user_city,user_sex,user_height,user_eye_clr,user_hair_clr) 
							VALUES ('$user_id','$name','$surname','$age','$country','$city','$sex','$height','$eye_clr','$hair_clr')";
	if(mysqli_query($con,$sql))
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