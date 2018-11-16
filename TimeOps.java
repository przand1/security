package przyrost1;

import java.util.Calendar;

public class TimeOps {

  public static int toMinutes(int hours,int minutes) {
    return hours*60 + minutes;
  }

  private static final int[] classTimes = {495,585,600,690,705,795,825,915,930,1020,1035,1125,1140,1230};

  public static int[] getClassTimes() {
    return classTimes;
  }

  public static boolean itsClassTime(int minutes) {
    boolean b = false;
    if (minutes >= classTimes[0] && minutes < classTimes[classTimes.length -1]) {
      for (int i = 0;i < classTimes.length -1 ; i++) {
        if (minutes < classTimes[i+1]) {
          b = b?false:true;
        }
      }
    }
    return b;
  }

  public static int getRemainingTime(int minutes) {
    if (minutes >= 1230) {
      return 1935 - minutes;
    }
    else if (minutes < 495) {
      return 495 - minutes;
    }
    else {
      int i = 0;
      while (minutes > classTimes[i]) {i++;}
      return classTimes[i] - minutes;
    }
  }

  public static String printInfo(int minutes) {
    String s = itsClassTime(minutes)?"zajęć.":"przerwy.";
    return String.valueOf(getRemainingTime(minutes)) + " minut do końca " + s;
  }

  public static String printCurrentTimeInfo() {
    Calendar c = Calendar.getInstance();
    return printInfo( toMinutes( c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) ) );
  }
  public static void main(String[] args) {
    System.out.println(printCurrentTimeInfo());
  }

}
