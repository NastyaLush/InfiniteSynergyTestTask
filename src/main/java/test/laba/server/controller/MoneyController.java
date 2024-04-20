package test.laba.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import rawhttp.core.EagerHttpRequest;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.EagerBodyReader;
import rawhttp.core.body.StringBody;
import test.laba.server.dto.UserSendMoney;
import test.laba.server.service.MoneyService;
import test.laba.server.service.auth.AuthenticationService;
import test.laba.server.service.auth.JwtService;
import test.laba.server.util.AppConfig;
import test.laba.server.util.HttpManager;
import test.laba.server.util.Markers;
import test.laba.server.util.MessageContent;

@Log4j2
@RequiredArgsConstructor
public class MoneyController implements Controller {
    private static final String PATH = AppConfig.getProperty("api.money.url");
    public static final String CONTENT_TYPE = AppConfig.getProperty("api.content.type");
    public static final String INCORRECT_EMAIL = "The email is incorrect";
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final MoneyService moneyService;
    private final ObjectMapper objectMapper;
    private final RawHttp http;


    @Override
    public RawHttpResponse execute(EagerHttpRequest request) {
        Optional<String> authorization = request.getHeaders()
                                                .getFirst(MessageContent.AUTHORIZATION_HEADER);
        if (authorization.isEmpty() || !authorization.get()
                                                     .startsWith(MessageContent.TOKEN_TYPE)) {
            return HttpManager.getTemplate(http, 401)
                              .withBody(new StringBody(MessageContent.AUTHORIZATION_HEADER_MISSING));
        }

        String email = getEmail(authorization.get());
        if (!authenticationService.authenticate(email)) {
            return HttpManager.getTemplate(http, 403)
                              .withBody(new StringBody(INCORRECT_EMAIL));
        }
        switch (request.getMethod()) {
            case "POST":
                return handlePostRequest(request, email);

            case "GET":
                return handleGetRequest(email);
            default:
                return HttpManager.getTemplate(http, 405);

        }
    }

    private String getEmail(String authorization) {
        return jwtService.extractUsername(authorization
                .split(MessageContent.TOKEN_TYPE)[1]);
    }

    private RawHttpResponse handlePostRequest(EagerHttpRequest request, String email) {
        Optional<EagerBodyReader> requestBody = request.getBody();
        if (requestBody.isEmpty()) {
            return HttpManager.getTemplate(http, 400);
        }
        try {
            UserSendMoney body = objectMapper.readValue(requestBody
                    .get()
                    .asRawBytes(), UserSendMoney.class);
            log.info(Markers.TRANSACTION_MARKER, MessageContent.SEND_MONEY_LOG, email, body.toEmail(), body.amount());
            moneyService.send(email, body.toEmail(), body.amount());
            return HttpManager.getTemplate(http, 200);
        } catch (Exception e) {
            return HttpManager.getTemplate(http, 400)
                              .withBody(new StringBody(e.getMessage(), CONTENT_TYPE));
        }
    }

    private RawHttpResponse handleGetRequest(String email) {
        String amount = String.valueOf(moneyService.get(email));
        log.info(Markers.TRANSACTION_MARKER, MessageContent.GET_MONEY_LOG, email, amount);
        return HttpManager.getTemplate(http, 200)
                          .withBody(new StringBody(amount, CONTENT_TYPE));
    }

    @Override
    public boolean contains(Command command) {
        return command.command()
                      .equals(PATH) && command.method()
                                              .equals("POST")
                || command.command()
                          .equals(PATH) && command.method()
                                                  .equals("GET");
    }
}
