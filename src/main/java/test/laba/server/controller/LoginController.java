package test.laba.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import rawhttp.core.EagerHttpRequest;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.EagerBodyReader;
import rawhttp.core.body.StringBody;
import test.laba.server.dto.AuthenticationRequest;
import test.laba.server.dto.AuthenticationResponse;
import test.laba.server.service.auth.AuthenticationService;
import test.laba.server.util.AppConfig;
import test.laba.server.util.HttpManager;
import test.laba.server.util.Markers;
import test.laba.server.util.MessageContent;

@RequiredArgsConstructor
@Log4j2
public class LoginController implements Controller {
    private static final String PATH = AppConfig.getProperty("api.signin.url");
    public static final String CONTENT_TYPE = AppConfig.getProperty("api.content.type");
    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;
    private final RawHttp http;


    @Override
    public RawHttpResponse execute(EagerHttpRequest request) {
        try {
            Optional<EagerBodyReader> requestBody = request.getBody();
            if (requestBody.isEmpty()) {
                return HttpManager.getTemplate(http, 400);
            }
            AuthenticationRequest authenticationRequest = objectMapper.readValue(requestBody.get()
                                                                                            .asRawBytes(), AuthenticationRequest.class);
            log.info(Markers.TRANSACTION_MARKER, MessageContent.SIGNIN_REQUEST_MESSAGE, authenticationRequest.email());
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest);
            String body = objectMapper.writeValueAsString(authenticationResponse);
            return HttpManager.getTemplate(http, 200)
                              .withBody(new StringBody(body, CONTENT_TYPE));
        } catch (IOException e) {
            return HttpManager.getTemplate(http, 400)
                              .withBody(new StringBody(e.getMessage(), CONTENT_TYPE));
        }
    }

    @Override
    public boolean contains(Command command) {
        return command.command()
                      .equals(PATH) && command.method()
                                              .equals("POST");
    }
}
