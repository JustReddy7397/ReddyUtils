package wiki.justreddy.ga.reddyutils.attribute;

public enum Slot {

    MAIN_HAND ("mainhand"),
    OFF_HAND ("offhand"),
    FEET ("feet"),
    LEGS ("legs"),
    CHEST ("chest"),
    HEAD ("head");
    private String name;
    Slot(String name) {
        this.name = name;
    }
    /**
     * Get the predefined, global and unique name of this slot.
     *
     * @return The name
     */
    public String getName() {
        return this.name;
    }

}
