<?php
    function Register($LoginClient,$PasswordClient,$UsersINI){
        $UsersINI->addParam($LoginClient, "Password", $PasswordClient);
        $return = 'Good|UserRegister';
    return $return;
    }
?>