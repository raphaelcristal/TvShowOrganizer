import dataload.DataLoad;
import dataload.parsers.AbstractShowParser;
import dataload.parsers.TvdbParser;
import dataload.provider.TvdbProvider;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        if (Play.isProd()) {
            Akka.system().scheduler().schedule(
                    Duration.create(60, TimeUnit.SECONDS),
                    Duration.create(1, TimeUnit.DAYS),
                    new Runnable() {
                        @Override
                        public void run() {
                            TvdbProvider tvdbProvider = new TvdbProvider(Play.application().configuration().getString("tvdb.token"), TvdbProvider.Frequency.DAILY);
                            TvdbParser tvdbParser = new TvdbParser(tvdbProvider, false);
                            List<AbstractShowParser> parsers = new ArrayList<>();
                            parsers.add(tvdbParser);
                            DataLoad dataLoad = new DataLoad(parsers);

                            try {
                                dataLoad.updateShows();
                            } catch (Exception e) {
                                Logger.error(e.toString());
                            }
                        }
                    },
                    Akka.system().dispatcher()
            );
        }

    }
}
