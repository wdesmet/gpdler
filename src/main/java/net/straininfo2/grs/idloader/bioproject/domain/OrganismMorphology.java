package net.straininfo2.grs.idloader.bioproject.domain;

import java.util.Set;

public class OrganismMorphology {

    private Gram gram;

    private Boolean enveloped;

    private Set<Shape> shapes;
    
    private Boolean endospores;
    
    private Boolean motility;

    public enum Gram {
        NEGATIVE,
        POSITIVE;

        @Override
        public String toString() {
            if (this == NEGATIVE) {
                return "eNegative";
            }
            else {
                return "ePositive";
            }
        }

        public static Gram fromString(String gram) {
            return gram.equals("ePositive") ? POSITIVE : NEGATIVE;
        }
    }

    public enum Shape {
        BACILLI("eBacilli"),
        COCCI("eCocci"),
        SPIRILLA("eSpirilla"),
        COCCOBACILLI("eCoccobacilli"),
        FILAMENTOUS("eFilamentous"),
        VIBRIOS("eVibrios"),
        FUSOBACTERIA("eFusobacteria"),
        SQUARE_SHAPED("eSquareShaped"),
        CURVED_SHAPED("eCurvedShaped"),
        TAILED("eTailed");

        private final String xmlName;

        Shape(String xmlName) {
            this.xmlName = xmlName;
        }

        @Override
        public String toString() {
            return xmlName;
        }

        public static Shape fromString(String name) {
            for (Shape shape : Shape.values()) {
                if (shape.toString().equals(name)) {
                    return shape;
                }
            }
            return null;
        }
    }

    public Gram getGram() {
        return gram;
    }

    public void setGram(Gram gram) {
        this.gram = gram;
    }

    public Boolean getEnveloped() {
        return enveloped;
    }

    public void setEnveloped(Boolean enveloped) {
        this.enveloped = enveloped;
    }

    public Set<Shape> getShapes() {
        return shapes;
    }

    public void setShapes(Set<Shape> shapes) {
        this.shapes = shapes;
    }

    public Boolean getEndospores() {
        return endospores;
    }

    public void setEndospores(Boolean endospores) {
        this.endospores = endospores;
    }

    public Boolean getMotility() {
        return motility;
    }

    public void setMotility(Boolean motility) {
        this.motility = motility;
    }

}
