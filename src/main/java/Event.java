public class Event extends Task {
    private String start;
    private String end;

    public Event(String description) throws MissingEventDatesException {
        super(description.split(" /from ")[0]);
        try {
            this.start = description.split(" /from ")[1].split(" /to ")[0];
            this.end = description.split(" /from ")[1].split(" /to ")[1];
        } catch (Exception e) {
            throw new MissingEventDatesException();
        }
    }

    public Event(String name, boolean done, String start, String end) {
        super(name);
        super.done = done;
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        String done = this.done ? "[X]" : "[ ]";
        return "[E]" + done + " " + this.name
                + " (from: " + this.start + " to: " + this.end + ")";
    }

    /**
     * Parses string into an Event object
     * @param str is in the form e.g. "0 | read book | 2pm | 4pm"
     * @return Event object
     * @throws IndexOutOfBoundsException when parsing fails, as string split does not occur correctly.
     */
    public static Event parseEvent(String str) throws IndexOutOfBoundsException {
        String[] strSplit = str.split(" \\| ", 4);
        boolean isDone = strSplit[0].equals("1");
        String name = strSplit[1];
        String start = strSplit[2];
        String end = strSplit[3];
        return new Event(name, isDone, start, end);
    }

    @Override
    public String toTxt() {
        String separation = " | ";
        return "event" + separation + (done ? 1 : 0) + separation
                + super.name + separation + start + separation + end;
    }
}
