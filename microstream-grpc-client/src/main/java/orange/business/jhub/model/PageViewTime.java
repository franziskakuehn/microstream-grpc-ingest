package orange.business.jhub.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PageViewTime(@NonNull String pageid, @NonNull String userid, @NonNull String hour, @NonNull double viewTimeAvg) {}