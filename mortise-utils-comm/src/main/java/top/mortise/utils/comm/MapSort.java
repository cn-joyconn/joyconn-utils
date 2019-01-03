package top.mortise.utils.comm;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eric.Zhang on 2017/1/10.
 */
public class MapSort {
    private static final String INT_REGEX="^\\d+";
    private static int getInt(String str) {
        int i = 0;
        try {
            Pattern p = Pattern.compile(INT_REGEX);
            Matcher m = p.matcher(str);
            if (m.find()) {
                i = Integer.valueOf(m.group());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return i;
    }
    public static Map<String, String> sortMapByKey(Map<String, String> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                int intKey1 = 0, intKey2 = 0;
                try {
                    intKey1 = getInt(key1);
                    intKey2 = getInt(key2);
                } catch (Exception e) {
                    intKey1 = 0;
                    intKey2 = 0;
                }
                return intKey1 - intKey2;
            }});
        sortedMap.putAll(oriMap);
        return sortedMap;
    }

    public Map<String, String> sortMapByValue(Map<String, String> oriMap) {
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        if (oriMap != null && !oriMap.isEmpty()) {
            List<Map.Entry<String, String>> entryList = new ArrayList<Map.Entry<String, String>>(oriMap.entrySet());
            Collections.sort(entryList,
                    new Comparator<Map.Entry<String, String>>() {
                        @Override
                        public int compare(Map.Entry<String, String> entry1,
                                           Map.Entry<String, String> entry2) {
                            int value1 = 0, value2 = 0;
                            try {
                                value1 = getInt(entry1.getValue());
                                value2 = getInt(entry2.getValue());
                            } catch (NumberFormatException e) {
                                value1 = 0;
                                value2 = 0;
                            }
                            return value2 - value1;
                        }
                    });
            Iterator<Map.Entry<String, String>> iter = entryList.iterator();
            Map.Entry<String, String> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
        }
        return sortedMap;
    }
}
