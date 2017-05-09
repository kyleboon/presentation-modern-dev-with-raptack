import com.google.inject.AbstractModule;

public class AdminModule extends AbstractModule {
    protected void configure() {
        bind(ConfigHandler.class);
    }
}
