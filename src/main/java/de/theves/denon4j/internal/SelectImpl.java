package de.theves.denon4j.internal;

import de.theves.denon4j.controls.CommandRegistry;
import de.theves.denon4j.controls.Select;
import de.theves.denon4j.internal.net.ParameterImpl;
import de.theves.denon4j.net.Parameter;
import de.theves.denon4j.net.RequestCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SelectImpl control.
 *
 * @author stheves
 */
public class SelectImpl<S extends Enum<S>> extends AbstractControl implements Select<S> {
    private final Class<S> enumCls;

    private List<String> paramList;

    public SelectImpl(CommandRegistry registry, String prefix, Class<S> source) {
        super(registry, prefix);
        this.enumCls = source;
    }

    @Override
    protected void doInit() {
        paramList = new ArrayList<>(enumCls.getEnumConstants().length + 1); // +1 for request parameter

        paramList.addAll(Stream.of(enumCls.getEnumConstants()).map(S::toString).collect(Collectors.toList()));

        paramList.add(ParameterImpl.REQUEST.getValue());
        register(paramList.toArray(new String[paramList.size()]));
    }

    @Override
    public void select(S source) {
        executeCommand(getCommands().get(paramList.indexOf(source.toString())).getId());
    }

    @Override
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
