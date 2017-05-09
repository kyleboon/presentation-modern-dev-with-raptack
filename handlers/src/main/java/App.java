import com.google.common.io.Resources;
import ratpack.guice.Guice;
import ratpack.handlebars.HandlebarsModule;
import ratpack.handlebars.internal.HandlebarsTemplateRenderer;
import ratpack.http.client.HttpClient;
import ratpack.server.RatpackServer;
import ratpack.server.internal.BaseDirFinder;

import static ratpack.handlebars.Template.handlebarsTemplate;

import java.net.URI;

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
                ))
                .handlers(chain -> chain
                        .get("admin", ctx -> {
                            ProxyConfig proxyConfig = ctx.get(ProxyConfig.class);
                            ctx.render(handlebarsTemplate("admin.html", m -> m.put("config", proxyConfig)));
                        })
                        .all(ctx -> {
                            HttpClient httpClient = ctx.get(HttpClient.class);
                            ProxyConfig proxyConfig = ctx.get(ProxyConfig.class);

                            URI oUri = new URI(ctx.getRequest().getRawUri());

                            URI proxyUri = new URI(proxyConfig.getScheme(),
                                    oUri.getUserInfo(),
                                    proxyConfig.getHost(),
                                    proxyConfig.getPort(),
                                    oUri.getPath(),
                                    oUri.getQuery(),
                                    oUri.getFragment());

                            httpClient.requestStream(proxyUri, requestSpec -> {
                                requestSpec.headers(mutableHeaders -> {
                                    mutableHeaders.copy(ctx.getRequest().getHeaders());
                                });
                                requestSpec.method(ctx.getRequest().getMethod());
                            }).then(responseStream -> {
                                responseStream.forwardTo(ctx.getResponse());
                            });
                        })));
    }
}