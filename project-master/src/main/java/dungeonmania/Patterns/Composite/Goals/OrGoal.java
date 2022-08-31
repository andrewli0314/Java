package dungeonmania.Patterns.Composite.Goals;

public class OrGoal implements DefaultGoal {
    private DefaultGoal goal1;
    private DefaultGoal goal2;

    /**
     * Or Goal class that requires two goals. Implements Goal
     * 
     * @param goal1
     * @param goal2
     */
    public OrGoal(DefaultGoal goal1, DefaultGoal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    @Override
    public boolean checkGoalCompleted() {
        if (goal1.checkGoalCompleted() == true || goal2.checkGoalCompleted() == true) {
            return true;
        }
        return false;
    }

    @Override
    public String getGoalString() {
        if (checkGoalCompleted()) {
            return "";
        }
        return "(" + goal1.getGoalString() + " OR " + goal2.getGoalString() + ")";
    }

}
