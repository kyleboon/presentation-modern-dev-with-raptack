import ratpack.http.client.HttpClient;
import ratpack.server.RatpackServer;

import java.net.URI;

public class App {
    private static String HOST = "localhost";
    private static int PORT = 5151;

    public static void main(String[] args) throws Exception {
        RatpackServer.start(serverSpec -> serverSpec
                .handlers(chain -> chain
                        .all(ctx -> {
                            HttpClient httpClient = ctx.get(HttpClient.class);
                            URI oUri = new URI(ctx.getRequest().getRawUri());

                            URI proxyUri = new URI("http",
                                    oUri.getUserInfo(),
                                    HOST,
                                    PORT,
                                    oUri.getPath(),
                                    oUri.getQuery(),
                                    oUri.getFragment());

                            httpClient.requestStream(proxyUri, requestSpec -> {
                                requestSpec.headers( mutableHeaders -> {
                                            mutableHeaders.copy(ctx.getRequest().getHeaders());
                                        });
                                requestSpec.method(ctx.getRequest().getMethod());
                            }).then( responseStream -> {
                                responseStream.forwardTo(ctx.getResponse());
                            });
                        })
                )
        );
    }
}