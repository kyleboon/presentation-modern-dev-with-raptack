import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class RestrictHostHandler implements Handler {
    private static Logger logger = LoggerFactory.getLogger(RestrictHostHandler.class);

    @Override
    public void handle(Context ctx) throws Exception {
        BlackListRepository blackListRepository = ctx.get(BlackListRepository.class);

        String host = ctx.getRequest().getRemoteAddress().getHostText();
        logger.info("Checking blacklist for " +  host);
        blackListRepository.findByHost(host).then(blackListedHost -> {
           if (blackListedHost.isPresent()) {
               ctx.getResponse().status(403);
               ctx.getResponse().send("BLACKLISTED!");
           } else {
               ctx.next();
           }
        });
    }
}
