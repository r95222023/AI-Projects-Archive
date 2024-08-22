import java.util.HashMap;

public class Cell {
    private boolean cellOK;
    private boolean visited;
    private HashMap<String, Integer> flags;

    public Cell(){
        cellOK = false;
        visited = false;
        flags = new HashMap<>();
        flags.put(Utils.pit, 0);
        flags.put(Utils.okNeighbor, 0);
        flags.put(Utils.wumpus, 0);
        flags.put(Utils.breeze, 0);
        flags.put(Utils.stench, 0);
    }

    public void updateFlags(String obj, Integer flag){
        int currFlag = flags.get(obj);
        flags.put(obj, flag > 0? flag+currFlag: flag);
    }

    // return a copied cell for resultveWorld with backtracking algorithm.
    public Cell copy(){
        Cell copy = new Cell();
        copy.cellOK = cellOK;
        copy.visited = visited;
        copy.flags = new HashMap<>();
        String[] keys = {Utils.okNeighbor, Utils.pit, Utils.wumpus, Utils.breeze,Utils.stench};
        for (String key : keys) {
            copy.flags.put(key, flags.get(key));
        }
//        copy.flags.put(Utils.okNeighbor, flags.get(Utils.okNeighbor));
//        copy.flags.put(Utils.pit, flags.get(Utils.pit));
//        copy.flags.put(Utils.wumpus, flags.get(Utils.wumpus));
//        copy.flags.put(Utils.breeze, flags.get(Utils.breeze));
//        copy.flags.put(Utils.stench, flags.get(Utils.stench));
        return copy;
    }

    public boolean isCellOK() {
        return cellOK;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setCellOK(boolean cellOK) {
        this.cellOK = cellOK;
    }

    public HashMap<String, Integer> getFlags() {
        return flags;
    }

    public void setFlags(HashMap<String, Integer> flags) {
        this.flags = flags;
    }
}
