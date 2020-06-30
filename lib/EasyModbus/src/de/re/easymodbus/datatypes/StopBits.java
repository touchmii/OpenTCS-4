package de.re.easymodbus.datatypes;

public enum StopBits 
{
	One (1),
	OnePointFive (3),
	Two (2);
	
	private int value;
    
    private StopBits(int value) 
    {
        this.value = value;
    }
    
    public int getValue() 
    {
        return value;
    }
}
