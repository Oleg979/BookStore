<?php

/////////////////////////////////////////////////////////////////////////////////////
require 'iniClass.php';
require 'Functions/FunctionsMainHelp/Auth.php';
require 'Functions/FunctionsMainHelp/AuthTest.php';
require 'Functions/FunctionsAuth/Register.php';
require 'Functions/FunctionsAuth/DeAuth.php';
require 'Functions/FunctionsBook/Add/BookAdd.php';
require 'Functions/FunctionsBook/Del/BookDelete.php';
require 'Functions/FunctionsBook/Get/GetAllBooks.php';
require 'Functions/FunctionsBook/Get/GetBook.php';
require 'Functions/FunctionsBook/Get/GetUserBooks.php';
require 'Functions/FunctionsBook/Edit/EditBook.php';

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
                        $EndTime = time()+3600;
                        $UsersINI->addParam($LoginClient, "EndTime", $EndTime);
                        $return = 'Good|Auth';
                    
                    } elseif($resA == 'Error|Password') {
                        $return = 'Error|UserHaveAccButPasswordNotCorrect';
                    }
            $explode = explode('|',$return);
            $array = ["Result" => $explode[0],"Param" => $explode[1]];
            $JasonRESULT = (json_encode($array));
            
            } else {
                $array = ["Result" => 'Error',"Param" => 'Incorrect',];
                $JasonRESULT = (json_encode($array));
            }
            
        break;
/////////////////////////////////////////////////////////////////////////////////////  

        case 'DeAuth':
            $LoginClient = $_POST["Login"];
            $PasswordClient = $_POST["Password"];
            
            if($LoginClient != NULL and $PasswordClient != NULL){
            
                $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
                $ResultTestAuth = explode('|',$ResultTestAuth); 
                
                if($ResultTestAuth[0] != 'Error'){
                    
                    $return = DeAuth($LoginClient, $UsersINI);
                    $explode = explode('|',$return);
                    $array = ["Result" => $explode[0],"Param" => $explode[1]];
                    
                } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
                
            } else $array = ["Result" => 'Error',"Param" => 'Incorrect'];
            
        break;
/////////////////////////////////////////////////////////////////////////////////////

        case 'BookDelete':
            
            $LoginClient = $_POST["Login"];
            $PasswordClient = $_POST["Password"];
            $BookId = $_POST["BookId"];
            
            if($LoginClient != NULL and $PasswordClient != NULL and $BookId != NULL){
            
                $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
                $ResultTestAuth = explode('|',$ResultTestAuth); 
                
                if($ResultTestAuth[0] != 'Error'){
                    
                    $return = BookDelete($LoginClient, $BookId, $BooksINI);
                    $explode = explode('|',$return);
                    $array = ["Result" => $explode[0],"Param" => $explode[1]];
                    
                } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
                
            } else $array = ["Result" => 'Error',"Param" => 'Incorrect'];
            
        break;
/////////////////////////////////////////////////////////////////////////////////////        
    
         case 'BookAdd':
             
            $LoginClient = $_POST["Login"];
            $PasswordClient = $_POST["Password"];
            $BookName = $_POST["BookName"];
            $BookAuthor = $_POST["BookAuthor"];
            $BookYear = $_POST["BookYear"];
            
            if($LoginClient != NULL and $PasswordClient != NULL and $BookName != NULL and $BookAuthor != NULL and $BookYear != NULL){
            
                $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
                $ResultTestAuth = explode('|',$ResultTestAuth); 
                
                if($ResultTestAuth[0] != 'Error'){
                    
                    $return = BookAdd($LoginClient,$BookName,$BookAuthor,$BookYear,$BooksINI);
                    $explode = explode('|',$return);
                    $array = ["Result" => $explode[0],"Param" => $explode[1]];
                    
                } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
                
            } else $array = ["Result" => 'Error',"Param" => 'Incorrect'];
            
        break;
/////////////////////////////////////////////////////////////////////////////////////       
        
      case 'GetAllBooks':  
            $LoginClient = $_POST["Login"];
            $PasswordClient = $_POST["Password"];
        
            if($LoginClient != NULL and $PasswordClient != NULL){
            
                $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
                $ResultTestAuth = explode('|',$ResultTestAuth); 
                
                if($ResultTestAuth[0] != 'Error'){
                    
                    $booksArray = GetAllBooks($LoginClient, $BooksINI);
                    $array = ["Result" => 'Good', "Books" => $booksArray];
                    
                } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
                
            } else $array = ["Result" => 'Error',"Param" => 'Incorrect'];

        break; 
/////////////////////////////////////////////////////////////////////////////////////
        case 'GetBook':   
            $LoginClient = $_POST["Login"];
            $PasswordClient = $_POST["Password"];
            $BookId = $_POST["BookId"];
            
            if($LoginClient != NULL and $PasswordClient != NULL and $BookId != NULL){
            
                $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
                $ResultTestAuth = explode('|',$ResultTestAuth); 
                
                if($ResultTestAuth[0] != 'Error'){
                    $booksArray = GetBook($BookId, $BooksINI);
                    $array = ["Result" => 'Good', "Book" => $booksArray];
                    
                } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
                
            } else $array = ["Result" => 'Error',"Param" => 'Incorrect'];

        break;  
/////////////////////////////////////////////////////////////////////////////////////
        case 'EditBook':

            $LoginClient = $_POST["Login"];
            $PasswordClient = $_POST["Password"];
            $BookId = $_POST["BookId"];
            $BookName = $_POST["BookName"];
            $BookAuthor = $_POST["BookAuthor"];
            $BookYear = $_POST["BookYear"];
            
            if($LoginClient != NULL and $PasswordClient != NULL and $BookId != NULL and $BookName != NULL and $BookAuthor != NULL and $BookYear != NULL){
                
                $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
                $ResultTestAuth = explode('|',$ResultTestAuth); 
                
                if($ResultTestAuth[0] != 'Error'){
                    $return = EditBook($LoginClient,$BookId,$BookName,$BookAuthor,$BookYear,$BooksINI);
                    $explode = explode('|',$return);
                    $array = ["Result" => $explode[0],"Param" => $explode[1]];
                    
                } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
                
            } else $array = ["Result" => 'Error',"Param" => 'Incorrect'];

        break;        
/////////////////////////////////////////////////////////////////////////////////////  

         case 'GetUserBooks':  
            $LoginClient = $_POST["Login"];
            $PasswordClient = $_POST["Password"];
        
            if($LoginClient != NULL and $PasswordClient != NULL){
            
                $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
                $ResultTestAuth = explode('|',$ResultTestAuth); 
                
                if($ResultTestAuth[0] != 'Error'){
                    
                    $booksArray = GetUserBooks($LoginClient, $BooksINI);
                    $array = ["Result" => 'Good', "Books" => $booksArray];
                    
                } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
                
            } else $array = ["Result" => 'Error',"Param" => 'Incorrect'];

        break;
/////////////////////////////////////////////////////////////////////////////////////
        default:
            $array = ["Result" => 'Error',"Param" => 'Request'];
/////////////////////////////////////////////////////////////////////////////////////
    }
    
    $BooksINI->save();
    $UsersINI->save();
    
    $JasonRESULT = (json_encode($array));
    echo($JasonRESULT);
/////////////////////////////////////////////////////////////////////////////////////    
?>
