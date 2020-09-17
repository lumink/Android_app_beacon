<?php
function unistr_to_xnstr($str){ 
    return preg_replace('/\\\u([a-z0-9]{4})/i', "&#x\\1;", $str); 
} 
$conn = mysqli_connect("203.252.195.99", "root", "choojeok", "joljak");
 
if (mysqli_connect_errno($con)){
	echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
 

$ID = $_GET['ID'];

$res = mysqli_query($conn,"SELECT friendlist.friend, searchinfo.searchid
FROM friendlist, searchinfo
WHERE friendlist.bid = searchinfo.bid AND friendlist.friendlistid IN ('$ID')");

$result = array();  
   
while($row = mysqli_fetch_array($res)){  
  array_push($result,  
    array('friend'=>$row[0],'searchid'=>$row[1]
    ));  
}  
   
 
$json = json_encode(array("result"=>$result), JSON_UNESCAPED_UNICODE);
echo unistr_to_xnstr($json);
 
mysqli_close($conn);
 
 
?>