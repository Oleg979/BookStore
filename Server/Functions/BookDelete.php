<?php
    function BookDelete($BookId,$BooksINI){
    
            $BooksINI->deleteSection($BookId);
            return "Good|Deleted";
            
    }
?>
