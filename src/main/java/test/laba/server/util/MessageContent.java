package test.laba.server.util;

public class MessageContent {
    public static final String THE_METHOD_IS_NOT_ALLOWED = "the method is not allowed";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SIGNIN_REQUEST_MESSAGE = "new signin request by {}";
    public static final String TOKEN_TYPE = "Bearer ";
    public static final String AUTHORIZATION_HEADER_MISSING = "Authorization header is missing or incorrect";
    public static final String GET_MONEY_LOG = "new get money request by {} balance {}";
    public static final String SEND_MONEY_LOG = "new send money request by {} to {} amount {}";
    private MessageContent() {

    }
}
