package coppelia;

public class StringW
{
    String w;

    public StringW(String s)
    {
        w = new String(s);
    }

    public void setValue(String s)
    {
        w = new String(s);
    }

    public String getValue()
    {
        return w;
    }
}