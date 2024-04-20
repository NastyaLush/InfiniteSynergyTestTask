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
import test.laba.server.dto.AuthenticationResponse;
import test.laba.server.dto.RegisterRequest;
import test.laba.server.service.auth.AuthenticationService;
import test.laba.server.util.HttpManager;
import test.laba.server.util.Markers;

@Log4j2
@RequiredArgsConstructor
public class RegisterController implements Controller {
    private static final String PATH = "/signup";
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

            RegisterRequest registerRequest = objectMapper.readValue(requestBody
                    .get()
                    .asRawBytes(), RegisterRequest.class);
            AuthenticationResponse authenticationResponse = authenticationService.register(registerRequest);
            log.info(Markers.TRANSACTION_MARKER, "new signup request by {}", registerRequest.email());
            String body = objectMapper.writeValueAsString(authenticationResponse);
            return HttpManager.getTemplate(http, 200)
                              .withBody(new StringBody(body, "application/json"));
        } catch (IOException e) {
            return HttpManager.getTemplate(http, 400)
                              .withBody(new StringBody(e.getMessage(), "application/json"));
        }
    }

    @Override
    public boolean contains(Command command) {
        return command.command()
                      .equals(PATH) && command.method()
                                              .equals("POST");
    }


}
