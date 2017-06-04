package de.theves.denon4j;

/**
 * Created by Elena on 04.06.2017.
 */
public class SelectVideo extends Select<VideoSource> {
    public SelectVideo(CommandRegistry registry) {
        super(registry, "SV", VideoSource.class);
    }
}
