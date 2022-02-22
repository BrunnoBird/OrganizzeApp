package br.com.brunno.organizzebird.helper;

import android.util.Base64;

public class Base64Custom {
    public static String codificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decodificarBase64(String textoCodificado){
            //Retornando uma string convertendo o texto decodificado pelo método
        return new String(Base64.decode(textoCodificado, Base64.DEFAULT));
    }
}
