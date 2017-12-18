// Функция редактирования книги

<?php
    function EditBook($LoginClient,$BookId,$BookName,$BookAuthor,$BookYear,$BooksINI){
        $BooksINI->addParam($BookId, 'Name', $BookName);
        $BooksINI->addParam($BookId, 'Author', $BookAuthor);
        $BooksINI->addParam($BookId, 'Year', $BookYear);
    return "Good|Edited";
    }
?>
