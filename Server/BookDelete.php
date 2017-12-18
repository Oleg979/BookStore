<?php
    function BookDelete($LoginClient,$BookId,$BooksINI){
        
        $WhoAdded = $BooksINI->read()[$BookId]['WhoAdded'];
        
        if($LoginClient == $WhoAdded){
            $BooksINI->deleteSection($BookId);
            $return = "Good|Deleted";
            
        } else $return = "Error|NotYouAddBook";
        
    return $return;
    }
?>