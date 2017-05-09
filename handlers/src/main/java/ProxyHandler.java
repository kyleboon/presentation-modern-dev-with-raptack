import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.client.HttpClient;

import java.net.URI;

class ProxyHandler implements Handler {

    @Override
    public void handle(Context ctx) throws Exception {
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
    }
}