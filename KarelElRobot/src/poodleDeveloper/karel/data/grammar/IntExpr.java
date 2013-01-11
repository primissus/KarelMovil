package grammar;

public class IntExpr{
	 //{ Decimal | Identificador | "PRECEDE" "(" ExpresionEntera ")" | "SUCEDE" "(" ExpresionEntera ")" }
	public static final int VALOR_DECIMAL=0x1;
	public static final int VALOR_IDENTIFICADOR=0x2;
	public static final int VALOR_PRECEDE=0x3;
	public static final int VALOR_SUCEDE=0x4;
	
	public int valorDecimal;
	public String valorIdentificador;
	public IntExpr argumento;
	public int tipo;
	
	public IntExpr(){
		
	}
	
	public IntExpr(int num){
		this.tipo = IntExpr.VALOR_DECIMAL;
		this.valorDecimal = num;
	}
	
	public void crearDecimal(int val){
		this.tipo = IntExpr.VALOR_DECIMAL;
		this.valorDecimal = val;
	}
	public void crearIdentificador(String val){
		this.tipo = IntExpr.VALOR_IDENTIFICADOR;
		this.valorIdentificador = val;
	}
	public void crearPrecede(IntExpr arg){
		this.tipo = IntExpr.VALOR_PRECEDE;
		this.argumento = arg;
	}
	public void crearSucede(IntExpr arg){
		this.tipo = IntExpr.VALOR_SUCEDE;
		this.argumento = arg;
	}
	
	public String toString(){
		switch(this.tipo){
			case IntExpr.VALOR_DECIMAL:
				return ""+this.valorDecimal;
			case IntExpr.VALOR_IDENTIFICADOR:
				return this.valorIdentificador;
			case IntExpr.VALOR_PRECEDE:
				return "precede("+this.argumento+")";
			case IntExpr.VALOR_SUCEDE:
				return "sucede("+this.argumento+")";
		}
		return "";
	}
}