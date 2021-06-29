
package weldingseamlength;

public enum StanDiameter {
    STAN_NONE(0),
    STAN_800(800),
    STAN_1000(1000);
    
    private StanDiameter(int diameter) {
        m_Diameter = diameter;
    }
    
    private final int m_Diameter;
    
    
    public int getDiameter() {
        return m_Diameter;
    }
};

