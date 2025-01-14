package bob;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import bob.exception.InvalidPriorityException;
import bob.exception.MissingDatesException;
import bob.exception.MissingTaskException;
import bob.exception.WrongInputException;
import bob.task.*;

/**
 * Contains list of tasks, and operations that alter it.
 */
public class TaskList {

    public ArrayList<Task> lst;

    public TaskList() {
        this.lst = new ArrayList<>();
    }
    public TaskList(ArrayList<Task> lst) {
        this.lst = lst;
    }

    /**
     * Generates the appropriate type of Task based on user input
     * Throws exceptions due to incorrect user input
     * @param description is of the form e.g. "event p/high read /from 2pm /to 4pm"
     * @return the relevant Task
     * @throws WrongInputException for unrecognised input.
     * @throws MissingTaskException when task name is missing.
     * @throws MissingDatesException when start and end of Event is missing.
     */
    public Task generateTask(String description)
            throws WrongInputException, MissingTaskException,
            MissingDatesException, DateTimeParseException, InvalidPriorityException, IndexOutOfBoundsException {

        // Split by the first " " into type, and task details
        String[] task = description.split(" ", 2);
        TaskType taskType;

        try {
            taskType = Enum.valueOf(TaskType.class, task[0]);
        } catch (Exception e) {
            throw new WrongInputException();
        }

        if (task.length == 1) {
            throw new MissingTaskException();
        }

        String taskDetails = task[1];
        assert taskDetails != null;

        if (taskType.equals(TaskType.deadline)) {
            return new Deadline(taskDetails);
        } else if (taskType.equals(TaskType.event)) {
            return new Event(taskDetails);
        } else {
            return new Todo(taskDetails);
        }
    }

    /**
     * Adds a Task to lst. Writes modified lst to bob.txt.
     * Handles exceptions that occur due to wrong input/ missing requirements
     * @param description is of the form e.g. "event p/low read /from 2pm /to 4pm"
     * @return message for adding a Task
     */
    public String[] addToList(String description)
        throws WrongInputException, MissingTaskException, MissingDatesException,
            DateTimeParseException, InvalidPriorityException, IndexOutOfBoundsException {

            Task taskObj = generateTask(description);
            assert taskObj != null;

            Task repeat = null;
            int index = 0;

            for (int i = 0; i < lst.size(); i++) {
                if (taskObj.equals(lst.get(i))) {
                    repeat = lst.get(i);
                    index = i + 1;
                    break;
                }
            }

            if (repeat != null) {
                return new String[]{"There is a similar task that was previously added: ",
                        (index + ". " + repeat.toString()),
                        "Delete this to add your new task."};
            }

            lst.add(taskObj);
            return new String[]{"You have added a new task: ", "\t" + taskObj.toString(),
                "Now, you have " + lst.size() + (lst.size() == 1 ? " task!" : " tasks!")};
    }

    /**
     * Prints out the Tasks on lst.
     * @return display of lst
     */
    public String[] displayList(String priority) {
        assert lst != null;

        List<String> tasksFound;
        String message;

        if (priority.equals("")) {
            tasksFound = lst.stream().map(Object::toString).collect(Collectors.toList());
            message = "Certainly. Here are all your tasks. Consider filtering by priority (e.g. list p/high)";
        } else {
            try {
                Priority p = Enum.valueOf(Priority.class, priority);
                tasksFound = getTasksWithPriority(p);
                message = "Certainly. Here are your tasks of " + priority + " priority!";
            } catch (Exception e) {
                return new String[] { "Apologies, please input valid priority! E.g. list p/high" };
            }
        }

        if (tasksFound.isEmpty()) {
            return new String[] { "Alas, no tasks are found." };
        }

        List<String> tasks = IntStream.range(1, tasksFound.size() + 1)
                .mapToObj(i -> i + ". " + tasksFound.get(--i))
                .collect(Collectors.toList());

        tasks.add(0, message);

        return tasks.toArray(new String[0]);
    }

    private List<String> getTasksWithPriority(Priority p) {
        return lst.stream()
                .filter(tsk -> tsk.getPriority().equals(p))
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    /**
     * Marks Task as done or undone at a specified index.
     * Writes modified lst to bob.txt.
     * @param index of Task to be marked
     * @param doneOrNot states whether the Task is done or not
     * @return message for marking a Task
     */
    public String[] markDoneOrNot(int index, boolean doneOrNot) {
        lst.get(index - 1).setDoneOrNot(doneOrNot);
        String statement = doneOrNot
                ? "Splendid! You completed a task!"
                : "This is now undone. Let that not happen again.";
        return new String[]{statement, "\t" + lst.get(index - 1).toString()};
    }

    /**
     * Deletes Task at specified index from list.
     * Writes modified lst to bob.txt.
     * @param index of Task to be deleted
     * @return message for deleting a Task
     */
    public String[] deleteTask(int index) {
        String taskStr = lst.get(index - 1).toString();
        lst.remove(index - 1);
        return new String[]{"The selected task is removed from list: ", "\t" + taskStr,
            "You now have " + lst.size() + (lst.size() == 1 ? " task!" : " tasks!")};
    }

    public String[] findTasks(String keyword) {

        List<String> tasksStream = lst.stream()
                .filter(tsk -> tsk.getName()
                .contains(keyword))
                .map(Object::toString)
                .collect(Collectors.toList());

        Stream<Integer> indexStream = IntStream.range(0, tasksStream.size()).boxed();

        List<String> tasksFound = indexStream
                .map(i -> (++i) + ". " + tasksStream.get(--i))
                .collect(Collectors.toList());

        tasksFound.add(0, "Certainly. Here are the matching tasks: ");

        return tasksFound.size() > 1
                ? tasksFound.toArray(new String[0])
                : new String[]{"No matching tasks were found. Try another keyword, or display the entire list."};
    }
}
