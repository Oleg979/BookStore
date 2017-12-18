// Функция добавления новой книги

<?php
    function BookAdd($LoginClient,$BookName,$BookAuthor,$BookYear,$BooksINI){
            $Max = $BooksINI->read()['Count']['Max'];
            $Max++;
            
            $BooksINI->addParam($Max, 'Name', $BookName);
            $BooksINI->addParam($Max, 'Author', $BookAuthor);
            $BooksINI->addParam($Max, 'Year', $BookYear);
            $BooksINI->addParam($Max, 'WhoAdded', $LoginClient);
            $BooksINI->addParam($Max, 'Id', $Max);
            $BooksINI->addParam('Count', 'Max', $Max);
            
            
            return "Good|Added";
    }
?>
