import ratpack.handling.Context;
import ratpack.handling.Handler;

import static ratpack.handlebars.Template.handlebarsTemplate;

public class ConfigHandler implements Handler {
    @Override
    public void handle(Context ctx) throws Exception {
        ProxyConfig proxyConfig = ctx.get(ProxyConfig.class);
        ctx.render(handlebarsTemplate("admin.html", m -> m.put("config", proxyConfig)));
    }
}
