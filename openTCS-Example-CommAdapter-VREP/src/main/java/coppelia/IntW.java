package coppelia;

public class IntW
{
    int w;

    /**
     * 创建int变量
     * @param i 数值
     */
    public IntW(int i)
    {
        w = i;
    }

    /**
     * 设置新的数值
     * @param i 新的数值
     */
    public void setValue(int i)
    {
        w = i;
    }

    /**
     * 获取数值
     * @return 数值
     */
    public int getValue()
    {
        return w;
    }
}
