package de.theves.denon4j;

/**
 * Created by Elena on 04.06.2017.
 */
public class SelectInput extends Select<InputSource> {

    public SelectInput(CommandRegistry registry) {
        super(registry, "SI", InputSource.class);
    }
}
