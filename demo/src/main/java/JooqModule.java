import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;

import javax.inject.Singleton;
import javax.sql.DataSource;

public class JooqModule extends AbstractModule {
    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    public DSLContext dslContext(DataSource dataSource) {
        return DSL.using(new DefaultConfiguration().derive(dataSource));
    }

    @Provides
    @Singleton
    BlackListRepository blackListRepository(DSLContext dslContext) {
        return new BlackListRepository(dslContext);
    }
}
