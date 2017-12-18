<?php
    function DeAuth($LoginClient,$UsersINI){
        $UsersINI->addParam($LoginClient, "EndTime", '');
        $return = 'Good|DeAuth';
    return $return;
    }
?>