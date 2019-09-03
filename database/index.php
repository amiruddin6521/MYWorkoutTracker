<?php
require "conn.php";
date_default_timezone_set('Asia/Kuala_Lumpur');

//Check login
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnLogin')
{
	$varEmail = $_REQUEST['varEmail'];
	$varPassword = $_REQUEST['varPassword'];
	
	$mysql_qry = "SELECT * FROM user_data WHERE email like '$varEmail' and password like '$varPassword';";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		while($row = $result->fetch_assoc()){
			$id = $row["_id"];
			$name = $row["name"];
		}
		$response["id"] = $id;
		$response["name"] = $name;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}
	
//Check valid email
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnCheckEmail')
{
	$varEmail = $_REQUEST['varEmail'];
	
	$mysql_qry = "SELECT * FROM user_data WHERE email like '$varEmail';";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "False";
	} else {
		$check = "True";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Save register data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnRegister')
{
	$varEmail = $_REQUEST['varEmail'];
	$varPassword = $_REQUEST['varPassword'];
	$varName = $_REQUEST['varName'];
	$varBdate = $_REQUEST['varBdate'];
	$varGender = $_REQUEST['varGender'];
	$varWeight = $_REQUEST['varWeight'];
	$varHeight = $_REQUEST['varHeight'];
	$encoded_string = $_REQUEST['encoded_string'];
	$image_name = $_REQUEST['image_name'];
	$currDate = date("Y-m-d");
	
	if($encoded_string != ""){
		$decoded_string = base64_decode($encoded_string);
		$path = 'images/'.$image_name;
		$file = fopen($path, 'wb');
		$is_written = fwrite($file, $decoded_string);
		fclose($file);
		$mysql_qry = "INSERT INTO user_data (date_create, email, password, password_date, name, bdate, gender, height, p_path) 
		VALUES ('$currDate', '$varEmail', '$varPassword', '$currDate', '$varName', '$varBdate', '$varGender', '$varHeight', '$path')";
	} else {
		$mysql_qry = "INSERT INTO user_data (date_create, email, password, password_date, name, bdate, gender, height) 
		VALUES ('$currDate', '$varEmail', '$varPassword', '$currDate', '$varName', '$varBdate', '$varGender', '$varHeight')";
		$is_written = 1;
	}
	
	if($is_written > 0) {
		if($conn->query($mysql_qry) === TRUE) {
			$last_id = $conn->insert_id;
			$mysql_qry2 = "INSERT INTO weight_track (cDate, weight, user_id) 
			VALUES ('$currDate', '$varWeight', '$last_id')";
			if($conn->query($mysql_qry2) === TRUE) {
				$check = "True";
			}
		} else {
			$check = "False";
		}
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Load image, name and email in nav header
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnLoadNavHeader') {
	$id = $_REQUEST['_id'];
	
	$mysql_qry = "SELECT * FROM user_data WHERE _id like '$id';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		while($row = $result->fetch_assoc()){
			$email = $row["email"];
			$name = $row["name"];
			$path = $row["p_path"];
		}
		if($path == ""){
			$encoded = "";
		}else{
			$encoded = base64_encode(file_get_contents($path));
		}
		$response["email"] = $email;
		$response["name"] = $name;
		$response["encoded"] = $encoded;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Load profile data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnLoadProfile') {
	$id = $_REQUEST['id'];
	
	$mysql_qry = "SELECT * FROM user_data WHERE _id like '$id';";
	$mysql_qry2 = "SELECT * FROM weight_track WHERE user_id like '$id' ORDER BY cDate ASC;";
	
	$result = mysqli_query($conn, $mysql_qry);
	$result2 = mysqli_query($conn, $mysql_qry2);
	if(mysqli_num_rows($result) > 0) {
		while($row = $result->fetch_assoc()){
			$email = $row["email"];
			$name = $row["name"];
			$dob = $row["bdate"];
			$height = $row["height"];
			$gender = $row["gender"];
			$pdate = $row["password_date"];
			$path = $row["p_path"];
		}
		if(mysqli_num_rows($result2) > 0) {
			while($row = $result2->fetch_assoc()){
				$weight = $row["weight"];
			}
		} else {
			$weight = "0";
		}
		if($path == ""){
			$encoded = "";
		}else{
			$encoded = base64_encode(file_get_contents($path));
		}
		$check = "True";
		$response["email"] = $email;
		$response["name"] = $name;
		$response["dob"] = $dob;
		$response["weight"] = $weight;
		$response["height"] = $height;
		$response["gender"] = $gender;
		$response["pdate"] = $pdate;
		$response["encoded"] = $encoded;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Update data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnUpdateProfile')
{
	$varId = $_REQUEST['varId'];
	$varAttr = $_REQUEST['varAttr'];
	$varData = $_REQUEST['varData'];
	
	$mysql_qry = "UPDATE user_data SET $varAttr = '$varData' WHERE _id like '$varId';";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Update weigth
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnUpdateWeight')
{
	$varId = $_REQUEST['varId'];
	$varData = $_REQUEST['varData'];
	$currDate = date("Y-m-d");
	
	$mysql_qry = "INSERT INTO weight_track (date, weight, user_id) VALUES ('$currDate', '$varData', '$varId')";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Update password
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnUpdatePassword')
{
	$varId = $_REQUEST['varId'];
	$varData = $_REQUEST['varData'];
	$currDate = date("Y-m-d");
	
	$mysql_qry = "UPDATE user_data SET password = '$varData', password_date = '$currDate' WHERE _id like '$varId';";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Get current password
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnGetCurrPassword')
{
	$varId = $_REQUEST['varId'];
	$varPassword = $_REQUEST['varPassword'];
	
	$mysql_qry = "SELECT * FROM user_data WHERE _id like '$varId';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row = $result->fetch_assoc()){
			$password = $row["password"];
		}
		if($password == $varPassword){
			$check = "True";
		} else {
			$check = "False";
		}
	} else {
		$check = "False";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Update image
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnUpdateImage') {
	$varId = $_REQUEST['id'];
	$encoded_string = $_REQUEST['encoded_string'];
	$image_name = $_REQUEST['image_name'];
	
	$decoded_string = base64_decode($encoded_string);
	
	$path = 'images/'.$image_name;
	
	$file = fopen($path, 'wb');
	
	$is_written = fwrite($file, $decoded_string);
	fclose($file);
	
	$mysql_qry1 = "SELECT * FROM user_data WHERE _id like '$varId';";
	$mysql_qry2 = "UPDATE user_data SET p_path = '$path' WHERE _id like '$varId';";
	
	$result1 = mysqli_query($conn, $mysql_qry1);
	if(mysqli_num_rows($result1) > 0) {
		while($row = $result1->fetch_assoc()){
			$p_path = $row["p_path"];
		}
		if (file_exists($p_path)){
			unlink($p_path);
			if($is_written > 0) {
				$result2 = mysqli_query($conn, $mysql_qry2);
				if($result2) {
					$check = "True";
				} else {
					$check = "False1";
				}
			}
		}else{
			if($is_written > 0) {
				$result2 = mysqli_query($conn, $mysql_qry2);
				if($result2) {
					$check = "True";
				} else {
					$check = "False2";
				}
			} 
		}
	}else{
		$check = "False3";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Delete image
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnDeleteImage') {
	$varId = $_REQUEST['id'];
	
	$mysql_qry1 = "SELECT * FROM user_data WHERE _id like '$varId';";
	$mysql_qry2 = "UPDATE user_data SET p_path = '' WHERE _id like '$varId';";
	
	$result = mysqli_query($conn, $mysql_qry1);
	if(mysqli_num_rows($result) > 0) {
		while($row = $result->fetch_assoc()){
			$p_path = $row["p_path"];
		}
		if (file_exists($p_path)){
			unlink($p_path);
			if($conn->query($mysql_qry2) === TRUE) {
				$check = "True";
			} else {
				$check = "False";
			}
		}else{
			$check = "False"; 
		}
	} else {
		$check = "False";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Get machine list
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMachineList')
{
	$varId = $_REQUEST['id'];
	$mysql_qry = "SELECT * FROM machine WHERE user_id like '$varId' ORDER BY name ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	} else {
		$item = null;
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get machine data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnGetMachine')
{
	$varName = $_REQUEST['name'];
	$varId = $_REQUEST['id'];
	$mysql_qry = "SELECT * FROM machine WHERE name like '$varName' AND user_id like '$varId';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		while($row = $result->fetch_assoc()){
			$type = $row["type"];
			$id = $row["_id"];
			$picture = $row["picture"];
		}
		if($picture == ""){
			$encoded = "";
		}else{
			$encoded = base64_encode(file_get_contents($picture));
		}
		$response["type"] = $type;
		$response["id"] = $id;
		$response["encoded"] = $encoded;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Save bodybuilding data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnAddWorkoutB')
{
	$varSets = $_REQUEST['varSets'];
	$varReps = $_REQUEST['varReps'];
	$varWeight = $_REQUEST['varWeight'];
	$varDate = $_REQUEST['varDate'];
	$varMachine = $_REQUEST['varMachine'];
	$varUser = $_REQUEST['varUser'];
	$varTime = $_REQUEST['varTime'];
	
	$mysql_qry = "INSERT INTO body_building (bDate, bTime, sets, reps, weight, machine_id, user_id) 
		VALUES ('$varDate', '$varTime', '$varSets', '$varReps', '$varWeight', '$varMachine', '$varUser')";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);

	$response["respond"] = $check;
	echo json_encode($response);
}

//Save cardio data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnAddWorkoutC')
{
	$varDist = $_REQUEST['varDist'];
	$varDurr = $_REQUEST['varDurr'];
	$varDate = $_REQUEST['varDate'];
	$varMachine = $_REQUEST['varMachine'];
	$varUser = $_REQUEST['varUser'];
	$varTime = $_REQUEST['varTime'];
	
	$mysql_qry = "INSERT INTO cardio (cDate, cTime, distance, duration, machine_id, user_id) 
		VALUES ('$varDate', '$varTime', '$varDist', '$varDurr', '$varMachine', '$varUser')";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Get workout list for Body Building
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMWorkoutListB')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT * FROM body_building WHERE machine_id like '$varIdM' and user_id like '$varIdU' ORDER BY bDate DESC, _id DESC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout list for Cardio
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMWorkoutListC')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT * FROM cardio WHERE machine_id like '$varIdM' and user_id like '$varIdU' ORDER BY cDate DESC, _id DESC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout date for Body Building
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMWorkoutDateB')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT DISTINCT bDate FROM body_building WHERE machine_id like '$varIdM' and user_id like '$varIdU' ORDER BY bDate DESC, _id DESC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout date for Cardio
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMWorkoutDateC')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT DISTINCT cDate FROM cardio WHERE machine_id like '$varIdM' and user_id like '$varIdU' ORDER BY cDate DESC, _id DESC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout list for Body Building by date
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMWorkoutListDateB')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];
	$varDate = $_REQUEST['date'];

	$mysql_qry = "SELECT * FROM body_building WHERE machine_id like '$varIdM' and user_id like '$varIdU' and bDate like '$varDate' ORDER BY bDate DESC, _id DESC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout list for Cardio by date
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMWorkoutListDateC')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];
	$varDate = $_REQUEST['date'];

	$mysql_qry = "SELECT * FROM cardio WHERE machine_id like '$varIdM' and user_id like '$varIdU' and cDate like '$varDate' ORDER BY cDate DESC, _id DESC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Load exercise data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnLoadExerciseData') {
	$varId = $_REQUEST['id'];
	$varName = $_REQUEST['name'];
	
	$mysql_qry = "SELECT * FROM machine WHERE name like '$varName' AND user_id like '$varId';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		while($row = $result->fetch_assoc()){
			$id = $row["_id"];
			$description = $row["description"];
			$type = $row["type"];
			$picture = $row["picture"];
		}
		if($picture == ""){
			$encoded = "";
		}else{
			$encoded = base64_encode(file_get_contents($picture));
		}
		$response["id"] = $id;
		$response["description"] = $description;
		$response["type"] = $type;
		$response["encoded"] = $encoded;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Delete Exercise Body Building
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnDeleteExerciseB')
{
	$varId = $_REQUEST['id'];
	
	$mysql_qry = "DELETE FROM body_building WHERE _id like '$varId'";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);

	$response["respond"] = $check;
	echo json_encode($response);
}

//Delete Exercise Cardio
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnDeleteExerciseC')
{
	$varId = $_REQUEST['id'];
	
	$mysql_qry = "DELETE FROM cardio WHERE _id like '$varId'";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);

	$response["respond"] = $check;
	echo json_encode($response);
}

//Get machine list picture
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMachineListPicture')
{
	$varId = $_REQUEST['id'];
	$mysql_qry = "SELECT * FROM machine WHERE user_id like '$varId' ORDER BY name ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row = mysqli_fetch_array($result)) {
            $paths[] = $row['picture'];
            
        }
		foreach($paths as $path) {
			if($path != null) {
				$encoded[] = base64_encode(file_get_contents($path));
			} else {
				$encoded[] = "";
			}
		}
		echo json_encode($encoded);
	}
	mysqli_close($conn);
}

//Save exercise data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnAddMachine')
{
	$varName = $_REQUEST['varName'];
	$varDesc = $_REQUEST['varDesc'];
	$varType = $_REQUEST['varType'];
	$varUser = $_REQUEST['varUser'];
	$currDate = date("Y-m-d");
	$encoded_string = $_REQUEST['encoded_string'];
	$image_name = $_REQUEST['image_name'];
	
	if($encoded_string != ""){
		$decoded_string = base64_decode($encoded_string);
		$path = 'images/'.$image_name;
		$file = fopen($path, 'wb');
		$is_written = fwrite($file, $decoded_string);
		fclose($file);
		$mysql_qry = "INSERT INTO machine (mDate, name, description, type, picture, user_id) 
		VALUES ('$currDate', '$varName', '$varDesc', '$varType', '$path', '$varUser')";
	} else {
		$mysql_qry = "INSERT INTO machine (mDate, name, description, type, picture, user_id) 
		VALUES ('$currDate', '$varName', '$varDesc', '$varType', '', '$varUser')";
		$is_written = 1;
	}
	
	if($is_written > 0) {
		if($conn->query($mysql_qry) === TRUE) {
			$check = "True";
		} else {
			$check = "False";
		}
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Update machine data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnUpdateMachine')
{
	$varName = $_REQUEST['varName'];
	$varDesc = $_REQUEST['varDesc'];
	$varId = $_REQUEST['varId'];
	$varStatus = $_REQUEST['varStatus'];
	$encoded_string = $_REQUEST['encoded_string'];
	$image_name = $_REQUEST['image_name'];

	if($varStatus == "true"){
		if($encoded_string != ""){
			$decoded_string = base64_decode($encoded_string);
			$path = 'images/'.$image_name;
			$file = fopen($path, 'wb');
			$is_written = fwrite($file, $decoded_string);
			fclose($file);
			$mysql_qry2 = "UPDATE machine SET name = '$varName', description = '$varDesc', picture = '$path' WHERE _id like '$varId';";
		} else {
			$mysql_qry2 = "UPDATE machine SET name = '$varName', description = '$varDesc' WHERE _id like '$varId';";
			$is_written = 1;
		}
	} else {
		$mysql_qry2 = "UPDATE machine SET name = '$varName', description = '$varDesc', picture = '' WHERE _id like '$varId';";
		$is_written = 1;
	}
	
	$mysql_qry1 = "SELECT * FROM machine WHERE _id like '$varId';";
	
	$result1 = mysqli_query($conn, $mysql_qry1);
	if(mysqli_num_rows($result1) > 0) {
		while($row = $result1->fetch_assoc()){
			$p_path = $row["picture"];
		}
		if($varStatus == "false"){
			if (file_exists($p_path)){
				unlink($p_path);
				if($is_written > 0) {
					$result2 = mysqli_query($conn, $mysql_qry2);
					if($result2) {
						$check = "True";
					} else {
						$check = "False";
					}
				}
			} else {
				if($is_written > 0) {
					$result2 = mysqli_query($conn, $mysql_qry2);
					if($result2) {
						$check = "True";
					} else {
						$check = "False";
					}
				} 
			}
		} else {
			if($is_written > 0) {
				$result2 = mysqli_query($conn, $mysql_qry2);
				if($result2) {
					$check = "True";
				} else {
					$check = "False";
				}
			}
		}
		
	}else{
		$check = "False";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Delete machine image
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnDeleteMachine') {
	$varId = $_REQUEST['id'];
	$varType = $_REQUEST['type'];

	if($varType == "Cardio") {
		$mysql_qry = "SELECT * FROM cardio WHERE machine_id like '$varId';";
		$result = mysqli_query($conn, $mysql_qry);
		if(mysqli_num_rows($result) > 0) {
			$mysql_qry2 = "DELETE FROM cardio WHERE machine_id like '$varId';";
			$result2 = mysqli_query($conn, $mysql_qry2);
			if($result2) {
				$check = "True";
			} else {
				$check = "False";
			}
		}
	} else {
		$mysql_qry = "SELECT * FROM body_building WHERE machine_id like '$varId';";
		$result = mysqli_query($conn, $mysql_qry);
		if(mysqli_num_rows($result) > 0) {
			$mysql_qry2 = "DELETE FROM body_building WHERE machine_id like '$varId';";
			$result2 = mysqli_query($conn, $mysql_qry2);
			if($result2) {
				$check = "True";
			} else {
				$check = "False";
			}
		}
	}
	
	$mysql_qry3 = "SELECT * FROM machine WHERE _id like '$varId';";
	$mysql_qry4 = "DELETE FROM machine WHERE _id like '$varId';";
	
	$result = mysqli_query($conn, $mysql_qry3);
	if(mysqli_num_rows($result) > 0) {
		while($row = $result->fetch_assoc()){
			$p_path = $row["picture"];
		}
		if (file_exists($p_path)){
			unlink($p_path);
			if($conn->query($mysql_qry4) === TRUE) {
				$check = "True";
			} else {
				$check = "False";
			}
		}else{
			if($conn->query($mysql_qry4) === TRUE) {
				$check = "True";
			} else {
				$check = "False";
			} 
		}
	} else {
		$check = "False";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Get weight list
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWeightList')
{
	$varId = $_REQUEST['id'];
	$mysql_qry = "SELECT * FROM weight_track WHERE user_id like '$varId' ORDER BY cDate DESC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get weight graph
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWeightGraph')
{
	$varId = $_REQUEST['id'];
	$mysql_qry = "SELECT * FROM weight_track WHERE user_id like '$varId' ORDER BY cDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Delete weight data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnDeleteWeightList')
{
	$varId = $_REQUEST['id'];

	$mysql_qry = "DELETE FROM weight_track WHERE _id like '$varId'";

	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);

	$response["respond"] = $check;
	echo json_encode($response);
}

//Save weight data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnSaveWeight')
{
	$varId = $_REQUEST['varId'];
	$varDate = $_REQUEST['varDate'];
	$varWeight = $_REQUEST['varWeight'];

	$mysql_qry = "SELECT * FROM weight_track WHERE user_id like '$varId' AND cDate like '$varDate';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row = $result->fetch_assoc()){
			$id = $row["_id"];
		}
		$mysql_qry1 = "UPDATE weight_track SET weight = '$varWeight' WHERE _id like '$id';";
		$result1 = mysqli_query($conn, $mysql_qry1);
		if($result1) {
			$check = "True";
		} else {
			$check = "False";
		}
	} else {
		$mysql_qry2 = "INSERT INTO weight_track (cDate, weight, user_id) VALUES ('$varDate', '$varWeight', '$varId')";
		$result2 = mysqli_query($conn, $mysql_qry2);
		if($result2) {
			$check = "True";
		} else {
			$check = "False";
		}
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Load BMI data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnLoadBMI') {
	$id = $_REQUEST['id'];
	
	$mysql_qry = "SELECT * FROM user_data WHERE _id like '$id';";
	$mysql_qry2 = "SELECT * FROM weight_track WHERE user_id like '$id' ORDER BY cDate ASC;";
	
	$result = mysqli_query($conn, $mysql_qry);
	$result2 = mysqli_query($conn, $mysql_qry2);
	if(mysqli_num_rows($result) > 0) {
		while($row = $result->fetch_assoc()){
			$height = $row["height"];
		}
		if(mysqli_num_rows($result2) > 0) {
			while($row = $result2->fetch_assoc()){
				$weight = $row["weight"];
			}
		}

		$check = "True";
		$response["weight"] = $weight;
		$response["height"] = $height;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Get body measure list
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnBodyMeasureList')
{
	$varId = $_REQUEST['id'];
	$mysql_qry = "SELECT * FROM body_track WHERE user_id like '$varId' ORDER BY bodypart ASC, btDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get measure list
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMeasureList')
{
	$varId = $_REQUEST['id'];
	$varbtId = $_REQUEST['btId'];
	$mysql_qry = "SELECT * FROM body_track WHERE bodypart like '$varbtId' AND user_id like '$varId' ORDER BY btDate DESC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get measure graph
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnMeasureGraph')
{
	$varId = $_REQUEST['id'];
	$varbtId = $_REQUEST['btId'];
	$mysql_qry = "SELECT * FROM body_track WHERE bodypart like '$varbtId' AND user_id like '$varId' ORDER BY btDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Delete measure data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnDeleteMeasureList')
{
	$varId = $_REQUEST['id'];
	$varbtId = $_REQUEST['btId'];
	
	$mysql_qry = "DELETE FROM body_track WHERE bodypart like '$varbtId' AND _id like '$varId'";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);

	$response["respond"] = $check;
	echo json_encode($response);
}

//Save measure data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnSaveMeasure')
{
	$varId = $_REQUEST['varId'];
	$varDate = $_REQUEST['varDate'];
	$varMeasure = $_REQUEST['varMeasure'];
	$varbtId = $_REQUEST['btId'];

	$mysql_qry = "SELECT * FROM body_track WHERE bodypart like '$varbtId' AND user_id like '$varId' AND btDate like '$varDate';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row = $result->fetch_assoc()){
			$id = $row["_id"];
		}
		$mysql_qry1 = "UPDATE body_track SET bodymeasure = '$varMeasure' WHERE _id like '$id';";
		$result1 = mysqli_query($conn, $mysql_qry1);
		if($result1) {
			$check = "True";
		} else {
			$check = "False";
		}
	} else {
		$mysql_qry2 = "INSERT INTO body_track (btDate, bodypart, bodymeasure, user_id) VALUES ('$varDate', '$varbtId', '$varMeasure', '$varId')";
		$result2 = mysqli_query($conn, $mysql_qry2);
		if($result2) {
			$check = "True";
		} else {
			$check = "False";
		}
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}

//Get workout distict date for Body Building Reps 1
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutDateB1')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT DISTINCT bDate FROM body_building WHERE machine_id like '$varIdM' AND user_id like '$varIdU' AND reps >= '1' ORDER BY bDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout max weight for Body Building Reps 1
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutWeightB1')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];
	$varDate = $_REQUEST['date'];

	$mysql_qry = "SELECT MAX(weight) FROM body_building WHERE bDate like '$varDate' AND machine_id like '$varIdM' AND user_id like '$varIdU' AND reps >= '1';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		while($row = $result->fetch_assoc()){
			$weight = $row["MAX(weight)"];
		}

		$response["weight"] = $weight;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Get workout distict date for Body Building Reps 5
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutDateB5')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT DISTINCT bDate FROM body_building WHERE machine_id like '$varIdM' AND user_id like '$varIdU' AND reps >= '5' ORDER BY bDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout max weight for Body Building Reps 5
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutWeightB5')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];
	$varDate = $_REQUEST['date'];

	$mysql_qry = "SELECT MAX(weight) FROM body_building WHERE bDate like '$varDate' AND machine_id like '$varIdM' AND user_id like '$varIdU' AND reps >= '5';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		while($row = $result->fetch_assoc()){
			$weight = $row["MAX(weight)"];
		}

		$response["weight"] = $weight;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Get workout date for Body Building Sum
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutDateBS')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT DISTINCT bDate FROM body_building WHERE machine_id like '$varIdM' AND user_id like '$varIdU' ORDER BY bDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout sum weight for Body Building Sum
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutSumBS')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];
	$varDate = $_REQUEST['date'];

	$mysql_qry = "SELECT reps, weight FROM body_building WHERE machine_id like '$varIdM' AND user_id like '$varIdU' AND bDate like '$varDate';";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout distict date for Cardio Distance
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutDateCD')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT DISTINCT cDate FROM cardio WHERE machine_id like '$varIdM' AND user_id like '$varIdU' ORDER BY cDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get sum distance for Cardio Distance
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutSumCD')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];
	$varDate = $_REQUEST['date'];

	$mysql_qry = "SELECT SUM(distance) FROM cardio WHERE machine_id like '$varIdM' AND user_id like '$varIdU' AND cDate like '$varDate';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		while($row = $result->fetch_assoc()){
			$distance = $row["SUM(distance)"];
		}

		$response["distance"] = $distance;
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Get workout distict date for Cardio Duration
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutDateCDD')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT DISTINCT cDate FROM cardio WHERE machine_id like '$varIdM' AND user_id like '$varIdU' ORDER BY cDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get sum duration for Cardio Duration
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutSumCDD')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];
	$varDate = $_REQUEST['date'];

	$mysql_qry = "SELECT duration FROM cardio WHERE machine_id like '$varIdM' AND user_id like '$varIdU' AND cDate like '$varDate';";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Get workout distict date for Cardio Speed
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnWorkoutDateCS')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT DISTINCT cDate FROM cardio WHERE machine_id like '$varIdM' AND user_id like '$varIdU' ORDER BY cDate ASC;";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		while($row[] = $result->fetch_assoc()){
			$item = $row;
		}
		echo json_encode($item);
	}
	mysqli_close($conn);
}

//Check body building data for graph
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnCheckBGraph')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT * FROM body_building WHERE machine_id like '$varIdM' AND user_id like '$varIdU';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Check cardio data for graph
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnCheckCGraph')
{
	$varIdM = $_REQUEST['mId'];
	$varIdU = $_REQUEST['uId'];

	$mysql_qry = "SELECT * FROM cardio WHERE machine_id like '$varIdM' AND user_id like '$varIdU';";
	
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "True";
		$response["respond"] = $check;
		echo json_encode($response);
	} else {
		$check = "False";
		$response["respond"] = $check;
		echo json_encode($response);
	}
	mysqli_close($conn);
}

//Delete account data
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnDeleteAcc')
{
	$varId = $_REQUEST['varId'];
	
	$mysql_qry = "DELETE FROM user_data WHERE _id like '$varId'";
	
	if($conn->query($mysql_qry) === TRUE) {
		$check = "True";
	} else {
		$check = "False";
	}
	mysqli_close($conn);

	$response["respond"] = $check;
	echo json_encode($response);
}

//Check valid exercise
if(isset($_POST['selectFn']) && $_POST['selectFn']=='fnCheckMachine')
{
	$varName = $_REQUEST['varName'];
	$varId = $_REQUEST['varId'];
	
	$mysql_qry = "SELECT * FROM machine WHERE user_id like '$varId' AND name like '$varName';";
	$result = mysqli_query($conn, $mysql_qry);
	if(mysqli_num_rows($result) > 0) {
		$check = "False";
	} else {
		$check = "True";
	}
	mysqli_close($conn);
	
	$response["respond"] = $check;
	echo json_encode($response);
}
	
?>