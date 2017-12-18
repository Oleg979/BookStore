<?php
    function GetBook($BookId, $BooksINI){
        
        $books_array = $BooksINI->read();
        
        foreach( $books_array as $key => $value){
            if($key == $BookId) {
                $result =  $value;  
            }
        }
        
    return $result;    
   }
?>