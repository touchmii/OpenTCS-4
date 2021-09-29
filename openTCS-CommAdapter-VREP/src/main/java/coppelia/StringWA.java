package coppelia;

public class StringWA
{
    String[] w;

    /**
     * 创建字符串数组
     * @param i 数组长度
     */
    public StringWA(int i)
    {
        w = new String[i];
    }

    /**
     * 初始化数组
     * @param i 数组长度
     */
    public void initArray(int i)
    {
        w = new String[i];
    }

    /**
     * 获取字符串数组
     * @return 字符串数组
     */
    public String[] getArray()
    {
        return w;
    }

    /**
     * 获取字符串数组长度
     * @return 字符串数组长度
     */
    public int getLength()
    {
        return w.length;
    }

    /**
     * 设置新字符串数组
     * @param i 字符串数组长度
     * @return 新的字符串数组
     */
    public String[] getNewArray(int i)
    {
        w = new String[i];
        return w;
    }
}