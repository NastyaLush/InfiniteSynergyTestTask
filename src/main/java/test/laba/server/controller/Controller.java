package test.laba.server.controller;

import rawhttp.core.EagerHttpRequest;
import rawhttp.core.RawHttpResponse;

public interface Controller {
    RawHttpResponse execute(EagerHttpRequest request);

    boolean contains(Command command);
}
