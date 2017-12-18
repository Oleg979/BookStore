// Функция добавления новой книги
<?php
    function BookDelete($LoginClient,$BookId,$BooksINI){
        
        $WhoAdded = $BooksINI->read()[$BookId]['WhoAdded'];
        
        if($LoginClient == $WhoAdded){
            $BooksINI->deleteSection($BookId);
            $result = "Good|Deleted";
            
        } else $result = "Error|NotYouAddedBook";
        
    return $result;
    }
?>
