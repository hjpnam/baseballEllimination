/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */


import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class BaseballElimination {
    private final int numTeams;
    private final ArrayList<String> teams;
    private final int[] wins;
    private final int[] loss;
    private final int[] remaining;
    private final int[][] against;
    private final int numGameVertices;
    private final int numVertices;

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
        int numTeamVertices = numberOfTeams() - 1;
        numGameVertices = (int) (Math.pow(numTeamVertices, 2) - numTeamVertices) / 2;
        // include source and terminal nodes to total number of vertices
        numVertices = 2 + numTeamVertices + numGameVertices;
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
        return wins[findTeamIndex(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        return loss[findTeamIndex(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        int teamIndex = teams.indexOf(team);
        return remaining[teamIndex];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return against[findTeamIndex(team1)][findTeamIndex(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        FlowNetwork flowNetwork = createFlowNetwork(findTeamIndex(team));
        new FordFulkerson(flowNetwork, 0, numVertices - 1);
        double epsilon = 1.0E-6;
        for (FlowEdge gameVertex : flowNetwork.adj(0)) {
            if (Math.abs(gameVertex.flow() - gameVertex.capacity()) > epsilon)
                return true;
        }
        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        int teamIndex = findTeamIndex(team);
        FlowNetwork flowNetwork = createFlowNetwork(teamIndex);
        FordFulkerson ff = new FordFulkerson(flowNetwork, 0, numVertices - 1);
        Stack<String> R = new Stack<>();
        if (isEliminated(team)) {
            for (int vertex = 1 + numGameVertices; vertex < numVertices - 1; vertex++) {
                if (ff.inCut(vertex))
                    // subtract numGameVertices and source vertex to get team index
                    R.push(getTeamName(vertex - numGameVertices - 1));
            }
            return R;
        }
        else return null;
    }

    private FlowNetwork createFlowNetwork(int elliminationTeam) {
        FlowNetwork flowNetwork = new FlowNetwork(numVertices);
        int vertexIndex = 1;
        for (int i = 0; i < numberOfTeams(); i++) {
            for (int j = i + 1; j < numberOfTeams(); j++) {
                if (i != j && i != elliminationTeam && j != elliminationTeam) {
                    // connect source to game vertex
                    flowNetwork.addEdge(new FlowEdge(0, vertexIndex, against[i][j]));
                    // connect game vertex i-j to team vertex i
                    flowNetwork.addEdge(new FlowEdge(vertexIndex, numGameVertices + i + 1,
                                                     Double.POSITIVE_INFINITY));
                    // connect game vertex i-j to team vertex j
                    flowNetwork.addEdge(new FlowEdge(vertexIndex++, numGameVertices + j + 1,
                                                     Double.POSITIVE_INFINITY));
                }
            }
            // connect team vertex to terminal
            double teamVertexCapacity = Math
                    .max(0.0, wins[elliminationTeam] + remaining[elliminationTeam] - wins[i]);
            flowNetwork.addEdge(new FlowEdge(numGameVertices + i + 1, numVertices - 1,
                                             teamVertexCapacity));
        }
        return flowNetwork;
    }

    private int findTeamIndex(String team) {
        int index = teams.indexOf(team);
        if (index == -1) throw new IllegalArgumentException("Team does not exist.");
        return index;
    }

    private String getTeamName(int index) {
        if (index < 0 || index >= teams.size())
            throw new IllegalArgumentException("Team index out of range");
        return teams.get(index);
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
