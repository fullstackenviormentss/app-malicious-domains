import org.python.util.PythonInterpreter;
import org.python.core.*;
import com.amazonaws.services.lambda.runtime.Context;
 
public class Classify {

  public static final class SharedPythonInterpreter {
    public static final PythonInterpreter interpreter = new PythonInterpreter();
  }

  public static class RequestClass {
    String domain;

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public RequestClass(String domain) { this.domain = domain; }

    public RequestClass() {}
  }

  public static class ResponseClass {
    int label;
    double class0Prob;
    double class1Prob;
    
    public int getLabel() { return label; }
    public void setLabel(int label) { this.label = label; }

    public double getClass0Prob() { return class0Prob; }
    public void setClass0Prob(double class0Prob) { this.class0Prob = class0Prob; }
    
    public double getClass1Prob() { return class1Prob; }
    public void setClass1Prob(double class1Prob) { this.class1Prob = class1Prob; }
    
    public ResponseClass(double label, double class0Prob, double class1Prob) {
      this.label = (int) label;
      this.class0Prob = class0Prob;
      this.class1Prob = class1Prob;
    }

    public ResponseClass() {}

  }


  public static ResponseClass myHandler(RequestClass request, Context context) throws PyException {
      
    PyModule module = new PyModule();
    
    //Prediction code is in pymodule.py
    double[] predictions = module.predict(request.domain);
    
    return new ResponseClass(predictions[0], predictions[1], predictions[2]);
  }

  public static class PyModule {
    private PythonInterpreter interpreter = SharedPythonInterpreter.interpreter;
    private PyFunction py_predict;

    public PyModule() {
      this.interpreter.exec("from pymodule import predict");
      this.py_predict = (PyFunction) this.interpreter.get("predict");
    }

    public double[] predict(String domain) {
      double[] predictions = new double[3];
      Iterable<PyObject> pyObjectIterable = py_predict.__call__(new PyString(domain)).asIterable();
      int i = 0;
      for (PyObject po : pyObjectIterable) {
         predictions[i++] = po.asDouble();
      }
      return predictions;
    }

  }
}
