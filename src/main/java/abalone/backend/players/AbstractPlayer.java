package abalone.backend.players;

import abalone.backend.model.interfaces.iGameState;
import abalone.backend.players.interfaces.iPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Contains common controller functionality to be used by players.
 * @author James Byrne
 */
public abstract class AbstractPlayer implements iPlayer {
    
    protected static final String STATSPATH = "player_stats/";
    protected static final int MINVALUE = -100;
    protected static final int MAXVALUE = 100;

    protected boolean isHuman = false;
    protected int playerNumber;
    protected iGameState gameState;
    protected iPlayer opponent;

    /**
     * @return A new set of player stats, with everything initilised to 0.
     * @throws JSONException
     */
    protected JSONObject newStatSet() throws JSONException {
        JSONObject stats = new JSONObject();
        stats.put("Won", 0);
        stats.put("Lost", 0);
        stats.put("WinningStreak", 0);
        stats.put("LosingStreak", 0);
        stats.put("BiggestWinningStreak", 0);
        stats.put("BiggestLosingStreak", 0);
        stats.put("Level", 0);
        return stats;
    }

    /**
     * Convenience class used to evaluate, store, sort and search moves available from a given game state.
     */
    protected class EvaluatedMove implements Comparable<EvaluatedMove>{

        public iGameState state;
        public JSONArray move;
        public double strength = 0;

        /**
         * Constructs a fully evaluated move, calculating the strength using
         * the calculateStrength method.
         * @param move The move to be evaluated.
         * @param state The state that the move will be applied to.
         * @throws JSONException
         */
        public EvaluatedMove(JSONArray move, iGameState state) throws JSONException{
            this.state = makeMove(move, state.cloneState());
            this.move = move;
            this.strength = calculateStrength(this.state);
        }

        /**
         * Used to run Collections.binarySearch on a list of type EvaluatedMove.
         * @param strength The strength of the move which is being searched for.
         * @throws JSONException
         */
        public EvaluatedMove(double strength) throws JSONException{
            this.strength = strength;
        }

        /**
         * Compares against the other evaluated move solely on their strengths.
         * @param other The other state to be compared against.
         * @return 1 if this state is stronger, -1 if this state is weaker, 0 if the states are equal strength.
         */
        @Override
        public int compareTo(EvaluatedMove other) {
            if(this.strength > other.strength){
                return 1;
            } else if(other.strength > this.strength){
                return -1;
            }
            return 0;
        }
    }

    /**
     * Based on the geometric score function published by Aichholzer et al.
     * http://www.ist.tugraz.at/staff/aichholzer/research/rp/abalone/tele1-02_aich-abalone.pdf, p 2.
     * @param state The state to be evaluated.
     * @return The heuristic strength of the given state.
     * @throws JSONException
     */
    protected double calculateStrength(iGameState state) throws JSONException{
        double strength = 0;
        // Evaluate score.
        if(playerNumber == 1){
            strength += state.getPlayer1score();
            strength -= state.getPlayer2score();
        } else if(playerNumber == 2){
            strength += state.getPlayer2score();
            strength -= state.getPlayer1score();
        }
        strength *= 2;
        // Evaluate threatened marbles.
        List<JSONArray> nextMoves = findAvailableMoves(state);
        List<String> threatened = new LinkedList<>(); // Marble threatened from > 1 direction is still only 1 threatened.
        for(JSONArray move : nextMoves){
            if(move.getJSONObject(0).getString("to").length() >= 4){ // You can push a marble off.
                if(!threatened.contains(move.getJSONObject(0).getString("from"))) {
                    threatened.add(move.getJSONObject(0).getString("from"));
                    if (state.getCurrentPlayer() == playerNumber) {
                        strength += 1.0;
                    } else {
                        strength -= 1.0;
                    }
                }
            }
        }
        // Calculate the heuristic value for each player.
        List<String> player1Marbles = new LinkedList<>();
        List<String> player2Marbles = new LinkedList<>();
        List<String> allMarbles = new LinkedList<>();

        // Fill marble lists for each player.
        for(int i = 0; i < 9; i++){ // Only 9 horizontal lines required.
            String[] line = iGameState.LINES[i];
            for(String space : line){
                if(space.length() == 2){ // Ignore "out" spaces.
                    if(state.getMarbleAt(space) != 0) {
                        allMarbles.add(space);
                        if (state.getMarbleAt(space) == 1) {
                            player1Marbles.add(space);
                        } else if (state.getMarbleAt(space) == 2) {
                            player2Marbles.add(space);
                        }
                    }
                }
            }
        }
        // Calculate the mass centres.
        String player1massCentre = getMassCentre(player1Marbles);
        String player2massCentre = getMassCentre(player2Marbles);
        String globalMassCentre = getMassCentre(allMarbles);
        // Calculate average manhattan distance of each player's marbles from their mass centre.
        double player1MassSpread = getSpreadFrom(player1massCentre, player1Marbles);
        double player2MassSpread = getSpreadFrom(player2massCentre, player2Marbles);
        // Calculate average manhattan distance of each player's marbles from the global mass centre.
        double player1CentreSpread = getSpreadFrom(globalMassCentre, player1Marbles);
        double player2CentreSpread = getSpreadFrom(globalMassCentre, player2Marbles);

        // Adjust strength based on heuristic.
        if(playerNumber == 1){
            strength -= player1MassSpread;
            strength -= player1CentreSpread;
            strength += player2MassSpread;
            strength += player2CentreSpread;
        } else if(playerNumber == 2){
            strength += player1MassSpread;
            strength += player1CentreSpread;
            strength -= player2MassSpread;
            strength -= player2CentreSpread;
        }
        return strength;
    }

