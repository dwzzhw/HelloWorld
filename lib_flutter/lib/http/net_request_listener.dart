abstract class NetRequestListener {
  void onDataComplete();
  void onDataError(int errorCode, String errorMsg);
}
