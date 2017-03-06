<?php 



require_once('init.php');
if($_SERVER['REQUEST_METHOD']=='GET')
{
	$user_id = $_GET['user_id'];
	$sql    = "SELECT * FROM filteres WHERE user_id = '$user_id'";
	$res     = mysqli_query($con,$sql);	
	$row     = mysqli_fetch_array($res);
		
	 		
if(empty($row['height']))
{
		$sql = "SELECT profiles.user_id FROM profiles,filteres  WHERE filteres.user_id = '$user_id' AND profiles.user_age=filteres.age 
		  																					    	AND profiles.user_sex=filteres.sex 
		  																					    	AND profiles.user_hair_clr=filteres.hair_clr
		  																					    	AND profiles.user_eye_clr=filteres.eye_clr";
	if(empty($row['eye_clr']))
	{
		$sql = "SELECT profiles.user_id FROM profiles,filteres  WHERE filteres.user_id = '$user_id' AND profiles.user_age=filteres.age 
		  																						    AND profiles.user_sex=filteres.sex 
		  																						    AND profiles.user_hair_clr=filteres.hair_clr";
		if(empty($row['hair_clr']))
		{
		$sql = "SELECT profiles.user_id FROM profiles,filteres  WHERE filteres.user_id = '$user_id' AND profiles.user_age=filteres.age 
		  																							AND profiles.user_sex=filteres.sex";
		}
	}		  
}
		  

elseif(empty($row['eye_clr']))
{
 		$sql = "SELECT profiles.user_id FROM profiles,filteres  WHERE filteres.user_id = '$user_id' AND profiles.user_age=filteres.age 
		  																					        AND profiles.user_sex=filteres.sex
		  																							AND profiles.user_height=filteres.height
		  																							AND profiles.user_hair_clr=filteres.hair_clr";
	if(empty($row['hair_clr']))
	{
		$sql = "SELECT profiles.user_id FROM profiles,filteres  WHERE filteres.user_id = '$user_id' AND profiles.user_age=filteres.age 
		  																							AND profiles.user_sex=filteres.sex
		  																							AND profiles.user_height=filteres.height";
	}
		
}
elseif(empty($row['hair_clr']))
{
	    $sql = "SELECT profiles.user_id FROM profiles,filteres  WHERE filteres.user_id = '$user_id' AND profiles.user_age=filteres.age 
		  																							AND profiles.user_sex=filteres.sex
		  																							AND profiles.user_height=filteres.height
		  																							AND profiles.user_eye_clr=filteres.eye_clr";
}
else
{
		$sql = "SELECT profiles.user_id FROM profiles,filteres  WHERE filteres.user_id = '$user_id' AND profiles.user_age=filteres.age 
		  																					 		AND profiles.user_sex=filteres.sex 
		  																					 		AND profiles.user_hair_clr=filteres.hair_clr
		  																					 		AND profiles.user_eye_clr=filteres.eye_clr
		  																							AND profiles.user_height=filteres.height";
}

	$res = mysqli_query($con,$sql);
	
    $all = array();

	while($row = mysqli_fetch_assoc($res))
	{
	
	//$all[] = $row['user_id'];
		 array_push($all, array('user'=> $row['user_id']));
		
	}
  $allJSON = json_encode($all);
  echo $allJSON;
	//echo json_encode(array_values($all));
	


	$like 	 = "SELECT likes.likes  FROM likes WHERE likes.user_id = '$user_id'";
	$dislike = "SELECT dislikes.disliked  FROM dislikes WHERE dislikes.user_id = '$user_id'";

	 $res = mysqli_query($con,$like);
	 $likearr = array();

	while($row = mysqli_fetch_assoc($res))
	{		
		array_push($likearr, array('user'=> $row['likes']));
	//$likearr[] = $row['likes'];
	}
	//print_r($likearr);
	$likearrJSON = json_encode($likearr);
    echo $likearrJSON;
	//echo json_encode($likearr);



	$res = mysqli_query($con,$dislike);
	$dislikearr = array();

	while($row = mysqli_fetch_assoc($res))
	{	
		array_push($dislikearr, array('user'=> $row['disliked']));
		//$dislikearr[] = $row['disliked'];
	}
	//print_r($dislikearr);
	//echo json_encode($dislikearr);
	$dislikearrJSON = json_encode($dislikearr);
    echo $dislikearrJSON;

$match = array();
array_push($match,$result);
$result = array_map(function ($i) { return array('name' => $i); },
                  array_diff(array_map(function ($i) { return $i['user']; }, $all),
                             array_map(function ($i) { return $i['user']; }, $likearr),
                             array_map(function ($i) { return $i['user']; }, $dislikearr)));

// array_push($match,array_diff_key(
// 	array_combine(array_map(function ($i) { return $i['user']; }, $all), $all),
//     array_combine(array_map(function ($i) { return $i['user']; }, $likearr), $likearr),
//     array_combine(array_map(function ($i) { return $i['user']; }, $dislikearr), $dislikearr)
//                         ));


//$match = array('Value' => '');
//$match['Value']=array_intersect($all,$likearr,$dislikearr);

//print_r($match['Value']);


echo json_encode($result);

}
 ?>