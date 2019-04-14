class MatchDetailSubTabInfo{
  static const String TAB_TYPE_PRE_POST_INFO = '1';
  static const String TAB_TYPE_IMG_TXT_LIVE = '2';
  static const String TAB_TYPE_STAT_DATA = '3';

  static String getSubTabTitle(String tabType){
    String title;
    switch(tabType){
      case TAB_TYPE_PRE_POST_INFO:
        title = "大家聊";
        break;
      case TAB_TYPE_IMG_TXT_LIVE:
        title = "赛况";
        break;
      case TAB_TYPE_STAT_DATA:
        title = "数据";
        break;
    }
    return title;
  }
}