
<?php

 // Переменные для соединения с базой данных
    define('USER', "melnik"); 
    define('PASSWORD', "123"); 
    define('DATABASE', "scruj"); 
    define('HOST', "localhost"); 

$con = mysqli_connect(HOST,USER,PASSWORD,DATABASE) or die ('Unable to Connect');


?>