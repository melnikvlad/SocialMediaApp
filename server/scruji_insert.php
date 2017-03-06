<?php
class DBOperations{
 
    private $host = 'localhost';
    private $user = 'melnik';
    private $db = 'scruj';
    private $pass = '123';
    public $conn;
 

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
try {
    $conn = new PDO("mysql:host=".$this -> host.";dbname=".$this -> db, $this -> user, $this -> pass);
    // set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
   $sql= "INSERT INTO profiles(user_id,user_name,user_surname,user_age,user_country,user_city,user_sex,user_height,user_eye_clr,user_hair_clr) 
							VALUES ('$user_id','$name','$surname','$age','$country','$city','$sex','$height','$eye_clr','$hair_clr')";
    // use exec() because no results are returned
    $conn->exec($sql);
}
catch(PDOException $e)
{
    echo $sql . "<br>" . $e->getMessage();
}
$conn = null;
}
?>