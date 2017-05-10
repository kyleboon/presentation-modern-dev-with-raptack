import com.google.inject.Inject;
import jooq.Tables;
import jooq.tables.records.BlacklistRecord;
import org.jooq.DSLContext;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import ratpack.exec.Blocking;
import ratpack.exec.Promise;

import java.util.List;
import java.util.Optional;

public class BlackListRepository {
    private final DSLContext context;

    @Inject
    public BlackListRepository(DSLContext context) {
        this.context = context;
    }

    public Promise<List<BlackListModel>> getBlacklists() {
        SelectJoinStep all = context.select().from(Tables.BLACKLIST);
        return Blocking.get(() -> all.fetchInto(BlackListModel.class));
    }

    public Promise<Optional<BlackListModel>> findByHost(String host) {
        SelectConditionStep one = context.select().from(Tables.BLACKLIST).where(Tables.BLACKLIST.HOST.eq(host));
        return Blocking.get(() -> one.fetchOptionalInto(BlackListModel.class));
    }

    public Promise<BlackListModel> addBlacklistItem(BlackListModel blackListModel) {
        BlacklistRecord record = context.newRecord(Tables.BLACKLIST, blackListModel);
        return Blocking.op(record::store)
                .next(Blocking.op(record::refresh))
                .map(() -> record.into(BlackListModel.class));
    }
}
