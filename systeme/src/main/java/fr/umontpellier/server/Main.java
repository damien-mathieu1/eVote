package fr.umontpellier.server;

import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        String heure = timeStamp.substring(11, 13);
        String minute = timeStamp.substring(14, 16);
        int minuteInt = Integer.parseInt(minute);
        if (minuteInt+15 >= 60) {
            int heureInt = Integer.parseInt(heure);
            heureInt++;
            heure = Integer.toString(heureInt);
            minuteInt = minuteInt+15-60;
            if (minuteInt < 10) {
                minute = "0" + Integer.toString(minuteInt);
            } else {
                minute = Integer.toString(minuteInt);
            }
        } else {
            minuteInt = minuteInt+15;
            minute = Integer.toString(minuteInt);
        }
        System.out.println(heure+":"+minute);
    }
}