    /**
     * @param from The 'centre' space to calculate the average distance from.
     * @param spaces The list of spaces which have to have their distances from 'from' calculated.
     * @return The average distance of all the spaces from 'from'.
     */
    protected double getSpreadFrom(String from, List<String> spaces){
        List<Integer> distances = new LinkedList<>();
        for(String space : spaces){
            String[] lineForSpaces = getLineForSpaces(space, from);
            if(lineForSpaces != null){
                List<String> centreLine = Arrays.asList( lineForSpaces );
                distances.add( Math.abs( centreLine.indexOf(space) - centreLine.indexOf(from) ) ); // centre space is always index 5.
            } else {
                List<List<String>> crossingLines = new LinkedList<>();
                for(String[] line : iGameState.LINES){ // Get lines that space is on.
                    List<String> thisLine = Arrays.asList(line);
                    if(thisLine.contains(space)){
                        crossingLines.add(thisLine);
                    }
                }
                int distance = 9; //Distance is never more than 8 from any space, so initialise with 9.
                for(List<String> line : crossingLines){
                    int index = line.indexOf(space);
                    if(space.charAt(0) < from.charAt(0)) { // Spaces above centre - look downwards.
                        for (int i = index; i < line.size() - 1; i++) { // Ignoring "out" spaces.
                            String neighbour = line.get(i);
                            String[] neighbourLine = getLineForSpaces(neighbour, from);
                            if (neighbourLine != null) {
                                List<String> fromLine = Arrays.asList(neighbourLine);
                                int dist = 0;
                                dist += (i - index);
                                dist += Math.abs(fromLine.indexOf(neighbour) - fromLine.indexOf(from));
                                if (dist < distance) {
                                    distance = dist;
                                }
                                break;
                            }
                        }
                    } else { // Spaces below centre - look upwards.
                        // TODO Try to remove duplications here and above.
                        for (int i = index; i > 0; i--) { // Ignoring "out" spaces.
                            String neighbour = line.get(i);
                            String[] neighbourLine = getLineForSpaces(neighbour, from);
                            if (neighbourLine != null) {
                                List<String> fromLine = Arrays.asList(neighbourLine);
                                int dist = 0;
                                dist += (index - i);
                                dist += Math.abs(fromLine.indexOf(neighbour) - fromLine.indexOf(from));
                                if (dist < distance) {
                                    distance = dist;
                                }
                                break;
                            }
                        }
                    }
                }
                distances.add(distance);
            }
        }
        double average = 0;
        for(Integer distance : distances){
            average += distance;
        }
        return average / distances.size();
    }

    /**
     * @param marbles A list of spaces containing marbles.
     * @return The space closest to the mass centre of the given marbles.
     */
    protected String getMassCentre(List<String> marbles){
        List<Character> rows = new LinkedList<>();
        List<Double> cols = new LinkedList<>();

        for(String space : marbles){
            rows.add(space.charAt(0));
        }
        double average = 0;
        for(char c : rows){
            average += c;
        }
        average /= rows.size();
        char centreRow = Character.toChars((int) Math.round(average))[0];

        for(String space : marbles){
            // Account for rows above and below 'centreRow' being narrower.
            double colWeight = (Math.abs(space.charAt(0)-'e')/2.0) - (Math.abs(centreRow-'e')/2.0);
            cols.add( Integer.parseInt(space.substring(1)) + colWeight );
        }
        average = 0;
        for(double d : cols){
            average += d;
        }
        average /= cols.size();
        int centreCol = (int) Math.round(average);
        return "" + centreRow + centreCol;
    }

