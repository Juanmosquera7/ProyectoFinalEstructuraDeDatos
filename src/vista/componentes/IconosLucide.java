package vista.componentes;

import javax.swing.Icon;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

/** Iconos Lucide SVG renderizados con Java2D, sin dependencias externas. */
public final class IconosLucide {
    private static final String CLASSPATH = "/recursos/iconos/";
    private static final String DESARROLLO = "src/recursos/iconos/";
    private static final Map<String, List<Figura>> CACHE = new HashMap<>();
    private IconosLucide() { }

    /** Devuelve null si el SVG no está disponible o no se puede interpretar. */
    public static Icon cargar(String archivo, Color color, int tamano) {
        if (archivo == null || !archivo.matches("[a-z0-9-]+\\.svg") || color == null || tamano <= 0) {
            return null;
        }
        List<Figura> figuras;
        synchronized (CACHE) {
            figuras = CACHE.get(archivo);
            if (figuras == null) {
                figuras = leer(archivo);
                if (figuras == null) return null;
                CACHE.put(archivo, figuras);
            }
        }
        List<Figura> formas = figuras;
        return new Icon() {
            @Override public int getIconWidth() { return tamano; }
            @Override public int getIconHeight() { return tamano; }
            @Override public void paintIcon(Component c, Graphics graphics, int x, int y) {
                Graphics2D g = (Graphics2D) graphics.create();
                try {
                    g.translate(x, y);
                    g.scale(tamano / 24.0, tamano / 24.0);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    g.setColor(color);
                    for (Figura f : formas) {
                        if (f.relleno) g.fill(f.trazo);
                        else {
                            g.setStroke(new BasicStroke(f.ancho, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                            g.draw(f.trazo);
                        }
                    }
                } finally { g.dispose(); }
            }
        };
    }

    private record Figura(Shape trazo, float ancho, boolean relleno) { }

    private static List<Figura> leer(String archivo) {
        try (InputStream in = abrir(archivo)) {
            if (in == null) { System.err.println("[ICONOS] Falta: " + archivo); return null; }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            Element raiz = factory.newDocumentBuilder().parse(in).getDocumentElement();
            ArrayList<Figura> figuras = new ArrayList<>();
            extraer(raiz, figuras, numero(raiz, "stroke-width", 2));
            return figuras.isEmpty() ? null : List.copyOf(figuras);
        } catch (Exception ex) {
            System.err.println("[ICONOS] Error en " + archivo + ": " + ex.getMessage());
            return null;
        }
    }

    private static InputStream abrir(String archivo) throws IOException {
        InputStream in = IconosLucide.class.getResourceAsStream(CLASSPATH + archivo);
        if (in != null) return in;
        Path p = Paths.get(DESARROLLO, archivo);
        return Files.isRegularFile(p) ? Files.newInputStream(p) : null;
    }

    private static double numero(Element e, String atributo, double defecto) {
        String v = e.getAttribute(atributo);
        if (v == null || v.isBlank()) return defecto;
        try { return Double.parseDouble(v); } catch (NumberFormatException ex) { return defecto; }
    }

    private static void extraer(Element e, List<Figura> resultado, double anchoPadre) {
        String etiqueta = e.getTagName();
        double ancho = numero(e, "stroke-width", anchoPadre);
        Shape forma = null;
        switch (etiqueta) {
            case "path" -> forma = camino(e.getAttribute("d"));
            case "line" -> {
                Path2D.Double p = new Path2D.Double();
                p.moveTo(numero(e,"x1",0),numero(e,"y1",0));
                p.lineTo(numero(e,"x2",0),numero(e,"y2",0));
                forma = p;
            }
            case "rect" -> {
                double x=numero(e,"x",0),y=numero(e,"y",0),w=numero(e,"width",0),h=numero(e,"height",0),r=numero(e,"rx",0);
                forma = new RoundRectangle2D.Double(x,y,w,h,2*r,2*r);
            }
            case "circle" -> {
                double x=numero(e,"cx",0),y=numero(e,"cy",0),r=numero(e,"r",0);
                forma = new Ellipse2D.Double(x-r,y-r,2*r,2*r);
            }
            case "ellipse" -> {
                double x=numero(e,"cx",0),y=numero(e,"cy",0),rx=numero(e,"rx",0),ry=numero(e,"ry",0);
                forma = new Ellipse2D.Double(x-rx,y-ry,2*rx,2*ry);
            }
            case "polyline", "polygon" -> {
                String[] tokens=e.getAttribute("points").trim().split("[\\s,]+");
                Path2D.Double p = new Path2D.Double();
                for (int i=0;i+1<tokens.length;i+=2) {
                    double x=Double.parseDouble(tokens[i]), y=Double.parseDouble(tokens[i+1]);
                    if(i==0)p.moveTo(x,y);else p.lineTo(x,y);
                }
                if (etiqueta.equals("polygon")) p.closePath();
                forma=p;
            }
        }
        if (forma != null) {
            boolean relleno = "currentColor".equals(e.getAttribute("fill"));
            resultado.add(new Figura(forma, (float) ancho, relleno));
        }
        NodeList hijos = e.getChildNodes();
        for(int i=0;i<hijos.getLength();i++) {
            Node n=hijos.item(i);
            if(n instanceof Element hijo) extraer(hijo,resultado,ancho);
        }
    }

    private static Path2D camino(String d) {
        return new Parser(d).parse();
    }

    private static final class Parser {
        final String d; int pos=0; char cmd=' ';
        double x=0,y=0,sx=0,sy=0, cx=0,cy=0, qx=0,qy=0;
        char previo=' ';
        final Path2D.Double p = new Path2D.Double();
        Parser(String d){this.d=d;}
        Path2D parse() {
            int seguro=0;
            while (saltos() && pos<d.length() && seguro++<10000) {
                char c=d.charAt(pos);
                if (Character.isLetter(c)) { cmd=c;pos++; }
                else if (cmd==' ') throw new IllegalArgumentException("SVG sin comando: " + d);
                boolean relativo=Character.isLowerCase(cmd);
                char tipo=Character.toUpperCase(cmd);
                double xx,yy,x1,y1,x2,y2;
                switch(tipo) {
                    case 'M' -> {
                        xx=num();yy=num();x=relativo?x+xx:xx;y=relativo?y+yy:yy;
                        p.moveTo(x,y);sx=x;sy=y;cmd=relativo?'l':'L';previo='M';
                    }
                    case 'L' -> { xx=num();yy=num();x=relativo?x+xx:xx;y=relativo?y+yy:yy;p.lineTo(x,y);previo='L'; }
                    case 'H' -> { xx=num();x=relativo?x+xx:xx;p.lineTo(x,y);previo='H'; }
                    case 'V' -> { yy=num();y=relativo?y+yy:yy;p.lineTo(x,y);previo='V'; }
                    case 'C' -> {
                        x1=num();y1=num();x2=num();y2=num();xx=num();yy=num();
                        if(relativo){x1+=x;y1+=y;x2+=x;y2+=y;xx+=x;yy+=y;}
                        p.curveTo(x1,y1,x2,y2,xx,yy);cx=x2;cy=y2;x=xx;y=yy;previo='C';
                    }
                    case 'S' -> {
                        x1=(previo=='C'||previo=='S')?2*x-cx:x;
                        y1=(previo=='C'||previo=='S')?2*y-cy:y;
                        x2=num();y2=num();xx=num();yy=num();
                        if(relativo){x2+=x;y2+=y;xx+=x;yy+=y;}
                        p.curveTo(x1,y1,x2,y2,xx,yy);cx=x2;cy=y2;x=xx;y=yy;previo='S';
                    }
                    case 'Q' -> {
                        x1=num();y1=num();xx=num();yy=num();
                        if(relativo){x1+=x;y1+=y;xx+=x;yy+=y;}
                        p.quadTo(x1,y1,xx,yy);qx=x1;qy=y1;x=xx;y=yy;previo='Q';
                    }
                    case 'T' -> {
                        x1=(previo=='Q'||previo=='T')?2*x-qx:x;
                        y1=(previo=='Q'||previo=='T')?2*y-qy:y;
                        xx=num();yy=num();if(relativo){xx+=x;yy+=y;}
                        p.quadTo(x1,y1,xx,yy);qx=x1;qy=y1;x=xx;y=yy;previo='T';
                    }
                    case 'A' -> {
                        double rx=num(),ry=num(),rot=num();boolean largo=num()!=0,barrido=num()!=0;
                        xx=num();yy=num();if(relativo){xx+=x;yy+=y;}
                        arco(p,x,y,rx,ry,rot,largo,barrido,xx,yy);x=xx;y=yy;previo='A';
                    }
                    case 'Z' -> {p.closePath();x=sx;y=sy;previo='Z';cmd=' ';}
                    default -> throw new IllegalArgumentException("Comando SVG no soportado: " + cmd);
                }
            }
            if(seguro>=10000) throw new IllegalArgumentException("SVG demasiado complejo");
            return p;
        }
        boolean saltos(){
            while(pos<d.length() && (Character.isWhitespace(d.charAt(pos)) || d.charAt(pos)==','))pos++;
            return pos<d.length();
        }
        double num(){
            if(!saltos())throw new IllegalArgumentException("Falta número SVG");
            int start=pos;
            if(d.charAt(pos)=='-'||d.charAt(pos)=='+')pos++;
            boolean digito=false;
            while(pos<d.length() && Character.isDigit(d.charAt(pos))){pos++;digito=true;}
            if(pos<d.length() && d.charAt(pos)=='.') {
                pos++;while(pos<d.length() && Character.isDigit(d.charAt(pos))){pos++;digito=true;}
            }
            if(!digito)throw new IllegalArgumentException("Número SVG incorrecto");
            if(pos<d.length() && (d.charAt(pos)=='e'||d.charAt(pos)=='E')) {
                pos++;if(pos<d.length()&&(d.charAt(pos)=='+'||d.charAt(pos)=='-'))pos++;
                while(pos<d.length()&&Character.isDigit(d.charAt(pos)))pos++;
            }
            return Double.parseDouble(d.substring(start,pos));
        }
    }

    // Conversión de arco elíptico SVG de extremos a segmentos cúbicos.
    private static void arco(Path2D.Double p,double x0,double y0,double rx,double ry,double grados,
                             boolean grande,boolean barrido,double x1,double y1) {
        rx=Math.abs(rx);ry=Math.abs(ry);
        if(rx==0||ry==0||(x0==x1&&y0==y1)){p.lineTo(x1,y1);return;}
        double phi=Math.toRadians(grados%360),cos=Math.cos(phi),sin=Math.sin(phi);
        double dx=(x0-x1)/2,dy=(y0-y1)/2;
        double xp=cos*dx+sin*dy,yp=-sin*dx+cos*dy;
        double lambda=xp*xp/(rx*rx)+yp*yp/(ry*ry);
        if(lambda>1){double scale=Math.sqrt(lambda);rx*=scale;ry*=scale;}
        double numer=Math.max(0,(rx*rx*ry*ry-rx*rx*yp*yp-ry*ry*xp*xp));
        double denom=rx*rx*yp*yp+ry*ry*xp*xp;
        double factor=(grande==barrido?-1:1)*Math.sqrt(denom==0?0:numer/denom);
        double cxp=factor*rx*yp/ry,cyp=-factor*ry*xp/rx;
        double cx=cos*cxp-sin*cyp+(x0+x1)/2,cy=sin*cxp+cos*cyp+(y0+y1)/2;
        double start=Math.atan2((yp-cyp)/ry,(xp-cxp)/rx);
        double dx2=(-xp-cxp)/rx,dy2=(-yp-cyp)/ry;
        double delta=Math.atan2(Math.sin(Math.atan2(dy2,dx2)-start),Math.cos(Math.atan2(dy2,dx2)-start));
        if(barrido&&delta<0)delta+=Math.PI*2;
        if(!barrido&&delta>0)delta-=Math.PI*2;
        int parts=(int)Math.ceil(Math.abs(delta)/(Math.PI/2));
        for(int i=0;i<parts;i++) {
            double a=start+delta*i/parts,b=start+delta*(i+1)/parts;
            double k=4.0/3.0*Math.tan((b-a)/4.0);
            double c1x=Math.cos(a)-k*Math.sin(a),c1y=Math.sin(a)+k*Math.cos(a);
            double c2x=Math.cos(b)+k*Math.sin(b),c2y=Math.sin(b)-k*Math.cos(b);
            double ex=Math.cos(b),ey=Math.sin(b);
            p.curveTo(cx+cos*rx*c1x-sin*ry*c1y,cy+sin*rx*c1x+cos*ry*c1y,
                    cx+cos*rx*c2x-sin*ry*c2y,cy+sin*rx*c2x+cos*ry*c2y,
                    cx+cos*rx*ex-sin*ry*ey,cy+sin*rx*ex+cos*ry*ey);
        }
    }
}
