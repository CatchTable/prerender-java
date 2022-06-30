package com.github.greengerong;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;

public interface PreRenderEventHandler {

  String beforeRender(HttpServletRequest clientRequest);

  String afterRender(HttpServletRequest clientRequest, HttpServletResponse clientResponse,
      HttpResponse prerenderResponse, String responseHtml);

  void destroy();
}
