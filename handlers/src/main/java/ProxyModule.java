import com.google.inject.AbstractModule;

public class ProxyModule extends AbstractModule {


    static ProxyHandler proxyHandler() {
        return new ProxyHandler();
    }

    @Override
    protected void configure() {

    }
}
