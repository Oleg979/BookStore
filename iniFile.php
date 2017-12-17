<?php
/**
   * Класс для чтения и записи ini файла
   */ 
  
   class iniFile{
      /**
       * (string) Путь и имя ini файла
       */ 
      public $ini_file;
      /**
       * (string) Весь ini файл в виде строки (file_get_contents())
       */ 
      public $ini_data;
      /**
       *  (array) Содержимое ini файла после работы parse_ini_file(file, true)
       */ 
      public $ini_array = array();
      
      /**
       * Для превью ini массива (перед сохранением)
       */ 
       private $preview;
      
      const WRONG_EXT = 'У файла должно быть расширение *.ini';
      const NOT_FOUND = 'Указанный файл не найден';
      const WRONG_READ = 'Не возможно прочитать указанный файл';
      const ERROR_SAVE = 'Не могу сохранить файл';
      /**
       * @param string Путь/имя_файла
       * @return void
       */ 
      public function __construct($path_to_ini_file){
         $this->ini_file = $path_to_ini_file;        
         
         if(!preg_match("#\.ini$#", $this->ini_file)){
             $this->Exept(self::WRONG_EXT);       
         }
         else{
            if(file_exists($this->ini_file)){
                $this->ini_array = parse_ini_file($this->ini_file,true);
                $this->ini_data = file_get_contents($this->ini_file);
            }
         }    
      }
      
      /**
       * Метод выбрасывает исключения в случае ошибок
       * @param string - текст исключения
       */ 
      private function Exept($text){
          throw new Exception($text);  
      }
      
      /**
       * Метод для создания пустого ini файла
       * @param string Путь/имя_файла
       * @return bool
       */ 
      public function create(){ 
          return file_put_contents($this->ini_file,'');
      }
      /**
       * Метод вернёт содержимое ini файла в виде строки
       * @return string
       */ 
      public function readStringIni(){
          if(!file_exists($this->ini_file))
              $this->Exept(self::NOT_FOUND);
          elseif(!is_readable($this->ini_file))
              $this->Exept(self::WRONG_READ);
          else{
             return $this->ini_data;
          }              
      }
      /**
       * Метод возвращает распарсенный ini файл
       * @return array
       */ 
      public function read(){
          if(!file_exists($this->ini_file))
              $this->Exept(self::NOT_FOUND);
          elseif(!is_readable($this->ini_file))
              $this->Exept(self::WRONG_READ);
          else{
             return $this->ini_array;
          }    
      }   
      
      /**
       *  Метод добавляет секцию в файл
       *  @param string - наименовании секции
       *  @return void
       */    
       public function addSection($namesection){
            file_put_contents($this->ini_file, '['.$namesection.']'.PHP_EOL);
       }
       
      /**
       *  Метод добавляет параметр в секцию
       *  @param string - наименовании секции в которую добавляется параметр
       *  @param string - наименование параметра
       *  @param mixid - значение параметра
       *  @return void
       */    
       public function addParam($namesection, $nameparam, $value){
            $this->ini_array[$namesection][$nameparam]= $value;
       }
       
       
       /**
        * Метод удаляет целую секцию с параметрами 
        * @param string наиманование секции
        * @param bool  
        *  true - удалить 
        *  false - удалить только из массива - возможность предосмотра метод preview()
        * @return void
        */ 
        public function deleteSection($namesection, $drop=true){
            
             if(is_array($this->ini_array)){
                foreach($this->ini_array as $gen=>$param){
                    if($gen != $namesection){
                        foreach($param as $p=>$v){  
                             $change[$gen][$p] = $v;
                        }
                    }    
                }
                
                $this->preview = $change;
             }
             
             if($drop === true){
                 $this->ini_array = $this->preview;
                 $this->save();
             }
        }

       /**
        * Метод удаляет параметр из указанной секции 
        * @param string наиманование секции
        * @param string наименование параметра
        * @param bool  
        *  true - удалить 
        *  false - удалить только из массива - возможность предосмотра метод preview()
        * @return void
        */ 
        public function deleteParam($namesection, $paramname, $drop=true){
             if(is_array($this->ini_array)){
                foreach($this->ini_array as $gen=>$param){

                    if($gen == $namesection){
                        foreach($param as $p=>$v){
                            if($p != $paramname)
                                 $change[$gen][$p] = $v; 
                        }
                    }    
                }
                
                $this->preview = $change;
             }
             
             if($drop === true){
                 $this->ini_array = $this->preview;
                 $this->save();
             }           
        }  
        
        /**
         * Метод для смены имени секции
         * @param string старое имя
         * @param string новое имя
         * @param bool  
         *  true - удалить 
         *  false - удалить только из массива - возможность предосмотра метод preview()
         * 
         *  @return void   
         */
         public function changeSectionName($oldname, $newname,$change=true){
             if(is_array($this->ini_array)){
                foreach($this->ini_array as $gen=>$param){
                    if($gen == $oldname)
                        $gen = $newname;

                    foreach($param as $p=>$v){
                         $change[$gen][$p] = $v;
                    }

                }
                
                $this->preview = $change;
             }
             
             if($change === true){
                 $this->ini_array = $this->preview;
                 $this->save();
             }            
         } 

        /**
         * Метод для смены имени параметра секции
         * @param string имя секции
         * @param string старое имя
         * @param string новое имя
         * @param bool  
         *  true - удалить 
         *  false - удалить только из массива - возможность предосмотра метод preview()
         * 
         *  @return void   
         */
         public function changeParamName($namesection, $oldname, $newname,$change=true){
            
             if(is_array($this->ini_array)){
                foreach($this->ini_array as $gen=>$param){
                    if($gen == $namesection){
                        foreach($param as $p=>$v){
                             if($p == $oldname)
                                 $p = $newname;
                             
                             $change[$gen][$p] = $v;
                        }
                    }
                }

                $this->preview = $change;
             }
             
             if($change === true){
                 $this->ini_array = $this->preview;
                 $this->save();
             }            
         } 
        
        /**
         * Метод для изменения значения параметра конкретной секции
         * @param string имя секции
         * @param string имя параметра
         * @param string новое значение
         * @param bool  
         *  true - удалить 
         *  false - удалить только из массива - возможность предосмотра метод preview()
         *   
         * @return void
         */       
        public function changeParamValue($namesection, $nameparam, $newvalue,$set=true){
             if(is_array($this->ini_array)){
                foreach($this->ini_array as $gen=>$param){
                    if($gen == $namesection){
                        foreach($param as $p=>$v){
                             if($p == $nameparam)
                                 $v = $newvalue;
                             
                             $change[$gen][$p] = $v;
                        }
                    }
                }

                $this->preview = $change;
             }
             
             if($set === true){
                 $this->ini_array = $this->preview;
                 $this->save();
             }              
        }
        
        
        /**
         * Метод для мониторинга изменений массива ini файла во время редактирования с помощью класса
         * @return array
         */ 
        public function preview(){
                return $this->preview;
        }
       
       /**
        * Метод сохраняет добавленные секции и параметры в файл
        * @return bool
        */        
        public function save(){
            
            if(is_array($this->ini_array)){
                $string =  '';
                foreach($this->ini_array as $gen=>$param){
                     $string .= '['.$gen.']'.PHP_EOL;

                     foreach($param as $p=>$v){
                        $string .= $p.'='.$v.PHP_EOL;
                     }
                }
                if(!file_put_contents($this->ini_file,$string)){
                   $this->Exept(self::ERROR_SAVE);
                   return false;
                }   
                          
            }
            
            return true;  
        }
   }
   
?>
