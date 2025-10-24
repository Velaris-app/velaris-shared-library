package com.velaris.shared.util;

import lombok.experimental.UtilityClass;
import ua_parser.Client;
import ua_parser.Parser;

@UtilityClass
public class UserAgentUtil {

    private static final Parser uaParser;

    static {
        uaParser = new Parser();
    }

    public static String parseDeviceAndOS(String userAgentString) {
        if (userAgentString == null || userAgentString.isBlank()) return "unknown";

        Client client = uaParser.parse(userAgentString);
        String device = client.device.family != null ? client.device.family : "unknown device";
        String os = client.os.family != null ? client.os.family : "unknown OS";

        if (client.os.major != null) {
            os += " " + client.os.major;
            if (client.os.minor != null) os += "." + client.os.minor;
            if (client.os.patch != null) os += "." + client.os.patch;
        }

        return device + " / " + os;
    }

    public static String getBrowser(String userAgentString) {
        if (userAgentString == null || userAgentString.isBlank()) return "unknown browser";

        Client client = uaParser.parse(userAgentString);
        String browser = client.userAgent.family != null ? client.userAgent.family : "unknown";
        if (client.userAgent.major != null) {
            browser += " " + client.userAgent.major;
            if (client.userAgent.minor != null) browser += "." + client.userAgent.minor;
        }
        return browser;
    }
}