package coppelia;

public class StringW
{
    String w;

    /**
     * 创建字符串
     * @param s 字符
     */
    public StringW(String s)
    {
        w = new String(s);
    }

    /**
     * 设置字符串
     * @param s 字符串
     */
    public void setValue(String s)
    {
        w = new String(s);
    }

    /**
     * 获取字符串
     * @return 字符串
     */
    public String getValue()
    {
        return w;
    }
}