package com.example.jose.carpool;

/**
 * Created by jose on 7/28/17.
 */

public final class emailUtils {


    private emailUtils(){

    }
    public static boolean correoPucp(String email){

        String pucpedu = "@pucp.edu.pe";
        String pucp = "@pucp.pe";


        if(email.toLowerCase().contains(pucp) || email.toLowerCase().contains(pucpedu)){
            return true;
        }

        return false;

    }

}
