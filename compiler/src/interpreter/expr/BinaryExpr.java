package interpreter.expr;

public abstract class BinaryExpr implements Expr {
  protected Expr l,r;
  public String toString() {
    return this.getClass().getName();
  }
}
