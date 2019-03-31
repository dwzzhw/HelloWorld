import 'package:lib_flutter/utils/date_format.dart';

class DateUtil {
  static const int HOUR_IN_MILL_SECONDS = 60 * 60 * 1000;
  static const int MINUTE_IN_MILL_SECONDS = 60 * 1000;
  static const int DAY_IN_MILL_SECONDS = 24 * HOUR_IN_MILL_SECONDS;

  static String getDateHourMinutePart(String dateStr) {
    return parseDateStr(dateStr, ['HH', ':', 'nn']);
  }

  static String parseDateStr(String oriDateStr, List<String> targetFormat) {
    DateTime dateTime = DateTime.tryParse(oriDateStr);

    if (dateTime != null && targetFormat != null) {
      return formatDate(dateTime, targetFormat);
    } else {
      return oriDateStr;
    }
  }
}
