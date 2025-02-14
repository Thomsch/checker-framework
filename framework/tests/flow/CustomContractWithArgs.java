import org.checkerframework.framework.qual.ConditionalPostconditionAnnotation;
import org.checkerframework.framework.qual.PostconditionAnnotation;
import org.checkerframework.framework.qual.PreconditionAnnotation;
import org.checkerframework.framework.qual.QualifierArgument;
import org.checkerframework.framework.testchecker.util.ValueTypeAnno;

public class CustomContractWithArgs {
  // Postcondition for Value
  @PostconditionAnnotation(qualifier = ValueTypeAnno.class)
  @interface EnsuresValue {
    public String[] value();

    @QualifierArgument("value")
    public int targetValue();
  }

  // Conditional postcondition for LTLengthOf
  @ConditionalPostconditionAnnotation(qualifier = ValueTypeAnno.class)
  @interface EnsuresValueIf {
    public boolean result();

    public String[] expression();

    @QualifierArgument("value")
    public int targetValue();
  }

  // Precondition for LTLengthOf
  @PreconditionAnnotation(qualifier = ValueTypeAnno.class)
  @interface RequiresValue {
    public String[] value();

    @QualifierArgument("value")
    public int targetValue();
  }

  class Base {
    Object o;

    @ValueTypeAnno(10)
    Object o10;

    @EnsuresValue(value = "o", targetValue = 10)
    void ensures() {
      o = o10;
    }

    @EnsuresValue(value = "o", targetValue = 9)
    // :: error: (contracts.postcondition)
    void ensuresWrong() {
      o = o10;
    }

    void ensuresUse() {
      ensures();
      o10 = o;
    }

    @EnsuresValueIf(expression = "o", targetValue = 10, result = true)
    boolean ensuresIf(boolean b) {
      if (b) {
        o = o10;
        return true;
      } else {
        return false;
      }
    }

    @RequiresValue(value = "o", targetValue = 10)
    void requires() {
      o10 = o;
    }

    void use(boolean b) {
      if (ensuresIf(b)) {
        o10 = o;
        requires();
      }
      // :: error: (assignment)
      o10 = o;
      // :: error: (contracts.precondition)
      requires();
    }
  }
}
