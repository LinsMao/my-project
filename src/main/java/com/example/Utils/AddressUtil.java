package com.example.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressUtil {
    
    /**
     * 从地址中提取城市
     */
    public static String extractCity(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "未知城市";
        }
        
        // xx省xx市
        Pattern pattern1 = Pattern.compile("(?:.*?省)?(\\S+?市)");
        Matcher matcher1 = pattern1.matcher(address);
        
        if (matcher1.find()) {
            return matcher1.group(1);
        }
        
        //直辖市
        Pattern pattern2 = Pattern.compile("^(北京市|上海市|天津市|重庆市)");
        Matcher matcher2 = pattern2.matcher(address);
        
        if (matcher2.find()) {
            return matcher2.group(1);
        }
        
        // 如果都没匹配到，尝试提取省份
        Pattern provincePattern = Pattern.compile("(\\S+?省)");
        Matcher provinceMatcher = provincePattern.matcher(address);
        
        if (provinceMatcher.find()) {
            return provinceMatcher.group(1);
        }
        
        return "未知城市";
    }
}
