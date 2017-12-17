<?php
/////////////////////////////////////////////////////////////////////////////////////
require 'iniClass.php';
require 'Functions/Auth.php';
require 'Functions/Register.php';
require 'Functions/BookAdd.php';
require 'Functions/BookDelete.php';

$UsersINI = new iniFile("Data/Users.ini");
$BooksINI = new iniFile("Data/Books.ini");
/////////////////////////////////////////////////////////////////////////////////////


$func = $_POST["Function"];

    switch($func) {
        
/////////////////////////////////////////////////////////////////////////////////////  

        case 'Auth':
            
            $LoginClient = $_POST["Login"];
            $PasswordClient = $_POST["Password"];
            
            if($PasswordClient != NULL and $LoginClient != NULL and 
            preg_match('/^[a-z]+([-_]?[a-z0-9]+){0,2}$/i',$LoginClient)) {
                    
                    $resA = Auth($LoginClient,$PasswordClient,$UsersINI);
                    
                    if($resA == 'Error|Login') {
                        $EndTime = time()+3600;
                        $UsersINI->addParam($LoginClient, "EndTime", $EndTime);
                        $return = Register($LoginClient,$PasswordClient,$UsersINI);
                   
                    } elseif($resA == 'Good|Auth') {
                        $return = 'Good|Auth';
                    
                    } elseif($resA == 'Error|Password') {
                        $return = 'Error|UserHaveAccBatPasswordNotCorrect';
                    }
            $explode = explode('|',$return);
            $array = ["Result" => $explode[0],"Param" => $explode[1]];
            $JasonRESULT = (json_encode($array));
            
            } else {
                $arrayE = ["Result" => 'Error',"Param" => 'Incorrect',];
                $JasonRESULT = (json_encode($arrayE));
            }
            
        break;
/////////////////////////////////////////////////////////////////////////////////////  

        case 'BookDelete':
            
            $LoginClient = $_POST["Login"];
            $BookId = $_POST["BookId"];
            
            if($LoginClient != NULL and $BookId != NULL) {
                    
                    $resA = Auth($LoginClient,$PasswordClient,$UsersINI);
                    
                    if($resA == 'Error|Password') {
                        $EndTime = $UsersINI->read()[$LoginClient]['EndTime'];
                        $EndTime = time()-$EndTime;
                        if($EndTime<0) {
                            $EndTime = time()+3600;
                            $UsersINI->addParam($LoginClient, "EndTime", $EndTime);
                            $return = BookDelete($BookId, $BooksINI);
                        } else {
                            $return = 'Error|EndTime';
                        }
                       
                    
                    } else {
                        $return = 'Error|Account';
                    }
            $explode = explode('|',$return);
            $array = ["Result" => $explode[0],"Param" => $explode[1]];
            $JasonRESULT = (json_encode($array));
                
            } else {
                $arrayE = ["Result" => 'Error',"Param" => 'Incorrect'];
                $JasonRESULT = (json_encode($arrayE));
            }
            
        break;
        
/////////////////////////////////////////////////////////////////////////////////////        
    
         case 'BookAdd':
             
            $LoginClient = $_POST["Login"];
            $BookName = $_POST["BookName"];
            $BookAuthor = $_POST["BookAuthor"];
            $BookYear = $_POST["BookYear"];
            
            if($LoginClient != NULL and $BookName != NULL and $BookAuthor != NULL and $BookYear != NULL){
                    
                    $resA = Auth($LoginClient,$PasswordClient,$UsersINI);
                    
                    if($resA == 'Error|Password') {
                        $EndTime = $UsersINI->read()[$LoginClient]['EndTime'];
                        $EndTime = time()-$EndTime;
                        if($EndTime<0) {
                            $EndTime = time()+3600;
                            $UsersINI->addParam($LoginClient, "EndTime", $EndTime);
                            $return = BookAdd($LoginClient,$BookName,$BookAuthor,$BookYear,$BooksINI);
                        } else {
                            $return = 'Error|EndTime';
                        }
                       
                
                    } else {
                        $return = 'Error|Account';
                    }
            $explode = explode('|',$return);
            $array = ["Result" => $explode[0],"Param" => $explode[1]];
            $JasonRESULT = (json_encode($array));
                
            } else {
                $arrayE = ["Result" => 'Error',"Param" => 'Incorrect'];
                $JasonRESULT = (json_encode($arrayE));
            }
            
        break;
/////////////////////////////////////////////////////////////////////////////////////       
        
       
// Здесь будут находиться остальные функции       
        
        
/////////////////////////////////////////////////////////////////////////////////////        
        default:
            $arrayD = ["Result" => 'Error',"Param" => 'Request'];
            $JasonRESULT = (json_encode($arrayD));
/////////////////////////////////////////////////////////////////////////////////////
    }
    
    $BooksINI->save();
    $UsersINI->save();
    
    echo($JasonRESULT);
/////////////////////////////////////////////////////////////////////////////////////    
?>
