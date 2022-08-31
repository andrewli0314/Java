package dungeonmania.Patterns.Factories;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.Patterns.Composite.Goals.AndGoal;
import dungeonmania.Patterns.Composite.Goals.BoulderGoal;
import dungeonmania.Patterns.Composite.Goals.DefaultGoal;
import dungeonmania.Patterns.Composite.Goals.EnemiesGoal;
import dungeonmania.Patterns.Composite.Goals.ExitGoal;
import dungeonmania.Patterns.Composite.Goals.OrGoal;
import dungeonmania.Patterns.Composite.Goals.TreasureGoal;

public class GoalFactory {
    public static DefaultGoal buildGoal(JSONObject goalsJSON, EntitiesManager environment) {
        String goal = goalsJSON.getString("goal");
        // If and goal create new and goal with sub goals
        if (goal.equals("AND")) {
            JSONArray subgoals = goalsJSON.getJSONArray("subgoals");
            JSONObject subgoal1 = subgoals.getJSONObject(0);
            JSONObject subgoal2 = subgoals.getJSONObject(1);

            return new AndGoal(buildGoal(subgoal1, environment), buildGoal(subgoal2, environment));
        }
        // If goal is or goal, get subgoals and return new or goal
        if (goal.equals("OR")) {
            JSONArray subgoals = goalsJSON.getJSONArray("subgoals");
            JSONObject subgoal1 = subgoals.getJSONObject(0);
            JSONObject subgoal2 = subgoals.getJSONObject(1);

            return new OrGoal(buildGoal(subgoal1, environment), buildGoal(subgoal2, environment));
        }

        if (goal.equals("exit")) {
            return new ExitGoal(environment);
        }
        if (goal.equals("enemies")) {
            return new EnemiesGoal(environment);
        }
        if (goal.equals("treasure")) {
            return new TreasureGoal(environment);
        }
        if (goal.equals("boulders")) {
            return new BoulderGoal(environment);
        }
        return null;
    }
}
