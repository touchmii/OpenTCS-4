package coppelia;

public class FloatWAA
{
    FloatWA[] w;

    /**
     * 创建多维浮点数组
     * @param i 浮点数组层数
     */
    public FloatWAA(int i)
    {
        w = new FloatWA[i];
    }

    public void initArray(int i)
    {
        w = new FloatWA[i];
    }

    public FloatWA[] getArray()
    {
        return w;
    }

    public int getLength()
    {
        return w.length;
    }

    public FloatWA[] getNewArray(int i)
    {
        w = new FloatWA[i];
        for (int k = 0; k < i; k++)
            w[k] = new FloatWA(1);
        return w;
    }
}