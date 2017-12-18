<?php
    function GetUserBooks($LoginClient, $BooksINI){
     //$result = 'test';   
        $books_array = $BooksINI->read();
        $i=0;
        foreach( $books_array as $key => $value){
            if($key != 'Count' and $value['WhoAdded'] == $LoginClient) {
                $result[$i]['Id'] = $value['Id'];
                $result[$i]['Name'] = $value['Name'];
                $result[$i]['WhoAdded'] = $value['WhoAdded'];
                $i++;
            }
        }
        
        
        
    return $result;    
   }
?>