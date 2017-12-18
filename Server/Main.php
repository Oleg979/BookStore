<?php
//Подключаем все используемые функции и классы.
/////////////////////////////////////////////////////////////////////////////////////
//Класс для работы с ini.
require 'iniClass.php';

//Функции, ктоторые не описаны в Swich, но используются в Main.php более четабильного вида.
require 'Functions/FunctionsMainHelp/Auth.php';
require 'Functions/FunctionsMainHelp/AuthTest.php';

//Функции регистарии/авторизации/деавторизации (P.S. функция Register.php включает в себя и автризацию и регистрацию.).
require 'Functions/FunctionsAuth/Register.php';
require 'Functions/FunctionsAuth/DeAuth.php';

//Функции работы с книгами.
require 'Functions/FunctionsBook/Add/BookAdd.php';
require 'Functions/FunctionsBook/Del/BookDelete.php';
require 'Functions/FunctionsBook/Get/GetAllBooks.php';
require 'Functions/FunctionsBook/Get/GetBook.php';
require 'Functions/FunctionsBook/Get/GetUserBooks.php';
require 'Functions/FunctionsBook/Edit/EditBook.php';

//Подключение "Базы даннцых", так требует iniClass.php.
$UsersINI = new iniFile("Data/Users.ini");
$BooksINI = new iniFile("Data/Books.ini");
/////////////////////////////////////////////////////////////////////////////////////

