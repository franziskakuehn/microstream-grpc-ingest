package orange.business.jhub.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PageViewClient(@NonNull String hour, @NonNull String userid, @NonNull String pageid, @NonNull int viewtime, Long processingTime) {}
