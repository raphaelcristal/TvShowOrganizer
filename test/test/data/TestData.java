package test.data;

import models.*;
import play.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

import static controllers.Security.generateToken;

public class TestData {

    public static void insertData() {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            Show showSimpsons = new Show();
            showSimpsons.setTvdbId(1);
            showSimpsons.setTitle("Die Simpsons");
            showSimpsons.setDescription("Die Simpsons ist eine von Matt Groening geschaffene, vielfach ausgezeichnete US-amerikanische Zeichentrickserie des Senders Fox. Sie ist die am längsten laufende US-Zeichentrickserie; bisher entstanden in 24 Staffeln über 530 Episoden");

            // Seasons
            List<Season> seasons = new ArrayList<>();

            Season season = new Season();
            season.setNumber(1);

            List<Episode> episodes = new ArrayList<>();

            Episode episode = new Episode();
            episode.setNumber(1);
            episode.setTitle("Es weihnachtet schwer");
            episode.setDescription("Keine Beschreibung verfügbar!");
            episode.setAirtime(dateFormat.parse("17.12.1989"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(2);
            episode.setTitle("Bart wird ein Genie");
            episode.setDescription("Keine Beschreibung verfügbar!");
            episode.setAirtime(dateFormat.parse("14.01.1990"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(3);
            episode.setTitle("Der Versager");
            episode.setDescription("Keine Beschreibung verfügbar!");
            episode.setAirtime(dateFormat.parse("21.01.1990"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(4);
            episode.setTitle("Eine ganz normale Familie");
            episode.setDescription("Keine Beschreibung verfügbar!");
            episode.setAirtime(dateFormat.parse("28.01.1990"));
            episodes.add(episode);

            season.setEpisodes(episodes);
            seasons.add(season);

            season = new Season();
            season.setNumber(2);

            episodes = new ArrayList<>();

            episode = new Episode();
            episode.setNumber(1);
            episode.setTitle("Der Musterschüler");
            episode.setDescription("Keine Beschreibung verfügbar!");
            episode.setAirtime(dateFormat.parse("17.12.1989"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(2);
            episode.setTitle("Karriere mit Köpfchen");
            episode.setDescription("Keine Beschreibung verfügbar!");
            episode.setAirtime(dateFormat.parse("14.01.1990"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(3);
            episode.setTitle("Horror frei Haus");
            episode.setDescription("Keine Beschreibung verfügbar!");
            episode.setAirtime(dateFormat.parse("21.01.1990"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(4);
            episode.setTitle("Frische Fische mit drei Augen");
            episode.setDescription("Keine Beschreibung verfügbar!");
            episode.setAirtime(new Date());
            episodes.add(episode);

            season.setEpisodes(episodes);
            seasons.add(season);

            showSimpsons.setSeasons(seasons);
            showSimpsons.save();


            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

            Show showSaberRider = new Show();
            showSaberRider.setTitle("Saber Rider und die Star Sheriffs");
            showSaberRider.setDescription("Saber Rider und die Starsheriffs ist eine Mecha-Anime-Fernsehserie des japanischen Studio Pierrot von 1984. Die Serie wurde 1987 durch die amerikanische Filmgesellschaft World Events Productions umgearbeitet und in westlichen Ländern vertrieben.");

            // Seasons
            seasons = new ArrayList<>();

            season = new Season();
            season.setNumber(1);

            episodes = new ArrayList<>();

            episode = new Episode();
            episode.setNumber(1);
            episode.setTitle("Die Star Sheriffs");
            episode.setDescription("Saber Rider und April werden auf den Planeten Yuma geschickt um das neue Projekt Ramrod vor den Outridern geheimzuhalten. Dort angekommen, treffen sie auf den Rennfahrer Fireball und den Kopfgeldjäger Colt und besiegen gemeinsam die Outrider. Die Geburtsstunde des Team von Saber Rider und den Star Sheriffs.");
            episode.setAirtime(dateFormat.parse("14.09.1987"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(2);
            episode.setTitle("Das Kavallerie-Oberkommando");
            episode.setDescription("Die Star Sheriffs werden von General Whitehawk zum Kavalerieoberkommando gerufen. General Whitehawk bittet sie den Kadetten ihre Fähigkeiten zu demonstrieren. Als ein Convoy angegriffen wird eilen die Star Sheriffs zur Rettung. Saber Rider rettet April aus den Fängen der Banditen und vermasselt so Jesses großen Auftritt als Retter. Jesse sieht Saber Rider von da an als seinen großen Rivalen.");
            episode.setAirtime(dateFormat.parse("15.09.1987"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(3);
            episode.setTitle("Jesses Rache");
            episode.setDescription("Die Star Sheriffs werden auf den Planeten Dakota gerufen, ein Außenposten mit riesigen Silbermienen. Colt verfolgt einen der Outrider und stellt dabei fest, daß dieser einen Gefangenen versteckt hielt, einen kleinen Jungen namens Hoyd der sich von Colt gar nicht einschüchtern läßt. In der Zwischenzeit plant Gattler, die Silberminen anzugreifen.");
            episode.setAirtime(dateFormat.parse("16.09.1987"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(4);
            episode.setTitle("Der vierbeinige Freund");
            episode.setDescription("Während einer Aufklärungsmission wird Colt von den Outridern in eine Falle gelockt und sitzt in einem Canyon fest. Dank der Hilfe eines kleinen Iguanas kann er sich befreien.");
            episode.setAirtime(dateFormat.parse("17.09.1987"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(5);
            episode.setTitle("Der kleine Freund");
            episode.setDescription("Die Star Sheriffs werden beauftragt, einen Außenposten auf dem Planeten Pecos zu schützen. Während einer Patroullie begegnen sie einem Farmer und seinem Sohn, dem kleinen Hombre. Durch Fireballs rechtzeitige Rückkehr kann ein Angriff der Outrider abgewehrt werden. Am Ende beschließt der Farmer sein Haus aufzugeben und mit dem kleinen Hombre in die Zivilisation zurückzukehren.");
            episode.setAirtime(dateFormat.parse("18.09.1987"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(6);
            episode.setTitle("Nur Zirkus mit dem Zirkus");
            episode.setDescription("Auf dem Weg zum neuen Außenposten auf dem Planeten Yuma treffen die Star Sheriffs auf einen intergalaktischen reisenden Zirkus. Saber Rider rettet die Trapezkünstlerin Lila vor einem Skorpion. Sie unterhalten sich über den Zirkus und Saber Rider beginnt dem Zirkusdirektor Mr. Bigtop gegenüber mißtrauisch zu werden.");
            episode.setAirtime(dateFormat.parse("21.09.1987"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(7);
            episode.setTitle("Der kleine große Held");
            episode.setDescription("Die Star Sheriffs werden auf den Planeten Dakota gerufen, ein Außenposten mit riesigen Silbermienen. Colt verfolgt einen der Outrider und stellt dabei fest, daß dieser einen Gefangenen versteckt hielt, einen kleinen Jungen namens Hoyd der sich von Colt gar nicht einschüchtern läßt. In der Zwischenzeit plant Gattler, die Silberminen anzugreifen.");
            episode.setAirtime(dateFormat.parse("17.07.2013"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(8);
            episode.setTitle("Der Wolf im Schafspelz");
            episode.setDescription("Der Lastwagenzug von Maddox wird an der neuen Grenze angegriffen und die Star Sheriffs vermuten, daß es sich um einen Angriff der Outrider handelt. Saber Rider, Colt und Fireball werden von einem unbekannten Mann angegriffen, der ihnen jedoch unabsichtlich behilflich ist. Er behauptet, Maddox hätte eine Allianz mit den Outridern und daß der Angriff nur vorgetäuscht wurde, um später April und Ramroad zu entführen.");
            episode.setAirtime(dateFormat.parse("25.07.2013"));
            episodes.add(episode);

            season.setEpisodes(episodes);
            seasons.add(season);

            showSaberRider.setSeasons(seasons);
            showSaberRider.save();


            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

            Show showDarkwingDuck = new Show();
            showDarkwingDuck.setTitle("Darkwing Duck");
            showDarkwingDuck.setDescription("Darkwing Duck − Der Schrecken der Bösewichte ist eine von 1991 bis 1992 von der Walt Disney Company produzierte Zeichentrickserie mit der gleichnamigen Hauptfigur");
            showDarkwingDuck.save();

            // Seasons
            seasons = new ArrayList<>();

            season = new Season();
            season.setNumber(1);

            episodes = new ArrayList<>();

            episode = new Episode();
            episode.setNumber(1);
            episode.setTitle("Wirbelwind Kiki in Gefahr - Teil 1");
            episode.setDescription("Darkwing Duck beschützt St. Erpelsburg vor Kleinkriminellen und wünscht sich einen großen Fall, den er schnell kriegt. Der inhaftierte Torro Bulba plant den Diebstahl einer Superwaffe namens \"Ultrablitz\" des verstorbenen Prof. Zündelmeyer, was ihm auch gelingt. Doch um den Aktivierungscode dieser Maschine zu knacken, braucht er die Hilfe von Prof. Zündelmeiers wilder Enkelin Kiki.");
            episode.setAirtime(dateFormat.parse("06.09.1991"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(2);
            episode.setTitle("Wirbelwind Kiki in Gefahr - Teil 2");
            episode.setDescription("Torro Bulba ist aus dem Gefängnis ausgebrochen und macht sich auf die Suche nach Darkwing und Kiki. Es gelingt dem Verbrechergenie die temperamentvolle Enkelin des Professors zu kidnappen und Darkwing macht sich mit seinem neuen Gefährten Quack zum alles entscheidenden Duell bereit.");
            episode.setAirtime(dateFormat.parse("05.09.1991"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(3);
            episode.setTitle("Rendezvous mit Rhoda Dendron");
            episode.setDescription("Darkwing berichtet, wie aus dem harmlosen Wissenschaftler Dr. Benjamin Buxbaum ein kriminelles Genie wurde, der die Pflanzen kontrollieren kann und selbst zu einer Pflanze wurde. Dabei spielt auch die Wissenschaftlerin Dr. Rhoda Dendron, in die Dr. Buxbaum verliebt ist, eine Rolle.");
            episode.setAirtime(dateFormat.parse("17.07.2013"));
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(4);
            episode.setTitle("Lilliput und seine Ameisenbande");
            episode.setDescription("In St. Erpelsburg verschwindet auf mysteriöse Weise ein Gebäude nach dem anderen. Darkwing kommt dem während eines Minigolf-Ausfluges mit Kiki, Quack und Alfred Wirrfuss auf die Schliche, wird aber vom Übeltäter auf Ameisengröße geschrumpft. Dabei handelt es sich um den verrückten Lilliput, der eine Horde Ameisen kontrollieren kann. Darkwing muss sich also nicht nur um die Festnahme von Lilliput, sondern auch um seine Vergrößerung kümmern.");
            episode.setAirtime(dateFormat.parse("25.07.2013"));
            episodes.add(episode);

            season.setEpisodes(episodes);
            seasons.add(season);

            season = new Season();
            season.setNumber(2);

            episodes = new ArrayList<>();

            episode = new Episode();
            episode.setNumber(1);
            episode.setTitle("Das Zeitalter der Maulwürfe");
            episode.setDescription("In St. Erpelsburg verschwinden Gebäude und Fahrzeuge, wobei nur riesige Löcher zurückbleiben. Darkwing und Quack springen in eins der Löcher und kommen auf die Spur von Professor Moriaty, der mit den Maulwürfen die Erdoberfläche erobern will. Zu diesem Zweck will er mit einer Mondfinsternis die Sonne verdunkeln, doch Darkwing stellt sich ihm entgegen.");
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(2);
            episode.setTitle("Ist Kiki eine Lügnerin?");
            episode.setDescription("Kiki wird Zeuge, wie ein Kettensägen schwingender Zombie aus einer Kinoleinwand hervorkommt, doch niemand glaubt ihr. Deswegen wird ihr ein Verbot für gewaltenthaltende Filme ausgesprochen. Doch als Darkwing ebenfalls Zeuge wird, wie ein Außerirdischer aus der Leinwand entspringt, wird ihm klar, dass Kiki die Wahrheit gesagt hatte. Tuskerninni, der filmemachende Schurke, nutzt dazu eine Projektionskamera, um die Filmfiguren in die Realität zu holen.");
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(3);
            episode.setTitle("Megavolts Sohn");
            episode.setDescription("Megavolt hat eine Maschine gebaut, mit der er die Positronen von den Elektronen trennen kann. Als Darkwing einschreitet, wird er versehentlich davon getroffen und wird so in einen guten und einen bösen Darkwing gespalten. Ein Test von Quack, Alfred und Kiki soll herausfinden, wer der Richtige ist. Jedoch geschieht den drei ein Fehler, so dass der böse Darkwing entkommt und mit Megavolt ein Team bilden will.");
            episodes.add(episode);

            episode = new Episode();
            episode.setNumber(4);
            episode.setTitle("Pilze, Pizza und Parfüm");
            episode.setDescription("Tiere stehlen überall in St. Erpelsburg Lebensmittel. Darkwing nimmt die Fährte auf und kommt dabei leicht ins Gruseln, als er die Makaber Pilz GmbH unter die Lupe nimmt. Darkwing verliebt sich dabei in die Vorstandsvorsitzende Morgana Makaber, die jedoch mit den Übeltätern unter einer Decke steckt. Die Firma plant mit ihrem Coup die komplette Pizzaindustrie aus dem Geschäft zu drängen.");
            episodes.add(episode);

            season.setEpisodes(episodes);
            seasons.add(season);

            showDarkwingDuck.setSeasons(seasons);
            showDarkwingDuck.save();

            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

            Actor actor = new Actor();
            actor.setName("Tim Smith");
            actor.save();

            showSimpsons.getActors().add(actor);
            showDarkwingDuck.getActors().add(actor);

            showSimpsons.save();
            showDarkwingDuck.save();

            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

            Network network = new Network();
            network.setName("Fox");
            showSimpsons.setNetwork(network);
            showDarkwingDuck.setNetwork(network);

            showSimpsons.save();
            showDarkwingDuck.save();

            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

            Show showWithNoEpisodes = new Show();
            showWithNoEpisodes.setTitle("Show with no Episodes");
            showWithNoEpisodes.setDescription("This is a show with a seasons but no episodes");

            seasons = new ArrayList<>();

            Season seasonWithNoEpisodes = new Season();
            seasons.add(seasonWithNoEpisodes);

            showWithNoEpisodes.setSeasons(seasons);

            showWithNoEpisodes.save();


            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

            User user = new User();
            user.setEmail("dagobert@duck.com");
            user.setPassword("Geldspeicher");

            Set<Show> shows = new HashSet<>();
            shows.add(showSimpsons);
            shows.add(showSaberRider);

            user.setShows(shows);

            AuthToken authToken = new AuthToken();
            authToken.setToken(generateToken());
            user.setAuthToken(authToken);

            user.save();


            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

            user = new User();
            user.setEmail("donald@duck.com");
            user.setPassword("DaisyDuck");

            shows = new HashSet<>();
            shows.add(showSimpsons);
            shows.add(showDarkwingDuck);

            user.setShows(shows);

            authToken = new AuthToken();
            authToken.setToken(generateToken());
            user.setAuthToken(authToken);

            user.save();

            // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

            user = new User();
            user.setEmail("userwithout@shows.com");
            user.setPassword("userwithoutshows");

            authToken = new AuthToken();
            authToken.setToken(generateToken());
            user.setAuthToken(authToken);

            user.save();


        } catch (Exception e) {

            Logger.error("Error while trying to insert test data");

        }

    }
}
