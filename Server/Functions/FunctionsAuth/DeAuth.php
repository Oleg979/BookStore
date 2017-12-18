// Функция деавторизации
<?php
    function DeAuth($LoginClient,$UsersINI){
        $UsersINI->addParam($LoginClient, "EndTime", '');
        $result = 'Good|DeAuth';
    return $result;
    }
?>
