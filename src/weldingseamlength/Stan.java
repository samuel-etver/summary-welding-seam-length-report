package weldingseamlength;

public class Stan {    
    private Double m_SeamLength;
    private StanDiameter m_StanDiameter;
    private StanType m_StanType;
    private Integer m_StanNumber;
    
    
    public double getSeamLength() {
        return m_SeamLength;
    }    
    
    
    public StanDiameter getStanDiameter() {
        return m_StanDiameter;
    }
    
    
    public StanType getStanType() {
        return m_StanType;
    }

    
    public Integer getStanNumber() {
        return m_StanNumber;
    }
    
    
    public static class Builder {
        public StanDiameter stanDiameter;
        public StanType stanType;
        public Double seamLength;
        public Integer stanNumber;
        
        
        public Stan build() {
            if (seamLength == null
                || stanNumber == null
                || stanType == null
                || stanDiameter == null) {                
                return null;
            }
            
            final Stan stan = new Stan();
            stan.m_SeamLength = seamLength;
            stan.m_StanType = stanType;
            stan.m_StanDiameter = stanDiameter;
            stan.m_StanNumber = stanNumber;
            return stan;
        }
    }
}
