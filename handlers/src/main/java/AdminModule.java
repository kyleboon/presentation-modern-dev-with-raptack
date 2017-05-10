import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class AdminModule extends AbstractModule {
    protected void configure() {
        bind(ConfigHandler.class);
    }

    @Provides
    @Singleton
    BlackListHandler getBlacklistHandler(BlackListRepository repository) {
        return new BlackListHandler(repository);
    }
}
