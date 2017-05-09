import ratpack.guice.Guice;
import ratpack.handlebars.HandlebarsModule;
import ratpack.handling.RequestLogger;
import ratpack.server.RatpackServer;

public class App {
    public static void main(String[] args) throws Exception {
        RatpackServer.start(serverSpec -> serverSpec
                .serverConfig(sc -> { sc
                        .findBaseDir()
                            .props("application.properties")
                            .env()
                            .sysProps()
                            .require("/proxyConfig", ProxyConfig.class);
                })
                .registry(Guice.registry(bindings -> bindings
                        .module(HandlebarsModule.class)
                        .module(AdminModule.class)
                        .module(ProxyModule.class)
                ))
                .handlers(chain -> chain
                        .all(RequestLogger.ncsa())
                        .get("admin", ConfigHandler.class)
                        .all(ProxyModule.proxyHandler())));
    }
}