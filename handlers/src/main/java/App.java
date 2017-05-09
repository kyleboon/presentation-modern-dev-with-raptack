import com.google.common.io.Resources;
import ratpack.http.client.HttpClient;
import ratpack.server.RatpackServer;

import java.net.URI;

public class App {
    public static void main(String[] args) throws Exception {
        RatpackServer.start(serverSpec -> serverSpec
                .serverConfig(sc -> { sc
                            .props(Resources.asByteSource(Resources.getResource("application.properties")))
                            .env()
                            .sysProps()
                            .require("/proxyConfig", ProxyConfig.class);
                })
                .handlers(chain -> chain
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
                        })
                )
        );
    }
}