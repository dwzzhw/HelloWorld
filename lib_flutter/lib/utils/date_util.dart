class DateUtil {
  static String getDateStrPartII(String dateStr) {
    List<String> list = dateStr.split(" ");
    if (list.length > 1) {
      return list[1].substring(0, 5);
    } else {
      return dateStr;
    }
  }
}
