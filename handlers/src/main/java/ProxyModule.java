import com.google.inject.AbstractModule;

public class ProxyModule extends AbstractModule {


    public static ProxyHandler proxyHandler() {
        return new ProxyHandler();
    }

    @Override
    protected void configure() {

    }

    public static RestrictHostHandler restrictHostHandler() {
        return new RestrictHostHandler();
    }
}
