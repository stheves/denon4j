package de.theves.denon4j;

import de.theves.denon4j.internal.AbstractControl;
import de.theves.denon4j.internal.net.RequestCommand;
import de.theves.denon4j.net.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Select control.
 *
 * @author Sascha Theves
 */
public class Select<S extends Enum<S>> extends AbstractControl {
    private final Class<S> enumCls;

    private List<String> paramList;

    Select(CommandRegistry registry, String prefix, Class<S> source) {
        super(registry, prefix);
        this.enumCls = source;
    }

    @Override
    protected void doInit() {
        paramList = new ArrayList<>(InputSource.values().length + 1); // +1 for request parameter

        paramList.addAll(Stream.of(enumCls.getEnumConstants()).map(
                ec -> Enum.valueOf(enumCls, ec.name()).toString()
        ).collect(Collectors.toList()));

        paramList.add(Parameter.REQUEST.getValue());
        register(paramList.toArray(new String[paramList.size()]));
    }

    public void select(S source) {
        executeCommand(getCommands().get(paramList.indexOf(source.toString())).getId());
    }

    public Optional<S> getSource() {
        Parameter state = getState();
        return findSource(state);
    }

    private Optional<S> findSource(Parameter state) {
        return Stream.of(enumCls.getEnumConstants()).filter(
                ec -> Enum.valueOf(enumCls, ec.name()).toString().equals(state.getValue())
        ).findFirst();
    }

    @Override
    protected RequestCommand getRequestCommand() {
        return (RequestCommand) getRegistry().getCommand(getCommands().get(getCommands().size() - 1).getId());
    }
}
