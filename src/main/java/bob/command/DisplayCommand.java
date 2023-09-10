package bob.command;
import bob.Storage;
import bob.TaskList;
import bob.Ui;

public class DisplayCommand extends Command {

    public DisplayCommand() {
    }

    /**
     * Displays list
     * @param tasks from Bob class
     * @param ui from Bob class
     * @param storage from Bob class
     */
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.stringFormat(tasks.displayList());
    }
}
