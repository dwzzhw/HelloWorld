class DateUtil {
  static const int HOUR_IN_MILL_SECONDS = 60 * 60 * 1000;
  static const int MINUTE_IN_MILL_SECONDS = 60 * 1000;
  static const int DAY_IN_MILL_SECONDS = 24 * HOUR_IN_MILL_SECONDS;

  static String getDateStrPartII(String dateStr) {
    List<String> list = dateStr.split(" ");
    if (list.length > 1) {
      return list[1].substring(0, 5);
    } else {
      return dateStr;
    }
  }
}
