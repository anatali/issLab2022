package annotations;

import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import unibo.actor22comm.utils.ColorsOut;

@AccessSpec(
        protocol = AccessSpec.issProtocol.HTTP,
        url      = "http://localHost:8090/api/move"
)
public class AnnotationUsageDemo {
	
    //  line example: http://localHost:8090/api/move
    public static String getHostAddr(String functor, String line){
        Pattern pattern = Pattern.compile(functor);
        Matcher matcher = pattern.matcher(line);
        ColorsOut.outappl("line: " + line, ColorsOut.CYAN);
        String content = null;
        if( matcher.find()) {
//            int end   = matcher.end() ;
//            int start = matcher.start();
//            ColorsOut.outappl("start: " + start, ColorsOut.CYAN);
//            ColorsOut.outappl("end:   " + end,   ColorsOut.CYAN);
//            ColorsOut.outappl("group: " + matcher.groupCount(),   ColorsOut.CYAN);
            for( int i = 1; i<=5; i++ ) {
                ColorsOut.outappl("goup " + i + ":" + matcher.group(i),   ColorsOut.CYAN);          	
            }
            content = matcher.group(2)+":"+matcher.group(3);
//            String a = line.substring(start,end); //uguale a  matcher.group(0)
//            ColorsOut.outappl("a:                     " + a,   ColorsOut.CYAN);
         }
        return content;
    }
    
    
    public static void readProtocolAnnotation(Object element) {
        try {
            Class<?> clazz            = element.getClass();
            Annotation[] annotations  = clazz.getAnnotations();
             for (Annotation annot : annotations) {
                 if (annot instanceof AccessSpec) {
                	AccessSpec p  = (AccessSpec) annot;
                    ColorsOut.outappl("Tipo del protocollo: " + p.protocol(), ColorsOut.CYAN);
                    ColorsOut.outappl("Url del protocollo:  " + p.url(), ColorsOut.CYAN);
                    //http://localHost:8090/api/move
                    String v = getHostAddr("(\\w*)://([a-zA-Z]*):(\\d*)/(\\w*)/(\\w*)", p.url());
                    ColorsOut.outappl("v: " + v, ColorsOut.CYAN);
               }
            }
        } catch (Exception e) {
        	ColorsOut.outerr("readAnnotation |  ERROR:" + e.getMessage());
        }
    }
	
	public AnnotationUsageDemo() {
		readProtocolAnnotation( this );			
	}
 	
	public static void main( String[] args) {
		new AnnotationUsageDemo();
	}
}
