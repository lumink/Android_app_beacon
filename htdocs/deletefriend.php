<?php

$con=mysqli_connect("203.252.195.99","root","choojeok","joljak");  
mysqli_set_charset($con,"utf8");
  
if (mysqli_connect_errno($con))  
{  
   echo "Failed to connect to MySQL: " . mysqli_connect_error();  
}  

session_start();


    $searchID = $_POST['friend_search'];
    $loginedID = $_POST['friend_logined'];

    $result = mysqli_query($con,"delete from friendlist where friendlistid='$loginedID' and friend='$searchID'");  
    $resultreverse = mysqli_query($con,"delete from friendlist where friendlistid='$searchID' and friend='$loginedID'"); 

if($result){
  echo "Be Friend Successfully"; 
}

mysqli_close($con);  

?>