// Вспомогательный класс для хранения глобальных переменных

package sstu_team.book;

import okhttp3.MediaType;

public class Metadata {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public  static String url = "http://gt99.xyz/Book/Main.php";

    public static String login;
    public  static  String password;

    public static String currentResponse;
    public static String currentNum;
    public static String currentId;
    public static String currentEditor;
    public static String currentTime;

    public static int count;
    public static int type;

    public static String names[];
    public static String ids[];
    public  static String editors[];
    public  static String times[];

    public static String EditName;
    public static String EditAuthor;
    public static String EditYear;
}