    /**
     * TODO The method is horribly monolithic and has a lot of duplications.  But it works, so tidy up later.
     * @param state The game state to be analysed for possible moves.
     * @return A list of possible moves from the given game state.
     * @throws JSONException
     */
    protected List<JSONArray> findAvailableMoves(iGameState state) throws JSONException {
        List<JSONArray> moves = new LinkedList<>();
        List<JSONObject> singleMoves = new LinkedList<>();
        int currentPlayer = state.getCurrentPlayer();
        int opponentPlayer = 0;
        if(currentPlayer == 1){
            opponentPlayer = 2;
        } else if(currentPlayer == 2){
            opponentPlayer = 1;
        }
        if(opponentPlayer == 0){
            throw new JSONException("Opponent player is invalid!");
        }
        for(String[] line : iGameState.LINES){
            for(int i = 1; i < line.length-1; i++){ // Ignore ends because "out" space not held in game state.
                int playerAtSpace = state.getMarbleAt(line[i]);
                if(playerAtSpace == currentPlayer){
                    int playerAtNeighbour1 = -1;
                    int playerAtNeighbour2 = -1;
                    if(i > 1) { // Ignore first "out" space.
                        playerAtNeighbour1 = state.getMarbleAt(line[i - 1]);
                    }
                    if(i < line.length-2){ // Ignore last "out" space.
                        playerAtNeighbour2 = state.getMarbleAt(line[i + 1]);
                    }
                    // Identify single and in-line moves to neighbour 1 if an empty space.
                    if (playerAtNeighbour1 == 0){
                        // Identify single-marble move.
                        JSONArray fullMove = new JSONArray();
                        JSONObject move1 = new JSONObject();
                        move1.put("from", line[i]);
                        move1.put("to", line[i - 1]);
                        singleMoves.add(move1);
                        fullMove.put(move1);
                        moves.add(fullMove);
                        // Identify 2-marble in-line move.
                        if(playerAtNeighbour2 == currentPlayer){
                            JSONObject move2 = new JSONObject();
                            move2.put("from", line[i+1]);
                            move2.put("to", line[i]);
                            fullMove = new JSONArray();
                            fullMove.put(move1);
                            fullMove.put(move2);
                            moves.add(fullMove);
                            // Identify 3-marble in-line move.
                            if(line[i+2].length() == 2) { // Not an out space.
                                int nextInlineMarble = state.getMarbleAt(line[i + 2]);
                                if (nextInlineMarble == currentPlayer) {
                                    JSONObject move3 = new JSONObject();
                                    move3.put("from", line[i+2]);
                                    move3.put("to", line[i+1]);
                                    fullMove = new JSONArray();
                                    fullMove.put(move1);
                                    fullMove.put(move2);
                                    fullMove.put(move3);
                                    moves.add(fullMove);
                                }
                            }
                        }
                    // Identify all pushing moves to neighbour 1.
                    } else if(playerAtNeighbour1 == opponentPlayer) {
                        int pushing = 1;
                        if (line[i-2].length() >= 4) {
                            // Enemy marble next to edge.
                            pushing = 1;
                        } else if (state.getMarbleAt(line[i-2]) == 0) {
                            // Enemy marble has a free space behind it.
                            pushing = 1;
                        } else if (state.getMarbleAt(line[i-2]) == currentPlayer) {
                            // Push blocked by own marble.
                            pushing = 3;
                        } else if (state.getMarbleAt(line[i-2]) == opponentPlayer){
                            // Pushing 2 enemy marbles.
                            if (line[i-3].length() >= 4) {
                                // Enemy marble next to edge.
                                pushing = 2;
                            } else if (state.getMarbleAt(line[i-3]) == 0) {
                                // Enemy marble has a free space behind it.
                                pushing = 2;
                            } else if (state.getMarbleAt(line[i-3]) == currentPlayer) {
                                // Push blocked by own marble.
                                pushing = 3;
                            } else if (state.getMarbleAt(line[i-3]) == opponentPlayer){
                                // 3 marbles so can't push.
                                pushing = 3;
                            }
                        }
                        if(pushing == 1){ // Pushing a single marble.
                            // 2 pushing 1.
                            if(playerAtNeighbour2 == currentPlayer){
                                JSONArray fullMove = new JSONArray();
                                JSONObject move1 = new JSONObject();
                                move1.put("from", line[i - 1]);
                                move1.put("to", line[i - 2]);
                                fullMove.put(move1);
                                JSONObject move2 = new JSONObject();
                                move2.put("from", line[i]);
                                move2.put("to", line[i-1]);
                                fullMove.put(move2);
                                JSONObject move3 = new JSONObject();
                                move3.put("from", line[i+1]);
                                move3.put("to", line[i]);
                                fullMove.put(move3);
                                moves.add(fullMove);
                                // 3 pushing 1.
                                if(line[i+2].length() == 2) {
                                    int nextInlineMarble = state.getMarbleAt(line[i + 2]);
                                    if (nextInlineMarble == currentPlayer) {
                                        JSONObject move4 = new JSONObject();
                                        move4.put("from", line[i+2]);
                                        move4.put("to", line[i+1]);
                                        fullMove = new JSONArray();
                                        fullMove.put(move1);
                                        fullMove.put(move2);
                                        fullMove.put(move3);
                                        fullMove.put(move4);
                                        moves.add(fullMove);
                                    }
                                }
                            }
                        } else if(pushing == 2){ // Pushing two marbles.
                            if(playerAtNeighbour2 == currentPlayer){
                                if(line[i+2].length() == 2) {
                                    int nextInlineMarble = state.getMarbleAt(line[i + 2]);
                                    if (nextInlineMarble == currentPlayer) {
                                        JSONArray fullMove = new JSONArray();
                                        JSONObject move1 = new JSONObject();
                                        move1.put("from", line[i - 2]);
                                        move1.put("to", line[i - 3]);
                                        fullMove.put(move1);
                                        JSONObject move2 = new JSONObject();
                                        move2.put("from", line[i-1]);
                                        move2.put("to", line[i-2]);
                                        fullMove.put(move2);
                                        JSONObject move3 = new JSONObject();
                                        move3.put("from", line[i]);
                                        move3.put("to", line[i-1]);
                                        fullMove.put(move3);
                                        JSONObject move4 = new JSONObject();
                                        move4.put("from", line[i+1]);
                                        move4.put("to", line[i]);
                                        fullMove.put(move4);
                                        JSONObject move5 = new JSONObject();
                                        move5.put("from", line[i+2]);
                                        move5.put("to", line[i+1]);
                                        fullMove.put(move5);
                                        moves.add(fullMove);
                                    }
                                }
                            }
                        }
                    }
                    // Identify single and in-line moves to neighbour 2.
                    if (playerAtNeighbour2 == 0){
                        // Identify single-marble move.
                        JSONArray fullMove = new JSONArray();
                        JSONObject move1 = new JSONObject();
                        move1.put("from", line[i]);
                        move1.put("to", line[i + 1]);
                        singleMoves.add(move1);
                        fullMove.put(move1);
                        moves.add(fullMove);

                        // Identify 2-marble in-line move.
                        if(playerAtNeighbour1 == currentPlayer){
                            JSONObject move2 = new JSONObject();
                            move2.put("from", line[i-1]);
                            move2.put("to", line[i]);
                            fullMove = new JSONArray();
                            fullMove.put(move1);
                            fullMove.put(move2);
                            moves.add(fullMove);
                            // Identify 3-marble in-line move.
                            if(line[i-2].length() == 2) {
                                int nextInlineMarble = state.getMarbleAt(line[i - 2]);
                                if (nextInlineMarble == currentPlayer) {
                                    JSONObject move3 = new JSONObject();
                                    move3.put("from", line[i-2]);
                                    move3.put("to", line[i-1]);
                                    fullMove = new JSONArray();
                                    fullMove.put(move1);
                                    fullMove.put(move2);
                                    fullMove.put(move3);
                                    moves.add(fullMove);
                                }
                            }
                        }
                    // Identify all pushing moves to neighbour 2.
                    } else if(playerAtNeighbour2 == opponentPlayer) {
                        int pushing = 1;
                        if (line[i+2].length() >= 4) {
                            // Enemy marble next to edge.
                            pushing = 1;
                        } else if (state.getMarbleAt(line[i+2]) == 0) {
                            // Enemy marble has a free space behind it.
                            pushing = 1;
                        } else if (state.getMarbleAt(line[i+2]) == currentPlayer) {
                            // Push blocked by own marble.
                            pushing = 3;
                        } else if (state.getMarbleAt(line[i+2]) == opponentPlayer){
                            // Pushing 2 enemy marbles.
                            if (line[i+3].length() >= 4) {
                                // Enemy marble next to edge.
                                pushing = 2;
                            } else if (state.getMarbleAt(line[i+3]) == 0) {
                                // Enemy marble has a free space behind it.
                                pushing = 2;
                            } else if (state.getMarbleAt(line[i+3]) == currentPlayer) {
                                // Push blocked by own marble.
                                pushing = 3;
                            } else if (state.getMarbleAt(line[i+3]) == opponentPlayer){
                                // 3 marbles so can't push.
                                pushing = 3;
                            }
                        }
                        if(pushing == 1){ // Pushing a single marble.
                            // 2 pushing 1.
                            if(playerAtNeighbour1 == currentPlayer){
                                JSONArray fullMove = new JSONArray();
                                JSONObject move1 = new JSONObject();
                                move1.put("from", line[i + 1]);
                                move1.put("to", line[i + 2]);
                                fullMove.put(move1);
                                JSONObject move2 = new JSONObject();
                                move2.put("from", line[i]);
                                move2.put("to", line[i+1]);
                                fullMove.put(move2);
                                JSONObject move3 = new JSONObject();
                                move3.put("from", line[i-1]);
                                move3.put("to", line[i]);
                                fullMove.put(move3);
                                moves.add(fullMove);
                                // 3 pushing 1.
                                if(line[i-2].length() == 2) {
                                    int nextInlineMarble = state.getMarbleAt(line[i - 2]);
                                    if (nextInlineMarble == currentPlayer) {
                                        JSONObject move4 = new JSONObject();
                                        move4.put("from", line[i-2]);
                                        move4.put("to", line[i-1]);
                                        fullMove = new JSONArray();
                                        fullMove.put(move1);
                                        fullMove.put(move2);
                                        fullMove.put(move3);
                                        fullMove.put(move4);
                                        moves.add(fullMove);
                                    }
                                }
                            }
                        } else if(pushing == 2){ // Pushing two marbles.
                            if(playerAtNeighbour1 == currentPlayer){
                                if(line[i-2].length() == 2) {
                                    int nextInlineMarble = state.getMarbleAt(line[i - 2]);
                                    if (nextInlineMarble == currentPlayer) {
                                        JSONArray fullMove = new JSONArray();
                                        JSONObject move1 = new JSONObject();
                                        move1.put("from", line[i+2]);
                                        move1.put("to", line[i+3]);
                                        fullMove.put(move1);
                                        JSONObject move2 = new JSONObject();
                                        move2.put("from", line[i+1]);
                                        move2.put("to", line[i+2]);
                                        fullMove.put(move2);
                                        JSONObject move3 = new JSONObject();
                                        move3.put("from", line[i]);
                                        move3.put("to", line[i+1]);
                                        fullMove.put(move3);
                                        JSONObject move4 = new JSONObject();
                                        move4.put("from", line[i-1]);
                                        move4.put("to", line[i]);
                                        fullMove.put(move4);
                                        JSONObject move5 = new JSONObject();
                                        move5.put("from", line[i-2]);
                                        move5.put("to", line[i-1]);
                                        fullMove.put(move5);
                                        moves.add(fullMove);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Identify all possible side-step moves.
        for(String[] line : iGameState.LINES) {
            // Find all single-marble moves which will move off of this line.
            List<JSONObject> offLines = new LinkedList<>();
            for(int i = 1; i < line.length-1; i++) { // Check each space.  Never moving from an out space.
                for (JSONObject move : singleMoves) { // Check each move.
                    if (move.getString("from").equals(line[i])) { // A move can be made from this space
                        if (!move.getString("to").equals(line[i-1]) && !move.getString("to").equals(line[i+1])) {
                            offLines.add(move); // Move is to a different line.
                        }
                    }
                }
            }
            
            // Identify all 2- and 3-marble side-step moves from the offLines list.
            for(int i = 0; i < offLines.size(); i++){
                String iFrom = offLines.get(i).getString("from");
                String iTo = offLines.get(i).getString("to");
                for(int j = i+1; j < offLines.size(); j++){
                    String jFrom = offLines.get(j).getString("from");
                    String jTo = offLines.get(j).getString("to");
                    if(!iTo.equals(jTo) && areNeighbours(line, iFrom, jFrom)){
                        if(getLineForSpaces(iTo, jTo) != null) {
                            if(areNeighbours(getLineForSpaces(iTo, jTo), iTo, jTo)){
                                JSONArray sideStep = new JSONArray();
                                sideStep.put(offLines.get(i));
                                sideStep.put(offLines.get(j));
                                moves.add(sideStep);
                                for(int k = j+1; k < offLines.size(); k++){
                                    String kFrom = offLines.get(k).getString("from");
                                    String kTo = offLines.get(k).getString("to");
                                    if( !kTo.equals(jTo) && !kTo.equals(iTo) && !kFrom.equals(jFrom) && !kFrom.equals(iFrom)
                                            && ( areNeighbours(line, iFrom, kFrom) || areNeighbours(line, jFrom, kFrom) ) ){
                                        if( getLineForSpaces(iTo, kTo) != null && getLineForSpaces(jTo, kTo) != null ) {
                                            if( areNeighbours(getLineForSpaces(iTo, kTo), iTo, kTo) 
                                                    || areNeighbours(getLineForSpaces(jTo, kTo), jTo, kTo)) {
                                                sideStep = new JSONArray();
                                                sideStep.put(offLines.get(i));
                                                sideStep.put(offLines.get(j));
                                                sideStep.put(offLines.get(k));
                                                moves.add(sideStep);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }        
        return moves;
    }

    /**
     * @param line The line to check the spaces on.
     * @param space1 One of the spaces to check.
     * @param space2 The other space to check.
     * @return Whether or not the given spaces are neighbours on the given line.
     */
    protected static boolean areNeighbours(String[] line, String space1, String space2){
        List<String> lineList = Arrays.asList(line);
        return (lineList.indexOf(space1) == lineList.indexOf(space2) - 1
                || lineList.indexOf(space1) == lineList.indexOf(space2) + 1);
    }

    /**
     * @param space1 One space to check.
     * @param space2 The other space to check.
     * @return The line which both spaces share.  Null if they don't share a line.
     */
    protected static String[] getLineForSpaces(String space1, String space2){
        for(String[] l : iGameState.LINES){
            List<String> line = Arrays.asList(l);
            if(line.contains(space1) && line.contains(space2)){
                return (String[]) line.toArray();
            }
        }
        return null;
    }

    /**
     * Applies the given move to the given game state.
     * @param movesArray JSON array containing the move to be applied.
     * @param state The game state to have the move applied.
     * @throws JSONException
     */
    protected iGameState makeMove(JSONArray movesArray, iGameState state) throws JSONException {
        List<JSONObject> list = new LinkedList<>();
        List<Integer> marbles = new LinkedList<>();
        for(int i = 0; i < movesArray.length(); i++){
            list.add(movesArray.getJSONObject(i));
        }
        // Clear the spaces previously occupied by each marble.
        for(JSONObject obj : list){
            marbles.add(state.getMarbleAt(obj.getString("from")));
            state.removeMarble(obj.getString("from"));
        }
        for(JSONObject obj : list){
            if(obj.getString("to").length() >= 4){
                // When marble pushed off, increment the other player's score and strength.
                int pushedOutPlayer = marbles.remove(0);
                if(pushedOutPlayer == 1) {
                    state.incrementPlayer2score();
                } else if(pushedOutPlayer == 2){
                    state.incrementPlayer1score();
                }
            } else {
                // Put marble in its new space.
                int movedPlayer = marbles.remove(0);
                state.setMarble(obj.getString("to"), movedPlayer);
            }
        }
        if(state.getCurrentPlayer() == 1){
            state.setCurrentPlayer(2);
        } else if(state.getCurrentPlayer() == 2){
            state.setCurrentPlayer(1);
        }
        Integer player1score = state.getPlayer1score();
        Integer player2score = state.getPlayer2score();
        if(player1score == 6){
            state.setWinner(1);
        } else if(player2score == 6){
            state.setWinner(2);
        }
        state.notifyObservers(movesArray);
        return state;
    }

    @Override
    public void makeMove(String moves) throws JSONException {
        JSONArray movesArray = new JSONArray(moves);
        gameState = makeMove(movesArray, gameState);
    }

    @Override
    public void update(Observable o, Object arg) {
        gameState = (iGameState) o;
        // arg not used by AI players.
    }
    
    @Override
    public iPlayer getOpponent(){
        return this.opponent;
    }
    
    @Override
    public int getPlayerNumber(){
        return playerNumber;
    }
    
    @Override
    public boolean isHuman(){
        return isHuman;
    }

    @Override
    public void setOpponent(iPlayer opponent){
        this.opponent = opponent;
    }
}
