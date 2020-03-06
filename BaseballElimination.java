/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */


import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;

public class BaseballElimination {
    private final int numTeams;
    private final ArrayList<String> teams;
    private final int[] wins;
    private final int[] loss;
    private final int[] remaining;
    private final int[][] against;

    // create a baseball division from given filename
    public BaseballElimination(String filename) {
        In file = new In(filename);
        numTeams = file.readInt();
        teams = new ArrayList<>();
        wins = new int[numTeams];
        loss = new int[numTeams];
        remaining = new int[numTeams];
        against = new int[numTeams][numTeams];

        for (int i = 0; i < numTeams; i++) {
            teams.add(file.readString());
            wins[i] = file.readInt();
            loss[i] = file.readInt();
            remaining[i] = file.readInt();
            for (int j = 0; j < numTeams; j++) {
                against[i][j] = file.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return new ArrayList<>(teams);
    }

    // number of wins for given team
    public int wins(String team) {
        int teamIndex = teams.indexOf(team);
        return wins[teamIndex];
    }

    // number of losses for given team
    public int losses(String team) {
        int teamIndex = teams.indexOf(team);
        return loss[teamIndex];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        int teamIndex = teams.indexOf(team);
        return remaining[teamIndex];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        int team1Index = teams.indexOf(team1);
        int team2Index = teams.indexOf(team2);
        return against[team1Index][team2Index];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        throw new UnsupportedOperationException();
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        throw new UnsupportedOperationException();
    }

    private FlowNetwork createFlowNetwork(int elliminationTeam) {
        int numTeamVertices = numberOfTeams() - 1;
        int numGameVertices = (int) (Math.pow(numTeamVertices, 2) - numTeamVertices) / 2;
        int numVertices = 2 + numTeamVertices + numGameVertices;
        FlowNetwork flowNetwork = new FlowNetwork(numVertices);
        // connect source vertex to game vertices
        int vertexIndex = 1;
        for (int i = 0; i < numTeamVertices; i++) {
            for (int j = i + 1; j < numTeamVertices; j++) {
                flowNetwork.addEdge(new FlowEdge(0, vertexIndex++, against[i][j], 0.0));
            }
        }
        for (int gameV = 1; gameV < vertexIndex; gameV++) {

        }
    }

    public static void main(String[] args) {

    }
}
