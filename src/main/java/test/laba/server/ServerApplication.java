package test.laba.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import rawhttp.core.RawHttp;
import test.laba.server.commands.HttpHandler;
import test.laba.server.controller.LoginController;
import test.laba.server.controller.MoneyController;
import test.laba.server.controller.RegisterController;
import test.laba.server.repository.AuthRepository;
import test.laba.server.repository.TokenRepositoryImpl;
import test.laba.server.repository.UserRepositoryImpl;
import test.laba.server.service.MoneyService;
import test.laba.server.service.auth.AuthenticationService;
import test.laba.server.service.auth.JwtService;


public class ServerApplication {
    public static void main(
            String[] args) {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();
        SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata()
                                                                     .buildSessionFactory();
        HttpHandler httpHandler = getHttpHandler(sessionFactory);
        ExecutorService executorService = Executors.newCachedThreadPool();
        ExecutorService executorService1 = Executors.newCachedThreadPool();
        ExecutorService executorService2 = Executors.newCachedThreadPool();
        new ServerApp(httpHandler, executorService, executorService1, executorService2).run();
    }

    private static HttpHandler getHttpHandler(SessionFactory sessionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        RawHttp rawHttp = new RawHttp();
        UserRepositoryImpl repository = new UserRepositoryImpl(sessionFactory);
        AuthenticationService authenticationService = new AuthenticationService(repository, new TokenRepositoryImpl(sessionFactory), new JwtService(), new AuthRepository(repository));
        return new HttpHandler(List.of(new LoginController(authenticationService, objectMapper, rawHttp), new RegisterController(authenticationService, objectMapper, rawHttp), new MoneyController(authenticationService, new JwtService(), new MoneyService(repository), objectMapper, rawHttp)), rawHttp);
    }
}
