<?php
require_once('init.php');
	
if($_SERVER['REQUEST_METHOD']=='GET')
{
	$user_id		= $_GET['user_id'];
	$age 			= $_GET['age'];
	$country 		= $_GET['country'];
	$city 			= $_GET['city'];
	$sex			= $_GET['sex'];
	$height			= $_GET['height'];
	$eye_clr		= $_GET['eye_clr'];
	$hair_clr		= $_GET['hair_clr'];

	$sql  = "SELECT * FROM filteres WHERE user_id = '$user_id'";//Количество строк с одинаковым user_id
	$res  = mysqli_query($con,$sql);
	$row  = mysqli_num_rows($res);

	if ($row > 0)//Если их несколько,то инсертим в существущую(подразумеваетсяя,что их не будет больше 1)
	{
	 $sql= "UPDATE filteres SET age='$age',country = '$country',city = '$city',sex='$sex',height='$height',eye_clr='$eye_clr',hair_clr = '$hair_clr' WHERE user_id = '$user_id'";

	if(mysqli_query($con,$sql))
	{
		echo "Filter created 1";
	}
	else
	{
		echo "Could not create 1";
	}
	}
	else//Или же создаем новую строку
	{
		$sql= "INSERT INTO filteres (user_id,age,country,city,sex,height,eye_clr,hair_clr)
					 	VALUES ('$user_id','$age','$country','$city','$sex','$height','$eye_clr','$hair_clr')";	
	if(mysqli_query($con,$sql))
	{
		echo "Filter created 2";
	}
	else
	{
		echo "Could not create 2";
	}
	}		
	
}

else
{
	echo "Error";
}
?>
