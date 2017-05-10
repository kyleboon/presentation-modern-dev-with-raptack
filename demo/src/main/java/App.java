import ratpack.guice.Guice;
import ratpack.handlebars.HandlebarsModule;
import ratpack.handling.RequestLogger;
import ratpack.hikari.HikariModule;
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
                        .module(JooqModule.class)
                        .module(AdminModule.class)
                        .module(ProxyModule.class)
                        .module(HikariModule.class, config -> {
                            config.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
                            config.addDataSourceProperty(
                                    "URL",
                                    "jdbc:h2:mem:todo;INIT=RUNSCRIPT FROM 'classpath:/init.sql'");
                        })
                ))
                .handlers(chain -> chain
                        .all(RequestLogger.ncsa())
                        .path("admin/blacklist",BlackListHandler.class)
                        .get("admin", ConfigHandler.class)
                        .all(ProxyModule.restrictHostHandler())
                        .all(ProxyModule.proxyHandler())));
    }
}