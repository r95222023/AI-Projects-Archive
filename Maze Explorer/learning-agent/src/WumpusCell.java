// Model of the wumpus world represented as 4 by 4 matrix with cell in the matrix having following variables
//	1. safe - If the cell is safe or not
//  2. pit - Probability of pit being present in the cell
//  3. wumpus - Probability of wumpus being present in the cell
//  4. visited - If the cell has already been visited
//  5. pitExists - To indicate if there exists a pit in the cell
//  6. wumpusExists - To indicate if there exists a wumpus in the cell

public class WumpusCell {
    public int safe = 0;
    public int pit = 0;
    public int breeze = 0;
    public int stench = 0;
    public int wumpus = 0;
    public int gold = 0;
    public int visited = 0;
    public int pitExists = 1;
    public int wumpusExists = 0;
    public int frontier = Integer.MAX_VALUE;
}
