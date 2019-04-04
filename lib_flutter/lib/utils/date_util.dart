import 'package:lib_flutter/utils/date_format.dart';

class DateUtil {
  static const int HOUR_IN_MILL_SECONDS = 60 * 60 * 1000;
  static const int MINUTE_IN_MILL_SECONDS = 60 * 1000;
  static const int DAY_IN_MILL_SECONDS = 24 * HOUR_IN_MILL_SECONDS;

  static const List<String> FORMAT_STR_MONTH_DAY_HOUR_MINUTE = [
    'mm',
    '月',
    'dd',
    '日 ',
    'HH',
    ':',
    'nn'
  ];

  static String getDateHourMinutePart(String dateStr) {
    return parseDateStr(dateStr, FORMAT_STR_MONTH_DAY_HOUR_MINUTE);
  }

  static String getDateMonthDayHourMinPart(String dateStr) {
    return parseDateStr(dateStr, ['HH', ':', 'nn']);
  }

  static String getFormattedStrFromDateStr(
      String dateStr, List<String> targetFormat) {
    return parseDateStr(dateStr, targetFormat);
  }

  static String getFormattedStrFromDateObj(
      DateTime dateObj, List<String> targetFormat) {
    String result;
    if (dateObj != null) {
      result = formatDate(dateObj, targetFormat);
    }
    return result;
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
