<?php  


$con=mysqli_connect("203.252.195.99","root","choojeok","joljak");  
mysqli_set_charset($con,"utf8");
  
if (mysqli_connect_errno($con))  
{  
   echo "Failed to connect to MySQL: " . mysqli_connect_error();  
}  

session_start();


$joinId = $_POST['joinId'];  
$joinPassword = $_POST['joinPassword'];
$joinNickname = $_POST['joinNickname'];  
  
$result = mysqli_query($con,"insert into accountinfo (id,pw,nickname) values ('$joinId','$joinPassword','$joinNickname')");  

  
mysqli_close($con);  
?>