//Принимаем Post запрос и смотрим к какой функции он относится.
$func = $_POST["Function"];
//Достаем из запроса данные о логине и пароле, они приходят при любом запросе.
$LoginClient = $_POST["Login"];
$PasswordClient = $_POST["Password"];
//Если логин и пароль додходит по критериям идем дальше(Пароль в формате md5, поэтому его проверяем только на наличие.
if($PasswordClient == NULL or !preg_match('/^[a-z]+([-_]?[a-z0-9]+){0,2}$/i',$LoginClient)){ 
    $array = ["Result" => 'Error',"Param" => 'Incorrect',];
    exit($JasonRESULT = (json_encode($array)));
}

                


    switch($func) {
        
/////////////////////////////////////////////////////////////////////////////////////  
//Авторизация.
        case 'Auth':
            //Выполняем авторизацию без проверки EndTime.        
            $resA = Auth($LoginClient,$PasswordClient,$UsersINI);
                //При получении ошибки 'Error|Login' мы понимаем, что человека нет  базе и регистрируем его по данным которые он прислал.    
                if($resA == 'Error|Login') {
                    //Псевдо ссесия. Накидываем час времени от прихода запоса, все последующие функции проверяют этот параметр.
                    $EndTime = time()+3600;
                    $UsersINI->addParam($LoginClient, "EndTime", $EndTime);
                    $return = Register($LoginClient,$PasswordClient,$UsersINI);
                //При ответе "Good|Auth", понимаем что пользователь порошел по следующим критериям 1.Есть в базе 2.Пароль совпадает.   
                } elseif($resA == 'Good|Auth') {
                    $EndTime = time()+3600;
                    $UsersINI->addParam($LoginClient, "EndTime", $EndTime);
                    $return = 'Good|Auth';
                //При ошибке пароля, просто выводит ошибку.   
                } elseif($resA == 'Error|Password') {
                    $return = 'Error|UserHaveAccButPasswordNotCorrect';
                }
        //Т.К. любой ответ в данной функции выглядит в формате (Error/Good)|(Param), разбиваем его на части записываем в array.        
        $explode = explode('|',$return);
        $array = ["Result" => $explode[0],"Param" => $explode[1]];
            
        break;
/////////////////////////////////////////////////////////////////////////////////////  
//Деавторизация. 
//(P.S. Все последущие функции начиная с этой выглядят по одному образцу, за исключением ответа. Иногда он приходит в
//формате array и его не нужно explode(экспоудить), иногда приходит в формате описанном в строке 64.).
        case 'DeAuth':
            //Выполняем авторизацию с проверкой EndTime.
            $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
            $ResultTestAuth = explode('|',$ResultTestAuth); 
            //Т.к. Функция AuthTest включает в себя еще и функцию Auth, то всевозможных ошибок много(3), поэтому отлавливаем их, explode
            //(эксполудим), записываем в array и просто отсылаем. Иначе выкидываем пользователя.
            if($ResultTestAuth[0] != 'Error'){
                    
                $return = DeAuth($LoginClient, $UsersINI);
                $explode = explode('|',$return);
                $array = ["Result" => $explode[0],"Param" => $explode[1]];
                    
            } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
            
        break;
/////////////////////////////////////////////////////////////////////////////////////
//Удалени книги.
//(P.S. отличие от образца выше, лишь в доп проверке, на валидность $BookId.).
        case 'BookDelete':

            $BookId = $_POST["BookId"];
            
            if($BookId != NULL){
            
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
//Добавление книги.    
         case 'BookAdd':

            $BookName = $_POST["BookName"];
            $BookAuthor = $_POST["BookAuthor"];
            $BookYear = $_POST["BookYear"];
            
            if($BookName != NULL and $BookAuthor != NULL and $BookYear != NULL){
            
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
//Получаем список всех книг.       
        case 'GetAllBooks':  

            $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
            $ResultTestAuth = explode('|',$ResultTestAuth); 
                
            if($ResultTestAuth[0] != 'Error'){
                    
                $booksArray = GetAllBooks($LoginClient, $BooksINI);
                $array = ["Result" => 'Good', "Books" => $booksArray];
                    
            } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];

        break; 
/////////////////////////////////////////////////////////////////////////////////////
//Информаця об определенной книге.
        case 'GetBook':   

            $BookId = $_POST["BookId"];
            
            if($BookId != NULL){
            
                $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
                $ResultTestAuth = explode('|',$ResultTestAuth); 
                
                if($ResultTestAuth[0] != 'Error'){
                    $booksArray = GetBook($BookId, $BooksINI);
                    $array = ["Result" => 'Good', "Book" => $booksArray];
                    
                } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];
                
            } else $array = ["Result" => 'Error',"Param" => 'Incorrect'];

        break;  
/////////////////////////////////////////////////////////////////////////////////////
//Редактирование  книги.
        case 'EditBook':

            $BookId = $_POST["BookId"];
            $BookName = $_POST["BookName"];
            $BookAuthor = $_POST["BookAuthor"];
            $BookYear = $_POST["BookYear"];
            
            if($BookId != NULL and $BookName != NULL and $BookAuthor != NULL and $BookYear != NULL){
                
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
//Получаем список всех книг пльзователя.  
         case 'GetUserBooks':  
            
            $ResultTestAuth = AuthTest($LoginClient,$PasswordClient,$UsersINI);  
            $ResultTestAuth = explode('|',$ResultTestAuth); 
                
            if($ResultTestAuth[0] != 'Error'){
                    
                $booksArray = GetUserBooks($LoginClient, $BooksINI);
                $array = ["Result" => 'Good', "Books" => $booksArray];
                    
            } else $array = ["Result" => $ResultTestAuth[0],"Param" => $ResultTestAuth[1]];

        break;
/////////////////////////////////////////////////////////////////////////////////////
//Если кривой запрос.
        default:
            $array = ["Result" => 'Error',"Param" => 'Request'];
/////////////////////////////////////////////////////////////////////////////////////
    }
//Сохраняем все изменения сделанные в "Базе данных", так требует iniClass.php.
    $BooksINI->save();
    $UsersINI->save();
//Кидаем все в Jason и отправляем ответ.
    $JasonRESULT = (json_encode($array));

    echo($JasonRESULT);
/////////////////////////////////////////////////////////////////////////////////////    
?>
