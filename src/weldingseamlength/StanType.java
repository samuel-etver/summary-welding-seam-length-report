
package weldingseamlength;


public enum StanType {
    NONE(null),
    ID("ID"),
    OD("OD");
    
    private StanType(String text) {
        m_Type = text;        
    }
    
    private String m_Type;
    
    public String getType() {
        return m_Type;
    }
};
