// Функция получения всех книг всех пользователей

<?php
    function GetAllBooks($LoginClient, $BooksINI){
        
        $books_array = $BooksINI->read();
        $i=0;
        foreach( $books_array as $key => $value){
            if($key != 'Count') {
                $result[$i]['Id'] = $value['Id'];
                $result[$i]['Name'] = $value['Name'];
                $result[$i]['WhoAdded'] = $value['WhoAdded'];
                $i++;
            }
        }
       
    return $result; 
        
   }
?>
