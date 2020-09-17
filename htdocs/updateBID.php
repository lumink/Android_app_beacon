<?php


	$con=mysqli_connect("203.252.195.99","root","choojeok","joljak");  
 
mysqli_set_charset($con,"utf8");
  
if (mysqli_connect_errno($con))  
{  
   echo "Failed to connect to MySQL: " . mysqli_connect_error();  
}

$bid = $_POST['bid'];
$loginedID = $_POST['loginedID'];
 
  
$result = mysqli_query($con,"update accountinfo set bid='$bid' where id='$loginedID'");  
$result_friendlist = mysqli_query($con,"update friendlist set bid='$bid' where friend='$loginedID'");  
  
  if($result){  
    echo 'success';
  }  
  else{  
    echo 'failure';  
  }  
  
  
mysqli_close($con);
?>
