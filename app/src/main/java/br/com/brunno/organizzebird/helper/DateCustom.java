package br.com.brunno.organizzebird.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String dataAtual(){
        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(data);
    }

    //Método que recebe data e devolve somente numeros
    public static String mesAnoDataEscolhida(String data){
        String[] retornoData = data.split("/");
        String mes = retornoData[1];
        String ano = retornoData[2];
        String mesAno = mes + ano;
        return mesAno;
    }
}
