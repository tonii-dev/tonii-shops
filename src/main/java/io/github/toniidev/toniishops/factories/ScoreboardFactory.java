package io.github.toniidev.toniishops.factories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scoreboard factory. This class will be used to create any custom Scoreboard
 * with custom content
 */
public class ScoreboardFactory {
    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<String> lines = new ArrayList<>();

    /**
     * Creates a blank ScoreboardFactory instance.
     * @param playerToDisplayScoreboardTo Player that has to see the scoreboard that this ScoreboardFactory will create
     * @param title The title of the scoreboard that this ScoreboardFactory will create
     */
    public ScoreboardFactory(Player playerToDisplayScoreboardTo, String title){
        this.player = playerToDisplayScoreboardTo;

        assert Bukkit.getScoreboardManager() != null;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        this.objective = scoreboard.registerNewObjective("Scoreboard", Criteria.DUMMY, title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Adds a line to the Scoreboard that this ScoreboardFactory instance is creating
     * @param line The line to add to the Scoreboard
     * @return This ScoreboardFactory instance
     */
    public ScoreboardFactory addLine(String line){
        this.lines.add(line);
        return this;
    }

    /**
     * Adds a blank line to the Scoreboard that this ScoreboardFactory instance is creating
     * @return This ScoreboardFactory instance
     */
    public ScoreboardFactory addBlankLine(){
        return this.addLine(" ");
    }

    /**
     * Gets the Scoreboard that this ScoreboardFactory has created. This should only be used when the creation
     * progress came to an end
     * @return This ScoreboardFactory instance
     */
    public Scoreboard getScoreboard(){
        for (String line : scoreboard.getEntries()) {
            scoreboard.resetScores(line);
        }

        for(int i = this.lines.size() - 1; i >= 0; i--){
            objective.getScore(this.lines.get(this.lines.size() - i - 1)).setScore(i);
        }

        return this.scoreboard;
    }

    /**
     * Displays this scoreboard to the Player linked to it
     */
    public void display(){
        this.player.setScoreboard(getScoreboard());
    }
}
