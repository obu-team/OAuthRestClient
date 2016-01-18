package si.um.feri.app.model;

public class POSTCommand {
    private CarCommand carCommand;
    private CommandState commandState;

    public POSTCommand() {
    }

    public CarCommand getCarCommand() {
        return carCommand;
    }

    public void setCarCommand(CarCommand carCommand) {
        this.carCommand = carCommand;
    }

    public CommandState getCommandState() {
        return commandState;
    }

    public void setCommandState(CommandState commandState) {
        this.commandState = commandState;
    }

    @Override
    public String toString() {
        return "POSTCommand{" +
                "carCommand=" + carCommand +
                ", commandState=" + commandState +
                '}';
    }
}
