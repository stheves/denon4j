package de.theves.denon4j.internal;

import de.theves.denon4j.controls.CommandRegistry;
import de.theves.denon4j.controls.Select;
import de.theves.denon4j.internal.net.ParameterImpl;
import de.theves.denon4j.net.Parameter;
import de.theves.denon4j.net.RequestCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SelectImpl control.
 *
 * @author stheves
 */
public class SelectImpl<S extends Enum> extends AbstractControl implements Select<S> {
    private final S[] values;

    private List<String> paramList;

    public SelectImpl(CommandRegistry registry, String prefix, S... values) {
        super(registry, prefix);
        this.values = values;
    }

    @Override
    protected void doInit() {
        paramList = new ArrayList<>(values.length + 1); // +1 for request parameter

        paramList.addAll(Stream.of(values).map(Enum::toString).collect(Collectors.toList()));

        paramList.add(ParameterImpl.REQUEST.getValue());
        register(paramList.toArray(new String[paramList.size()]));
    }

    @Override
    public void select(S source) {
        executeCommand(getCommands().get(paramList.indexOf(source.toString())).getId());
    }

    @Override
    public S getSource() {
        Parameter state = getState();
        return findSource(state);
    }

    private S findSource(Parameter state) {
        return Stream.of(values)
                .filter(e -> state.getValue().equals(e.toString()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    protected RequestCommand getRequestCommand() {
        return (RequestCommand) getRegistry().getCommand(getCommands().get(getCommands().size() - 1).getId());
    }
}
