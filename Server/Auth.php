<?php
    function Auth($LoginClient,$PasswordClient,$UsersINI){
        $LoginServer = $UsersINI->read()[$LoginClient];
        if($LoginServer != NULL){
            $PasswordServer = $UsersINI->read()[$LoginClient]['Password'];
            if($PasswordClient == $PasswordServer){
                $return = 'Good|Auth';
            }else{
                $return = 'Error|Password';
            }
        }else{
            $return = 'Error|Login';
        }
    return $return;
    }
?>