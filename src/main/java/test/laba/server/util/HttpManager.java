package test.laba.server.util;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;

public class HttpManager {
    private HttpManager() {
    }

    public static RawHttpResponse getTemplate(RawHttp http, Integer httpStatus) {
        String stringBuilder = "HTTP/1.1 " + httpStatus
                + " "
                + getHttpMessage(httpStatus)
                + "\r\n"
                + "Content-Type: text/html; charset=utf-8\r\n";

        return http.parseResponse(stringBuilder);
    }

    private static String getHttpMessage(Integer httpStatus) {
        return switch (httpStatus) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default -> "";
        };
    }
}
