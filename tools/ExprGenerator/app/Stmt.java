package jlox.expression;
import jlox.lexer.Token;
import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitBlockStmt(Block expr);
		R visitFunctionStmt(Function expr);
		R visitIFStmt(IF expr);
		R visitExpressionStmt(Expression expr);
		R visitPrintStmt(Print expr);
		R visitReturnStmt(Return expr);
		R visitVarStmt(Var expr);
		R visitWhileStmt(While expr);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    public static class Block extends Stmt {
	    public final List<Stmt> statements;
	
	    public Block(List<Stmt> statements) {
	        this.statements = statements;
	    }
	
	    @Override
	    public <R> R accept(Visitor<R> visitor) {
	      return visitor.visitBlockStmt(this);
	    }
	}
	
	public static class Function extends Stmt {
	    public final Token name;
		public final List<Token> params;
		public final List<Stmt> body;
	
	    public Function(Token name, List<Token> params, List<Stmt> body) {
	        this.name = name;
			this.params = params;
			this.body = body;
	    }
	
	    @Override
	    public <R> R accept(Visitor<R> visitor) {
	      return visitor.visitFunctionStmt(this);
	    }
	}
	
	public static class IF extends Stmt {
	    public final Expr expression;
		public final Stmt trueBranch;
		public final Stmt falseBranch;
	
	    public IF(Expr expression, Stmt trueBranch, Stmt falseBranch) {
	        this.expression = expression;
			this.trueBranch = trueBranch;
			this.falseBranch = falseBranch;
	    }
	
	    @Override
	    public <R> R accept(Visitor<R> visitor) {
	      return visitor.visitIFStmt(this);
	    }
	}
	
	public static class Expression extends Stmt {
	    public final Expr expression;
	
	    public Expression(Expr expression) {
	        this.expression = expression;
	    }
	
	    @Override
	    public <R> R accept(Visitor<R> visitor) {
	      return visitor.visitExpressionStmt(this);
	    }
	}
	
	public static class Print extends Stmt {
	    public final Expr expression;
	
	    public Print(Expr expression) {
	        this.expression = expression;
	    }
	
	    @Override
	    public <R> R accept(Visitor<R> visitor) {
	      return visitor.visitPrintStmt(this);
	    }
	}
	
	public static class Return extends Stmt {
	    public final Token keyword;
		public final Expr value;
	
	    public Return(Token keyword, Expr value) {
	        this.keyword = keyword;
			this.value = value;
	    }
	
	    @Override
	    public <R> R accept(Visitor<R> visitor) {
	      return visitor.visitReturnStmt(this);
	    }
	}
	
	public static class Var extends Stmt {
	    public final Token name;
		public final Expr initializer;
	
	    public Var(Token name, Expr initializer) {
	        this.name = name;
			this.initializer = initializer;
	    }
	
	    @Override
	    public <R> R accept(Visitor<R> visitor) {
	      return visitor.visitVarStmt(this);
	    }
	}
	
	public static class While extends Stmt {
	    public final Expr condition;
		public final Stmt body;
	
	    public While(Expr condition, Stmt body) {
	        this.condition = condition;
			this.body = body;
	    }
	
	    @Override
	    public <R> R accept(Visitor<R> visitor) {
	      return visitor.visitWhileStmt(this);
	    }
	}
}