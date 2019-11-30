package lexical_analuzer;

public class ValueImpl implements Value {

    private ValueType type;
    private String value;
    int iValue=0;
    double dValue=0.0;
    boolean bValue=false;

    public ValueImpl(String s) {
        type = ValueType.STRING;
        value = s;
        try {
			iValue=Integer.parseInt(s);
		} catch (Exception e){}
		try {
			dValue=Double.parseDouble(s);
		} catch (Exception e){}
		if (s!=""){
			bValue=true;
}        
    }

    public ValueImpl(int i) {
        type = ValueType.INTEGER;
        value=String.valueOf(i);
        iValue=i;
	dValue=(double)i;
	if (i!=0){
	bValue=true;
       }
    }

    public ValueImpl(double d) {
        type = ValueType.DOUBLE;
        value = String.valueOf(d);
        dValue=d;
	iValue=(int)d;
	if (d!=0.00){
            bValue=true;
        }
    }

    public ValueImpl(boolean b) {
        type = ValueType.BOOL;
        value = String.valueOf(b);
        if (b==true){
            bValue=true;
            iValue=1;
            dValue=1.00;
        }
    }

    @Override
    public String getSValue() {
        return value;
    }

    @Override
    public int getIValue() {
        return iValue;
    }

    @Override
    public double getDValue() {
        return dValue;
    }

    @Override
    public boolean getBValue() {
        return bValue;
    }

    @Override
    public ValueType getType() {
        return type;
    }

    public String toString() {
        return getSValue();
    }
}
