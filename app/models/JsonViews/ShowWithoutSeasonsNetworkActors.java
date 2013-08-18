package models.JsonViews;

import models.Show;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({"seasons", "network", "actors"})
public class ShowWithoutSeasonsNetworkActors extends Show {

}
