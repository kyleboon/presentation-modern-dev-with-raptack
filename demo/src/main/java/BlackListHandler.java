import ratpack.exec.Promise;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.jackson.Jackson;

public class BlackListHandler implements Handler {
    BlackListRepository repository;

    public BlackListHandler(BlackListRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        ctx.byMethod(method -> method
                .get(() ->
                        repository.getBlacklists()
                                .map(Jackson::json)
                                .then(ctx::render)
                )
                .post(() -> {
                    Promise<BlackListModel> item = ctx.parse(Jackson.fromJson(BlackListModel.class));
                    item.flatMap(repository::addBlacklistItem).map(Jackson::json).then(ctx::render);
                })
        );
    }
}
