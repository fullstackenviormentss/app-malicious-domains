import org.python.util.PythonInterpreter;
import org.python.core.*;
import com.amazonaws.services.lambda.runtime.Context;
import java.util.Properties;
 
public class Classify {

  public static final class SharedPythonInterpreter {
    public static PythonInterpreter get() {
      Properties props = new Properties(System.getProperties());
      props.setProperty("python.security.respectJavaAccessibility", "false");
      PythonInterpreter.initialize(props, null, new String[0]);
      return new PythonInterpreter();
    }
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
    double intercept;
    double length;
    double entropy;
    double proVowels;
    double numWords;
    
    public int getLabel() { return label; }
    public void setLabel(int label) { this.label = label; }

    public double getClass0Prob() { return class0Prob; }
    public void setClass0Prob(double class0Prob) { this.class0Prob = class0Prob; }
    
    public double getClass1Prob() { return class1Prob; }
    public void setClass1Prob(double class1Prob) { this.class1Prob = class1Prob; }
    
    public double getIntercept() { return intercept; }
    public void setIntercept(double intercept) { this.intercept = intercept; }

    public double getLength() { return length; }
    public void setLength(double length) { this.length = length; }
    
    public double getEntropy() { return entropy; }
    public void setEntropy(double entropy) { this.entropy = entropy; }

    public double getProVowels() { return proVowels; }
    public void setProVowels(double proVowels) { this.proVowels = proVowels; }

    public double getNumWords() { return numWords; }
    public void setNumWords(double numWords) { this.numWords = numWords; }

    public ResponseClass(double[] predictions) {
      this.label = (int) predictions[0];
      this.class0Prob = predictions[1];
      this.class1Prob = predictions[2];
      this.intercept = predictions[3];
      this.length = predictions[4];
      this.entropy = predictions[5];
      this.proVowels = predictions[6];
      this.numWords = predictions[7];
    }

    public ResponseClass() {}

  }

  public static ResponseClass myHandler(RequestClass request, Context context) throws PyException {
      
    PyModule module = new PyModule();
    
    //Prediction code is in pymodule.py
    double[] predictions = module.predict(request.domain);
    return new ResponseClass(predictions);
  }

  public static class PyModule {
    private PythonInterpreter interpreter = SharedPythonInterpreter.get();
    private PyFunction py_predict;

    public PyModule() {
      this.interpreter.exec("from pymodule import predict");
      this.py_predict = (PyFunction) this.interpreter.get("predict");
    }

    public double[] predict(String domain) {
      double[] predictions = new double[3 + 1 + MaliciousDomainModel.NAMES.length]; 
      //[label, class0Prob, class1Prob], [intercept], [features] = 3 + 1 + x 
      Iterable<PyObject> pyObjectIterable = py_predict.__call__(new PyString(domain)).asIterable();
      int i = 0;
      for (PyObject po : pyObjectIterable) {
         predictions[i++] = po.asDouble();
      }
      return predictions;
    }

  }
}
