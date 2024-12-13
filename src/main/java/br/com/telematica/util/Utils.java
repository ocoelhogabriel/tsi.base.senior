package br.com.telematica.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

public class Utils {
    
    private static DateFormat df_webhookFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    public static String padStart(String stringToFill, int sizeToFill, char charToFill) {
        if (stringToFill.length() >= sizeToFill)
            return stringToFill;
        String temp = stringToFill;
        for (int i = stringToFill.length(); i < sizeToFill; i++)
            temp = charToFill + temp;
        return temp;
    }
    
    public static String padEnd(String stringToFill, int sizeToFill, char charToFill) {
        if (stringToFill.length() >= sizeToFill)
            return stringToFill;
        String temp = stringToFill;
        for (int i = stringToFill.length(); i < sizeToFill; i++)
            temp += charToFill;
        return temp;
    }
    
    public static String[] splitStringIntoXStringSize(String str, Integer size) {
        return str.split(String.format("(?<=\\G.{%s})", size));
    }
    
    public static String formatDateToWebhookPattern(Date date) {
        return df_webhookFormat.format(date);
    }
    
    public static String decodeToBase64(String y) {
        String yh = "";
        byte[] x;
        x = Base64.getDecoder().decode(y);
        yh = byteArrayToString(x);
        yh = yh.toUpperCase();
        
        return yh;
    }
    
    public static String byteArrayToString(byte[] b) {
        StringBuilder hex = new StringBuilder(b.length * 2);
        for (byte bh : b)
            hex.append(String.format("%02x", bh));
        return hex.toString();
    }
    
    public static String encodeToBase64(String yh) {
        byte[] bytes = hexStringToByteArray(yh);
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    public static String addGmtToDateTime(String dataString, Integer gmt) {
        ZoneOffset offsetGMT = ZoneOffset.ofHours(gmt / 60);
        
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(dataString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        OffsetDateTime offsetDateTimeGMT = offsetDateTime.withOffsetSameInstant(offsetGMT);
        String formattedDateTime = offsetDateTimeGMT.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        
        return formattedDateTime;
    }
    
    public static String removeGmtToDateTime(String horarioComGMT, Integer gmtEmMinutos) {
        LocalDateTime dataAtual = LocalDateTime.parse(horarioComGMT, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        LocalDateTime dataSemOffset = dataAtual.minusMinutes(gmtEmMinutos);
        
        String formatoDesejado = dataSemOffset.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".000Z";
        
        return formatoDesejado;
    }
    
}
