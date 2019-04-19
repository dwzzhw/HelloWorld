package com.loading.modules.interfaces.download;

import java.util.List;
import java.util.Map;

public interface IQueryFileInfoListener {
    void onQueryFileInfoDone(String url, boolean success, Map<String, List<String>> headerFieldsMap);
}
