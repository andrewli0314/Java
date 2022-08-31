package dungeonmania.Interfaces.BattleInterfaces;

public interface LinearStatIncrease {
    public double increaseAttackLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth);
    public double increaseDefenceLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth);
}
