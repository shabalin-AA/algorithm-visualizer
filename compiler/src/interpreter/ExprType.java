package interpreter;

enum ExprType {
  NEXT_EXPR,
  LIST,
  ASSIGN,
  EQ, NOT_EQ,
  GT, LS,
  ADD, SUB,
  MUL, DIV,
  NUM,
  ID,
  UNDEFINED
}
