package xmlrmi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * @author matthieudelgado
 * @author marcgregoire
 * Cette annotation permet de marquer les champs d'une classe a envoyer au serveur
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XMLRMIField {
  String serializationName() ; // nom à utiliser dans la sérialisation XML-RMI
  String serializationType() ; // type XML-RMI à utiliser dans la sérialisation
}