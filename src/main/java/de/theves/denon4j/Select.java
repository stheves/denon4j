package de.theves.denon4j;

import de.theves.denon4j.internal.AbstractControl;
import de.theves.denon4j.internal.net.RequestCommand;
import de.theves.denon4j.net.Command;
import de.theves.denon4j.net.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Elena on 04.06.2017.
 */
public class Select<S extends Enum<S>> extends AbstractControl {
    private final Class<S> source;

    private List<Command> commandList;
    private List<String> paramList;

    public Select(CommandRegistry registry, String prefix, Class<S> source) {
        super(registry, prefix);
        this.source = source;
    }

    @Override
    protected void doInit() {
        paramList = new ArrayList<>(InputSource.values().length + 1); // +1 for request parameter
        paramList.addAll(Stream.of(source.getEnumConstants()).map(Enum::name).collect(Collectors.toList()));
        paramList.add(Parameter.REQUEST.getValue());
        commandList = register(paramList.toArray(new String[paramList.size()]));
    }

    public void select(S source) {
        executeCommand(commandList.get(paramList.indexOf(source.name())).getId());
    }

    @Override
    protected RequestCommand getRequestCommand() {
        return (RequestCommand) getRegistry().getCommand(commandList.get(commandList.size() - 1).getId());
    }
}
