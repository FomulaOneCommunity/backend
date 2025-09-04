package com.f1community.backend.util;

import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;

public class TimeZoneUtil {

    /**
     * 국가 코드 (예: "KR")로 타임존 반환
     */
    public static String getTimeZoneByCountryCode(String countryCode) {
        if (countryCode == null || countryCode.isBlank()) return "UTC";

        ULocale locale = new ULocale("", countryCode.toUpperCase());
        String[] timeZones = TimeZone.getAvailableIDs(locale.getCountry());

        return timeZones.length > 0 ? timeZones[0] : "UTC";
    }

    private TimeZoneUtil() {}
}
