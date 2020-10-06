package coppelia;

public class FloatW
{
    float w;

    /**
     * 创建浮点数
     * @param f 数值
     */
    public FloatW(float f)
    {
        w = f;
    }

    /**
     * 设置新的数值
     * @param i 数值
     */
    public void setValue(float i)
    {
        w = i;
    }

    /**
     * 获取数值
     * @return 数值
     */
    public float getValue()
    {
        return w;
    }
}