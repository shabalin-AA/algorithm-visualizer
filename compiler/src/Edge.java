public class Edge {
  public int target;
  public int source;
  public int id;
  public boolean branch;

  public Edge(int id, int source, int target) {
    this.id = id;
    this.source = source;
    this.target = target;
    this.branch = true;
  }

  public Edge(int id, int source, int target, boolean branch) {
    this.id = id;
    this.source = source;
    this.target = target;
    this.branch = branch;
  }
}
