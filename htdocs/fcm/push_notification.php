<?php 
	
	function send_notification ($tokens, $message)
	{
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			 'registration_ids' => $tokens,
			 'data' => $message
			);

		$headers = array(
			'Authorization:key =' . GOOGLE_API_KEY,
			'Content-Type: application/json'
			);

	   $ch = curl_init();
       curl_setopt($ch, CURLOPT_URL, $url);
       curl_setopt($ch, CURLOPT_POST, true);
       curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
       curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
       curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);  
       curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
       curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
       $result = curl_exec($ch);           
       if ($result === FALSE) {
           die('Curl failed: ' . curl_error($ch));
       }
       curl_close($ch);
       return $result;
	}
	

	//�����ͺ��̽��� �����ؼ� ��ū���� �����ͼ� FCM�� �߽ſ�û
	include_once 'config.php';
	$conn = mysqli_connect("203.252.195.99","root","choojeok","joljak");
	
	$loginedID = $_POST['loginedID'];

	$sql = "SELECT Token
		FROM tokenusers
		WHERE id IN (SELECT friend
	    		    FROM friendlist
	    		    WHERE '$loginedID' = friendlistid)";

	$result = mysqli_query($conn,$sql);
	$tokens = array();

	if(mysqli_num_rows($result) > 0 ){

		while ($row = mysqli_fetch_assoc($result)) {
			$tokens[] = $row["Token"];
		}
	}

	mysqli_close($conn);
	
        $myMessage = $_POST['message']; //������ �Է��� �޼����� ����
	if ($myMessage == ""){
		$myMessage = "new message";
	}

	$message = iconv("euc-kr","utf-8",$message);
	$message = array("message" => $myMessage);
	$message_status = send_notification($tokens, $message);
	echo $message_status;

 


 ?>