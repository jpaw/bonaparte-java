package de.jpaw.bonaparte.demo.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.jpaw.bonaparte.core.MessageParserException;
import de.jpaw.bonaparte.pojos.demo.servlet.HdrParams;
import de.jpaw.bonaparte.pojos.demo.servlet.ReqParams;
import de.jpaw.bonaparte.servlet.HttpHeaderParameterParser;
import de.jpaw.bonaparte.servlet.HttpRequestParameterParser;
import de.jpaw.bonaparte.util.ToStringHelper;


// try with http://localhost:8080/bonaparte-servlet-demo-3.5.1-SNAPSHOT/bon/demo?hello=5

public class DemoServlet extends HttpServlet {
   private static final long serialVersionUID = -427689554319215L;

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       response.setContentType("text/plain");
       
       PrintWriter out = response.getWriter();
       
       try {
           // parse request headers:
           HdrParams hdr = new HdrParams();
           HttpHeaderParameterParser.unmarshal(request, hdr);
       
           // parse request parameters:
           ReqParams req = new ReqParams();
           HttpRequestParameterParser.unmarshal(request, req);
       
           out.println("Supported Query parameters: q, hello, value (int)\n\nreceived result:\nHeader parameters = "
               + ToStringHelper.toStringML(hdr)
               + "\nQuery Parameters = "
               + ToStringHelper.toStringML(req));
       } catch (MessageParserException e) {
           out.println("Parsing error: got " + e.getMessage());
           e.printStackTrace();
       }
   }
}
