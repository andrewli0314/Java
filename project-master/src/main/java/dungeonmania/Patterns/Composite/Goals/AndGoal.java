package dungeonmania.Patterns.Composite.Goals;

public class AndGoal implements DefaultGoal {
    private DefaultGoal goal1;
    private DefaultGoal goal2;

    /**
     * And Goal class that requires two goal composites. Implements Goal Composite
     * 
     * @param goal1
     * @param goal2
     */
    public AndGoal(DefaultGoal goal1, DefaultGoal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    @Override
    public boolean checkGoalCompleted() {
        if (goal1.checkGoalCompleted() == true && goal2.checkGoalCompleted() == true) {
            return true;
        }
        return false;
    }

    @Override
    public String getGoalString() {
        if (checkGoalCompleted()) {
            return "";
        }
        return "(" + goal1.getGoalString() + " AND " + goal2.getGoalString() + ")";

    }

}
