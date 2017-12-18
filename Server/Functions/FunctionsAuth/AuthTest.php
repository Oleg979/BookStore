<?php
function AuthTest($LoginClient,$PasswordClient,$UsersINI){
    
    
    $resA = Auth($LoginClient,$PasswordClient,$UsersINI);

        if($resA == 'Good|Auth') {
            $EndTime = $UsersINI->read()[$LoginClient]['EndTime'];
            $EndTime = time()-$EndTime;
            
                if($EndTime<0) {
                    $EndTime = time()+3600;
                    $UsersINI->addParam($LoginClient, "EndTime", $EndTime);
                    $return = 'Good|NextStep';
                
                } else $return = 'Error|EndTime';
            
        } else $return = $resA;

    return $return;
    
    
}
?>