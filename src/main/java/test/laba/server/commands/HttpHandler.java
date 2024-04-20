package test.laba.server.commands;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import rawhttp.core.EagerHttpRequest;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.StringBody;
import test.laba.server.controller.Command;
import test.laba.server.controller.Controller;
import test.laba.server.util.HttpManager;
import static test.laba.server.util.MessageContent.THE_METHOD_IS_NOT_ALLOWED;

@RequiredArgsConstructor
public class HttpHandler {
    private final List<Controller> controllers;
    private final RawHttp rawHttp;


    public RawHttpResponse executeCommand(EagerHttpRequest request) {
        Command command = new Command(request.getMethod(), request.getUri()
                                                                  .getPath());
        Optional<Controller> first = controllers.stream()
                                                .filter(controller -> controller.contains(command))
                                                .findFirst();
        if (first.isEmpty()) {
            return HttpManager.getTemplate(rawHttp, 405)
                              .withBody(new StringBody(THE_METHOD_IS_NOT_ALLOWED));
        }

        return first.get()
                    .execute(request);
    }
}
